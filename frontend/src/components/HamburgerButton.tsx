// HamburgerButton.tsx
import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { RxHamburgerMenu } from "react-icons/rx";
import Calendar from "./Calendar";
import AlertModal from "./AlertModal";
import calendarIcon from "/assets/hamburger/calendar.png";
import modiInfoIcon from "/assets/hamburger/modiInfo.png";
import logoutIcon from "/assets/hamburger/logout.png";
// import loginIcon from "/assets/hamburger/login.png";
import { logout } from "../apis/api";
import useUserStore from "../store/useUserStore";
import { AnimatePresence } from "framer-motion";

const HamburgerButton: React.FC = () => {
  const [showMenu, setShowMenu] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [showCalendar, setShowCalendar] = useState(false);
  const [showLogoutAlert, setShowLogoutAlert] = useState(false);
  const isLoggedOut = !useUserStore((state) => state.userSeq);
  const navigate = useNavigate();
  const menuRef = useRef<HTMLDivElement>(null);
  const { resetUser } = useUserStore();

  const toggleMenu = () => {
    if (showMenu) {
      setIsAnimating(false);
      setTimeout(() => {
        setShowMenu(false);
      }, 300);
    } else {
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

  const handleLogout = async () => {
    try {
      await logout();
      resetUser();
      setShowLogoutAlert(true);
      setShowMenu(false);
      navigate("/home");
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  };

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
      {/* 로그인 상태에서만 햄버거 버튼 표시 */}
      {!isLoggedOut && (
        <button
          onClick={toggleMenu}
          className="fixed bottom-36 right-10 bg-white rounded-full shadow-lg cursor-pointer flex items-center justify-center w-20 h-20 z-50 group transition-transform transform hover:-translate-y-0.5"
        >
          <RxHamburgerMenu size={40} color="black" />
        </button>
      )}

      {/* 햄버거 메뉴 */}
      {showMenu && !isLoggedOut && (
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
            onClick={handleLogout}
            className="flex flex-col items-center transform transition-transform duration-200 hover:scale-105"
          >
            <img src={logoutIcon} alt="로그아웃" className="w-11 h-10 mb-2" />
            <span className="text-[13px] font-bold">로그아웃</span>
          </button>
          <div className="absolute -bottom-1.5 w-3 h-4 bg-white transform rotate-45"></div>
        </div>
      )}

      {/* 캘린더 모달 */}
      <AnimatePresence>
        {showCalendar && <Calendar onClose={() => setShowCalendar(false)} />}
      </AnimatePresence>

      {/* 로그아웃 알림 모달 */}
      {showLogoutAlert && (
        <AlertModal
          icon={<img src="/assets/alerticon/check.png" alt="로그아웃 확인" />}
          message="로그아웃 되었어요"
          onConfirm={() => setShowLogoutAlert(false)}
        />
      )}
    </>
  );
};

export default HamburgerButton;
