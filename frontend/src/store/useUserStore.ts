import { create } from "zustand";
import { UserState, UserResponse } from "../type";
import { formatBirthDate } from "../utils/formatBirthDate";

const getUserSeqFromLocalStorage = () => {
  try {
    const storedUserSeq = localStorage.getItem("userSeq");
    return storedUserSeq ? JSON.parse(storedUserSeq) : null;
  } catch {
    return null;
  }
};

const useUserStore = create<UserState>((set) => ({
  userSeq: getUserSeqFromLocalStorage(),
  notFirstLogin: false,
  name: "",
  birth: "",
  gender: "",
  favorite: "",
  remark: "",
  setUserSeq: (userSeq) => {
    set({ userSeq });
    localStorage.setItem("userSeq", JSON.stringify(userSeq)); // 상태 업데이트 시 localStorage도 갱신
  },
  setNotFirstLogin: (notFirstLogin) => set({ notFirstLogin }),
  setUserInfo: (userInfo: UserResponse) =>
    set({
      name: userInfo.name,
      birth: formatBirthDate(userInfo.birth),
      gender: userInfo.gender,
      favorite: userInfo.favorite,
      remark: userInfo.remark,
    }),
  resetUser: () => {
    set({
      userSeq: null,
      notFirstLogin: false,
      name: "",
      birth: "",
      gender: "",
      favorite: "",
      remark: "",
    });
    localStorage.removeItem("userSeq");
  },
}));

export default useUserStore;
