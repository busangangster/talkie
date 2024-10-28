FROM node:13.12.0-alpine

# 작업 디렉터리 설정
WORKDIR /app

# 패키지 파일 복사
COPY package*.json ./

# 의존성 설치
RUN npm install

# 소스 코드 복사
COPY . .

# 빌드 명령 실행
RUN npm run build