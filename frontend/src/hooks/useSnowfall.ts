import { create } from "zustand";

interface SnowfallState {
  isSnowing: boolean;
  toggleSnow: () => void;
}

export const useSnowfall = create<SnowfallState>((set) => ({
  isSnowing: true, // 초기 상태: 눈 내림 효과 활성화
  toggleSnow: () => set((state) => ({ isSnowing: !state.isSnowing })),
}));
