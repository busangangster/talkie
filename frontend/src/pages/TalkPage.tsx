import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import { FaRegCalendarAlt } from "react-icons/fa";
import Calendar from "../components/Calendar";
import WordCloud from "../components/WordCloud";
import ChatSummary from "../components/ChatSummary";
import Chat from "../components/Chat";
import TalkVoca from "../components/TalkVoca";
import TalkEmotion from "../components/TalkEmotion";
import SnowfallBackground from "../components/SnowfallBackground";
import moment from "moment";
import { getConversationDetail, getConversationSummary } from "../apis/api";
import { ConversationDetailResponse } from "../type";
import useUserStore from "../store/useUserStore";

const TalkPage: React.FC = () => {
  const { date, conversationSeq } = useParams<{
    date: string;
    conversationSeq: string;
  }>();
  const { userSeq } = useUserStore();
  const [showCalendar, setShowCalendar] = useState(false);
  const [showSummary, setShowSummary] = useState(false);
  const [summaryContent, setSummaryContent] = useState<string | null>(null);
  const [conversationDetail, setConversationDetail] =
    useState<ConversationDetailResponse | null>(null);
  const location = useLocation();
  const { title } = location.state || {};

  const toggleCalendar = () => setShowCalendar(!showCalendar);

  const openSummary = async () => {
    if (userSeq && conversationSeq) {
      try {
        const summaryResponse = await getConversationSummary(
          userSeq,
          Number(conversationSeq)
        );
        console.log(summaryResponse.data.content);
        setSummaryContent(summaryResponse.data.content);
        setShowSummary(true);
      } catch (error) {
        console.error("요약 데이터를 가져오는 중 오류 발생:", error);
      }
    }
  };

  const closeSummary = () => setShowSummary(false);

  useEffect(() => {
    const fetchConversationDetail = async () => {
      if (userSeq && conversationSeq) {
        try {
          const response = await getConversationDetail(
            userSeq,
            Number(conversationSeq)
          );
          console.log(response);
          setConversationDetail(response);
        } catch (error) {
          console.error("대화 상세 정보를 가져오는 중 오류 발생:", error);
        }
      }
    };

    fetchConversationDetail();
  }, [userSeq, conversationSeq]);

  // `conversationContents` 데이터를 `Chat` 컴포넌트 형식에 맞게 변환
  const messages =
    conversationDetail?.conversationContents.map((content) => ({
      user_Seq: content.answer ? 1 : 0,
      content: content.content,
    })) || [];

  return (
    <div
      className="relative flex flex-col items-center px-[260px] py-12 min-h-screen overflow-y-scroll bg-cover bg-center"
      style={{ backgroundImage: "url('/assets/background.jpg')" }}
    >
      <style>{`
        .left-tail::after {
          content: '';
          position: absolute;
          bottom: 5;
          left: -20px;
          border-width: 14px;
          border-style: solid;
          border-color: transparent #D9D9D9 transparent transparent;
        }
        
        .right-tail::after {
          content: '';
          position: absolute;
          bottom: 5;
          right: -20px;
          border-width: 14px;
          border-style: solid;
          border-color: transparent transparent transparent #CED1EE;
        }
      `}</style>

      <div className="z-10">
        <SnowfallBackground />
      </div>

      {/* 제목 영역 */}
      <div className="flex items-center justify-center relative -mt-3 z-20">
        <img src="/assets/cloud.png" alt="cloud" className="w-96" />
        <div className="absolute inset-0 flex flex-col items-center justify-center text-gray-700">
          {/* 날짜 및 달력 아이콘 */}
          <div className="flex items-center text-2xl font-medium text-[#4E4E4E] mb-2">
            <FaRegCalendarAlt
              onClick={toggleCalendar}
              className="cursor-pointer mr-2"
            />
            <span onClick={toggleCalendar} className="font-bold cursor-pointer">
              {date ? moment(date).format("YYYY년 MM월 DD일") : "날짜 선택"}
            </span>
          </div>
          {/* 이야기 결과지 제목 */}
          <span className="text-3xl font-bold mt-1">첫번째 이야기 결과지</span>
        </div>
      </div>

      {/* 이야기 제목 */}
      <div className="flex items-center mt-10 text-3xl font-bold self-start z-20">
        <div className="bg-[#F3E651] w-2 h-10 mr-4" />
        <span>{title}</span>
      </div>

      {/* 그래프 및 분석 결과 영역 */}
      <div className="flex flex-wrap justify-between mt-5 w-full space-y-8 z-20">
        {/* 첫 번째 열: WordCloud 및 관심사 */}
        <div className="w-[42%] h-[400px] bg-white rounded-xl shadow-md mt-6 p-8">
          <WordCloud words={conversationDetail?.wordClouds || []} />
        </div>
        <div className="w-[53%] bg-white p-10 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#b3c5d3] text-black text-[25px] px-5 py-2 rounded-2xl font-bold mr-2">
              관심사
            </span>
          </div>
          <p className="text-gray-900 text-[25px] leading-relaxed">
            {conversationDetail?.wordCloudSummary ||
              "관심사 데이터를 불러오는 중..."}
          </p>
        </div>

        {/* 두 번째 열: Word 및 어휘력 */}
        <div className="w-[42%] h-[400px] bg-white rounded-xl shadow-md mt-6 p-8 z-20">
          <TalkVoca vocabularyScore={conversationDetail?.vocabularyScore} />
        </div>
        <div className="w-[53%] bg-white p-10 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#C6D4DF] text-black text-[25px] px-5 py-2 rounded-2xl font-bold mr-2">
              어휘력
            </span>
          </div>
          <p className="text-gray-900 text-[25px] leading-relaxed">
            {conversationDetail?.vocabularySummary ||
              "어휘력 데이터를 불러오는 중..."}
          </p>
        </div>

        {/* 세 번째 열: Emotion 및 감정 */}
        <div className="w-[42%] h-[400px] bg-white rounded-xl shadow-md mt-6 p-6 z-20">
          {conversationDetail ? (
            <TalkEmotion
              happyScore={conversationDetail.happyScore}
              loveScore={conversationDetail.loveScore}
              sadScore={conversationDetail.sadScore}
              scaryScore={conversationDetail.scaryScore}
              angryScore={conversationDetail.angryScore}
              amazingScore={conversationDetail.amazingScore}
            />
          ) : (
            <p>감정 데이터를 불러오는 중...</p>
          )}
        </div>
        <div className="w-[53%] bg-white p-10 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#E3E7F0] text-black text-[25px] px-5 py-2 rounded-2xl font-bold mr-2">
              감정
            </span>
          </div>
          <p className="text-gray-900 text-[25px] leading-relaxed">
            {conversationDetail?.emotionSummary ||
              "감정 데이터를 불러오는 중..."}
          </p>
        </div>
      </div>

      {/* 이야기 전체 내용 및 요약보기 */}
      <div className="flex justify-between items-center mt-10 w-full z-20">
        <div className="flex items-center mb-4">
          <span className="bg-[#E3E7F0] text-black text-[24px] px-5 py-2 rounded-2xl font-bold mr-2">
            이야기 전체 내용
          </span>
        </div>
        <span
          className="text-black underline cursor-pointer hover:text-[#4e4e4e] mr-3 text-2xl"
          onClick={openSummary}
        >
          요약보기
        </span>
      </div>

      {/* 대화 내용 컴포넌트 */}
      <div className="z-30">
        <Chat messages={messages} />
      </div>

      {/* 달력 모달 */}
      {showCalendar && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-40"
          onClick={() => setShowCalendar(false)}
        >
          <div
            className="relative bg-white rounded-lg shadow-lg p-4"
            onClick={(e) => e.stopPropagation()}
          >
            <Calendar onClose={() => setShowCalendar(false)} />
          </div>
        </div>
      )}

      {/* 요약 모달 */}
      {showSummary && summaryContent && (
        <ChatSummary
          title={title}
          content={summaryContent}
          onClose={closeSummary}
        />
      )}
    </div>
  );
};

export default TalkPage;
