import React from "react";

type ImageProps = React.ImgHTMLAttributes<HTMLImageElement>;

const Image = (props: ImageProps) => {
  const { src, ...rest } = props;

  if (!src) {
    console.error("이미지 src 속성이 필요합니다.");
    return null;
  }

  // 외부 이미지 URL 확인
  let isExternalImage;
  try {
    const url = new URL(src);
    isExternalImage = url.origin !== window.location.origin;
  } catch (e) {
    if (e instanceof Error) {
      console.error("유효하지 않은 URL입니다:", e.message);
    } else {
      console.error("유효하지 않은 URL입니다:", e);
    }
    isExternalImage = false;
  }

  // 개발 환경과 프로덕션 환경에 따라 확장자 설정
  const imgUrl =
    import.meta.env.MODE === "development" || isExternalImage
      ? src // 개발 환경 또는 외부 이미지는 확장자 그대로 사용
      : `${src}.webp`; // 프로덕션 환경에서는 WebP 사용

  // 최종 이미지 경로 생성
  const imgSrc = isExternalImage
    ? imgUrl
    : new URL(imgUrl, import.meta.url).href;

  return <img src={imgSrc} {...rest} />;
};

export default Image;
