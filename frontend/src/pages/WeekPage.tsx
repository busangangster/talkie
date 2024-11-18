import React, { useState, useRef, useEffect } from "react";
import { FaRegCalendarAlt } from "react-icons/fa";
import { useParams, useLocation } from "react-router-dom";
import WeekFrame from "../components/WeekFrame";
import Calendar from "../components/Calendar";
import useTabStore from "../store/useTabStore";
import { Swiper, SwiperSlide } from "swiper/react";
import { Swiper as SwiperClass } from "swiper";
import "swiper/swiper-bundle.css";
import { EffectCube } from "swiper/modules";
import moment from "moment";
import { getWeeklyConversationStats } from "../apis/api";
import { WeeklyConversationResponse } from "../type";

const WeekPage: React.FC = () => {
  const tabs = ["감정", "어휘력", "관심사", "대화 빈도"];
  const [showCalendar, setShowCalendar] = useState(false);
  const { selectedTab, setSelectedTab } = useTabStore();
  const [weeklyData, setWeeklyData] = useState<
    WeeklyConversationResponse | null | undefined
  >(undefined);

  const { startDate } = useParams<{ startDate: string }>();
  const startMoment = moment(startDate, "YYYY-MM-DD");
  const endMoment = startMoment.clone().add(6, "days");
  const location = useLocation();

  const swiperRef = useRef<SwiperClass | null>(null);
  const currentTabIndex = tabs.findIndex((tab) => tab === selectedTab);

  const toggleCalendar = () => setShowCalendar(!showCalendar);
  const closeCalendar = () => setShowCalendar(false);

  const handleTabClick = (tab: string) => {
    const index = tabs.findIndex((t) => t === tab);
    if (swiperRef.current) {
      swiperRef.current.slideTo(index);
    }
    setSelectedTab(tab);
  };

  // 페이지가 처음 로드되었는지 확인 (새로고침이 아닌 경우에만 기본 탭 설정)
  useEffect(() => {
    const isReload = window.performance
      .getEntriesByType("navigation")
      .some((nav) => {
        return (nav as PerformanceNavigationTiming).type === "reload";
      });

    if (!isReload && !location.state?.keepTab) {
      setSelectedTab("감정");
    }
  }, [location.state, setSelectedTab]);

  // 데이터 fetch
  useEffect(() => {
    setWeeklyData(undefined); // 로딩 중에 undefined로 초기화

    const fetchData = async () => {
      if (startDate) {
        try {
          const response = await getWeeklyConversationStats(
            Number(localStorage.getItem("userSeq")),
            startMoment.format("YYYY-MM-DD"),
            endMoment.format("YYYY-MM-DD")
          );
          setWeeklyData(response);
        } catch (error) {
          console.error("Failed to fetch weekly data:", error);
          setWeeklyData(null); // 오류 발생 시 데이터 없음으로 처리
        }
      }
    };
    fetchData();
  }, [startDate]);

  // 로딩 중에는 아무것도 표시하지 않음
  if (weeklyData === undefined) {
    return null;
  }

  return (
    <div className="flex flex-col items-center min-h-screen relative">
      <div className="flex items-center mt-20 px-16 py-4 bg-white rounded-2xl shadow-md text-2xl font-semibold cursor-pointer space-x-2">
        <FaRegCalendarAlt onClick={toggleCalendar} className="text-[#3F3F3F]" />
        <span onClick={toggleCalendar} className="text-[#3F3F3F]">
          {startMoment.format("M월 D일")} ~ {endMoment.format("M월 D일")} 주간
          통계
        </span>
      </div>

      {weeklyData ? (
        <>
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
            className="mt-8 w-2/3 h-[650px]"
            onSwiper={(swiper) => (swiperRef.current = swiper)}
          >
            {tabs.map((tab) => (
              <SwiperSlide key={tab}>
                <WeekFrame selectedTab={tab} weeklyData={weeklyData} />
              </SwiperSlide>
            ))}
          </Swiper>
        </>
      ) : (
        <div className="text-2xl font-semibold text-gray-700 mt-10">
          이 주에는 대화 데이터가 없어요!
        </div>
      )}

      {showCalendar && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div
            className="absolute inset-0 bg-black opacity-50"
            onClick={closeCalendar}
          />
          <div className="relative z-100 bg-white rounded-lg shadow-lg p-4">
            <Calendar onClose={closeCalendar} />
          </div>
        </div>
      )}
    </div>
  );
};

export default WeekPage;
