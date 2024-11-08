/**
 * 주어진 생년월일 배열([year, month, day])을 yyyy-mm-dd 형식의 문자열로 변환합니다.
 * @param birthArray - [year, month, day] 형식의 생년월일 배열
 * @returns yyyy-mm-dd 형식의 문자열
 */
export const formatBirthDate = (birthArray: number[]): string => {
  if (birthArray && birthArray.length === 3) {
    const [year, month, day] = birthArray;
    const formattedMonth = String(month).padStart(2, "0");
    const formattedDay = String(day).padStart(2, "0");
    return `${year}-${formattedMonth}-${formattedDay}`;
  }
  return "";
};
