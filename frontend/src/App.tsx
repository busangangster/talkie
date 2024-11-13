import React from "react";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import AppRouter from "./router/AppRouter";
import SnowfallBackground from "./components/SnowfallBackground";
import QuestionButton from "./components/QuestionButton";
import HamburgerButton from "./components/HamburgerButton";
import LogoHeader from "./components/LogoHeader";

// Service Worker 등록 코드 추가
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker
      .register("/serviceWorker.js")
      .then((registration) => {
        console.log(
          "Service Worker registered with scope:",
          registration.scope
        );
      })
      .catch((error) => {
        console.error("Service Worker registration failed:", error);
      });
  });
}

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div className="relative min-h-screen overflow-hidden">
        {/* 배경 이미지 */}
        <div
          className="fixed inset-0 bg-cover -z-20 bg-center-top"
          style={{ backgroundImage: "url('/assets/background.jpg')" }}
        />
        {/* 눈 내림 효과 */}
        <SnowfallBackground />

        {/* 항상 표시되는 로고 */}
        <LogoHeader />

        {/* 콘텐츠 영역 */}
        <div className="relative z-10">
          <AppRouter />
        </div>

        {/* 오른쪽 하단 질문 버튼 */}
        <QuestionButton />

        {/* 오른쪽 하단 햄버거 버튼 */}
        <HamburgerButton />
      </div>
    </BrowserRouter>
  );
};

export default App;
