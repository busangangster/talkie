import os
import pvporcupine
import pyaudio
import struct
from src.config.api_key import WAKEWORD_KEY

# Porcupine과 PyAudio 초기화 함수
def initialize_wake_word():
    porcupine = pvporcupine.create(
        access_key=WAKEWORD_KEY,
        keyword_paths=[os.path.join(os.path.dirname(__file__), "talkie_ko_windows_v3_0_0.ppn")],
        model_path=os.path.join(os.path.dirname(__file__), "porcupine_params_ko.pv")
    )

    p = pyaudio.PyAudio()
    stream = p.open(
        rate=porcupine.sample_rate,
        channels=1,
        format=pyaudio.paInt16,
        input=True,
        frames_per_buffer=porcupine.frame_length
    )

    print("[웨이크 워드 대기 중] '토키야'라고 부르면 대화가 시작됩니다.")
    return porcupine, stream

# 웨이크 워드 감지 함수
async def detect_wake_word(porcupine, stream):
    pcm = stream.read(porcupine.frame_length, exception_on_overflow=False)
    pcm_unpacked = struct.unpack_from("h" * porcupine.frame_length, pcm)
    keyword_index = porcupine.process(pcm_unpacked)

    if keyword_index >= 0:
        print("웨이크 워드 감지됨!")
        return True
    return False
