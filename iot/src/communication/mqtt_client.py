import paho.mqtt.client as mqtt
import json
import sys
import os
from gtts import gTTS
from playsound import playsound

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))
from src.config.settings import BROKER_ADDRESS, TOPIC_SUB, TOPIC_PUB, CLIENT_ID, PROTOCOL
from src.stt.stt_handler import start_conversation

# 서버에서 메시지를 수신할 때 호출됩니다.
def on_message(client, userdata, message):
    # 메시지를 텍스트로 디코딩하여 출력
    text = message.payload.decode()
    print("B: " + text)
    
    # 텍스트를 음성으로 변환
    tts = gTTS(text=text, lang='ko')  # 한국어 설정
    audio_file = "response.mp3"
    tts.save(audio_file)

    # 음성을 재생
    playsound(audio_file)
    
    # 파일 삭제하여 중복 방지
    os.remove(audio_file)

# MQTT 클라이언트 설정
# client_id는 각 클라이언트를 고유하게 식별하는 데 사용됩니다.
client = mqtt.Client(client_id=CLIENT_ID, protocol=getattr(mqtt, PROTOCOL))
client.on_message = on_message  # 메시지 수신 콜백 함수 설정

# 브로커에 연결
# keepalive 설정을 통해 5초마다 연결 상태를 확인합니다. (지연을 최소화)
client.connect(BROKER_ADDRESS, keepalive=5)

# 구독 설정
# 주제(TOPIC_SUB)를 통해 수신 대기하며, QoS 0을 통해 빠른 메시지 수신을 우선합니다.
client.subscribe(TOPIC_SUB, qos=0)  # QoS 0: 최소 지연으로 메시지 전송

# 이벤트 루프 시작
# 비동기 이벤트 루프 시작 (메시지를 실시간으로 처리하기 위함)
client.loop_start() 

user_seq = 1

def publish_message(header, data=None):
    msg = {
        "userSeq": user_seq,
        "header": header
    }
    if data:
        msg["data"] = data
    else:
        print("데이터가 비어 있어 전송되지 않음")
    client.publish(TOPIC_PUB, json.dumps(msg), qos=0)


# 각 요청에 맞는 메시지 발행
# WebSocket 연결 요청
# publish_message("topic/websocket/connect")

# 메시지 전송 요청

text = start_conversation()

if text:  # 변환된 텍스트가 있을 때만 메시지 발행
    publish_message("topic/message/send", data={"content": text})
else:
    print("텍스트 변환 결과가 비어 있어 전송하지 않음")
# 대화 종료 요청
# publish_message("topic/conversation/end")

# # 사용자 감지 요청
# publish_message("topic/user/detection")

# # 음성 인식 요청
# publish_message("topic/voice/recognition")

# msg = start_conversation()
# client.publish(TOPIC_PUB, msg, qos=0)

# 이벤트 루프 중지 및 브로커 연결 종료
try:
    while True:
        pass  # 무한 루프를 사용하여 프로그램이 종료되지 않도록 유지
except KeyboardInterrupt:
    print("프로그램 종료")

# 이벤트 루프 중지 및 연결 종료
client.loop_stop()
client.disconnect()
