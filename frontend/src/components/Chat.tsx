import React from "react";

interface Message {
  user_Seq: number;
  content: string;
}

interface ChatProps {
  messages: Message[];
}

const Chat: React.FC<ChatProps> = ({ messages }) => {
  return (
    <div className="bg-[#F9F9F9] p-10 rounded-xl mt-5 w-[1100px] overflow-y-scroll space-y-4 max-h-[720px]">
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
  );
};

export default Chat;
