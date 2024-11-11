import asyncio
import time
import os
import sys
import base64
import json
import paho.mqtt.client as mqtt

sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from src.sensors.pir_sensor_controll import detect_child_approach  # PIR 센서 감지 함수
from src.wake_word.talkie_wake_word import initialize_wake_word, detect_wake_word  # 웨이크 워드 함수
from src.communication.mqtt_client import publish_message  # MQTT 메시지 전송 함수
from src.stt.stt_handler import speech_to_text  # 음성 인식

# MQTT 설정
BROKER_ADDRESS = "YOUR_BROKER_ADDRESS"  # 실제 브로커 주소
TOPIC_SUB = "YOUR_SUB_TOPIC"
TOPIC_PUB = "YOUR_PUB_TOPIC"
CLIENT_ID = "YOUR_CLIENT_ID"
PROTOCOL = "MQTTv311"  # 사용할 프로토콜 버전

# MQTT 클라이언트 초기화
client = mqtt.Client(client_id=CLIENT_ID, protocol=getattr(mqtt, PROTOCOL))

# 대화 상태 플래그
conversation_active = False

# 대화 시작 함수
async def initiate_conversation():
    global conversation_active
    conversation_active = True
    
    # WebSocket 연결 메시지 전송 (ChatGPT와의 연결 요청)
    publish_message("topic/websocket/connect", "웹소켓 연결 요청")
    print("대화를 시작합니다...")
    
    # 음성 인식 루프 시작
    while conversation_active:
        text = speech_to_text()
        if text:
            publish_message("topic/message/send", data={"content": text})
        await asyncio.sleep(1)  # 서버 과부하 방지
    print("대화가 종료되었습니다.")

# PIR 센서 감지 함수
async def detect_motion():
    global conversation_active
    while True:
        if not conversation_active and detect_child_approach():
            print("PIR 센서로 감지됨! 대화를 시작합니다.")
            # 사용자 감지 주제로 메시지 전송
            publish_message("topic/user/detection", "사용자 접근 감지")
            await initiate_conversation()
        await asyncio.sleep(1)

# 웨이크 워드 감지 함수
async def check_wake_word():
    global conversation_active
    porcupine, stream = initialize_wake_word()
    try:
        while True:
            if not conversation_active and await detect_wake_word(porcupine, stream):
                print("웨이크 워드 감지됨! 대화를 시작합니다.")
                # 음성 인식 주제로 메시지 전송
                publish_message("topic/voice/recognition", "웨이크 워드 감지됨")
                await initiate_conversation()
            await asyncio.sleep(0.01)
    finally:
        stream.stop_stream()
        stream.close()
        porcupine.delete()

# MQTT 수신 콜백 함수
def on_message(client, userdata, message):
    global conversation_active
    try:
        payload = json.loads(message.payload.decode())
        audio_base64 = payload.get("audio", None)
        if audio_base64:
            # Base64 디코딩 및 재생
            pcm_data = base64.b64decode(audio_base64)
            # 파일 처리 및 재생 로직 (간략화)
            conversation_active = False  # 대화 종료 플래그 설정
    except json.JSONDecodeError:
        print("Failed to decode JSON from message payload.")

# MQTT 설정 및 시작
client.on_message = on_message
client.connect(BROKER_ADDRESS)
client.subscribe(TOPIC_SUB)
client.loop_start()

# 메인 함수 (비동기)
async def main():
    await asyncio.gather(
        detect_motion(),
        check_wake_word()
    )

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        publish_message("topic/conversation/end")
        print("프로그램 종료")
    finally:
        client.loop_stop()
        client.disconnect()
