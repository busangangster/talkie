import { create } from "zustand";
import { UserState, UserResponse } from "../type";

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
  name: '',
  age: 0,
  gender: '',
  favorite: '',
  remark: '',
  setUserSeq: (userSeq) => {
    set({ userSeq });
    localStorage.setItem("userSeq", JSON.stringify(userSeq)); // 상태 업데이트 시 localStorage도 갱신
  },
  setNotFirstLogin: (notFirstLogin) => set({ notFirstLogin }),
  setUserInfo: (userInfo: UserResponse) => set({
    name: userInfo.name,
    age: userInfo.age,
    gender: userInfo.gender,
    favorite: userInfo.favorite,
    remark: userInfo.remark,
  }),
}));

export default useUserStore;
