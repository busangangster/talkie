import logging
import os

# logs 폴더 설정
log_dir = os.path.join(os.path.dirname(__file__), 'logs')
if not os.path.exists(log_dir):
    os.makedirs(log_dir)

# 로그 파일 경로
log_file_path = os.path.join(log_dir, 'talkie_log.log')

# FileHandler를 사용하여 UTF-8 인코딩 설정
file_handler = logging.FileHandler(log_file_path, encoding='utf-8')
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
file_handler.setFormatter(formatter)

# 로거 설정
logger = logging.getLogger(__name__)
logger.addHandler(file_handler)
logger.setLevel(logging.INFO)

# 로거 객체 반환 함수
def get_logger(name=__name__):
    return logger
