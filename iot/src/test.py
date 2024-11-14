import asyncio
import time
import os
import sys
import base64
import json
import paho.mqtt.client as mqtt
import threading
import wave

sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from pydub import AudioSegment
from pydub.playback import play
from src.sensors.pir_controll_wpy import detect_child_approach  # PIR 센서 감지 함수
from src.stt.mqtt_for_stt import speech_to_text  # 음성 인식
from src.config.settings import BROKER_ADDRESS, TOPIC_SUB, TOPIC_PUB, CLIENT_ID, PROTOCOL

# 이벤트 객체 생성
voice_event = threading.Event()
exit_event = threading.Event()
session_active = True  # 세션 상태 플래그
response_received = False
conversation_active = False  # 대화 상태 플래그
user_seq = 1

playback_in_progress = threading.Event()


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

                # 음성 파일 재생을 동기적으로 대기
                print("음성 파일 재생 시작")
                playback_event = threading.Event()
                playback_in_progress.set()  # 재생 중 상태 설정

                # 오디오 재생 스레드
                def play_audio():
                    play(audio)
                    playback_event.set()  # 재생이 끝나면 이벤트를 설정하여 대기 해제

                # 재생 스레드 시작 및 대기
                play_thread = threading.Thread(target=play_audio)
                play_thread.start()
                playback_event.wait()  # 재생 완료까지 대기

                playback_in_progress.clear()  # 재생 중 상태 해제
                print("음성 파일 재생 완료")
                os.remove("response.wav")
                
                # 응답 시간 갱신
                last_response_time = time.time()
                response_received = True
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

    global last_response_time, response_received, conversation_active
    while not exit_event.is_set():
        print("텍스트 받는 중")

        text = ""
        idle_time = 0  # 음성 입력 대기 시간 카운터


        while not text and not exit_event.is_set():
            text = speech_to_text()
            print("stt complete")

            if text:
                print(text)
                break
            else:
                idle_time += 1
                print(f"음성 입력 대기 중... 경과 시간: {idle_time}초")
                if idle_time >= 30:
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


        if exit_event.is_set():
            print("exit_event 설정됨, 상위 루프 최종 종료")
            break
        

    print("대화가 종료되었습니다.")
    conversation_active = False  # 대화 상태 플래그 리셋

# PIR 센서 감지 함수
async def detect_motion():
    global conversation_active
    while True:
        if not conversation_active and detect_child_approach():
            print("PIR 센서로 감지됨! 대화를 시작합니다.")
            # 사용자 감지 주제로 메시지 전송
            publish_message("topic/user/detection")
            # 대화 시작
            conversation_active = True
            exit_event.clear()
            conversation_thread = threading.Thread(target=start_conversation)
            conversation_thread.start()
        await asyncio.sleep(1)

# 메인 함수 (비동기)
async def main():
    await asyncio.gather(
        detect_motion()
    )

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

if __name__ == "__main__":
    try:
        loop = asyncio.get_event_loop()
        loop.run_until_complete(main())
        loop.close()
    except KeyboardInterrupt:
        publish_message("topic/conversation/end")
        print("프로그램 종료")
        exit_event.set()
    finally:
        client.loop_stop()
        client.disconnect()
        print("프로그램이 종료되었습니다.")

