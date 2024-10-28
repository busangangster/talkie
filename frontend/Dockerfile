FROM node:13.12.0-alpine

# 작업 디렉토리 설정
WORKDIR /app

# package.json과 package-lock.json을 복사하여 의존성 설치
COPY package.json package-lock.json ./
RUN npm install

# 모든 파일 복사
COPY . ./

# 컨테이너의 포트 3000 노출
EXPOSE 3000

# 애플리케이션 실행 명령 추가
CMD ["npm", "start"]
