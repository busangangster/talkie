import React from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import HomePage from "../pages/HomePage";
import DayPage from "../pages/DayPage";
import InfoPage from "../pages/InfoPage";
import QuestionPage from "../pages/QuestionPage";
import TalkPage from "../pages/TalkPage";
import WeekPage from "../pages/WeekPage";
import ProtectedRoute from "../components/ProtectedRoute";
import { AnimatePresence } from "framer-motion";
import Motion from "../components/Motion";
import LogoHeader from "../components/LogoHeader"; 
const AppRouter: React.FC = () => {
  const location = useLocation();

  const routes = [
    { path: "/home", element: <HomePage />, protected: false },
    { path: "/", element: <HomePage />, protected: false },
    { path: "/login", element: <LoginPage />, protected: false },
    { path: "/day/:date", element: <DayPage />, protected: true },
    { path: "/info", element: <InfoPage />, protected: true },
    { path: "/question", element: <QuestionPage />, protected: true },
    {
      path: "/talk/:date/:conversationSeq",
      element: <TalkPage />,
      protected: true,
    },
    { path: "/week/:startDate", element: <WeekPage />, protected: true },
  ];

  // 현재 페이지가 HomePage인지 확인
  const isHomePage = location.pathname === "/home" || location.pathname === "/";

  return (
    <AnimatePresence mode="wait">
      {!isHomePage && <LogoHeader />}

      <Routes location={location} key={location.pathname}>
        {routes.map(({ path, element, protected: isProtected }) => (
          <Route
            key={path}
            path={path}
            element={
              <Motion>
                {isProtected ? (
                  <ProtectedRoute>{element}</ProtectedRoute>
                ) : (
                  element
                )}
              </Motion>
            }
          />
        ))}
      </Routes>
    </AnimatePresence>
  );
};

export default AppRouter;
