import React, { useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { WeekProps } from "../type";

type LineKeys = "기쁨" | "놀람" | "사랑스러움" | "화남" | "슬픔" | "두려움";

const WeekEmotion: React.FC<WeekProps> = ({ data }) => {
  const [lines, setLines] = useState<Record<LineKeys, boolean>>({
    기쁨: true,
    놀람: false,
    사랑스러움: false,
    화남: false,
    슬픔: false,
    두려움: false,
  });

  const [isAllSelected, setIsAllSelected] = useState(false);

  const handleToggleAll = () => {
    const newSelection = !isAllSelected;
    const newLines = Object.keys(lines).reduce((acc, key) => {
      acc[key as LineKeys] = newSelection;
      return acc;
    }, {} as Record<LineKeys, boolean>);
    setLines(newLines);
    setIsAllSelected(newSelection);
  };

  const handleToggle = (lineKey: LineKeys) => {
    const updatedLines = { ...lines, [lineKey]: !lines[lineKey] };
    setLines(updatedLines);
    setIsAllSelected(Object.values(updatedLines).every(Boolean));
  };

  const weekDays = ["Sun", "Mon", "Tue", "Wen", "Thr", "Fri", "Sat"];

  const formattedData = weekDays.map((day, index) => ({
    name: day,
    기쁨: data[index]?.happyScore || 0,
    놀람: data[index]?.amazingScore || 0,
    사랑스러움: data[index]?.loveScore || 0,
    화남: data[index]?.angryScore || 0,
    슬픔: data[index]?.sadScore || 0,
    두려움: data[index]?.scaryScore || 0,
  }));

  const maxEmotionValue = Math.max(
    ...formattedData.flatMap((day) => [
      day.기쁨,
      day.놀람,
      day.사랑스러움,
      day.화남,
      day.슬픔,
      day.두려움,
    ])
  );

  return (
    <div
      style={{
        width: "100%",
        height: "100%",
        paddingTop: "0px",
        paddingBottom: "20px",
      }}
    >
      <div className="flex gap-2 mb-4 items-center justify-between px-8">
        <div className="flex gap-2">
          {Object.keys(lines).map((key) => (
            <label key={key}>
              <input
                type="checkbox"
                checked={lines[key as LineKeys]}
                onChange={() => handleToggle(key as LineKeys)}
              />
              {key}
            </label>
          ))}
        </div>

        <label className="flex items-center gap-1 font-semibold">
          <input
            type="checkbox"
            checked={isAllSelected}
            onChange={handleToggleAll}
          />
          전체
        </label>
      </div>

      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={formattedData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis domain={[0, maxEmotionValue]} />
          <Tooltip />
          <Legend />
          {lines.기쁨 && (
            <Line
              type="monotone"
              dataKey="기쁨"
              strokeWidth={3}
              stroke="#82ca9d"
            />
          )}
          {lines.놀람 && (
            <Line
              type="monotone"
              dataKey="놀람"
              strokeWidth={3}
              stroke="#8884d8"
            />
          )}
          {lines.사랑스러움 && (
            <Line
              type="monotone"
              dataKey="사랑스러움"
              strokeWidth={3}
              stroke="#FF9054"
            />
          )}
          {lines.화남 && (
            <Line
              type="monotone"
              dataKey="화남"
              strokeWidth={3}
              stroke="#FF6464"
            />
          )}
          {lines.슬픔 && (
            <Line
              type="monotone"
              dataKey="슬픔"
              strokeWidth={3}
              stroke="#80CCFF"
            />
          )}
          {lines.두려움 && (
            <Line
              type="monotone"
              dataKey="두려움"
              strokeWidth={3}
              stroke="#ffc658"
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default WeekEmotion;
