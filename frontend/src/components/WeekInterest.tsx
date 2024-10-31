import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";
// 관심사
const WeekInterest:React.FC = () => {
    const chartRef = useRef<HTMLDivElement | null>(null);
    useEffect(() => {
      // chartRef가 DOM 요소를 참조할 때만 실행
      if (chartRef.current) {
        const chartInstance = echarts.init(chartRef.current);

        const option = {
          tooltip: {
            trigger: "item",
          },
          legend: {
            orient: "horizontal",
            bottom: "70px",
          },
          series: [
            {
              name: "Access From",
              type: "pie",
              radius: "50%",
              data: [
                { value: 1048, name: "Search Engine" },
                { value: 735, name: "Direct" },
                { value: 580, name: "Email" },
                { value: 484, name: "Union Ads" },
                { value: 300, name: "Video Ads" },
              ],
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

        // 옵션을 설정하여 차트를 렌더링합니다.
        chartInstance.setOption(option);

        // 컴포넌트가 언마운트될 때 차트를 인스턴스에서 제거합니다.
        return () => {
          chartInstance.dispose();
        };
      }
    }, []);

    // 차트를 렌더링할 div 요소
    return (
      <div
        ref={chartRef}
        style={{ margin: "0 auto", width: "700px", height: "600px" }}
      />
    );
};

export default WeekInterest;
