# -*- coding: utf-8 -*-
import ctypes
import time

# C 라이브러리 불러오기
pir_sensor_lib = ctypes.CDLL('./libpir_detect.so')

# 상시 감지 및 대화 시작 로직
def main():
    while True:
        # PIR 센서로 접근 감지
        motion_detected = pir_sensor_lib.detect_motion()
        if motion_detected == 1:
            print("아이 접근 감지됨!")
            time.sleep(5)  # 대화가 끝난 후 일정 시간 동안 감지 중단 (재감지 딜레이)
        else:
            time.sleep(1)  # 감지 간격 (반복 체크 주기)

if __name__ == "__main__":
    main()

