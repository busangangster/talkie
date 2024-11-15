import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import moment from "moment";
import useUserStore from "../store/useUserStore";
import "../css/Home.css";
import Image from "../components/Image";

const getToday = () => moment().format("YYYY-MM-DD");

const HomePage: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const navigate = useNavigate();
  const { userSeq } = useUserStore();

  useEffect(() => {
    setIsLoggedIn(!!userSeq);
  }, [userSeq]);

  const goToTodayPage = () => {
    const today = getToday();
    navigate(`/day/${today}`);
  };

  const handleButtonClick = () => {
    if (isLoggedIn) {
      goToTodayPage();
    } else {
      navigate("/login");
    }
  };

  const handleMouseMove = (event: React.MouseEvent<HTMLDivElement>) => {
    const { clientX, clientY } = event;
    const xOffset = (clientX / window.innerWidth - 0.5) * 30;
    const yOffset = (clientY / window.innerHeight - 0.5) * 30;
    setOffset({ x: xOffset, y: yOffset });
  };

  return (
    <div
      className="relative flex justify-center items-center h-screen bg-cover bg-center"
      onMouseMove={handleMouseMove}
    >
      <h1
        className="absolute top-[20%] left-[25%] text-[55px] font-bold text-center text-[#F3F7FF]"
      >
        우리 아이의 친구
      </h1>

      <Image
        src="/assets/home/logo"
        alt="Home Logo"
        className="w-1/2 mx-auto translate-y-[-20%]"
        style={{
          transform: `perspective(1000px) rotateY(${offset.x / 6}deg) rotateX(${
            offset.y / 6
          }deg)`,
        }}
      />

      <button
        onClick={handleButtonClick}
        className="absolute top-12 right-16 px-8 py-2.5 bg-[#f5f5f5] text-gray-800 rounded-2xl shadow-lg hover:bg-gray-200 text-2xl font-bold"
      >
        {isLoggedIn ? "대화 통계 보러 가기" : "로그인"}
      </button>

      <Image
        src="/assets/home/snowmanShadow"
        alt="Snowman"
        className="snowman"
      />
      <Image
        src="/assets/home/teddybearShadow"
        alt="Teddy Bear"
        className="teddybear"
      />
      <Image src="/assets/home/childShadow" alt="Child" className="child" />
    </div>
  );
};

export default HomePage;
