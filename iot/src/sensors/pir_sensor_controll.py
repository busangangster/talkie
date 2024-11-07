# sensor.py
# -*- coding: utf-8 -*-
import ctypes
import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
src_path = os.path.join(current_dir, '..', 'src')
if src_path not in sys.path:
    sys.path.append(src_path)

from src.logger.logger import get_logger

# 로거 가져오기
logger = get_logger()

# C 라이브러리 불러오기
pir_sensor_lib = ctypes.CDLL('./libpir_detect.so')

# 아이 접근 감지 함수
def detect_child_approach():
    # PIR 센서로 접근 감지
    motion_detected = pir_sensor_lib.detect_motion()
    if motion_detected == 1:
        logger.info("아이 접근 감지됨!")
        print("아이 접근 감지됨!")
        return True
    return False
