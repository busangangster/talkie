import os
import io
import wave
import webrtcvad
import pyaudio
import sys
from difflib import SequenceMatcher

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from src.config.settings import WAKE_WORD_LIST
from src.logger.logger import get_logger
from src.stt.stt_handler import clova_stt

# 로거 설정
logger = get_logger()

# 오디오 설정
RATE = 16000  # 샘플링 속도
CHUNK = 320  # 20ms 프레임 크기
WAKE_WORDS = WAKE_WORD_LIST
SIMILARITY_THRESHOLD = 0.5  # 유사도 임계값
SILENCE_DURATION = 1  # 침묵 지속 시간 (초)

# 최소 STT 요청 데이터 길이
MIN_AUDIO_LENGTH = RATE * 3

# VAD 설정 - 민감도를 3으로 설정하여 음성을 더 잘 감지
vad = webrtcvad.Vad(3)

def detect_silence_with_vad(frames):
    """ VAD를 사용해 음성을 감지하고, 음성이 아닌 경우 침묵으로 판단 """
    return vad.is_speech(frames, RATE)


def record_until_silence():
    """ VAD를 이용하여 음성을 수집하며, 음성이 일정 기간 지속되면 수집 종료 """
    audio = pyaudio.PyAudio()
    stream = audio.open(format=pyaudio.paInt16, channels=1, rate=RATE, input=True, frames_per_buffer=CHUNK)

    logger.info("녹음을 시작합니다. 음성이 감지되면 일정 시간 동안 유지됩니다.")
    frames = []
    silence_frames = 0
    speaking_frames = 0
    max_silence_frames = int(SILENCE_DURATION * RATE / CHUNK)

    try:
        while silence_frames < max_silence_frames:
            data = stream.read(CHUNK, exception_on_overflow=False)
            frames.append(data)

            if detect_silence_with_vad(data):
                speaking_frames += 1
                silence_frames = 0
                if speaking_frames > 10:  # 최소 음성 지속 시간 (0.2초)
                    print("음성 감지됨. 수집 중...")
            else:
                silence_frames += 1
                if speaking_frames > 0:
                    print("음성 종료됨.")
                speaking_frames = 0
    finally:
        stream.stop_stream()
        stream.close()
        audio.terminate()
        logger.info("녹음이 종료되었습니다.")

    # 수집된 음성 데이터 길이 확인
    if len(b''.join(frames)) < MIN_AUDIO_LENGTH:
        return None

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


def is_similar_to_wake_word(text):
    """ STT 텍스트가 WAKE_WORDS에 포함된 단어들과 유사한지 비교 """
    for wake_word in WAKE_WORDS:
        similarity = SequenceMatcher(None, wake_word, text).ratio()
        logger.info(f"{wake_word}와의 유사도: {similarity}")
        if similarity >= SIMILARITY_THRESHOLD:
            return True
    return False


def detect_wake_word():
    """ 음성 수집 후 STT 변환 결과를 확인하여 wake word 감지 시 동작하는 함수 """
    audio_data = record_until_silence()  # 음성 수집 및 침묵 감지 종료
    if audio_data is None:
        return False

    text_result = clova_stt(audio_data)  # STT 변환 요청

    # STT 결과 확인
    if text_result is None:
        print("STT 결과가 없습니다. 요청을 확인하세요.")
    else:
        print("STT 결과:", text_result)

    # WAKE_WORDS 리스트 내 단어와 유사한 단어 감지
    if text_result and is_similar_to_wake_word(text_result):
        logger.info("Wake word detected in '%s'!", text_result)
        print("Wake word detected!")
        return True
    else:
        logger.info("Wake word not detected.")
    return False


# 실행
# if __name__ == "__main__":
#     print("Listening for wake word...")
#     while True:
#         if detect_wake_word():
#             print("Wake word activated!")
#             break

