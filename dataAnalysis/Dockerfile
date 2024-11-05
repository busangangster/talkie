# Python 베이스 이미지
FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# 필수 패키지 설치 및 default-jdk 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends apt-utils default-jdk g++ && \
    rm -rf /var/lib/apt/lists/*

# requirements.txt 파일 복사 및 Python 의존성 설치
COPY requirements.txt requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . .

# 환경 변수 설정 (Java의 경우)
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# FastAPI 애플리케이션 실행 (uvicorn 사용)
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]
