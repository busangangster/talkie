import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";

interface TalkVocaProps {
  vocabularyScore: number | undefined;
}

const TalkVoca: React.FC<TalkVocaProps> = ({ vocabularyScore }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);

      const option: echarts.EChartsOption = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "none",
          },
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
            [vocabularyScore || 0, "재찬"],
            [4, "평균"], // 예시 평균 값, 필요에 따라 변경 가능
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
            encode: {
              x: "word",
              y: "person",
            },
          },
        ],
      };

      chartInstance.setOption(option);

      return () => {
        chartInstance.dispose();
      };
    }
  }, [vocabularyScore]);

  return <div ref={chartRef} style={{ width: "100%", height: "100%" }} />;
};

export default TalkVoca;
