import React from "react";
import { motion } from "framer-motion";

const Motion: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <motion.div
    initial={{ opacity: 0, x: -100 }} // 초기 상태
    animate={{ opacity: 1, x: 0 }} // 애니메이션되는 상태
    exit={{ opacity: 0, x: 100 }} // 종료 상태
    transition={{ duration: 0.3 }} // 애니메이션 지속 시간
  >
    {children}
  </motion.div>
);

export default Motion;
