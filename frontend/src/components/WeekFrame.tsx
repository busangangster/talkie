import React from "react";
import WeekEmotion from "../components/WeekEmotion";
import WeekVoca from "../components/WeekVoca";
import WeekInterest from "../components/WeekInterest";
import TalkCnt from "../components/TalkCnt";

interface WeekFrameProps {
  selectedTab: string;
}

const WeekFrame: React.FC<WeekFrameProps> = ({ selectedTab }) => {
  const renderGraphComponent = () => {
    switch (selectedTab) {
      case "감정":
        return <WeekEmotion />;
      case "어휘력":
        return <WeekVoca />;
      case "관심사":
        return <WeekInterest />;
      case "대화 빈도":
        return <TalkCnt />;
      default:
        return null;
    }
  };

  return (
    <div className="flex flex-col items-center w-full h-full py-20 px-12 rounded-2xl shadow-md bg-[#C7D2E3] bg-opacity-80">
      {/* 제목 */}
      <h2 className="text-3xl font-bold text-black mb-6 -mt-8">
        {`${selectedTab} 그래프`}
      </h2>

      {/* 그래프와 설명 영역 */}
      <div className="flex justify-between space-x-8 w-full h-full">
        {/* 그래프 영역 */}
        <div className="w-2/3 h-full bg-white p-6 rounded-lg shadow-md flex items-center justify-center">
          {renderGraphComponent()}
        </div>

        {/* 설명 영역 */}
        <div className="w-1/3 h-full bg-white p-8 rounded-lg shadow-md">
          <p className="text-gray-900 text-xl font-bold leading-loose">
            {/* 설명 내용 */}
            아이의 감정 그래프를 보면, 기쁨과 놀라움에서 높은 반응을 보이며,
            두려움과 슬픔에서 낮은 반응을 보여요. 이는 아이가 주로 긍정적인
            감정을 더 강하게 느끼는 경향이 있음을 나타내요.
          </p>
        </div>
      </div>
    </div>
  );
};

export default WeekFrame;
