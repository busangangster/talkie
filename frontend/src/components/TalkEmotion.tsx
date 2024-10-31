import React from "react";
import {
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  ResponsiveContainer,
  Tooltip,
} from "recharts";

interface DataItem {
  subject: string;
  score: number;
  fullMark: number;
}

const data: DataItem[] = [
  { subject: "기쁨", score: 120, fullMark: 150 },
  { subject: "놀람", score: 98, fullMark: 150 },
  { subject: "화남", score: 86, fullMark: 150 },
  { subject: "사랑스러움", score: 99, fullMark: 150 },
  { subject: "두려움", score: 85, fullMark: 150 },
  { subject: "슬픔", score: 65, fullMark: 150 },
];
const TalkEmotion: React.FC = () => {
return (
  <div style={{ width: "600px", height: "350px" }}>
    <ResponsiveContainer width="100%" height="100%">
      <RadarChart cx="250" cy="180" outerRadius="80%" data={data}>
        <PolarGrid stroke="#000" strokeWidth={2} />
        <PolarAngleAxis dataKey="subject" tickSize={20} />
        <PolarRadiusAxis tick={false} axisLine={false} />
        <Tooltip />
        <Radar
          name="감정 레벨"
          dataKey="score"
          stroke="#8884d8"
          fill="#8884d8"
          fillOpacity={0.6}
        />
      </RadarChart>
    </ResponsiveContainer>
  </div>
);};

export default TalkEmotion;
