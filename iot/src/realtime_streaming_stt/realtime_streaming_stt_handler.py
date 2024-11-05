import grpc
import json
import pyaudio
import threading
import nest_pb2
import nest_pb2_grpc
from src.config.api_key import STREAMING_KEY

# 오디오 설정
RATE = 16000
CHUNK = 32000  # 장문 인식에 맞춰 청크 크기를 설정
CLIENT_SECRET = STREAMING_KEY  # 네이버 클라우드에서 발급받은 Secret Key를 입력하세요

# 종료 플래그
stop_recording = False


def listen_for_exit():
    """사용자가 'q'를 입력하면 녹음을 종료하는 함수"""
    global stop_recording
    input("종료하려면 'q'를 입력하고 Enter 키를 누르세요.\n")
    stop_recording = True  # 종료 플래그 설정


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

    try:
        seq_id = 0
        while not stop_recording:
            # 마이크 입력에서 CHUNK 크기만큼 읽음
            data = stream.read(CHUNK)
            # gRPC 요청 생성 및 전송
            yield nest_pb2.NestRequest(
                type=nest_pb2.RequestType.DATA,
                data=nest_pb2.NestData(
                    chunk=data,
                    extra_contents=json.dumps({"seqId": seq_id, "epFlag": stop_recording})
                )
            )
            seq_id += 1
    except KeyboardInterrupt:
        print("마이크 입력을 종료합니다.")
    finally:
        stream.stop_stream()
        stream.close()
        audio.terminate()
        print("오디오 스트림이 종료되었습니다.")


def main():
    # Clova Speech 서버에 대한 보안 gRPC 채널을 설정
    channel = grpc.secure_channel(
        "clovaspeech-gw.ncloud.com:50051",
        grpc.ssl_channel_credentials()
    )
    stub = nest_pb2_grpc.NestServiceStub(channel)  # NestService에 대한 stub 생성
    metadata = (("authorization", f"Bearer {CLIENT_SECRET}"),)  # 인증 토큰과 함께 메타데이터 설정
    responses = stub.recognize(generate_requests(), metadata=metadata)  # 생성된 요청으로 인식(recognize) 메서드 호출

    # 최종 텍스트 결과를 저장할 변수
    final_sentence = ""
    completed_text = []

    # 사용자 입력을 기다리는 스레드 시작
    exit_thread = threading.Thread(target=listen_for_exit)
    exit_thread.start()

    try:
        # 서버로부터 응답을 반복 처리
        for response in responses:
            response_data = json.loads(response.contents)  # JSON으로 변환
            transcription_text = response_data.get("transcription", {}).get("text", "").strip()
            print("Parsed text:", transcription_text)  # 파싱된 텍스트 출력

            # 빈 텍스트가 아닌 경우에만 추가
            if transcription_text:
                final_sentence += transcription_text  # 받은 텍스트를 문장에 추가

                # 문장의 끝을 감지 (예: 마침표, 느낌표 등으로 문장 종료)
                if any(final_sentence.endswith(p) for p in ['.', '!', '?']):
                    completed_text.append(final_sentence.strip())  # 완성된 문장을 리스트에 추가
                    print("완성된 문장:", final_sentence.strip())  # 즉시 출력
                    final_sentence = ""  # 새로운 문장 시작을 위해 초기화

            if stop_recording:
                if final_sentence:  # 남아 있는 텍스트가 있으면 추가
                    completed_text.append(final_sentence.strip())
                break  # 종료 플래그가 설정되면 응답 수신 중단
    except grpc.RpcError as e:
        # gRPC 오류 처리
        print(f"gRPC 오류: {e.details()}")
    finally:
        channel.close()  # 작업이 끝나면 채널 닫기
        print("gRPC 채널이 종료되었습니다.")

        # 전체 완성된 텍스트 출력
        print("최종 완성된 텍스트:")
        print(" ".join(completed_text))  # 모든 문장을 하나의 텍스트로 출력


if __name__ == "__main__":
    main()
