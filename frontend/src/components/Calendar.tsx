// Calendar.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
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
import { motion } from "framer-motion"; // **Framer Motion import 추가**

type ValuePiece = Date | null;
type Value = ValuePiece | [ValuePiece, ValuePiece];

interface CalendarProps {
  onClose: () => void;
}

const Calendar: React.FC<CalendarProps> = ({ onClose }) => {
  const today = new Date();
  const navigate = useNavigate();
  const [date, setDate] = useState<Value>(today);
  const [activeStartDate, setActiveStartDate] = useState<Date | null>(today);
  const [selectedWeek, setSelectedWeek] = useState<Date[]>([]);
  const [checked, setChecked] = useState(false);

  const attendDay = ["2024-10-25", "2024-10-10"];

  const handleDateChange = (newDate: Value) => {
    setDate(newDate);
    setSelectedWeek([]);
  };

  const handleCheckboxChange = () => {
    setChecked(!checked);

    if (!checked && date && !Array.isArray(date)) {
      selectWeek(date);
    } else {
      setSelectedWeek([]);
    }
  };

  const selectWeek = (clickedDate: Date) => {
    const startOfWeek = moment(clickedDate).startOf("week").toDate();
    const endOfWeek = moment(clickedDate).endOf("week").toDate();

    const weekDates: Date[] = [];
    const currentDate = new Date(startOfWeek);

    while (currentDate <= endOfWeek) {
      weekDates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }

    setSelectedWeek(weekDates);
  };

  const handleMoveNext = () => {
    if (Array.isArray(date) || !date) return;

    if (checked && selectedWeek.length > 0) {
      const startOfWeek = moment(selectedWeek[0]).format("YYYY-MM-DD");
      navigate(`/week/${startOfWeek}`);
    } else {
      const selectedDate = moment(date).format("YYYY-MM-DD");
      navigate(`/day/${selectedDate}`);
    }

    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[100]">
      {/* 배경을 클릭하면 모달이 닫히도록 설정 */}
      <div className="absolute inset-0" onClick={onClose}></div>

      {/* 애니메이션이 적용된 달력 컴포넌트 */}
      <motion.div
        className="relative rounded-lg shadow-lg p-4 z-[101]"
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.8 }}
        transition={{ duration: 0.2 }}
      >
        <StyledCalendarWrapper>
          {/* 기존 달력 내용 */}
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

              if (
                view === "month" &&
                date.getMonth() === today.getMonth() &&
                date.getDate() === today.getDate()
              ) {
                html.push(<StyledToday key={"today"}>오늘</StyledToday>);
              }

              if (attendDay.includes(moment(date).format("YYYY-MM-DD"))) {
                html.push(
                  <StyledDot key={moment(date).format("YYYY-MM-DD")} />
                );
              }

              return <>{html}</>;
            }}
            tileClassName={({ date, view }) => {
              if (view === "month") {
                const currentMonth = activeStartDate?.getMonth();
                const currentYear = activeStartDate?.getFullYear();

                if (selectedWeek.some((d) => moment(d).isSame(date, "day"))) {
                  return "highlight-week";
                }

                if (
                  date.getMonth() !== currentMonth ||
                  date.getFullYear() !== currentYear
                ) {
                  return "neighboring-month";
                }
                if (date.getDay() === 0) {
                  return "sunday";
                }
              }
              return "";
            }}
            maxDate={new Date()} // 오늘 이후의 날짜는 선택 불가
          />
          <MoveNext onClick={handleMoveNext}>이동하기</MoveNext>
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
      </motion.div>
    </div>
  );
};

export default Calendar;
