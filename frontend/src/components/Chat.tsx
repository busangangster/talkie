import React from "react";
import { ChatProps } from "../type";
import childIcon from "/assets/talk/child.png";
import talkieIcon from "/assets/talk/talkie.png";

const Chat: React.FC<ChatProps> = ({ messages }) => {
  return (
    <div className="bg-[#F9F9F9] p-10 rounded-xl mt-5 w-[1100px] overflow-y-scroll space-y-4 max-h-[720px]">
      {messages.map((message, index) => (
        <div
          key={index}
          className={`flex items-end ${
            message.user_Seq === 1 ? "justify-end" : ""
          } space-x-4`}
        >
          {/* 톡이의 말풍선 (user_Seq === 0) */}
          {message.user_Seq === 0 && (
            <img src={talkieIcon} alt="Talkie" className="w-10 h-10" />
          )}

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

          {/* 아이의 말풍선 (user_Seq === 1) */}
          {message.user_Seq === 1 && (
            <img src={childIcon} alt="Child" className="w-10 h-10 mb-2" />
          )}
        </div>
      ))}
    </div>
  );
};

export default Chat;
