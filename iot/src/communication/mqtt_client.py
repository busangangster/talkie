import base64
import paho.mqtt.client as mqtt
import json
import sys
import os
import wave
import time
import threading

from pydub import AudioSegment
from pydub.playback import play

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))
    
from src.config.settings import BROKER_ADDRESS, TOPIC_SUB, TOPIC_PUB, CLIENT_ID, PROTOCOL
from src.stt.stt_handler import speech_to_text  # 이 함수에 타임아웃 기능을 추가해야 합니다.

# 이벤트 객체 생성
voice_event = threading.Event()
exit_event = threading.Event()
session_active = True  # 세션 상태 플래그
response_received = False

user_seq = 1

def on_disconnect(client, userdata, rc):
    global session_active
    if rc != 0:
        print(f"비정상적으로 연결이 끊겼습니다. 재연결을 시도합니다. (rc: {rc})")
        session_active = False  # 세션이 끊겼음을 표시

        # 재연결 시도
        reconnect_attempts = 0
        while not session_active and reconnect_attempts < 5:
            try:
                client.reconnect()
                client.subscribe(TOPIC_SUB, qos=1)  # 재연결 후 구독 재설정
                session_active = True
                print("재연결 및 구독 재설정 성공")
            except Exception as e:
                reconnect_attempts += 1
                print(f"재연결 실패 ({reconnect_attempts}/5): {e}")
                time.sleep(2)
    else:
        print("정상적으로 연결이 종료되었습니다.")
        session_active = False

def on_message(client, userdata, message):
    global session_active, last_response_time, response_received

    if not session_active:
        print("세션이 끊겨 메시지를 받을 수 없습니다.")
        return
    
    print("on_message 트리거됨")  # 추가된 로그

    try:
        payload = json.loads(message.payload.decode())

        audio_base64 = payload.get("audio", None)
        if audio_base64 and isinstance(audio_base64, str):
            try:
                pcm_data = base64.b64decode(audio_base64)
                with wave.open("response.wav", "wb") as wav_file:
                    wav_file.setnchannels(1)
                    wav_file.setsampwidth(2)
                    wav_file.setframerate(24000)
                    wav_file.writeframes(pcm_data)

                audio = AudioSegment.from_wav("response.wav")
                
                # 음성 파일 재생 시작
                print("음성 파일 재생 시작")
                play(audio)
                print("음성 파일 재생 완료")
                
                os.remove("response.wav")
                
                # 음성 파일 재생 후에 응답 시간 갱신
                last_response_time = time.time()
                print(f"음성 파일 재생 완료 후 last_response_time 갱신됨: {last_response_time}")
                
                # 응답 수신 상태를 True로 설정
                response_received = True

                # 응답 대기 상태를 종료하도록 이벤트 설정
                voice_event.set()

            except Exception as e:
                print("PCM 데이터 처리 중 오류:", e)
        else:
            print("유효한 'audio' 필드가 메시지에 없습니다.")
    except json.JSONDecodeError:
        print("메시지 페이로드의 JSON 디코딩에 실패했습니다.")
    except Exception as e:
        print("on_message 오류:", e)

def publish_message(header, data=None):
    msg = {
        "userSeq": user_seq,
        "header": header
    }
    if data:
        msg["data"] = data
    result = client.publish(TOPIC_PUB, json.dumps(msg), qos=1)
    print(f"메시지 발행 결과: {result.rc}, MID: {result.mid}")

def start_conversation():
    global last_response_time, response_received
    while not exit_event.is_set():
        print("텍스트 받는 중")
        text = ""
        idle_time = 0  # 음성 입력 대기 시간 카운터

        while not text and not exit_event.is_set():
            text = speech_to_text(timeout=10)  # 타임아웃을 1초로 설정
            if text:
                break
            else:
                idle_time += 1
                print(f"음성 입력 대기 중... 경과 시간: {idle_time}초")
                if idle_time >= 10:
                    print("대화가 종료됩니다.")
                    publish_message("topic/conversation/end")
                    exit_event.set()
                    print("exit_event 설정됨, 내부 루프 종료")
                    break

        if exit_event.is_set():
            print("exit_event 설정됨, 상위 루프 종료")
            break

        print("메시지 전송:", text)
        publish_message("topic/message/send", data={"content": text})

        print("응답 대기 중...")
        voice_event.clear()
        
        # 응답 상태 초기화
        response_received = False  

        # 응답 대기 루프
        response_time = 0  # 응답 대기 시간 카운터
        while not voice_event.is_set() and not exit_event.is_set():
            # 응답 대기 중 2초 이상 경과 시 재요청 (단, 아직 응답이 오지 않은 경우에만)
            if response_time > 4 and not response_received:
                print("응답 시간이 초과되었습니다. 다시 요청을 시도합니다.")
                publish_message("topic/message/send", data={"content": text})
                response_time = 0  # 재요청 후 대기 시간 초기화

            time.sleep(1)
            response_time += 1

        if exit_event.is_set():
            print("exit_event 설정됨, 상위 루프 최종 종료")
            break

        # 음성 파일 재생 완료 후부터 1초마다 카운터 증가 및 로그 출력
        idle_time = 0
        while not exit_event.is_set():
            time.sleep(1)
            idle_time += 1
            print(f"음성 파일 재생 후 경과 시간: {idle_time}초")
            if idle_time >= 10:
                print("10초 동안 입력이 없어 대화를 종료합니다.")
                publish_message("topic/conversation/end")
                exit_event.set()
                break

    print("대화가 종료되었습니다.")

# MQTT 클라이언트 설정
client = mqtt.Client(client_id=CLIENT_ID, clean_session=False, protocol=getattr(mqtt, PROTOCOL))
client.on_disconnect = on_disconnect
client.on_message = on_message
print("콜백 함수 등록 완료")

# 브로커에 연결
client.connect(BROKER_ADDRESS, keepalive=30)
print("브로커에 연결 완료")

# 구독 설정
client.subscribe(TOPIC_SUB, qos=1)
print(f"주제 {TOPIC_SUB} 구독 완료")

# 이벤트 루프 시작
client.loop_start()
print("MQTT 이벤트 루프 시작")

# start_conversation 함수를 별도의 스레드에서 실행
conversation_thread = threading.Thread(target=start_conversation)
conversation_thread.start()

try:
    while not exit_event.is_set():
        time.sleep(1)
except KeyboardInterrupt:
    publish_message("topic/conversation/end")
    print("프로그램 종료")
    exit_event.set()

# 프로그램 종료 시 정리
conversation_thread.join()
client.loop_stop()
client.disconnect()
print("프로그램이 종료되었습니다.")
