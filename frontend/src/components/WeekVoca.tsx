import React from "react";
import {
  ComposedChart,
  Line,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  TooltipProps,
} from "recharts";
import { WeekProps } from "../type";

type ValueType = number | string | Array<number | string>;
type NameType = string;

const CustomTooltip: React.FC<TooltipProps<ValueType, NameType>> = ({
  active,
  payload,
  label,
}) => {
  if (active && payload && payload.length) {
    return (
      <div
        style={{
          backgroundColor: "#fff",
          border: "1px solid #ccc",
          padding: "10px",
          borderRadius: "8px",
        }}
      >
        <p style={{ margin: 0, fontWeight: "bold", color: "#000" }}>{label}</p>
        <p
          style={{
            margin: 0,
            color: "blue",
            fontWeight: 500,
          }}
        >{`단어수: ${payload[0].value}`}</p>
      </div>
    );
  }
  return null;
};

const WeekVoca: React.FC<WeekProps> = ({ data }) => {
  const weekDays = ["Sun", "Mon", "Tue", "Wen", "Thr", "Fri", "Sat"];

  const formattedData = weekDays.map((day, index) => {
    const dailyData = data.find(
      (entry) => new Date(entry.createdAt).getDay() === index
    );
    return {
      name: day,
      단어수: dailyData ? dailyData.vocabularyScore : 0,
    };
  });

  return (
    <div style={{ width: "800px", height: "480px", paddingTop: "30px" }}>
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart
          data={formattedData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis domain={[0, 8]} />
          <Tooltip content={<CustomTooltip />} />
          <Bar dataKey="단어수" barSize={20} fill="#82ca9d" />
          <Line
            type="monotone"
            dataKey="단어수"
            strokeWidth={3}
            stroke="#8884d8"
          />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );
};

export default WeekVoca;
