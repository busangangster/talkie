// store/useTabStore.ts
import { create } from "zustand";
import { persist } from "zustand/middleware";

interface TabState {
  selectedTab: string;
  setSelectedTab: (tab: string) => void;
}

const useTabStore = create<TabState>()(
  persist(
    (set) => ({
      selectedTab: "감정", // 기본값으로 "감정"을 설정
      setSelectedTab: (tab) => set({ selectedTab: tab }),
    }),
    {
      name: "tab-storage", // localStorage에 저장될 키 이름
    }
  )
);

export default useTabStore;
