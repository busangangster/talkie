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
