import Calendar from "react-calendar";
import styled from "styled-components";
import "react-calendar/dist/Calendar.css";

export const StyledCalendarWrapper = styled.div`
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column; /* 수직 정렬 */
  align-items: center; /* 중앙 정렬 */
  position: relative;
  .react-calendar {
    width: 700px;
    border: none;
    border-radius: 0.5rem;
    box-shadow: 4px 2px 10px 0px rgba(0, 0, 0, 0.13);
    padding: 3% 5%;
    background-color: white;
  }

  /* 선택된 주 강조 스타일 */
  .highlight-week {
    background-color: rgba(255, 215, 0, 0.3); /* 연한 노란색 배경 */
    font-weight: bold;
    color: black;
  }

  /* 전체 폰트 컬러 */
  .react-calendar__month-view {
    abbr {
      color: ${(props) => props.theme.gray_1};
    }
  }

  /* 네비게이션 가운데 정렬 */
  .react-calendar__navigation {
    justify-content: center;
  }

  /* 네비게이션 폰트 설정 */
  .react-calendar__navigation button {
    font-weight: 800;
    font-size: 1rem;
  }

  /* 네비게이션 버튼 컬러 */
  .react-calendar__navigation button:focus {
    background-color: white;
  }

  /* 네비게이션 비활성화 됐을때 스타일 */
  .react-calendar__navigation button:disabled {
    background-color: white;
    color: ${(props) => props.theme.darkBlack};
  }

  /* 년/월 상단 네비게이션 칸 크기 줄이기 */
  .react-calendar__navigation__label {
    flex-grow: 0 !important;
  }

  /* 요일 밑줄 제거 */
  .react-calendar__month-view__weekdays abbr {
    text-decoration: none;
    font-weight: 800;
  }

  .react-calendar__month-view__weekdays__weekday--weekend abbr[title="일요일"] {
    color: red !important;
    font-weight: bold;
  }

  /* 일요일 날짜 빨간색 */
  .sunday {
    color: red !important;
    font-weight: bold;
  }

  .neighboring-month {
    color: #a8a8a8 !important;
  }

  .react-calendar__tile {
    color: black; /* 기본 날짜 텍스트 검정색 */
  }

  /* 오늘 날짜 폰트 컬러 */
  .react-calendar__tile--now {
    background: none;
    abbr {
      color: ${(props) => props.theme.primary_2};
    }
  }

  /* 네비게이션 현재 월 스타일 적용 */
  .react-calendar__tile--hasActive {
    background-color: #76baff;
    abbr {
      color: white;
    }
  }

  /* 일 날짜 간격 */
  .react-calendar__tile {
    padding: 5px 0px 18px;
    position: relative;
  }

  /* 네비게이션 월 스타일 적용 */
  .react-calendar__year-view__months__month {
    flex: 0 0 calc(33.3333% - 10px) !important;
    margin-inline-start: 5px !important;
    margin-inline-end: 5px !important;
    margin-block-end: 10px;
    padding: 20px 6.6667px;
    font-size: 0.9rem;
    font-weight: 600;
    color: gray;
  }

  .react-calendar__tile:enabled:hover {
    background-color: ${(props) => props.theme.primary_2};
    border-radius: 0.3rem;
  }

  /* 선택한 날짜 스타일 적용 */

  .react-calendar__tile:enabled:focus,
  .react-calendar__tile--active {
    background-color: #76baff;
    border-radius: 0.3rem;
  }
`;

export const StyledCalendar = styled(Calendar)``;

/* 오늘 버튼 스타일 */
export const StyledDate = styled.div`
  position: absolute;
  right: 7%;
  top: 6%;
  background-color: yellow;
  color: black;
  width: 18%;
  min-width: fit-content;
  height: 1.5rem;
  text-align: center;
  margin: 0 auto;
  line-height: 1.6rem;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 800;
`;

/* 오늘 날짜에 텍스트 삽입 스타일 */
export const StyledToday = styled.div`
  font-size: x-small;
  color: blue;
  font-weight: 600;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateX(-50%);
`;

export const MoveNext = styled.div`
  font-size: x-small;
  color: blue;
  font-weight: 600;
  position: absolute;
  top: 90%;
  left: 50%;
  width: 70px;
  height: 23px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: black;
  border-radius: 15px;
  background-color: #d4ceff;
  transform: translateX(-50%);
  cursor: pointer;
`;

/* 출석한 날짜에 점 표시 스타일 */
export const StyledDot = styled.div`
  background-color: orange;
  border-radius: 50%;
  width: 0.3rem;
  height: 0.3rem;
  position: absolute;
  top: 60%;
  left: 50%;
  transform: translateX(-50%);
`;

export const StyledWrapper = styled.div`
  display: flex;
  position: absolute;
  top: 20%;
  left: 57.5%;
  flex-direction: column;
  align-items: center;
  color: #5e5e5e;
`;

export const StyledLabel = styled.label`
  display: flex;
  align-items: center;
  font-size: 12px;
  font-weight: 500;
  color: #6d6d6d; /* 텍스트 색상 */
`;

export const StyledCheckbox = styled.input`
  appearance: none;
  width: 14px;
  height: 14px;
  border: 2px solid #a084ca; /* 체크박스 외곽선 */
  border-radius: 2px;
  margin-right: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  &:checked {
    background-color: #a084ca; /* 체크된 색상 */
    border: none;

    &::after {
      content: "✔";
      color: white;
      font-size: 0.7rem;
    }
  }
`;

export const StyledUnderline = styled.div`
  margin-top: 2px;
  width: 100%;
  max-width: 200px;
  border-bottom: 1px solid #bdbdbd; /* 밑줄 색상 */
`;
