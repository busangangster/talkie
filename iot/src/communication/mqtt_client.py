import base64
import paho.mqtt.client as mqtt
import json
import sys
import os
import wave
import time

from pydub import AudioSegment
from pydub.playback import play

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.config.settings import BROKER_ADDRESS, TOPIC_SUB, TOPIC_PUB, CLIENT_ID, PROTOCOL
# from src.stt.stt_handler import start_conversation
from src.stt.stt_handler import speech_to_text

voiceFlag = True

# 서버에서 메시지를 수신할 때 호출됩니다.
def on_message(client, userdata, message):
    try:
        # JSON 형식으로 메시지 파싱
        payload = json.loads(message.payload.decode())

        # "audio" 필드가 존재하는지 확인
        audio_base64 = payload.get("audio", None)
        
        if audio_base64 and isinstance(audio_base64, str):
            # Base64 디코딩
            try:
                pcm_data = base64.b64decode(audio_base64)
                
                # PCM 데이터를 WAV 파일로 저장
                with wave.open("response.wav", "wb") as wav_file:
                    wav_file.setnchannels(1)       # 채널 수 (모노)
                    wav_file.setsampwidth(2)       # 샘플 폭 (16비트 = 2바이트)
                    wav_file.setframerate(24000)   # 샘플링 레이트 (24kHz)
                    wav_file.writeframes(pcm_data)
                
                # WAV 파일을 로드하고 재생
                audio = AudioSegment.from_wav("response.wav")
                play(audio)
                
                # 파일 삭제하여 중복 방지
                os.remove("response.wav")
            except Exception as e:
                print("Error processing PCM data:", e)
        else:
            print("No valid 'audio' field in message.")
    except json.JSONDecodeError:
        print("Failed to decode JSON from message payload.")
    global voiceFlag
    voiceFlag = False

# MQTT 클라이언트 설정
# client_id는 각 클라이언트를 고유하게 식별하는 데 사용됩니다.
client = mqtt.Client(client_id=CLIENT_ID, protocol=getattr(mqtt, PROTOCOL))
client.on_message = on_message  # 메시지 수신 콜백 함수 설정

# 브로커에 연결
# keepalive 설정을 통해 5초마다 연결 상태를 확인합니다. (지연을 최소화)
client.connect(BROKER_ADDRESS, keepalive=5)

# 구독 설정
# 주제(TOPIC_SUB)를 통해 수신 대기하며, QoS 0을 통해 빠른 메시지 수신을 우선합니다.
client.subscribe(TOPIC_SUB, qos=1)  # QoS 0: 최소 지연으로 메시지 전송

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
    client.publish(TOPIC_PUB, json.dumps(msg), qos=1)


# 각 요청에 맞는 메시지 발행

# 메시지 전송 요청
def start_conversation():
    global voiceFlag
    while True:
        text = ""
        while not text:
            text = speech_to_text()
            pass

        publish_message("topic/message/send", data={"content":text})

        voiceFlag = True
        while voiceFlag:
            pass
        # time.sleep(1)  # 서버 과부하 방지


# WebSocket 연결 요청
publish_message("topic/websocket/connect")

# 대화 종료 요청
# publish_message("topic/conversation/end")

# # 사용자 감지 요청
# publish_message("topic/user/detection")

# # 음성 인식 요청
# publish_message("topic/voice/recognition")

# msg = start_conversation()
# client.publish(TOPIC_PUB, msg, qos=0)

# 이벤트 루프 중지 및 브로커 연결 종료
# try:
#     while True:
#         start_conversation_loop()
# except KeyboardInterrupt:
#     print("프로그램 종료")

start_conversation()

# 이벤트 루프 중지 및 연결 종료
client.loop_stop()
client.disconnect()
