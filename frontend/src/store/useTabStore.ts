import { create } from "zustand";

interface TabState {
  selectedTab: string;
  setSelectedTab: (tab: string) => void;
}

const useTabStore = create<TabState>((set) => ({
  selectedTab: "감정",
  setSelectedTab: (tab) => set({ selectedTab: tab }),
}));

export default useTabStore;
