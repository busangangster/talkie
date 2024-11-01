import React from "react";
import { useNavigate } from "react-router-dom";

const QuestionButton: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div
      className="fixed bottom-10 right-10 bg-white rounded-full shadow-lg cursor-pointer flex items-center justify-center w-20 h-20 z-50 group transition-transform transform hover:-translate-y-1"
      onClick={() => navigate("/question")}
    >
      <img
        src="/assets/question.png"
        alt="question icon"
        className="w-12 h-12"
      />
      {/* 툴팁 */}
      <div className="absolute top-0 left-0 mt-[-2.5rem] ml-[-2.3rem] bg-white bg-opacity-90 text-black font-medium rounded-md shadow-lg px-3 py-1 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
        아이에게 질문하기
      </div>
    </div>
  );
};

export default QuestionButton;
