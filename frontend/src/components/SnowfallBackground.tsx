import React, { useEffect, useRef } from "react";
import * as THREE from "three";
import { useSnowfall } from "../hooks/useSnowfall";

const SnowfallBackground: React.FC = () => {
  const mountRef = useRef<HTMLDivElement>(null);
  const isSnowing = useSnowfall((state) => state.isSnowing);
  // const animationId = useRef<number | null>(null);

  useEffect(() => {
    if (!isSnowing) return;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(
      75,
      window.innerWidth / window.innerHeight,
      0.1,
      1000
    );
    camera.position.z = 5;

    // 렌더러 설정 변경: high-performance 옵션 추가
    const renderer = new THREE.WebGLRenderer({
      alpha: true,
      powerPreference: "high-performance",
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    mountRef.current?.appendChild(renderer.domElement);

    const particlesCount = 1000;
    const particlesGeometry = new THREE.BufferGeometry();
    const particlesPosition = new Float32Array(particlesCount * 3);
    for (let i = 0; i < particlesCount * 3; i++) {
      particlesPosition[i] = (Math.random() - 0.5) * 20;
    }

    particlesGeometry.setAttribute(
      "position",
      new THREE.BufferAttribute(particlesPosition, 3)
    );

    const particlesMaterial = new THREE.PointsMaterial({
      color: 0xffffff,
      size: 0.1,
      transparent: true,
      opacity: 0.5,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
    });

    const sprite = new THREE.TextureLoader().load(
      "https://threejs.org/examples/textures/sprites/disc.png"
    );
    particlesMaterial.map = sprite;
    particlesMaterial.alphaTest = 0.5;

    const particlesMesh = new THREE.Points(
      particlesGeometry,
      particlesMaterial
    );
    scene.add(particlesMesh);

    // setInterval로 애니메이션 루프를 제어
    const intervalId = setInterval(() => {
      particlesMesh.rotation.y += 0.001;
      for (let i = 0; i < particlesCount * 3; i += 3) {
        particlesPosition[i + 1] -= 0.02;
        if (particlesPosition[i + 1] < -10) {
          particlesPosition[i + 1] = 10;
        }
      }
      particlesGeometry.attributes.position.needsUpdate = true;
      renderer.render(scene, camera);
    }, 16); // 약 60fps를 맞추기 위한 16ms

    const handleResize = () => {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    };
    window.addEventListener("resize", handleResize);

    return () => {
      clearInterval(intervalId); // setInterval 해제
      window.removeEventListener("resize", handleResize);
      renderer.dispose();
      particlesGeometry.dispose();
      particlesMaterial.dispose();
      mountRef.current?.removeChild(renderer.domElement);
    };
  }, [isSnowing]);

  return isSnowing ? (
    <div ref={mountRef} className="fixed inset-0 -z-10" />
  ) : null;
};

export default SnowfallBackground;
