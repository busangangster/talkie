import React from "react";
import { ChatProps } from "../type";
import Image from "./Image";

const Chat: React.FC<ChatProps> = ({ messages }) => {
  return (
    <div
      className="bg-[#F9F9F9] p-10 rounded-xl mt-5 overflow-y-scroll space-y-4 max-h-[70vh]"
      style={{ width: "68vw" }}
    >
      {messages.map((message, index) => (
        <div
          key={index}
          className={`flex items-end ${
            message.user_Seq === 1 ? "justify-end" : ""
          } space-x-4`}
        >
          {message.user_Seq === 0 && (
            <Image
              src="/assets/talk/talkie"
              alt="Talkie"
              className="w-10 h-10"
            />
          )}

          <span
            className={`px-4 py-3 rounded-2xl relative ${
              message.user_Seq === 0
                ? "bg-[#D9D9D9] left-tail"
                : "bg-[#CED1EE] right-tail"
            }`}
            style={{ maxWidth: "45%" }}
          >
            {/* 빈 content인 경우 스페이스 한 칸으로 대체 */}
            {message.content.trim() !== "" ? message.content : "..."}
          </span>

          {message.user_Seq === 1 && (
            <Image
              src="/assets/talk/child"
              alt="Child"
              className="w-10 h-10 mb-2"
            />
          )}
        </div>
      ))}
    </div>
  );
};

export default Chat;
