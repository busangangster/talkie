import React, { useEffect, useRef } from "react";
import * as echarts from "echarts";

const TalkVoca: React.FC = () => {
  const chartRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (chartRef.current) {
      const chartInstance = echarts.init(chartRef.current);

      const option: echarts.EChartsOption = {
        tooltip: {
          trigger: "axis", // 축 위에 있는 모든 데이터 표시
          axisPointer: {
            type: "none", // 축을 따라 그어지는 선 비활성화
          },
          formatter: (params) => {
            const param = Array.isArray(params) ? params[0] : params;
            const name = param?.name ?? "Unknown"; // name이 null/undefined인 경우 처리
            const value = Array.isArray(param?.value)
              ? param.value[0]
              : param?.value; // value가 배열인지 검사

            return `${name}: ${value ?? "N/A"}점`; // value가 null/undefined일 경우 "N/A" 표시
          },
        },
        dataset: {
          source: [
            ["word", "person"],
            [4.5, "재찬"],
            [6.7, "평균"],
          ],
        },

        grid: { containLabel: true },
        xAxis: { name: "word", min: 0, max: 10 },
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

      // 옵션 설정
      chartInstance.setOption(option);

      // 컴포넌트 언마운트 시 인스턴스 해제
      return () => {
        chartInstance.dispose();
      };
    }
  }, []); // 빈 배열을 두어 한 번만 실행되도록 설정

  return <div ref={chartRef} style={{ width: "500px", height: "400px" }} />;
};

export default TalkVoca;
