import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import moment from "moment";
import {
  StyledCalendar,
  StyledCalendarWrapper,
  StyledToday,
  MoveNext,
  StyledWrapper,
  StyledLabel,
  StyledCheckbox,
  StyledUnderline,
} from "../style";
import { motion } from "framer-motion";

type ValuePiece = Date | null;
type Value = ValuePiece | [ValuePiece, ValuePiece];

interface CalendarProps {
  onClose: () => void;
}

const Calendar: React.FC<CalendarProps> = ({ onClose }) => {
  const today = new Date();
  const navigate = useNavigate();
  const location = useLocation();

  const [date, setDate] = useState<Value>(today);
const [activeStartDate, setActiveStartDate] = useState<Date | null>(today);
  const [selectedWeek, setSelectedWeek] = useState<Date[]>([]);
  const [checked, setChecked] = useState(false);
  const [currentView, setCurrentView] = useState("month");

  const extractDateFromUrl = (): Date => {
    const path = location.pathname;
    const regex = /\/(day|week)\/(\d{4}-\d{2}-\d{2})/;
    const match = path.match(regex);

    if (match) {
      return new Date(match[2]);
    }
    return today;
  };

  // 주간 선택 모드에서 URL의 날짜를 기준으로 주간 날짜 배열 설정
  const selectWeekFromUrl = () => {
    const dateFromUrl = extractDateFromUrl();
    const startOfWeek = moment(dateFromUrl).startOf("week").toDate();
    const endOfWeek = moment(dateFromUrl).endOf("week").toDate();

    const weekDates: Date[] = [];
    const currentDate = new Date(startOfWeek);

    while (currentDate <= endOfWeek) {
      weekDates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }

    setSelectedWeek(weekDates);
    setDate(dateFromUrl);
    setActiveStartDate(startOfWeek); // 선택된 주의 시작 날짜로 이동
  };

  // URL에서 가져온 날짜로 초기 설정
  useEffect(() => {
    const path = location.pathname;
    const dateFromUrl = extractDateFromUrl();

    if (path.startsWith("/week")) {
      selectWeekFromUrl();
      setChecked(true);
    } else {
      setDate(dateFromUrl);
      setSelectedWeek([]);
      setChecked(false);
    }

    // 선택된 날짜의 월로 달력 이동
    if (!moment(activeStartDate).isSame(dateFromUrl, "month")) {
      setActiveStartDate(moment(dateFromUrl).startOf("month").toDate());
    }
  }, [location]);

  const handleDateChange = (newDate: Value) => {
    setDate(newDate);
    setSelectedWeek([]);
    setChecked(false);
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
      <div className="absolute inset-0" onClick={onClose}></div>

      <motion.div
        className="relative rounded-lg shadow-lg p-4 z-[101]"
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.8 }}
        transition={{ duration: 0.2 }}
      >
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
            onActiveStartDateChange={({ activeStartDate, view }) => {
              setActiveStartDate(activeStartDate);
              setCurrentView(view);
            }}
            nextLabel={
              (currentView === "month" &&
                activeStartDate &&
                moment(activeStartDate).isSameOrAfter(moment(), "month")) ||
              (currentView === "year" &&
                activeStartDate &&
                activeStartDate.getFullYear() >= new Date().getFullYear())
                ? null
                : ">"
            }
            prevLabel={"<"}
            tileContent={({ date, view }) => {
              const html = [];

              if (
                view === "month" &&
                date.getMonth() === today.getMonth() &&
                date.getDate() === today.getDate() &&
                date.getFullYear() === today.getFullYear()
              ) {
                html.push(<StyledToday key={"today"}>오늘</StyledToday>);
              }

              return <>{html}</>;
            }}
            tileClassName={({ date, view }) => {
              if (view === "month") {
                const currentMonth = activeStartDate?.getMonth();
                const currentYear = activeStartDate?.getFullYear();

                if (selectedWeek.some((d) => moment(d).isSame(date, "day"))) {
                  if (
                    date.getMonth() !== currentMonth ||
                    date.getFullYear() !== currentYear
                  ) {
                    return "highlight-week";
                  }
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
            maxDate={new Date()}
          />

          {currentView === "month" && (
            <>
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
            </>
          )}
        </StyledCalendarWrapper>
      </motion.div>
    </div>
  );
};

export default Calendar;
