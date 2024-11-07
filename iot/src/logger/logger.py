import logging
import os

# 현재 파일 위치 기준 상대 경로로 logs 폴더 설정
log_dir = os.path.join(os.path.dirname(__file__), 'logs')

# logs 폴더가 없으면 생성
if not os.path.exists(log_dir):
    os.makedirs(log_dir)

# 로그 파일 경로
log_file_path = os.path.join(log_dir, 'talkie_log.log')

# 로깅 설정 (UTF-8 인코딩 추가)
logging.basicConfig(
    filename=log_file_path,
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    encoding='utf-8'  # 한글 인코딩 설정
)

# 로거 객체 반환 함수
def get_logger(name=__name__):
    return logging.getLogger(name)
