import React, { useEffect, useRef, useState } from "react";

function App() {
  const [message, setMessage] = useState("");
  const [socket, setSocket] = useState(null);
  const audioPlayerRef = useRef(null);

  useEffect(() => {
    // WebSocket 연결 설정
    const ws = new WebSocket("ws://localhost:8080/ws/chat"); // 서버의 WebSocket URL에 맞게 수정
    setSocket(ws);

    ws.onopen = () => {
      console.log("Connected to WebSocket server");
    };

    ws.onmessage = (event) => {
      const receivedMessage = event.data;
      console.log("Received:", receivedMessage);

      // "Audio data: "로 시작하는 메시지인 경우 오디오 데이터 처리
      if (receivedMessage.startsWith("Audio data: ")) {
        const audioBase64 = receivedMessage.replace("Audio data: ", "");
        const audioArrayBuffer = base64ToArrayBuffer(audioBase64); // Base64를 ArrayBuffer로 변환
        const pcmData = new Int16Array(audioArrayBuffer); // PCM 데이터를 Int16Array로 변환
        const sampleRate = 24000; // 샘플링 레이트

        const wavBlob = pcmToWav(pcmData, sampleRate); // PCM 데이터를 WAV로 변환
        const audioUrl = URL.createObjectURL(wavBlob); // Blob URL 생성

        audioPlayerRef.current.src = audioUrl; // 오디오 재생
        audioPlayerRef.current.play(); // 오디오 재생 시작
      }
    };

    ws.onclose = (event) => {
      console.log("Disconnected from WebSocket server:", event.reason);
    };

    ws.onerror = (error) => {
      console.error("WebSocket error:", error);
    };

    return () => {
      ws.close(); // 컴포넌트 언마운트 시 WebSocket 연결 종료
    };
  }, []);

  const sendMessage = () => {
    if (socket && message) {
      console.log("Sending:", message);
      socket.send(message); // 메시지 전송
      setMessage(""); // 입력 필드 초기화
    }
  };

  // Base64 문자열을 ArrayBuffer로 변환하는 함수
  const base64ToArrayBuffer = (base64) => {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  };

  // PCM 데이터를 WAV로 변환하는 함수
  const pcmToWav = (pcmData, sampleRate) => {
    const byteRate = sampleRate * 2; // 16비트 = 2바이트
    const blockAlign = 2; // 16비트 = 2바이트

    const buffer = new ArrayBuffer(44 + pcmData.length * 2); // WAV 헤더 크기 + PCM 데이터 크기
    const view = new DataView(buffer);

    // RIFF 헤더
    writeString(view, 0, "RIFF");
    view.setUint32(4, 36 + pcmData.length * 2, true); // 전체 파일 크기
    writeString(view, 8, "WAVE");
    writeString(view, 12, "fmt ");
    view.setUint32(16, 16, true); // fmt 섹션 크기
    view.setUint16(20, 1, true); // PCM 포맷
    view.setUint16(22, 1, true); // 채널 수 (1: 모노)
    view.setUint32(24, sampleRate, true); // 샘플링 레이트
    view.setUint32(28, byteRate, true); // 바이트 레이트
    view.setUint16(32, blockAlign, true); // 블록 정렬
    view.setUint16(34, 16, true); // 비트 깊이
    writeString(view, 36, "data");
    view.setUint32(40, pcmData.length * 2, true); // PCM 데이터 크기

    // PCM 데이터를 ArrayBuffer에 추가
    for (let i = 0; i < pcmData.length; i++) {
      view.setInt16(44 + i * 2, pcmData[i], true); // PCM 데이터
    }

    return new Blob([view], { type: "audio/wav" });
  };

  // 문자열을 DataView에 작성하는 유틸리티 함수
  const writeString = (view, offset, string) => {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  };

  return (
    <div>
      <h1>WebSocket Audio Client</h1>
      <textarea
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="메시지를 입력하세요..."
        rows="4"
        cols="50"
      ></textarea>
      <br />
      <button onClick={sendMessage}>전송</button>
      <audio ref={audioPlayerRef} controls></audio>
    </div>
  );
}

export default App;
