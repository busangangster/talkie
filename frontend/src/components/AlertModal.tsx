import React, { useEffect, useState } from "react";

interface AlertModalProps {
  icon: React.ReactNode; // 아이콘 이미지 컴포넌트
  message: string; // 알림 메시지
  onConfirm: () => void; // 확인 버튼 클릭 핸들러
}

const AlertModal: React.FC<AlertModalProps> = ({
  icon,
  message,
  onConfirm,
}) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
  }, []);

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50 transition-opacity duration-300 ease-out">
      <div
        className={`bg-white rounded-2xl shadow-lg py-8 px-24 flex flex-col items-center transform transition-transform duration-300 ${
          isVisible ? "opacity-100 scale-100" : "opacity-0 scale-95"
        }`}
      >
        <div className="mb-4 w-20">{icon}</div> {/* 아이콘 표시 */}
        <p className="text-center text-gray-800 text-2xl mb-6 font-bold">
          {message}
        </p>{" "}
        {/* 메시지 */}
        <button
          onClick={onConfirm}
          className="bg-[#BBB4ED] text-white py-2 px-6 rounded-xl text-lg hover:bg-[#a09bcd]"
        >
          확인
        </button>
      </div>
    </div>
  );
};

export default AlertModal;
