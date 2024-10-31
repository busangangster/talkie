import paho.mqtt.client as mqtt
from src.config.settings import BROKER_ADDRESS, TOPIC_SUB, TOPIC_PUB, CLIENT_ID, PROTOCOL

# 메시지 수신 콜백 함수
# 서버에서 메시지를 수신할 때 호출됩니다.
def on_message(client, userdata, message):
    print("B: " + message.payload.decode())

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
client.loop_start()  # 비동기 이벤트 루프 시작 (메시지를 실시간으로 처리하기 위함)
print("채팅을 시작합니다. 종료하려면 'exit'를 입력하세요.")

# 메시지 발행 및 사용자 입력 처리
while True:
    msg = input("A: ")
    if msg.lower() == 'exit':
        break
    # QoS 0을 사용하여 최소 지연으로 메시지 발행
    client.publish(TOPIC_PUB, msg, qos=0)

# 이벤트 루프 중지 및 브로커 연결 종료
client.loop_stop()  # 비동기 루프 중지
client.disconnect()  # 연결 종료
