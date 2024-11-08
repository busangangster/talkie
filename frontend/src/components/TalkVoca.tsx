import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";
import useUserStore from "../store/useUserStore";

interface TalkVocaProps {
  vocabularyScore: number | undefined;
}

const TalkVoca: React.FC<TalkVocaProps> = ({ vocabularyScore }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);
  const { name, birth } = useUserStore();

  // 한국 나이를 계산하는 함수
  const calculateKoreanAge = (birth: string) => {
    const birthYear = new Date(birth).getFullYear();
    const currentYear = new Date().getFullYear();
    return currentYear - birthYear + 1;
  };

  const age = calculateKoreanAge(birth);

  const getAverageScoreByAge = (age: number) => {
    switch (age) {
      case 4:
        return 4;
      case 5:
        return 5;
      case 6:
        return 6;
      default:
        return 5;
    }
  };

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);
      const averageScore = getAverageScoreByAge(age);

      const option: echarts.EChartsOption = {
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "none" },
          formatter: (params) => {
            const param = Array.isArray(params) ? params[0] : params;
            const name = param?.name ?? "Unknown";
            const value = Array.isArray(param?.value)
              ? param.value[0]
              : param?.value;
            return `${name}: ${value ?? "N/A"}점`;
          },
        },
        dataset: {
          source: [
            ["word", "person"],
            [vocabularyScore || 0, name],
            [averageScore, "평균"],
          ],
        },
        grid: { containLabel: true },
        xAxis: {
          name: "word",
          nameLocation: "middle",
          nameGap: 30,
          min: 0,
          max: 10,
        },
        yAxis: { type: "category", inverse: true },
        series: [
          {
            type: "bar",
            encode: { x: "word", y: "person" },
            itemStyle: {
              color: (params) =>
                params.dataIndex === 0 ? "#D2ADE5" : "#948FCF",
            },
          },
        ],
      };

      chartInstance.setOption(option);

      return () => {
        chartInstance.dispose();
      };
    }
  }, [vocabularyScore, age, name]);

  return <div ref={chartRef} style={{ width: "100%", height: "100%" }} />;
};

export default TalkVoca;
