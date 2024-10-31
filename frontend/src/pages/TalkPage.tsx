import React, { useState } from "react";
import { FaRegCalendarAlt } from "react-icons/fa";
import Calendar from "../components/Calendar";
import WordCloud from "../components/WordCloud";
import Word from "../components/Word";
import Emotion from "../components/Emotion";
import ChatSummary from "../components/ChatSummary";

const TalkPage: React.FC = () => {
  const [showCalendar, setShowCalendar] = useState(false);
  const [showSummary, setShowSummary] = useState(false);

  const toggleCalendar = () => setShowCalendar(!showCalendar);
  const openSummary = () => setShowSummary(true);
  const closeSummary = () => setShowSummary(false);

  // 대화 목업데이터 (user_Seq: 0 - AI, 1 - 아이)
  const messages = [
    { user_Seq: 0, content: "놀이터 가서 뭐하고 놀고 있어요??" },
    {
      user_Seq: 1,
      content:
        "미끄럼틀도 타고, 친구랑 태그, 숨박꼭질도 하고 재밌게 놀다 왔어요!!",
    },
    { user_Seq: 0, content: "우리 집에 자주 오는 친구가 어른처럼 얘기하네요." },
    {
      user_Seq: 1,
      content: "맞아요, 제 친구도 요즘 어른들 따라하는 것 같아요!",
    },
    {
      user_Seq: 0,
      content:
        "어른들 말투를 따라하는 게 재밌나 봐요. 요즘에 더 많이 따라하나요?",
    },
    {
      user_Seq: 1,
      content: "네! 요즘엔 TV에서 본 것들도 따라하고 친구들이랑 같이 연습해요!",
    },
    { user_Seq: 0, content: "어떤 TV 프로그램을 보면서 많이 따라해요?" },
    {
      user_Seq: 1,
      content:
        "어린이 프로그램이나 애니메이션에서 나오는 대사를 따라하는 걸 좋아해요!",
    },
    {
      user_Seq: 0,
      content:
        "정말 재밌겠네요! 친구들이랑 대사를 같이 따라하는 게 즐거운가 봐요.",
    },
    {
      user_Seq: 1,
      content: "네! 친구들이랑 같은 대사를 하면 웃기고 재밌어요!",
    },
    { user_Seq: 0, content: "그럼 다음엔 어떤 놀이를 하고 싶어요?" },
    { user_Seq: 1, content: "다음엔 친구들이랑 술래잡기를 해보고 싶어요!" },
    {
      user_Seq: 0,
      content: "오, 술래잡기도 재밌죠! 그럼 다음에 또 놀러 가기로 약속했나요?",
    },
    {
      user_Seq: 1,
      content: "네! 다음 주에 같이 놀기로 했어요. 정말 기대돼요!",
    },
    { user_Seq: 0, content: "좋은 시간 보내고 오길 바랄게요!" },
    { user_Seq: 1, content: "고마워요! 다녀와서 이야기 들려줄게요!" },
  ];

  return (
    <div className="relative flex flex-col items-center px-[260px] py-12 min-h-screen overflow-y-scroll">
      <style>{`
        /* 커스텀 말풍선 꼬리 스타일 */
        .left-tail::after {
          content: '';
          position: absolute;
          bottom: 5;
          left: -20px; /* 꼬리 위치 조정 */
          border-width: 14px;
          border-style: solid;
          border-color: transparent #D9D9D9 transparent transparent;
        }
        
        .right-tail::after {
          content: '';
          position: absolute;
          bottom: 5;
          right: -20px; /* 꼬리 위치 조정 */
          border-width: 14px;
          border-style: solid;
          border-color: transparent transparent transparent #CED1EE;
        }
      `}</style>

      {/* 제목 영역 */}
      <div className="flex items-center justify-center relative -mt-3">
        <img src="/assets/cloud.png" alt="cloud" className="w-96" />
        <div className="absolute inset-0 flex flex-col items-center justify-center text-gray-700">
          {/* 날짜 및 달력 아이콘 */}
          <div className="flex items-center text-2xl font-medium text-[#4E4E4E] mb-2">
            <FaRegCalendarAlt
              onClick={toggleCalendar}
              className="cursor-pointer mr-2"
            />
            <span onClick={toggleCalendar} className="font-bold cursor-pointer">
              10월 11일
            </span>
          </div>
          {/* 이야기 결과지 제목 */}
          <span className="text-3xl font-bold mt-1">첫번째 이야기 결과지</span>
        </div>
      </div>

      {/* 이야기 제목 */}
      <div className="flex items-center mt-10 text-3xl font-bold self-start">
        <div className="bg-[#F3E651] w-2 h-10 mr-4" />
        <span>토끼 인형 이야기</span>
      </div>

      {/* 그래프 및 분석 결과 영역 */}
      <div className="flex flex-wrap justify-between mt-5 w-full space-y-8">
        {/* 첫 번째 열: WordCloud 및 관심사 */}
        <div className="w-[42%] bg-white rounded-xl shadow-md mt-6 p-8">
          <WordCloud />
        </div>
        <div className="w-[53%] bg-white p-8 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#b3c5d3] text-black text-[24px] px-5 py-2 rounded-2xl font-bold mr-2">
              관심사
            </span>
          </div>
          <p className="text-gray-900 text-[22px] leading-relaxed">
            어린 아이가 관심을 가지는 단어들로
            <br /> 토끼인형, 엄마, 고구마, 티니핑 등이 포함되어 있어요. <br />
            아이가 좋아하는 장난감, 가족, 계절, 음식 등이 주된 주제로 보여요.
          </p>
        </div>

        {/* 두 번째 열: Word 및 어휘력 */}
        <div className="w-[42%] bg-white rounded-xl shadow-md mt-6 p-8">
          <Word />
        </div>
        <div className="w-[53%] bg-white p-8 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#C6D4DF] text-black text-[24px] px-5 py-2 rounded-2xl font-bold mr-2">
              어휘력
            </span>
          </div>
          <p className="text-gray-900 text-[22px] leading-relaxed">
            그래프에 따르면, <br />
            재산의 어휘력은 평균 이상으로, 또래보다 더 많은 어휘를 사용하는
            것으로 나타나요. <br /> 이는 언어 발달에서 긍정적인 신호로 볼 수
            있어요.
          </p>
        </div>

        {/* 세 번째 열: Emotion 및 감정 */}
        <div className="w-[42%] bg-white rounded-xl shadow-md mt-6 p-8">
          <Emotion />
        </div>
        <div className="w-[53%] bg-white p-8 rounded-xl shadow-md bg-opacity-60">
          <div className="flex items-center mb-4">
            <span className="bg-[#E3E7F0] text-black text-[24px] px-5 py-2 rounded-2xl font-bold mr-2">
              감정
            </span>
          </div>
          <p className="text-gray-900 text-[22px] leading-relaxed">
            아이의 감정 그래프를 보면, <br />
            기쁨과 놀라움에서 높은 반응을 보이며,
            <br />
            두려움과 슬픔에서 상대적으로 낮은 반응을 보여요. <br />
            이는 아이가 주로 긍정적인 감정을 <br />더 강하게 느끼는 경향이
            있음을 나타내요.
          </p>
        </div>
      </div>

      {/* 이야기 전체 내용 및 요약보기 */}
      <div className="flex justify-between items-center mt-10 w-full">
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

      {/* 대화 내용 */}
      <div className="bg-[#F9F9F9] p-10 rounded-xl mt-5 w-full overflow-y-scroll space-y-4 max-h-[720px]">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`flex items-start ${
              message.user_Seq === 1 ? "justify-end" : ""
            } space-x-2`}
          >
            <span
              className={`px-4 py-3 rounded-2xl relative ${
                message.user_Seq === 0
                  ? "bg-[#D9D9D9] left-tail"
                  : "bg-[#CED1EE] right-tail"
              }`}
              style={{ maxWidth: "60%" }}
            >
              {message.content}
            </span>
          </div>
        ))}
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

      {/* 요약 모달 */}
      {showSummary && (
        <ChatSummary
          title="놀이터 이야기"
          content={`아이들은 엄마와 함께 놀이터에 가서 미끄럼틀을 타고,\n 친구들과 즐거운 시간을 보냈어요.\n유치원 친구들과의 관계가 더욱 돈독해졌답니다.`}
          onClose={closeSummary}
        />
      )}
    </div>
  );
};

export default TalkPage;
