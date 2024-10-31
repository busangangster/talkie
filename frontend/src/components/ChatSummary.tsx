import React from "react";

interface ChatSummaryProps {
  title: string; // 제목
  content: string; // 내용
  onClose: () => void;
}

const ChatSummary: React.FC<ChatSummaryProps> = ({
  title,
  content,
  onClose,
}) => {
  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      onClick={onClose} // 모달 외부를 클릭하면 onClose 호출
    >
      <div
        className="relative h-96 w-[820px] p-8 shadow-lg bg-cover"
        style={{
          backgroundImage: "url('/assets/note.png')", // 노트 이미지 경로
        }}
        onClick={(e) => e.stopPropagation()} // 모달 자체를 클릭할 때는 이벤트 전파를 막음
      >
        {/* 닫기 버튼 */}
        <button
          className="absolute top-12 right-5 text-gray-500 hover:text-gray-700"
          onClick={onClose}
        >
          X
        </button>

        {/* 제목과 내용 */}
        <div className="text-center text-black">
          <h2 className="text-3xl font-bold mt-24 mb-6">{title}</h2> {/* 제목 */}
          <p className="text-2xl leading-loose whitespace-pre-line">
            {content}
          </p>{" "}
          {/* 내용 */}
        </div>
      </div>
    </div>
  );
};

export default ChatSummary;
