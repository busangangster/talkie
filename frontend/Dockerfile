FROM node:16-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 패키지 파일 복사 및 의존성 설치
COPY package.json package-lock.json ./
RUN npm install

# 애플리케이션 코드 복사
COPY . .

# 컨테이너의 포트 5173 노출
EXPOSE 5173

# 애플리케이션 실행 명령
CMD ["npm", "run", "dev"]
