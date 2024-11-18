import React, { useMemo } from "react";
import WordCloud from "react-d3-cloud";
import { WordCloudComponentProps } from "../type";

const WordCloudComponent: React.FC<WordCloudComponentProps> = ({ words }) => {
  const formattedWords = words.map((word) => ({
    text: word.word,
    value: word.count,
  }));

  const fontSize = (word: { value: number }) => {
    const minSize = 50;
    const maxSize = 150;
    return Math.min((Math.pow(word.value * 0.5, 1.5), minSize), maxSize);
  };

  const rotate = () => 0;

  const wordCloudComponent = useMemo(() => {
    const colors = ["#507ECE", "#5054CE", "#9254C2", "#8E77D9", "#507ECE"];
    return (
      <WordCloud
        data={formattedWords}
        font={() => "SuncheonR"} // 폰트를 직접 지정
        fontSize={fontSize}
        rotate={rotate}
        padding={20}
        fill={() => colors[Math.floor(Math.random() * colors.length)]}
        width={500}
        height={300}
      />
    );
  }, [formattedWords]);

  return (
    <div className="mt-4" style={{ fontFamily: "SuncheonR, sans-serif" }}>
      <div className="bg-white p-2 rounded">{wordCloudComponent}</div>
    </div>
  );
};

export default React.memo(WordCloudComponent);
