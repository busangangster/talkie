export interface ApiErrorResponse {
  status: number;
  message: string;
}

export interface LoginType {
  userId: string;
}

export interface UserInfo {
  userSeq: number;
  name: string;
  age: number;
  gender: string;
  favorite: string;
  remark?: string;
}

export interface UserState {
  userSeq: number | null;
  isNotFirstLogin: boolean;
  setUserSeq: (userSeq: number) => void;
  setIsNotFirstLogin: (isNotFirstLogin: boolean) => void;
}

export interface QuestionData {
  questionSeq: number;
  question: string;
  questionCreatedAt: number[];
  questionIsActive: boolean;
  answer: string;
  answerCreatedAt: number[];
}

export interface QnaProps {
  data: QuestionData[];
  itemsPerPage: number;
}

export interface UpdateQuestionParams {
  userSeq: number;
  content: string;
}

export interface ConversationItem {
  conversationSeq: number;
  title: string;
  createdAt: number[];
  order?: string;
  formattedTime?: string; 
}

export interface ConversationListResponse {
  data: {
    conversationList: ConversationItem[];
  };
}
