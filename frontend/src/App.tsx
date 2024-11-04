import React from "react";
import "./App.css";
import { BrowserRouter } from "react-router-dom";
import AppRouter from "./router/AppRouter";
import SnowfallBackground from "./components/SnowfallBackground";
import QuestionButton from "./components/QuestionButton";
import { GlobalStyle } from "./style";
// import { useSnowfall } from "./hooks/useSnowfall";

const App: React.FC = () => {
  // const toggleSnow = useSnowfall((state) => state.toggleSnow);
  // const isSnowing = useSnowfall((state) => state.isSnowing);

  return (
    <BrowserRouter>
      <GlobalStyle />
      <div className="relative min-h-screen overflow-hidden">
        {/* 배경 이미지 */}
        <div
          className="fixed inset-0 bg-cover -z-20 bg-center-top"
          style={{ backgroundImage: "url('/assets/background.jpg')" }}
        />

        {/* 눈 내림 효과 */}
        <SnowfallBackground />

        {/* 콘텐츠 영역 */}
        <div className="relative z-10">
          <AppRouter />
        </div>

        {/* 오른쪽 하단 질문 버튼 */}
        <QuestionButton />

        {/* 눈 내림 효과를 제어하는 버튼 */}
        {/* <div className="fixed top-6 right-6 flex items-center z-20">
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              checked={isSnowing}
              onChange={toggleSnow}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-[#d2d2d2] peer-focus:outline-none rounded-full peer dark:bg-gray-700 peer-checked:bg-[#ACC4EA]"></div> */}

        {/* 토글 스위치의 동그라미 부분에 이미지 배경 추가 */}
        {/* <span
              className="absolute left-[2px] top-[2px] bg-[#507ECE] w-5 h-5 rounded-full transition-transform peer-checked:translate-x-5"
              style={{
                backgroundImage: "url('/assets/snow.png')",
                backgroundSize: "cover",
                backgroundPosition: "center",
              }}
            ></span>
          </label>
        </div> */}
      </div>
    </BrowserRouter>
  );
};

export default App;
