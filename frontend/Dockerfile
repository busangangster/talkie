FROM node:13.12.0-alpine
COPY package.json package-lock.json ./
RUN npm install 
COPY . ./
EXPOSE 3000