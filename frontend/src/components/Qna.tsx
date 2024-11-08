import React, { useState, useEffect } from "react";
import { IoIosArrowUp, IoIosArrowDown } from "react-icons/io";
import { QnaProps } from "../type";
import { deleteQuestion } from "../apis/api";
import AlertModal from "../components/AlertModal";
import useUserStore from "../store/useUserStore";
import childIcon from "/assets/talk/child.png";

interface QnaPropsExtended extends QnaProps {
  onQuestionDelete: (questionSeq: number) => void;
}

const Qna: React.FC<QnaPropsExtended> = ({
  data,
  itemsPerPage,
  onQuestionDelete,
}) => {
  const [questions, setQuestions] = useState(data);
  const [activePage, setActivePage] = useState(1);
  const [openQuestionSeqs, setOpenQuestionSeqs] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { userSeq } = useUserStore();

  useEffect(() => {
    setQuestions(data);
  }, [data]);

  const sortedData = [...questions].sort((a, b) => {
    for (let i = 0; i < a.questionCreatedAt.length; i++) {
      if (a.questionCreatedAt[i] !== b.questionCreatedAt[i]) {
        return b.questionCreatedAt[i] - a.questionCreatedAt[i];
      }
    }
    return 0;
  });

  const totalPages = Math.ceil(sortedData.length / itemsPerPage);
  const paginatedData = sortedData.slice(
    (activePage - 1) * itemsPerPage,
    activePage * itemsPerPage
  );

  const handlePageChange = (page: number) => {
    setActivePage(page);
  };

  const toggleQuestion = (questionSeq: number) => {
    setOpenQuestionSeqs((prev) =>
      prev.includes(questionSeq)
        ? prev.filter((seq) => seq !== questionSeq)
        : [...prev, questionSeq]
    );
  };

  const formatDate = (dateArray: number[]) => {
    if (dateArray.length >= 3) {
      const year = dateArray[0];
      const month = String(dateArray[1]).padStart(2, "0");
      const day = String(dateArray[2]).padStart(2, "0");
      return `${year}.${month}.${day}`;
    }
    return "유효하지 않은 날짜";
  };

  const handleDelete = async (questionSeq: number) => {
    try {
      if (userSeq !== null) {
        await deleteQuestion(userSeq);
        setQuestions((prevQuestions) =>
          prevQuestions.filter(
            (question) => question.questionSeq !== questionSeq
          )
        );
        onQuestionDelete(questionSeq); // 삭제된 질문을 QuestionPage 상태에 반영
        setIsModalOpen(true);
      } else {
        console.error("userSeq가 설정되지 않았습니다.");
      }
    } catch (error) {
      console.error("질문 삭제 중 오류 발생:", error);
    }
  };

  return (
    <div className="relative w-4/5 bg-white p-8 rounded-xl shadow-md space-y-4">
      <div className="overflow-y-auto h-[500px] space-y-2.5">
        {paginatedData.map((item) => (
          <div key={item.questionSeq} className="border-b pb-3 mb-3">
            <div
              className="flex justify-between items-center cursor-pointer"
              onClick={() => toggleQuestion(item.questionSeq)}
            >
              <div className="flex items-center space-x-3">
                {openQuestionSeqs.includes(item.questionSeq) ? (
                  <IoIosArrowUp className="text-gray-500" />
                ) : (
                  <IoIosArrowDown className="text-gray-500 text-lg" />
                )}
                <p
                  className={`font-bold text-[18px] py-1.5 ${
                    item.answer ? "text-black" : "text-neutral-500"
                  }`}
                >
                  {item.question}
                </p>
              </div>
              <div className="flex items-center">
                {!item.answer && userSeq !== null && (
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(item.questionSeq);
                    }}
                    className="bg-[#dadada] px-1.5 py-0.5 rounded-md text-sm hover:bg-[#bfbfbf] -mr-5"
                  >
                    삭제
                  </button>
                )}
                <span className="text-sm ml-12 mr-3 bottom-0">
                  {formatDate(item.questionCreatedAt)}
                </span>
              </div>
            </div>
            {openQuestionSeqs.includes(item.questionSeq) && (
              <div className="mt-3 ml-6 mr-28">
                {item.answer ? (
                  <div className="flex items-center space-x-2">
                    <img src={childIcon} alt="Child" className="w-6 h-6" />
                    <span>: {item.answer}</span>
                  </div>
                ) : (
                  <div className="text-gray-400">
                    <p>아직 질문을 하지 않았어요!</p>
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="flex justify-center space-x-3 mt-4">
        <button
          onClick={() => handlePageChange(activePage - 1)}
          disabled={activePage === 1}
          className={`${
            activePage === 1 ? "text-gray-300" : "text-neutral-600"
          }`}
        >
          &lt;
        </button>
        {[...Array(totalPages)].map((_, pageIndex) => (
          <button
            key={pageIndex}
            onClick={() => handlePageChange(pageIndex + 1)}
            className={`px-3 py-1 rounded-xl ${
              activePage === pageIndex + 1
                ? "bg-[#C6D4DF] text-black font-bold"
                : "text-neutral-600"
            }`}
          >
            {pageIndex + 1}
          </button>
        ))}
        <button
          onClick={() => handlePageChange(activePage + 1)}
          disabled={activePage === totalPages}
          className={`text-lg ${
            activePage === totalPages ? "text-gray-300" : "text-neutral-600"
          }`}
        >
          &gt;
        </button>
      </div>

      {isModalOpen && (
        <AlertModal
          icon={<img src="/assets/alerticon/check.png" alt="alert icon" />}
          message="질문이 삭제되었어요."
          onConfirm={() => setIsModalOpen(false)}
        />
      )}
    </div>
  );
};

export default Qna;
