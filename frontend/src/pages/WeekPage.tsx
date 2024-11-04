import React, { useState, useRef } from "react";
import { FaRegCalendarAlt } from "react-icons/fa";
import WeekFrame from "../components/WeekFrame";
import Calendar from "../components/Calendar";
import useTabStore from "../store/useTabStore";
import { Swiper, SwiperSlide } from "swiper/react";
import { Swiper as SwiperClass } from "swiper";
import "swiper/swiper-bundle.css";
import { EffectCube } from "swiper/modules";
import { GlobalStyle } from "../style";

const WeekPage: React.FC = () => {
  const tabs = ["감정", "어휘력", "관심사", "대화 빈도"];
  const [showCalendar, setShowCalendar] = useState(false);

  const { selectedTab, setSelectedTab } = useTabStore();

  const toggleCalendar = () => setShowCalendar(!showCalendar);

  const swiperRef = useRef<SwiperClass | null>(null);

  const currentTabIndex = tabs.findIndex((tab) => tab === selectedTab);

  const handleTabClick = (tab: string) => {
    const index = tabs.findIndex((t) => t === tab);
    if (swiperRef.current) {
      swiperRef.current.slideTo(index);
    }
    setSelectedTab(tab);
  };

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
            onClick={() => handleTabClick(tab)}
            className={`w-48 py-2 rounded-xl text-2xl font-semibold shadow-md ${
              selectedTab === tab
                ? "bg-[#C4BDF5] text-black hover:bg-[#d3cdf4]"
                : "bg-white text-black hover:bg-[#e9e9e9]"
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Swiper 적용 */}
      <Swiper
        effect="cube"
        modules={[EffectCube]}
        cubeEffect={{
          shadow: true,
          slideShadows: true,
          shadowOffset: 20,
          shadowScale: 0.94,
        }}
        onSlideChange={(swiper) => {
          setSelectedTab(tabs[swiper.activeIndex]);
        }}
        initialSlide={currentTabIndex}
        className="mt-8 w-2/3 h-[650px] " 
        onSwiper={(swiper) => (swiperRef.current = swiper)}
      >
        {tabs.map((tab) => (
          <SwiperSlide key={tab}>
            <WeekFrame selectedTab={tab} />
          </SwiperSlide>
        ))}
      </Swiper>
      
      {/* 달력 모달 */}
      {showCalendar && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="relative bg-white rounded-lg shadow-lg p-6">
            <GlobalStyle />
            <Calendar />
          </div>
          <div className="absolute" onClick={toggleCalendar}></div>
        </div>
      )}
    </div>
  );
};

export default WeekPage;
