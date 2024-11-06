import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";
import { WordCloudResponse } from "../type";

interface WeekInterestProps {
  data: WordCloudResponse[];
}

const WeekInterest: React.FC<WeekInterestProps> = ({ data }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);
      const totalCount = data.reduce((sum, item) => sum + item.count, 0);
      const formattedData = data.map((item) => ({
        value: ((item.count / totalCount) * 100).toFixed(2),
        name: item.word,
      }));

      const option = {
        tooltip: {
          trigger: "item",
          textStyle: {
            fontSize: 16, 
          },
        },
        legend: { orient: "horizontal", selectedMode: false, bottom: "70px" },
        series: [
          {
            name: "관심사",
            type: "pie",
            radius: "50%",
            data: formattedData,
            label: {
              fontSize: 20, 
              formatter: "{b}"
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
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

  return (
    <div
      ref={chartRef}
      style={{ margin: "0 auto", width: "700px", height: "600px" }}
    />
  );
};

export default WeekInterest;
