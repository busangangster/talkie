// ProtectedRoute.tsx
import React, { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import useUserStore from "../store/useUserStore";
import AlertModal from "../components/AlertModal";
import exclamationMarkIcon from "/assets/alerticon/exclamationMark.png";

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const userSeq = useUserStore((state) => state.userSeq);
  const [showAlert, setShowAlert] = useState(false);
  const [redirect, setRedirect] = useState(false);

  useEffect(() => {
    if (!userSeq) {
      setShowAlert(true);
      const timer = setTimeout(() => setRedirect(true), 800); 
      return () => clearTimeout(timer);
    }
  }, [userSeq]);

  if (redirect) {
    return <Navigate to="/login" />;
  }

  return (
    <>
      {showAlert && (
        <AlertModal
          icon={<img src={exclamationMarkIcon} alt="Exclamation Mark" />}
          message="로그인 후 이용가능해요"
          onConfirm={() => setShowAlert(false)}
        />
      )}
      {userSeq && children}
    </>
  );
};

export default ProtectedRoute;
