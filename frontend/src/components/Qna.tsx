import React, { useState } from "react";
import { IoIosArrowUp, IoIosArrowDown } from "react-icons/io";

interface QnaProps {
  data: Array<{
    id: number;
    question: string;
    answer: string;
    date: string;
  }>;
  itemsPerPage: number;
}

const Qna: React.FC<QnaProps> = ({ data, itemsPerPage }) => {
  const [activePage, setActivePage] = useState(1);
  const [openQuestions, setOpenQuestions] = useState<number[]>([]);

  const totalPages = Math.ceil(data.length / itemsPerPage);
  const paginatedData = data.slice(
    (activePage - 1) * itemsPerPage,
    activePage * itemsPerPage
  );

  const handlePageChange = (page: number) => {
    setActivePage(page);
    setOpenQuestions([]);
  };

  const toggleQuestion = (id: number) => {
    if (openQuestions.includes(id)) {
      setOpenQuestions(openQuestions.filter((questionId) => questionId !== id));
    } else {
      setOpenQuestions([...openQuestions, id]);
    }
  };

  return (
    <div className="relative w-4/5 bg-white p-8 rounded-xl shadow-md space-y-4">
      {/* 질문 리스트 영역 */}
      <div className="overflow-y-auto h-[500px] space-y-2.5">
        {paginatedData.map((item) => (
          <div key={item.id} className="border-b pb-3 mb-3">
            <div
              className="flex justify-between items-center cursor-pointer"
              onClick={() => toggleQuestion(item.id)}
            >
              <div className="flex items-center space-x-3">
                {openQuestions.includes(item.id) ? (
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
              <span className="text-sm ml-12 mr-3 bottom-0">{item.date}</span>
            </div>
            {openQuestions.includes(item.id) && (
              <div className="mt-3 ml-6 mr-28">
                {item.answer ? (
                  <p>{item.answer}</p>
                ) : (
                  <div className="text-gray-400">
                    <p>아직 질문을 하지 않았어요!</p>
                  </div>
                )}
                {/* 수정/삭제 버튼 */}
                {!item.answer && (
                  <div className="flex space-x-2 mt-2">
                    <button className="text-blue-500">수정</button>
                    <button className="text-red-500">삭제</button>
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>

      {/* 페이지 네이션 */}
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
    </div>
  );
};

export default Qna;
