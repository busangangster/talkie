# -*- coding: utf-8 -*-
import asyncio
import time
from sensors.pir_controll_wpy import detect_child_approach
# from wake_word.talkie_wake_word import initialize_wake_word, detect_wake_word
from wake_word.wake_word_for_stt import detect_wake_word

import Jetson.GPIO as GPIO

# 핀 설정
led_pin = 37
PIR_PIN = 7  # BOARD 핀 번호로 설정합니다. (BCM 번호 4, Jetson에서는 BOARD 모드를 사용)

# GPIO 설정
GPIO.setmode(GPIO.BOARD)
GPIO.setup(PIR_PIN, GPIO.IN)  # PIR 센서를 입력 모드로 설정
GPIO.setup(led_pin, GPIO.OUT)  # 핀을 출력으로 설정


# 대화 상태 플래그
conversation_active = False

# 대화 시작 함수
def initiate_conversation():
    global conversation_active
    conversation_active = True
    # 실제 대화 로직 호출
    # await start_conversation()
    GPIO.output(led_pin, GPIO.HIGH)  # LED 켜기
    time.sleep(5)  # 1초 대기
    GPIO.output(led_pin, GPIO.LOW) 
    conversation_active = False

# PIR 센서 감지
async def detect_motion():
    global conversation_active
    while True:
        #if conversation_active is not True:
            #print("a")
        if not conversation_active and detect_child_approach():
            initiate_conversation()
        await asyncio.sleep(0.5)

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

# 메인 함수
async def main():
    await asyncio.gather(
        detect_motion(),
        check_wake_word()
    )

if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    loop.close()

