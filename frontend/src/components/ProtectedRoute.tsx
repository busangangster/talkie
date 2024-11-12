// ProtectedRoute.tsx
import React, { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import useUserStore from "../store/useUserStore";
import LoginModal from "../components/LoginModal"; // 새로운 ConfirmModal 사용
import exclamationMarkIcon from "/assets/alerticon/exclamationMark.png";

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const userSeq = useUserStore((state) => state.userSeq);
  const [showConfirm, setShowConfirm] = useState(false);
  const [redirect, setRedirect] = useState(false);

  useEffect(() => {
    if (!userSeq) {
      setShowConfirm(true);
    }
  }, [userSeq]);

  const handleConfirm = () => {
    setShowConfirm(false);
    setRedirect(true);
  };

  if (redirect) {
    return <Navigate to="/login" />;
  }

  return (
    <>
      {showConfirm && (
        <LoginModal
          icon={<img src={exclamationMarkIcon} alt="Exclamation Mark" />}
          message="로그인 후 이용가능해요"
          onConfirm={handleConfirm}
        />
      )}
      {userSeq && children}
    </>
  );
};

export default ProtectedRoute;
