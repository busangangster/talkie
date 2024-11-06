import React, { useEffect, useRef, useState } from "react";
import * as echarts from "echarts";
import { getUserInfo } from "../apis/api";
import useUserStore from "../store/useUserStore";

interface TalkVocaProps {
  vocabularyScore: number | undefined;
}

const TalkVoca: React.FC<TalkVocaProps> = ({ vocabularyScore }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);
  const { userSeq } = useUserStore();
  const [userInfo, setUserInfo] = useState<{
    name: string;
    age: number;
  } | null>(null);

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
    const fetchUserInfo = async () => {
      if (userSeq) {
        try {
          const response = await getUserInfo(userSeq);
          setUserInfo({ name: response.name, age: response.age });
        } catch (error) {
          console.error("유저 정보를 가져오는 중 오류 발생:", error);
        }
      }
    };

    fetchUserInfo();
  }, [userSeq]);

  useEffect(() => {
    if (chartRef.current && userInfo) {
      const chartInstance = echarts.init(chartRef.current);
      const averageScore = getAverageScoreByAge(userInfo.age);

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
            [vocabularyScore || 0, userInfo.name],
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
  }, [vocabularyScore, userInfo]);

  return <div ref={chartRef} style={{ width: "100%", height: "100%" }} />;
};

export default TalkVoca;
