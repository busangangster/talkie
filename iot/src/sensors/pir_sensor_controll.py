# sensor.py
# -*- coding: utf-8 -*-
import ctypes
import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.logger.logger import get_logger

# 로거 가져오기
logger = get_logger()

# C 라이브러리 불러오기
pir_sensor_lib = ctypes.CDLL(os.path.join(os.path.dirname(__file__), 'libpir_detect.so'))

# 아이 접근 감지 함수
def detect_child_approach():
    # PIR 센서로 접근 감지
    motion_detected = pir_sensor_lib.detect_motion()
    if motion_detected == 1:
        logger.info("아이 접근 감지됨!")
        print("아이 접근 감지됨!")
        return True
    return False