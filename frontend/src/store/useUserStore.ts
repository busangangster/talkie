import { create } from "zustand";
import { UserState } from "../type";

const useUserStore = create<UserState>((set) => ({
  userSeq: JSON.parse(localStorage.getItem("userSeq") || "null"),
  isNotFirstLogin: false,
  setUserSeq: (userSeq) => {
    set({ userSeq });
    localStorage.setItem("userSeq", JSON.stringify(userSeq)); // 상태 업데이트 시 localStorage도 갱신
  },
  setIsNotFirstLogin: (isNotFirstLogin) => set({ isNotFirstLogin }),
}));

export default useUserStore;
