import instance from "./axios";
import { handleApiError } from "../utils/ErrorHandling";
import useUserStore from "../store/useUserStore";
import {
  UpdateQuestionParams,
  UserInfo,
  ConversationListResponse,
} from "../type";

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

// 질문하기
export const createQuestion = async (userSeq: number, content: string) => {
  try {
    const response = await instance.post("/api/question/create", {
      userSeq,
      content,
    });
    return response.data;
  } catch (error) {
    handleApiError(error as never);
    throw error;
  }
};

// 질문 목록
export const getQuestionsAndAnswers = async (userSeq: number) => {
  try {
    const response = await instance.get(`/api/question/${userSeq}`);
    return response.data;
  } catch (error) {
    handleApiError(error as never);
    throw error;
  }
};

// 질문 수정
export const updateQuestion = async (params: UpdateQuestionParams) => {
  try {
    const response = await instance.put("/api/question/update", params);
    return response.data;
  } catch (error) {
    console.error("질문 수정 API 요청 중 오류 발생:", error);
    throw error;
  }
};

// 질문 삭제
export const deleteQuestion = async (userSeq: number) => {
  try {
    const response = await instance.delete(`/api/question/delete/${userSeq}`);
    return response.data;
  } catch (error) {
    console.error("질문 삭제 중 오류가 발생했습니다:", error);
    throw error;
  }
};

// 질문 가능 여부
export const availableQuestion = async (userSeq: number) => {
  try {
    const response = await instance.get(`/api/question/available/${userSeq}`);
    return response.data;
  } catch (error) {
    handleApiError(error as never);
    throw error;
  }
};

// 일별 대화 목록 조회
export const getConversationListByDate = async (
  userSeq: number,
  day: string
): Promise<ConversationListResponse> => {
  try {
    const response = await instance.get<ConversationListResponse>(
      `/api/conversation/list/${userSeq}`,
      {
        params: { day },
      }
    );
    return response.data;
  } catch (error) {
    console.error("일자별 대화 목록 조회 중 오류 발생:", error);
    throw error;
  }
};