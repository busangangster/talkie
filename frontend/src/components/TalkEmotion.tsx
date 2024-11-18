import React, { useMemo } from "react";
import {
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import { DataItem, TalkEmotionProps } from "../type"

const TalkEmotion: React.FC<TalkEmotionProps> = ({
  happyScore,
  loveScore,
  sadScore,
  scaryScore,
  angryScore,
  amazingScore,
}) => {
  const data: DataItem[] = useMemo(
    () => [
      { subject: "사랑스러움", score: loveScore, fullMark: 150 },
      { subject: "기쁨", score: happyScore, fullMark: 150 },
      { subject: "슬픔", score: sadScore, fullMark: 150 },
      { subject: "두려움", score: scaryScore, fullMark: 150 },
      { subject: "화남", score: angryScore, fullMark: 150 },
      { subject: "놀람", score: amazingScore, fullMark: 150 },
    ],
    [happyScore, loveScore, sadScore, scaryScore, angryScore, amazingScore]
  );

  return (
    <div style={{ width: "100%", height: "100%" }}>
      <ResponsiveContainer width="100%" height={350}>
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
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
  );
};

export default TalkEmotion;
