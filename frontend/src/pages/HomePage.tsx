import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import moment from "moment";
import useUserStore from "../store/useUserStore";
import "../css/home.css"

// 날짜 계산 함수 (이번 주 일요일을 계산)
const getEndOfWeek = () => {
  const today = moment();
  const endOfWeek = today.clone().endOf("isoWeek").format("YYYY-MM-DD"); // 이번 주 일요일
  console.log("이번 주 마지막 날짜 (일요일):", endOfWeek);
  return endOfWeek;
};

// HomePage 컴포넌트
const HomePage: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();
  const { userSeq } = useUserStore();

  // 로그인 상태 확인
  useEffect(() => {
    setIsLoggedIn(!!userSeq); // userSeq가 있으면 로그인 상태로 설정
  }, [userSeq]);

  // 이번 주 일요일로 이동하는 함수
  const goToThisWeekPage = () => {
    const endOfWeek = getEndOfWeek();
    navigate(`/week/${endOfWeek}`);
  };

  // 버튼 클릭 핸들러
  const handleButtonClick = () => {
    if (isLoggedIn) {
      goToThisWeekPage(); // 로그인 상태면 이번 주 일요일 페이지로 이동
    } else {
      navigate("/login"); // 비로그인 상태면 로그인 페이지로 이동
    }
  };

  return (
    <div className="relative flex justify-center items-center h-screen bg-cover bg-center">
      {/* 중앙 텍스트 */}
      <h1 className="absolute top-[20%] left-[25%] text-[55px] font-bold text-center text-[#F3F7FF]">
        우리 아이의 친구
      </h1>

      {/* 로고 이미지 */}
      <img
        src="/assets/home/logo.png"
        alt="Home Logo"
        className="w-1/2 mx-auto translate-y-[-20%]"
      />

      {/* 로그인 버튼 */}
      <button
        onClick={handleButtonClick}
        className="absolute top-12 right-16 px-8 py-2.5 bg-[#f5f5f5] text-gray-800 rounded-2xl shadow-lg hover:bg-gray-200 text-2xl font-bold"
      >
        {isLoggedIn ? "대화 통계 보러 가기" : "로그인"}
      </button>

      {/* 눈사람 이미지 (왼쪽 아래) */}
      <img
        src="/assets/home/snowmanShadow.png"
        alt="Snowman"
        className="snowman"
      />

      {/* 테디베어 및 아이 이미지 (오른쪽 아래) */}
      <img
        src="/assets/home/teddybearShadow.png"
        alt="Teddy Bear"
        className="teddybear"
      />
      <img src="/assets/home/childShadow.png" alt="Child" className="child" />
    </div>
  );
};

export default HomePage;
