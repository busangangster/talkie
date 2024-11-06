import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";
import { WeeklyConversation } from "../type";

interface TalkCntProps {
  data: WeeklyConversation[];
}

const TalkCnt: React.FC<TalkCntProps> = ({ data }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);
      const formattedData = data.map((day) => day.conversationCount);

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "line" },
          formatter: "{b}: {c}",
        },
        xAxis: {
          type: "category",
          data: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
          axisLabel: {
            fontSize: 16, 
          },
        },
        yAxis: { type: "value" },
        series: [{ data: formattedData, type: "line" }],
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
