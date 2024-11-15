import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Image from "./Image";

const LogoHeader: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // HomePage에서는 로고를 숨김
  const isHomePage = location.pathname === "/home" || location.pathname === "/";

  const handleLogoClick = () => {
    navigate("/home"); 
  };

  if (isHomePage) return null;

  return (
    <div
      className="fixed top-8 left-10 cursor-pointer z-50"
      onClick={handleLogoClick}
    >
      <Image src="/assets/logoMini" alt="Logo" className="w-[100px] h-auto" />
    </div>
  );
};

export default LogoHeader;
