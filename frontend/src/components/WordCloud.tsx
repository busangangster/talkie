import React, { useMemo, useEffect, useState } from "react";
import WordCloud from "react-d3-cloud";

const WordCloudComponent: React.FC = () => {
  const [words, setWords] = useState<{ text: string; value: number }[]>([]);

  useEffect(() => {
    // 목업 데이터 설정
    const mockData = [
      { text: "엄마", value: 130 },
      { text: "고구마", value: 180 },
      { text: "토끼인형", value: 200 },
      { text: "겨울", value: 60 },
      { text: "티니핑", value: 100 },
    ];
    setWords(mockData);
  }, []);

  const fontSize = (word: { value: number }) => {
    const minSize = 30;
    const maxSize = 100;
    return Math.min(Math.max(word.value * 0.3, minSize), maxSize);
  };

  const rotate = () => 0;

  const wordCloudComponent = useMemo(() => {
    const colors = ["#507ECE", "#5054CE", "#9254C2", "#8E77D9", "#507ECE"];
    return (
      <WordCloud
        data={words}
        font={() => "NanumGothic"}
        fontSize={fontSize}
        rotate={rotate}
        padding={20}
        fill={() => colors[Math.floor(Math.random() * colors.length)]}
        width={500}
        height={350}
      />
    );
  }, [words]);

  return (
    <div className="mt-4">
      <div className="bg-white p-2 rounded">{wordCloudComponent}</div>
    </div>
  );
};

export default React.memo(WordCloudComponent);
