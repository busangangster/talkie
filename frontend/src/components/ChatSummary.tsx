import React from "react";

interface ChatSummaryProps {
  title: string; 
  content: string; 
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
      onClick={onClose} 
    >
      <div
        className="relative h-96 w-[820px] p-8 shadow-lg bg-cover"
        style={{
          backgroundImage: "url('/assets/note.png')", 
        }}
        onClick={(e) => e.stopPropagation()} 
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
          <h2 className="text-3xl font-bold mt-28 mb-6">{title}</h2>
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
