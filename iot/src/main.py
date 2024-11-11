# -*- coding: utf-8 -*-
import asyncio
import time
from sensors.pir_sensor_controll import detect_child_approach
# from wake_word.talkie_wake_word import initialize_wake_word, detect_wake_word
from wake_word.wake_word_for_stt import detect_wake_word

# 대화 상태 플래그
conversation_active = False

# 대화 시작 함수
def initiate_conversation():
    global conversation_active
    conversation_active = True
    # 실제 대화 로직 호출
    # await start_conversation()
    time.sleep(5)
    conversation_active = False

# PIR 센서 감지
async def detect_motion():
    global conversation_active
    while True:
        if conversation_active is not True:
            print("a")
        if not conversation_active and detect_child_approach():
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

