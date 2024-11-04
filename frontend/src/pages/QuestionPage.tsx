import React, { useState, useEffect } from "react";
import Question from "../components/Question";
import Qna from "../components/Qna";
import { getQuestionsAndAnswers } from "../apis/api"; // API 함수 불러오기
import { QuestionData } from "../type"; // 타입 불러오기
import useUserStore from "../store/useUserStore";

const QuestionPage: React.FC = () => {
  const [isQuestionUsed, setIsQuestionUsed] = useState(false);
  const [data, setData] = useState<QuestionData[]>([]); // 데이터 상태 추가
  const { userSeq } = useUserStore();

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (userSeq) {
          const response = await getQuestionsAndAnswers(userSeq);
          setData(response.data); // API 응답 데이터를 상태에 저장
        }
      } catch (error) {
        console.error("데이터를 불러오는 중 오류가 발생했습니다:", error);
      }
    };

    fetchData();
  }, [userSeq]);

  return (
    <div className="flex items-center justify-center min-h-[100vh]">
      <div className="flex flex-col items-center py-10 px-20 bg-[#EFEFEF] bg-opacity-60 rounded-xl w-[1300px] space-y-6">
        {/* 제목 */}
        <h1 className="self-start text-[32px] font-bold">
          우리 아이에게 궁금한 것들
        </h1>

        {/* 질문 리스트와 질문하기 영역 */}
        <div className="flex w-full space-x-7">
          {/* Qna 컴포넌트에 API에서 받아온 데이터 전달 */}
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
