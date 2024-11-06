import React from "react";
import { Route, Routes } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import HomePage from "../pages/HomePage";
import DayPage from "../pages/DayPage";
import InfoPage from "../pages/InfoPage";
import QuestionPage from "../pages/QuestionPage";
import TalkPage from "../pages/TalkPage";
import WeekPage from "../pages/WeekPage";

const AppRouter: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/day/:date" element={<DayPage />} />{" "}
      <Route path="/info" element={<InfoPage />} />
      <Route path="/question" element={<QuestionPage />} />
      <Route path="/talk/:date/:conversationSeq" element={<TalkPage />} />
      <Route path="/week/:startDate" element={<WeekPage />} />
    </Routes>
  );
};

export default AppRouter;
