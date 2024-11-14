import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";
import { WeekProps } from "../type";

const TalkCnt: React.FC<WeekProps> = ({ data }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);

      // 기본적으로 모든 요일을 포함한 배열을 생성
      const weekDays = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
      const formattedData = weekDays.map((_, index) => {
        const dayData = data.find(
          (entry) => new Date(entry.createdAt).getDay() === index
        );
        return dayData ? dayData.conversationCount : 0;
      });

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "line" },
          formatter: "{b}: {c}",
        },
        xAxis: {
          type: "category",
          data: weekDays,
          axisLabel: {
            fontSize: 16,
          },
        },
        yAxis: {
          type: "value",
          minInterval: 1,
        },
        series: [
          {
            data: formattedData,
            type: "line",
            // smooth: true,
            lineStyle: {
              width: 3,
            },
          },
        ],
      };

      chartInstance.setOption(option);

      return () => {
        chartInstance.dispose();
      };
    }
  }, [data]);

  return <div ref={chartRef} style={{ width: "100%", height: "100%" }} />;
};

export default TalkCnt;
