import React, { useState } from "react";
import Question from "../components/Question";
import Qna from "../components/Qna";

const QuestionPage: React.FC = () => {
  const [isQuestionUsed, setIsQuestionUsed] = useState(false);

  // 목업 데이터
  const data = [
    {
      id: 1,
      question: "팔에 멍들었는데, 그건 어떻게 생긴거야?",
      answer: "",
      date: "2024.08.02",
    },
    {
      id: 2,
      question: "최근에 어린이집에서 선생님이랑 사이좋게 지냈어?",
      answer:
        "응 엄청 사이좋게 지냈어. 같이 재밌는 게임도 하고 밥도 같이 먹고, 어린이집 가는 게 기다려져",
      date: "2024.08.01",
    },
    {
      id: 3,
      question: "주말에 놀러가고 싶은 곳 있어?",
      answer: "응! 놀이공원에 가고 싶어. 거기서 친구들이랑 같이 놀고 싶어.",
      date: "2024.07.30",
    },
    {
      id: 4,
      question: "요즘 제일 좋아하는 만화 캐릭터는 누구야?",
      answer: "도라에몽이 제일 좋아. 항상 친구들을 도와주는 모습이 멋져 보여.",
      date: "2024.07.28",
    },
    {
      id: 5,
      question: "어제 ○○이랑 다퉈서 속상하다며, ○○이랑 어떻게 다투게 된거야?",
      answer:
        "○○이 나 바보라고 놀렸어. 난 바보가 아니야! ○○이랑 앞으로 안놀거야 !!",
      date: "2024.07.25",
    },
    {
      id: 6,
      question: "엄마랑 아빠 중에 누가 더 좋아?",
      answer: "둘 다 좋아! 엄마는 요리 잘하고, 아빠는 놀아줘서 좋아.",
      date: "2024.07.20",
    },
    {
      id: 7,
      question: "어제 먹은 음식 중에 제일 맛있었던 건 뭐야?",
      answer:
        "어제 먹은 파스타가 제일 맛있었어. 고소하고 치즈가 많이 들어가서 좋았어.",
      date: "2024.07.18",
    },
    {
      id: 8,
      question: "오늘 어린이집에서 제일 재밌었던 활동은 뭐야?",
      answer: "미술 시간에 그림 그리는 게 제일 재밌었어. 나무랑 꽃을 그렸어.",
      date: "2024.07.16",
    },
    {
      id: 9,
      question: "친구랑 무슨 놀이를 제일 많이 해?",
      answer: "숨바꼭질이랑 술래잡기 놀이를 제일 많이 해. 엄청 재밌어!",
      date: "2024.07.15",
    },
    {
      id: 10,
      question: "최근에 꿈꾼 것 중에 기억나는 게 있어?",
      answer: "어젯밤에 슈퍼히어로가 되어 하늘을 나는 꿈을 꿨어!",
      date: "2024.07.14",
    },
    {
      id: 11,
      question: "다음 생일 때 받고 싶은 선물은 뭐야?",
      answer: "로봇 장난감을 받고 싶어! 움직이고 소리 나는 장난감이 좋아.",
      date: "2024.07.13",
    },
    {
      id: 12,
      question: "친구들 중에 제일 친한 친구는 누구야?",
      answer: "민수야. 같이 놀이터에서 자주 놀고 재밌게 지내.",
      date: "2024.07.10",
    },
    {
      id: 13,
      question: "어린이집에서 선생님이랑 어떤 얘기 했어?",
      answer: "선생님이랑 책 읽는 얘기를 했어. 새로운 책도 추천해주셨어.",
      date: "2024.07.08",
    },
    {
      id: 14,
      question: "아빠랑 뭐하고 놀 때가 제일 재밌어?",
      answer: "아빠랑 레고 조립하면서 노는 게 제일 재밌어!",
      date: "2024.07.05",
    },
    {
      id: 15,
      question: "어제 본 만화 영화는 어땠어?",
      answer: "너무 재밌었어! 특히 주인공이 용감하게 싸우는 장면이 멋졌어.",
      date: "2024.07.02",
    },
  ];

  return (
    <div className="flex items-center justify-center min-h-[100vh]">
      <div className="flex flex-col items-center py-10 px-20 bg-[#EFEFEF] bg-opacity-60 rounded-xl w-[1300px] space-y-6">
        {/* 제목 */}
        <h1 className="self-start text-[32px] font-bold">
          우리 아이에게 궁금한 것들
        </h1>

        {/* 질문 리스트와 질문하기 영역 */}
        <div className="flex w-full space-x-7">
          {" "}
          {/* 여기서 space-x-6 추가 */}
          <Qna data={data} itemsPerPage={8} />
          {/* 오른쪽 질문하기 영역 */}
          <Question
            isQuestionUsed={isQuestionUsed}
            onQuestionSubmit={() => setIsQuestionUsed(true)}
          />
        </div>
      </div>
    </div>
  );
};

export default QuestionPage;
 