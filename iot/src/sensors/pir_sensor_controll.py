# sensor.py
# -*- coding: utf-8 -*-
import ctypes

# C 라이브러리 불러오기
pir_sensor_lib = ctypes.CDLL('./libpir_detect.so')

# 아이 접근 감지 함수
def detect_child_approach():
    # PIR 센서로 접근 감지
    motion_detected = pir_sensor_lib.detect_motion()
    if motion_detected == 1:
        print("아이 접근 감지됨!")
        return True
    return False