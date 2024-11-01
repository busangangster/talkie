import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";

// 대화빈도
const TalkCnt: React.FC = () => {
const chartRef = useRef<HTMLDivElement | null>(null);

useEffect(() => {
  if (chartRef.current) {
    // 차트 인스턴스가 없을 때만 초기화
    const chartInstance = echarts.init(chartRef.current);

    const option = {
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "line",
        },
        formatter: "{b}: {c}",
      },
      xAxis: {
        type: "category",
        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: [150, 230, 224, 218, 135, 147, 260],
          type: "line",
        },
      ],
    };

    chartInstance.setOption(option);

    return () => {
      chartInstance.dispose();
    };
  }
}, []);

return (
  <div
    ref={chartRef}
    style={{
      width: "700px",
      height: "500px",
    }}
  />
);};

export default TalkCnt;
