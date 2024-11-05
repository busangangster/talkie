import React, { useState, useEffect } from "react";
import Question from "../components/Question";
import Qna from "../components/Qna";
import { getQuestionsAndAnswers, availableQuestion } from "../apis/api";
import { QuestionData } from "../type";
import useUserStore from "../store/useUserStore";

const QuestionPage: React.FC = () => {
  const [isQuestionAvailable, setIsQuestionAvailable] = useState(true); // Boolean 값으로 설정
  const [data, setData] = useState<QuestionData[]>([]);
  const { userSeq } = useUserStore();

  // 질문 가능 여부 확인 함수
  const checkQuestionAvailability = async () => {
    if (userSeq) {
      const response = await availableQuestion(userSeq);
      setIsQuestionAvailable(response.data); // Boolean 값으로만 설정
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      if (userSeq) {
        const response = await getQuestionsAndAnswers(userSeq);
        setData(response.data);
      }
    };

    fetchData();
    checkQuestionAvailability(); // 페이지 로드 시 질문 가능 여부 확인
  }, [userSeq]);

  // 새 질문 추가 핸들러
  const handleQuestionSubmit = (newQuestion: QuestionData) => {
    setData((prevData) => [newQuestion, ...prevData]); // 새 질문을 리스트 맨 앞에 추가
    setIsQuestionAvailable(false); // 질문 등록 후 질문 불가 상태로 설정
    checkQuestionAvailability(); // 질문 가능 여부를 재확인
  };

  return (
    <div className="flex items-center justify-center min-h-[100vh]">
      <div className="flex flex-col items-center py-10 px-20 bg-[#EFEFEF] bg-opacity-60 rounded-xl w-[1300px] space-y-6">
        <h1 className="self-start text-[32px] font-bold">
          우리 아이에게 궁금한 것들
        </h1>

        <div className="flex w-full space-x-7">
          <Qna
            data={data}
            itemsPerPage={8}
            onQuestionDelete={(deletedQuestionSeq) => {
              setData((prevData) =>
                prevData.filter((q) => q.questionSeq !== deletedQuestionSeq)
              ); // 삭제된 질문을 리스트에서 제거
              checkQuestionAvailability(); // 삭제 후 질문 가능 여부 확인
            }}
          />

          <Question
            isQuestionAvailable={isQuestionAvailable}
            onQuestionSubmit={handleQuestionSubmit} // 새 질문 추가 핸들러 전달
          />
        </div>
      </div>
    </div>
  );
};

export default QuestionPage;
