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
// 어휘력

type ValueType = number | string | Array<number | string>;
type NameType = string;

const data = [
  {
    name: "Mon",
    단어수: 4,
  },
  {
    name: "Tue",
    단어수: 1,
  },
  {
    name: "Wen",
    단어수: 7,
  },
  {
    name: "Thu",
    단어수: 6,
  },
  {
    name: "Fri",
    단어수: 2,
  },
  {
    name: "Sat",
    단어수: 10,
  },
  {
    name: "Sun",
    단어수: 3,
  },
];

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

const WeekVoca: React.FC = () => {
  return (
    <div style={{ width: "800px", height: "600px", paddingTop: "100px" }}>
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart
          data={data}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis domain={[0, 10]} />
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
