export interface ApiErrorResponse {
  status: number;
  message: string;
}

export interface LoginType {
  userId: string;
}

export interface LoginInfoResponse {
  data: {
    userSeq: number;
    notFirstLogin: boolean;
  };
  message: string;
  timestamp: string;
  status: number;
}


export interface UserInfo {
  userSeq: number;
  name: string;
  birth: string;
  gender: string;
  favorite: string;
  remark?: string;
}


export interface UserResponse {
  userSeq: number;
  name: string;
  birth: number[];
  gender: string;
  favorite: string;
  remark: string;
}

export interface UserState {
  userSeq: number | null;
  notFirstLogin: boolean;
  name: string;
  birth: string;
  gender: string;
  favorite: string;
  remark?: string;
  setUserSeq: (userSeq: number) => void;
  setNotFirstLogin: (notFirstLogin: boolean) => void;
  setUserInfo: (userInfo: UserResponse) => void;
  resetUser: () => void;
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

export interface WordCloud {
  word: string;
  count: number;
}

export interface WordCloudComponentProps {
  words: WordCloud[]; 
}

export interface ConversationContent {
  content: string;
  answer: boolean;
  createdAt: string;
}

export interface ConversationDetailResponse {
  conversationSeq: number;
  emotionSummary: string;
  vocabularySummary: string;
  wordCloudSummary: string;
  happyScore: number;
  loveScore: number;
  sadScore: number;
  scaryScore: number;
  angryScore: number;
  amazingScore: number;
  vocabularyScore: number;
  wordClouds: WordCloud[];
  conversationContents: ConversationContent[];
}

export interface DataItem {
  subject: string;
  score: number;
  fullMark: number;
}

export interface TalkEmotionProps {
  happyScore: number;
  loveScore: number;
  sadScore: number;
  scaryScore: number;
  angryScore: number;
  amazingScore: number;
}

export interface Message {
  user_Seq: number;
  content: string;
}

export interface ChatProps {
  messages: Message[];
}

export interface WeeklyConversation {
  vocabularyScore: number;
  happyScore: number;
  loveScore: number;
  sadScore: number;
  scaryScore: number;
  angryScore: number;
  amazingScore: number;
  conversationCount: number;
  createdAt: string;
}

export interface WordCloudResponse {
  word: string;
  count: number;
}

export interface WeeklyConversationResponse {
  weeklyConversations: WeeklyConversation[];
  wordCloudResponses: WordCloudResponse[];
  wordCloudSummary: string;
  emotionSummary: string;
  vocabularySummary: string;
  countSummary: string;
}

export interface WeekFrameProps {
  selectedTab: string;
  weeklyData: WeeklyConversationResponse | null;
}

export interface WeekProps {
  data: WeeklyConversation[];
}

export interface WeekInterestProps {
  data: WordCloudResponse[];
}
