import grpc
import json
import pyaudio
import audioop
import nest_pb2
import nest_pb2_grpc

import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
src_path = os.path.join(current_dir, '..', 'src')
if src_path not in sys.path:
    sys.path.append(src_path)

from src.config.api_key import STREAMING_KEY

# 오디오 설정
RATE = 16000
CHUNK = 32000
CLIENT_SECRET = STREAMING_KEY

# 침묵 감지 설정
SILENCE_THRESHOLD = 100  # 소음 감지 임계값
SILENCE_DURATION = 2  # 침묵으로 간주할 지속 시간 (초)

# 종료 플래그
stop_recording = False

def generate_requests():
    """마이크에서 오디오 데이터를 읽어 gRPC 요청 생성"""
    global stop_recording
    audio = pyaudio.PyAudio()
    stream = audio.open(format=pyaudio.paInt16, channels=1, rate=RATE, input=True, frames_per_buffer=CHUNK)
    print("마이크 입력을 시작합니다...")

    # 초기 설정 요청: 음성 인식 설정
    yield nest_pb2.NestRequest(
        type=nest_pb2.RequestType.CONFIG,
        config=nest_pb2.NestConfig(
            config=json.dumps({"transcription": {"language": "ko"}})
        )
    )

    silent_chunks = 0
    max_silent_chunks = int(RATE / CHUNK * SILENCE_DURATION)
    seq_id = 0

    try:
        while not stop_recording:
            data = stream.read(CHUNK)
            rms = audioop.rms(data, 2)

            # 침묵인지 확인하고 카운트 증가
            if rms < SILENCE_THRESHOLD:
                silent_chunks += 1
                if silent_chunks >= max_silent_chunks:
                    # print("지속적인 침묵이 감지되어 녹음을 종료합니다.")
                    stop_recording = True
                    break
            else:
                silent_chunks = 0  # 유효한 음성이 감지되면 침묵 카운트 초기화

            yield nest_pb2.NestRequest(
                type=nest_pb2.RequestType.DATA,
                data=nest_pb2.NestData(
                    chunk=data,
                    extra_contents=json.dumps({"seqId": seq_id})
                )
            )
            seq_id += 1
    finally:
        stream.stop_stream()
        stream.close()
        audio.terminate()
        # print("오디오 스트림이 종료되었습니다.")

def main():
    channel = grpc.secure_channel(
        "clovaspeech-gw.ncloud.com:50051",
        grpc.ssl_channel_credentials()
    )
    stub = nest_pb2_grpc.NestServiceStub(channel)
    metadata = (("authorization", f"Bearer {CLIENT_SECRET}"),)
    responses = stub.recognize(generate_requests(), metadata=metadata)

    # 최종 텍스트 결과를 저장할 변수
    all_text = ""

    try:
        for response in responses:
            if stop_recording:
                break

            response_data = json.loads(response.contents)
            transcription_text = response_data.get("transcription", {}).get("text", "").strip()

            # 실시간으로 변환된 텍스트를 즉시 출력
            if transcription_text:
                # print("변환된 텍스트:", transcription_text)
                all_text += transcription_text + ""  # 변환된 텍스트를 전체 텍스트에 추가
    except grpc.RpcError as e:
        print(f"gRPC 오류: {e.details()}")
    finally:
        # 채널을 닫고, 전체 텍스트를 최종 출력
        channel.close()
        # print("gRPC 채널이 종료되었습니다.")
        # print("최종 완성된 텍스트:")
        print(all_text.strip())  # 최종 완성된 텍스트 출력

if __name__ == "__main__":
    main()
