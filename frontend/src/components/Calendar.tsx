import React, { useState } from "react";
import moment from "moment";
import {
  StyledCalendar,
  StyledCalendarWrapper,
  StyledDot,
  StyledToday,
  MoveNext,
  StyledWrapper,
  StyledLabel,
  StyledCheckbox,
  StyledUnderline,
  
} from "../style";

type ValuePiece = Date | null;
type Value = ValuePiece | [ValuePiece, ValuePiece];

const Calendar: React.FC = () => {
  const today = new Date();
  const [date, setDate] = useState<Value>(today);
  const [activeStartDate, setActiveStartDate] = useState<Date | null>(today);
  const [selectedWeek, setSelectedWeek] = useState<Date[]>([]); // 선택된 주의 날짜 목록
  const [checked, setChecked] = useState(false); // 체크박스 상태 관리

  const attendDay = ["2024-10-25", "2024-10-10"]; // 출석한 날짜 예시

  const handleDateChange = (newDate: Value) => {
    setDate(newDate);
    setSelectedWeek([]); // 날짜만 클릭 시 주 초기화
  };

  const handleCheckboxChange = () => {
    setChecked(!checked);

    if (!checked && date && !Array.isArray(date)) {
      // 체크박스가 활성화될 때 선택한 날짜의 주를 계산하여 하이라이트
      selectWeek(date);
    } else {
      // 체크박스가 해제되면 주 선택 해제
      setSelectedWeek([]);
    }
  };

  const selectWeek = (clickedDate: Date) => {
    const startOfWeek = moment(clickedDate).startOf("isoWeek").toDate(); // 주의 시작일 (월요일)
    const endOfWeek = moment(clickedDate).endOf("isoWeek").toDate(); // 주의 마지막일 (일요일)

    const weekDates: Date[] = [];
    const currentDate = new Date(startOfWeek);

    while (currentDate <= endOfWeek) {
      weekDates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }

    setSelectedWeek(weekDates); // 주의 날짜들 저장
  };

  // const handleTodayClick = () => {
  //   const today = new Date();
  //   setActiveStartDate(today);
  //   setDate(today);
  // };

  return (
      <StyledCalendarWrapper>
        <StyledCalendar
          value={date}
          onChange={handleDateChange}
          formatDay={(_, date) => moment(date).format("D")}
          formatYear={(_, date) => moment(date).format("YYYY")}
          formatMonthYear={(_, date) => moment(date).format("YYYY. MM")}
          calendarType="gregory"
          showNeighboringMonth={true}
          next2Label={null}
          prev2Label={null}
          minDetail="year"
          activeStartDate={activeStartDate || undefined}
          onActiveStartDateChange={({ activeStartDate }) =>
            setActiveStartDate(activeStartDate)
          }
          tileContent={({ date, view }) => {
            const html = [];

            // 오늘 날짜 표시
            if (
              view === "month" &&
              date.getMonth() === today.getMonth() &&
              date.getDate() === today.getDate()
            ) {
              html.push(<StyledToday key={"today"}>오늘</StyledToday>);
            }

            // 출석한 날짜에 점 표시
            if (attendDay.includes(moment(date).format("YYYY-MM-DD"))) {
              html.push(<StyledDot key={moment(date).format("YYYY-MM-DD")} />);
            }

            return <>{html}</>;
          }}
          // 선택된 주의 날짜에 클래스 부여
          tileClassName={({ date, view }) => {
            if (view === "month") {
              const currentMonth = activeStartDate?.getMonth(); // 현재 렌더링 중인 달
              const currentYear = activeStartDate?.getFullYear(); // 현재 렌더링 중인 연도

              if (selectedWeek.some((d) => moment(d).isSame(date, "day"))) {
                return "highlight-week"; // 선택된 주 강조
              }

              // 현재 렌더링 중인 달에 속하지 않는 날짜만 회색 처리
              if (
                date.getMonth() !== currentMonth ||
                date.getFullYear() !== currentYear
              ) {
                return "neighboring-month"; // 이웃한 달의 날짜들만 회색 처리
              }
              if (date.getDay() === 0) {
                return "sunday"; // 일요일 강조
              }
            }
            return ""; // 조건에 맞지 않으면 클래스 없음
          }}
        />
        <MoveNext>이동하기</MoveNext>
        <StyledWrapper>
        <StyledLabel>
          
            <StyledCheckbox
              type="checkbox"
              checked={checked}
              onChange={handleCheckboxChange}
            />
            <span>주간 통계 보기</span>
          </StyledLabel>
          <StyledUnderline />
        </StyledWrapper>
      </StyledCalendarWrapper>
  );
};

export default Calendar;
