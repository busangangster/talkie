import instance from "./axios";
import { handleApiError } from "../utils/ErrorHandling";
import useUserStore from "../store/useUserStore";
import { UserInfo } from "../type"

// 로그인
export const login = async (params: { userId: string }) => {
  const { setUserSeq, setIsNotFirstLogin } = useUserStore.getState();

  try {
    const response = await instance.post("/api/login", params);

    // API 응답에서 userSeq와 isNotFirstLogin을 추출하여 zustand 상태 업데이트
    const { userSeq, isNotFirstLogin } = response.data.data;
    setUserSeq(userSeq);
    setIsNotFirstLogin(isNotFirstLogin);

    return response.data;
  } catch (error) {
    handleApiError(error as never);
    throw error;
  }
};

// 아이 정보 입력 및 수정
export const updateUserInfo = async (userInfo: UserInfo) => {
  try {
    const response = await instance.put("/api/user/update", userInfo);
    return response.data;
  } catch (error) {
    handleApiError(error as never);
    throw error;
  }
};
