import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaRegCalendarAlt } from "react-icons/fa";
import Calendar from "../components/Calendar";

const DayPage: React.FC = () => {
  const navigate = useNavigate();
  const [showCalendar, setShowCalendar] = useState(false);

  const toggleCalendar = () => {
    setShowCalendar(!showCalendar);
  };

  // 임의의 목업 데이터
  const stories = [
    {
      id: 1,
      order: "첫번째",
      title: "토끼인형 이야기",
      time: "9시 15분 - 9시 32분",
    },
    {
      id: 2,
      order: "두번째",
      title: "간식에 대한 이야기",
      time: "11시 26분 - 12시 19분",
    },
    {
      id: 3,
      order: "세번째",
      title: "놀이터에서 논 이야기",
      time: "15시 23분 - 15시 30분",
    },
    {
      id: 4,
      order: "네번째",
      title: "어린이집 선생님 이야기",
      time: "18시 06분 - 18시 09분",
    },
    {
      id: 5,
      order: "다섯번째",
      title: "어린이집 선생님 이야기",
      time: "18시 06분 - 18시 09분",
    },
  ];

  return (
    <div className="relative flex flex-col items-center min-h-screen">
      {/* 구름과 날짜 */}
      <div className="flex items-center justify-center mt-16 relative">
        <img src="/assets/cloud.png" alt="cloud" className="w-80" />
        <div
          onClick={toggleCalendar}
          className="absolute inset-0 flex items-center justify-center text-2xl font-bold text-gray-700 mt-3 cursor-pointer"
        >
          <FaRegCalendarAlt className="cursor-pointer mr-2" />
          <span>10월 11일 월요일</span>
        </div>
      </div>

      {/* 이야기 리스트 */}
      <div className="w-4/5 mt-24 bg-[#D9D9D9] bg-opacity-60 rounded-xl shadow-lg h-[450px] overflow-y-scroll py-16 px-10">
        <style>{`
        /* 위아래로 미세하게 움직이는 애니메이션 */
        @keyframes float {
          0%, 100% {
            transform: translateY(0);
          }
          50% {
            transform: translateY(-4px);
          }
        }
      `}</style>
        <div className="grid grid-cols-2 gap-y-20 gap-x-10">
          {stories.map((story, index) => (
            <div key={story.id} className="relative">
              {/* 왼쪽 상단의 "첫번째 이야기" */}
              <span className="absolute -top-8 left-0 text-neutral-800 font-bold text-xl ml-2">
                {story.order} 이야기
              </span>

              <div
                className="flex items-center p-4 bg-white rounded-2xl shadow-md cursor-pointer"
                onClick={() => navigate("/talk")}
              >
                {/* 동물 이미지 */}
                <img
                  src={`/assets/animals/animal${(index % 3) + 1}.png`} // 동물 이미지를 랜덤으로 매칭
                  alt="animal"
                  className="w-40 rounded-3xl px-10 py-3 bg-[#dadbe9] mr-4"
                />

                {/* 이야기 제목과 시간 */}
                <div className="flex flex-col flex-1 ml-4 mb-3">
                  <span className="font-semibold text-2xl mb-3">
                    {story.title}
                  </span>
                  <span className="text-[#707070] text">{story.time}</span>
                </div>

                {/* 오른쪽 하단의 "결과 상세 보기" */}
                {/* <span className="absolute bottom-4 right-6 text-[#616161] text-sm underline">
                  결과 상세 보기
                </span> */}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 달력 모달 */}
      {showCalendar && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="relative z-10 bg-white rounded-lg shadow-lg p-4">
            <Calendar />
          </div>
          <div
            className="absolute inset-0"
            onClick={() => setShowCalendar(false)}
          />
        </div>
      )}
    </div>
  );
};

export default DayPage;
