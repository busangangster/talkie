// AlertModal.tsx
import React from "react";
import ReactDOM from "react-dom";
import { motion, AnimatePresence } from "framer-motion";

interface AlertModalProps {
  icon: React.ReactNode;
  message: string;
  onConfirm: () => void;
}

const AlertModal: React.FC<AlertModalProps> = ({
  icon,
  message,
  onConfirm,
}) => {
  const modalContent = (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <motion.div
        className="bg-white rounded-2xl shadow-lg py-8 px-24 flex flex-col items-center"
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.8 }}
        transition={{ duration: 0.2 }}
      >
        <div className="mb-4 w-20">{icon}</div>
        <p className="text-center text-gray-800 text-2xl mb-6 font-bold">
          {message}
        </p>
        <button
          onClick={onConfirm}
          className="bg-[#BBB4ED] text-white py-2 px-6 rounded-xl text-lg hover:bg-[#a09bcd]"
        >
          확인
        </button>
      </motion.div>
    </div>
  );

  return ReactDOM.createPortal(
    <AnimatePresence>{modalContent}</AnimatePresence>,
    document.getElementById("modal-root") as HTMLElement
  );
};

export default AlertModal;
