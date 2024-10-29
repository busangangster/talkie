import React from "react";
import { Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import HomePage from "../pages/HomePage";
import DayPage from "../pages/DayPage";
import InfoPage from "../pages/InfoPage";
import QuestionPage from "../pages/QuestionPage";
import TalkPage from "../pages/TalkPage";
import WeekPage from "../pages/WeekPage";
import Calendar from "../components/Calendar.tsx";

const AppRouter: React.FC = () => {
  return (
    <Routes>
      <Route path="/calendar" element={<Calendar />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/day" element={<DayPage />} />
      <Route path="/info" element={<InfoPage />} />
      <Route path="/question" element={<QuestionPage />} />
      <Route path="/talk" element={<TalkPage />} />
      <Route path="/week" element={<WeekPage />} />
    </Routes>
  );
};

export default AppRouter;
