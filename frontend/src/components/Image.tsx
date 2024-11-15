import React from "react";

type ImageProps = React.ImgHTMLAttributes<HTMLImageElement>;

const Image: React.FC<ImageProps> = (props) => {
  const { src, ...rest } = props;

  if (!src) {
    console.error("이미지 src 속성이 필요합니다.");
    return null;
  }

  // 외부 이미지 URL인지 확인하는 함수
  const isExternalImage =
    src.startsWith("https") || src.startsWith("http") || src.startsWith("blob");

  // 개발 환경과 프로덕션 환경을 구분하여 이미지 URL 설정
  const imgUrl =
    import.meta.env.MODE === "development"
      ? `${src}.png` // 개발 환경에서는 PNG 확장자를 사용
      : isExternalImage
      ? src // 외부 이미지는 변환하지 않음
      : `${src}.webp`; // 프로덕션에서는 WebP 확장자 사용

  // 이미지 경로를 최종적으로 생성
  const imgSrc = new URL(imgUrl, import.meta.url).href;

  return <img src={imgSrc} {...rest} />;
};

export default Image;
