import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { RxHamburgerMenu } from "react-icons/rx";
import Calendar from "./Calendar"; 
import calendarIcon from "/assets/hamburger/calendar.png";
import modiInfoIcon from "/assets/hamburger/modiInfo.png";
import logoutIcon from "/assets/hamburger/logout.png";

const HamburgerButton: React.FC = () => {
  const [showMenu, setShowMenu] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [showCalendar, setShowCalendar] = useState(false);
  const navigate = useNavigate();
  const menuRef = useRef<HTMLDivElement>(null);

  const toggleMenu = () => {
    if (showMenu) {
      // 메뉴 닫기
      setIsAnimating(false); 
      setTimeout(() => {
        setShowMenu(false); 
      }, 300); 
    } else {
      // 메뉴 열기
      setShowMenu(true); 
      setTimeout(() => {
        setIsAnimating(true); 
      }, 10);
    }
  };

  const openCalendar = () => {
    setIsAnimating(false); 
    setTimeout(() => {
      setShowMenu(false); 
    }, 300);
    setShowCalendar(true); 
  };

  // 메뉴 외부 클릭 시 메뉴 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsAnimating(false); 
        setTimeout(() => {
          setShowMenu(false);
        }, 300);
      }
    };

    if (showMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showMenu]);

  return (
    <>
      {/* 항상 보이는 햄버거 버튼 */}
      <button
        onClick={toggleMenu}
        className="fixed bottom-36 right-10 bg-white rounded-full shadow-lg cursor-pointer flex items-center justify-center w-20 h-20 z-50 group transition-transform transform hover:-translate-y-0.5"
      >
        <RxHamburgerMenu size={40} color="black" />
      </button>

      {/* 햄버거 버튼 클릭 시 나타나는 말풍선 메뉴 */}
      {showMenu && (
        <div
          ref={menuRef}
          className={`fixed bottom-[250px] right-10 w-[83px] bg-white rounded-lg shadow-lg px-3 py-6 flex flex-col items-center z-20 transition-transform transition-opacity duration-300 ease-out transform origin-bottom ${
            isAnimating ? "scale-y-100 opacity-100" : "scale-y-0 opacity-0"
          }`}
        >
          <button
            onClick={openCalendar}
            className="flex flex-col items-center mb-5 transform transition-transform duration-200 hover:scale-105"
          >
            <img
              src={calendarIcon}
              alt="날짜 이동"
              className="w-11 h-11 mb-2"
            />
            <span className="text-[13px] font-bold">날짜 이동</span>
          </button>
          <button
            onClick={() => navigate("/info")}
            className="flex flex-col items-center mb-5 transform transition-transform duration-200 hover:scale-105"
          >
            <img
              src={modiInfoIcon}
              alt="정보 수정"
              className="w-11 h-11 mb-2"
            />
            <span className="text-[13px] font-bold">정보 수정</span>
          </button>
          <button
            onClick={() => alert("로그아웃은 아직 구현되지 않았습니다.")}
            className="flex flex-col items-center transform transition-transform duration-200 hover:scale-105"
          >
            <img src={logoutIcon} alt="로그아웃" className="w-11 h-10 mb-2" />
            <span className="text-[13px] font-bold">로그아웃</span>
          </button>
          {/* 아래 화살표 */}
          <div className="absolute -bottom-1.5 w-3 h-4 bg-white transform rotate-45"></div>
        </div>
      )}

      {/* 달력 모달 */}
      {showCalendar && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[100]">
          <div className="relative bg-white rounded-lg shadow-lg p-4 z-[101]">
            <Calendar onClose={() => setShowCalendar(false)} />
          </div>
          <div
            className="absolute inset-0 z-[100]"
            onClick={() => setShowCalendar(false)}
          />
        </div>
      )}
    </>
  );
};

export default HamburgerButton;
