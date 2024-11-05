import os
import pvporcupine
import pyaudio
import struct
from src.config.api_keys import WAKEWORD_KEY

# 웨이크 워드 설정
porcupine = pvporcupine.create(
    access_key= WAKEWORD_KEY,  # Picovoice 콘솔에서 받은 access key
    keyword_paths=[os.getcwd() + "\\talkie_ko_windows_v3_0_0.ppn"],  # "토키야" 키워드 파일 경로
    model_path=os.getcwd() + "\\porcupine_params_ko.pv"  # 한국어 모델 파일 경로
)

# PyAudio 설정
p = pyaudio.PyAudio()
stream = p.open(
    rate=porcupine.sample_rate,
    channels=1,
    format=pyaudio.paInt16,
    input=True,
    frames_per_buffer=porcupine.frame_length
)

print("[준비 완료] 마이크 입력이 준비되었습니다")

try:
    while True:
        # 마이크에서 오디오 데이터를 읽어와서 `Porcupine`에 전달
        pcm = stream.read(porcupine.frame_length, exception_on_overflow=False)
        pcm_unpacked = struct.unpack_from("h" * porcupine.frame_length, pcm)

        # 키워드 인식
        keyword_index = porcupine.process(pcm_unpacked)
        if keyword_index >= 0:
            print("왜 불러")  # 키워드가 인식되면 실행되는 코드

except KeyboardInterrupt:
    print("프로그램을 종료합니다.")

finally:
    # 자원 정리
    stream.stop_stream()
    stream.close()
    p.terminate()
    porcupine.delete()