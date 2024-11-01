import React, { useState } from "react";
import { FaQuestionCircle } from "react-icons/fa";
import { IoIosInformationCircleOutline } from "react-icons/io";
import AlertModal from "../components/AlertModal";

interface QuestionProps {
  isQuestionUsed: boolean;
  onQuestionSubmit: () => void;
}

const Question: React.FC<QuestionProps> = ({
  isQuestionUsed,
  onQuestionSubmit,
}) => {
  const [question, setQuestion] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSubmit = () => {
    if (!question.trim()) {
      setIsModalOpen(true); // 질문이 없을 경우 모달을 표시
    } else {
      onQuestionSubmit();
      setQuestion(""); // 질문 제출 후 입력란 초기화
    }
  };

  return (
    <div className="w-2/5 bg-[#d1e0ed] p-6 rounded-lg shadow-md flex flex-col space-y-4 relative">
      {/* 질문하기 타이틀 */}
      <div className="flex items-center text-3xl font-bold space-x-2">
        <FaQuestionCircle className="text-[#333f49]" />
        <span>질문하기</span>

        {/* 정보 아이콘 */}
        <div className="relative group flex items-center">
          <IoIosInformationCircleOutline className="text-lg relative -bottom-2 ml-1 cursor-pointer" />
          <span className="absolute left-full top-1/2 transform -translate-y-1/2 ml-2 w-48 p-2 bg-white text-black text-xs rounded-lg shadow-md opacity-0 group-hover:opacity-100 transition-opacity duration-200">
            질문은 하루에 1개만 가능해요!
          </span>
        </div>
      </div>

      <p className="text-gray-500">아이의 궁금한 속마음에 대해서 물어보세요.</p>

      {/* 질문 입력 영역 */}
      {!isQuestionUsed ? (
        <textarea
          placeholder="아이에게 질문할 내용을 입력해주세요."
          className="w-full p-6 rounded-lg resize-none h-4/5 bg-white focus:outline-none focus:ring-1 focus:ring-[#a0a0a0]"
          rows={5}
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
        />
      ) : (
        <div className="w-full p-6 rounded-lg h-4/5 bg-[#EAEAEA] flex items-center justify-center text-center font-bold text-black text-lg">
          오늘의 질문을 이미 사용해서, <br />
          더이상 질문할 수 없어요! <br />
          내일 다시 이용해주세요.
        </div>
      )}

      {/* 등록 버튼 */}
      <button
        onClick={handleSubmit}
        className="w-2/5 p-3 mt-2 bg-[#869FD3] text-white rounded-lg ml-auto hover:bg-[#7286b0] disabled:opacity-50"
        disabled={isQuestionUsed}
      >
        등록하기
      </button>

      {/* AlertModal */}
      {isModalOpen && (
        <AlertModal
          icon={<img src="/assets/alerticon/question.png" alt="alert icon" />}
          message="질문을 입력해주세요"
          onConfirm={() => setIsModalOpen(false)}
        />
      )}
    </div>
  );
};

export default Question;
