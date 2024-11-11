import pyaudio
import wave
import requests
import io
import numpy as np
import os
import sys

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.config.api_key import CLIENT_ID, CLIENT_SECRET, URL
from src.logger.logger import get_logger

# 로거 설정
logger = get_logger()

# 오디오 설정
RATE = 16000  # 샘플링 속도
CHUNK = 1024  # 청크 크기 (0.25초)
SILENCE_THRESHOLD = 2500  # 볼륨 기준치 (이하의 값이면 침묵으로 간주)
SILENCE_DURATION = 2  # 침묵 지속 시간 (초)

# 헤더 설정
headers = {
    "X-NCP-APIGW-API-KEY-ID": CLIENT_ID,
    "X-NCP-APIGW-API-KEY": CLIENT_SECRET,
    "Content-Type": "application/octet-stream"
}


def clova_stt(audio_data):
    """
     네이버 클로바 STT API로 WAV 데이터를 텍스트로 변환하는 함수
    :param audio_data: WAV 포맷의 바이너리 데이터
    :return: STT 변환된 텍스트 또는 None
    """
    try:
        response = requests.post(URL, headers=headers, data=audio_data)
        if response.status_code == 200:
            result = response.json().get("text", "")
            logger.info("STT 변환 성공: %s", result)
            return result
        else:
            logger.error("STT 변환 오류: %s %s", response.status_code, response.text)
            return None
    except requests.exceptions.RequestException as e:
        logger.error("STT 요청 실패: %s", e)
        return None


def detect_silence(frames, silence_frames):
    """
    침묵 감지 함수: 청크의 RMS 값이 기준 이하로 떨어진다면 침묵으로 판단
    :param frames: 마이크 입력 청크
    :param silence_frames: 일정 시간 동안 침묵이 유지되었는지 판단하는 카운터
    :return: 음성 또는 침묵 여부, 갱신된 침묵 카운터
    """
    # RMS 계산, frames가 비정상적인 경우 0으로 설정
    try:
        rms = np.sqrt(np.mean(np.square(np.frombuffer(frames, dtype=np.int16))))
    except ValueError:  # frames가 유효하지 않다면 RMS를 0으로 설정
        rms = 0

    if rms < SILENCE_THRESHOLD:
        silence_frames += 1
    else:
        silence_frames = 0
    return silence_frames


def record_until_silence():
    """
    일정 시간 동안 음성을 수집하며, 침묵이 감지되면 종료하고 수집된 음성 데이터 반환
    :return: WAV 포맷의 바이너리 데이터
    """
    audio = pyaudio.PyAudio()
    stream = audio.open(format=pyaudio.paInt16, channels=1, rate=RATE, input=True, frames_per_buffer=CHUNK)

    logger.info("녹음을 시작합니다. 침묵이 감지되면 녹음이 종료됩니다.")
    frames = []
    silence_frames = 0
    max_silence_frames = int(SILENCE_DURATION * RATE / CHUNK)

    try:
        while silence_frames < max_silence_frames:
            data = stream.read(CHUNK, exception_on_overflow=False)
            frames.append(data)
            silence_frames = detect_silence(data, silence_frames)
    finally:
        stream.stop_stream()
        stream.close()
        audio.terminate()
        logger.info("녹음이 종료되었습니다.")

    # 메모리 내에서 WAV 포맷으로 저장
    wav_data = io.BytesIO()
    wf = wave.open(wav_data, 'wb')
    wf.setnchannels(1)
    wf.setsampwidth(audio.get_sample_size(pyaudio.paInt16))
    wf.setframerate(RATE)
    wf.writeframes(b''.join(frames))
    wf.close()
    wav_data.seek(0)

    return wav_data.read()


def start_conversation():
    """
    음성 수집 후 STT 변환 결과를 반환하는 함수
    """
    audio_data = record_until_silence()  # 음성 수집 및 침묵 감지 종료
    text_result = clova_stt(audio_data)  # STT 변환 요청
    if text_result:
        logger.info("변환된 텍스트: %s", text_result)
    return text_result