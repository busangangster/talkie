import React, { useState } from "react";
import { FaRegCalendarAlt } from "react-icons/fa";
import WeekFrame from "../components/WeekFrame";
import Calendar from "../components/Calendar";

const WeekPage: React.FC = () => {
  const [selectedTab, setSelectedTab] = useState("감정");
  const [showCalendar, setShowCalendar] = useState(false);

  const toggleCalendar = () => setShowCalendar(!showCalendar);

  const tabs = ["감정", "어휘력", "관심사", "대화 빈도"];

  return (
    <div className="flex flex-col items-center min-h-screen relative">
      {/* 달력 및 주간 통계 제목 */}
      <div className="flex items-center mt-12 px-16 py-4 bg-white rounded-2xl shadow-md text-2xl font-semibold cursor-pointer space-x-2">
        <FaRegCalendarAlt onClick={toggleCalendar} className="text-[#3F3F3F]" />
        <span onClick={toggleCalendar} className="text-[#3F3F3F]">
          10월 11일 ~ 10월 18일 주간 통계
        </span>
      </div>

      {/* 소제목 탭 */}
      <div className="flex space-x-12 mt-10">
        {tabs.map((tab) => (
          <button
            key={tab}
            onClick={() => setSelectedTab(tab)}
            className={`w-48 py-2 rounded-xl text-2xl font-semibold shadow-md  ${
              selectedTab === tab
                ? "bg-[#C4BDF5] text-black hover:bg-[#d3cdf4]"
                : "bg-white text-black hover:bg-[#e9e9e9]"
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* WeekFrame 컨텐츠 영역 */}
      <div className="mt-8 p-12 w-2/3 rounded-2xl shadow-md bg-[#C7D2E3] bg-opacity-80">
        <WeekFrame selectedTab={selectedTab} />
      </div>

      {/* 달력 모달 */}
      {showCalendar && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="relative bg-white rounded-lg shadow-lg p-6">
            <Calendar />
          </div>
          <div className="absolute inset-0" onClick={toggleCalendar}></div>
        </div>
      )}
    </div>
  );
};

export default WeekPage;
