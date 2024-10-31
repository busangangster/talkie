import React, { useState } from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";


type LineKeys = "기쁨" | "놀람" | "사랑스러움" | "화남" | "슬픔" | "두려움";

const data = [
  {
    name: "Mon",
    기쁨: 11,
    놀라움: 19,
    사랑스러움: 73,
    화남: 95,
    슬픔: 8,
    두려움: 95,
  },
  {
    name: "Tue",
    기쁨: 33,
    놀라움: 4,
    사랑스러움: 82,
    화남: 16,
    슬픔: 79,
    두려움: 97,
  },
  {
    name: "Wen",
    기쁨: 38,
    놀라움: 54,
    사랑스러움: 61,
    화남: 67,
    슬픔: 0,
    두려움: 44,
  },
  {
    name: "Thu",
    기쁨: 46,
    놀라움: 49,
    사랑스러움: 6,
    화남: 21,
    슬픔: 20,
    두려움: 31,
  },
  {
    name: "Fri",
    기쁨: 93,
    놀라움: 65,
    사랑스러움: 72,
    화남: 60,
    슬픔: 54,
    두려움: 48,
  },
  {
    name: "Sat",
    기쁨: 6,
    놀라움: 53,
    사랑스러움: 12,
    화남: 89,
    슬픔: 25,
    두려움: 97,
  },
  {
    name: "Sun",
    기쁨: 89,
    놀라움: 37,
    사랑스러움: 8,
    화남: 85,
    슬픔: 72,
    두려움: 33,
  },
];

const WeekEmotion: React.FC = () => {
  const [lines, setLines] = useState<Record<LineKeys, boolean>>({
    기쁨: true,
    놀람: true,
    사랑스러움: true,
    화남: true,
    슬픔: true,
    두려움: true,
  });

  const handleToggle = (lineKey: LineKeys) => {
    setLines((prevLines) => ({
      ...prevLines,
      [lineKey]: !prevLines[lineKey],
    }));
  }

  return (
    <div style={{ width: "800px", height: "600px", paddingTop: "100px" }}>
      <div>
        <label>
          <input
            type="checkbox"
            checked={lines.기쁨}
            onChange={() => handleToggle("기쁨")}
          />
          기쁨
        </label>
        <label>
          <input
            type="checkbox"
            checked={lines.놀람}
            onChange={() => handleToggle("놀람")}
          />
          놀람
        </label>
        <label>
          <input
            type="checkbox"
            checked={lines.사랑스러움}
            onChange={() => handleToggle("사랑스러움")}
          />
          사랑스러움
        </label>
        <label>
          <input
            type="checkbox"
            checked={lines.화남}
            onChange={() => handleToggle("화남")}
          />
          화남
        </label>
        <label>
          <input
            type="checkbox"
            checked={lines.슬픔}
            onChange={() => handleToggle("슬픔")}
          />
          슬픔
        </label>
        <label>
          <input
            type="checkbox"
            checked={lines.두려움}
            onChange={() => handleToggle("두려움")}
          />
          두려움
        </label>
      </div>

      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}
        margin={{top: 5, right: 30, left: 20, bottom: 5}}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis domain={[0, 100]} />
          <Tooltip />
          <Legend />
          {lines.기쁨 && (
            <Line
              type="monotone"
              dataKey="기쁨"
              strokeWidth={3}
              stroke="#82ca9d"
              //   activeDot={{ r: 8 }}
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
              stroke="#ffc658"
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
              stroke="#65F0FF"
            />
          )}
          {lines.두려움 && (
            <Line
              type="monotone"
              dataKey="두려움"
              strokeWidth={3}
              stroke="#FEAE24"
            />
          )}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default WeekEmotion;