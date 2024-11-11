# sensor.py
# -*- coding: utf-8 -*-
import Jetson.GPIO as GPIO
import time
import os
import sys

# 경로 설정
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

# 로거 가져오기
from src.logger.logger import get_logger
logger = get_logger()

# 핀 설정
PIR_PIN = 7  # BOARD 핀 번호로 설정합니다. (BCM 번호 4, Jetson에서는 BOARD 모드를 사용)

# GPIO 설정
GPIO.setmode(GPIO.BOARD)
GPIO.setup(PIR_PIN, GPIO.IN)  # PIR 센서를 입력 모드로 설정

def detect_child_approach():
    try:
        # PIR 센서로 접근 감지
        motion_detected = GPIO.input(PIR_PIN)
        if motion_detected == 1:
            logger.info("아이 접근 감지됨!")
            print("아이 접근 감지됨!")
            return True
        return False
    except Exception as e:
        logger.error(f"PIR 센서 오류 발생: {e}")
        return False
