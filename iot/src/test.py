import asyncio
import time
import os
import sys
import base64
import json
import paho.mqtt.client as mqtt

sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from src.sensors.pir_controll_wpy import detect_child_approach  # PIR 센서 감지 함수
from src.wake_word.wake_word_for_stt import detect_wake_word  # 웨이크 워드 함수
from src.communication.mqtt_client import publish_message  # MQTT 메시지 전송 함수
from src.stt.stt_handler import speech_to_text  # 음성 인식

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

def on_message(client, userdata,message):
    global session_active, last_response_time, response_received

    if not session_active:
        print("세션이 끊겨 메시지를 받을 수 없습니다.")
        return
    
    print("on_message 트리거됨")  # 추가된 로그


    try:
        payload = json.loads(message.payload.decode())

        audio_base64 = payload.get("audio", None)
        # print("Received message:", audio_base64)
        if audio_base64 and isinstance(audio_base64, str):
            try:
                pcm_data = base64.b64decode(audio_base64)
                with wave.open("response.wav", "wb") as wav_file:
                    wav_file.setnchannels(1)
                    wav_file.setsampwidth(2)
                    wav_file.setframerate(24000)
                    wav_file.writeframes(pcm_data)

                audio = AudioSegment.from_wav("response.wav")
                
                # 응답 수신 상태를 True로 설정
                response_received = True
                
                # 음성 파일 재생 시작
                print("음성 파일 재생 시작")
                play(audio)
                print("음성 파일 재생 완료")
                
                os.remove("response.wav")
                
                # 응답 수신 상태 초기화
                response_received = True
                voice_event.set()

                # 음성 파일 재생 후에 응답 시간 갱신
                last_response_time = time.time()


            except Exception as e:
                print("PCM 데이터 처리 중 오류:", e)
        else:
            print("유효한 'audio' 필드가 메시지에 없습니다.")
    except json.JSONDecodeError:
        print("메시지 페이로드의 JSON 디코딩에 실패했습니다.")
    except Exception as e:
        print("on_message 오류:", e)

# mqtt 설정
BROKER_ADDRESS = "k11e104.p.ssafy.io"
TOPIC_SUB = "tokie-client"
TOPIC_PUB = "tokie-server"
CLIENT_ID = "Participant_A"
PROTOCOL = "MQTTv311"

# MQTT 클라이언트 초기화
client = mqtt.Client(client_id=CLIENT_ID, protocol=getattr(mqtt, PROTOCOL))

# 대화 상태 플래그
conversation_active = False

# 대화 시작 함수
def initiate_conversation():
    global conversation_active
    conversation_active = True
    
    # WebSocket 연결 메시지 전송 (ChatGPT와의 연결 요청)
    publish_message("topic/websocket/connect")
    print("대화를 시작합니다...")
    
    # 음성 인식 루프 시작
    while conversation_active:
        text = speech_to_text()
        if text:
            publish_message("topic/message/send", data={"content": text})
        time.sleep(1)  # 서버 과부하 방지
    print("대화가 종료되었습니다.")

# PIR 센서 감지 함수
async def detect_motion():
    global conversation_active
    while True:
        if not conversation_active and detect_child_approach():
            print("PIR 센서로 감지됨! 대화를 시작합니다.")
            # 사용자 감지 주제로 메시지 전송
            publish_message("topic/user/detection")
            initiate_conversation()
        await asyncio.sleep(1)

# 웨이크 워드 감지
async def check_wake_word():
    global conversation_active
    try:
        while True:
            if not conversation_active and detect_wake_word():
                print("웨이크 워드 감지됨! 대화를 시작합니다.")
                initiate_conversation()
            await asyncio.sleep(0.01)
    finally:
        print("웨이크 워드 감지 종료")


# 메인 함수 (비동기)
async def main():
    await asyncio.gather(
        detect_motion(),
        check_wake_word()
    )

if __name__ == "__main__":
    try:
        loop = asyncio.get_event_loop()
        loop.run_until_complete(main())
        loop.close()
    except KeyboardInterrupt:
        publish_message("topic/conversation/end")
        print("프로그램 종료")
    finally:
        client.loop_stop()
        client.disconnect()