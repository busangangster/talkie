import React from "react";
import WeekEmotion from "../components/WeekEmotion";
import WeekVoca from "../components/WeekVoca";
import WeekInterest from "../components/WeekInterest";
import TalkCnt from "../components/TalkCnt";
import { WeekFrameProps } from "../type";

const WeekFrame: React.FC<WeekFrameProps> = ({ selectedTab, weeklyData }) => {
  const renderGraphComponent = () => {
    switch (selectedTab) {
      case "감정":
        return <WeekEmotion data={weeklyData?.weeklyConversations || []} />;
      case "어휘력":
        return <WeekVoca data={weeklyData?.weeklyConversations || []} />;
      case "관심사":
        return <WeekInterest data={weeklyData?.wordCloudResponses || []} />;
      case "대화 빈도":
        return <TalkCnt data={weeklyData?.weeklyConversations || []} />;
      default:
        return null;
    }
  };

  const getSummaryText = () => {
    switch (selectedTab) {
      case "감정":
        return weeklyData?.emotionSummary || "";
      case "어휘력":
        return weeklyData?.vocabularySummary || "";
      case "관심사":
        return weeklyData?.wordCloudSummary || "";
      case "대화 빈도":
        return weeklyData?.countSummary || "";
      default:
        return "";
    }
  };

  return (
    <div className="flex flex-col items-center w-full h-full py-20 px-12 rounded-2xl shadow-md bg-[#C7D2E3] bg-opacity-80">
      <h2 className="text-3xl font-bold text-black mb-6 -mt-8">
        {`${selectedTab} 그래프`}
      </h2>

      <div className="flex justify-between space-x-8 w-full h-full">
        <div className="w-2/3 h-full bg-white p-6 rounded-lg shadow-md flex items-center justify-center">
          {renderGraphComponent()}
        </div>

        <div className="w-1/3 h-full bg-white p-8 rounded-lg shadow-md">
          <p className="text-gray-900 text-xl font-bold leading-loose overflow-y-auto max-h-full px-2">
            {getSummaryText()}
          </p>
        </div>
      </div>
    </div>
  );
};

export default WeekFrame;
