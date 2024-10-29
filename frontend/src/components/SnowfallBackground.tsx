import React, { useEffect, useRef } from "react";
import * as THREE from "three";
import { useSnowfall } from "../hooks/useSnowfall";

const SnowfallBackground: React.FC = () => {
  const mountRef = useRef<HTMLDivElement>(null);
  const isSnowing = useSnowfall((state) => state.isSnowing); // 눈 내림 상태 가져오기

  useEffect(() => {
    if (!isSnowing) return; // 눈 내림이 비활성화된 경우 렌더링하지 않음

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(
      75,
      window.innerWidth / window.innerHeight,
      0.1,
      1000
    );
    camera.position.z = 5;

    const renderer = new THREE.WebGLRenderer({ alpha: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    mountRef.current?.appendChild(renderer.domElement);

    // 눈 입자 설정
    const particlesCount = 1000;
    const particlesGeometry = new THREE.BufferGeometry();

    // 파티클 위치 랜덤 설정
    const particlesPosition = new Float32Array(particlesCount * 3);
    for (let i = 0; i < particlesCount * 3; i++) {
      particlesPosition[i] = (Math.random() - 0.5) * 20;
    }

    particlesGeometry.setAttribute(
      "position",
      new THREE.BufferAttribute(particlesPosition, 3)
    );

    // 원형 입자를 위한 머티리얼 설정
    const particlesMaterial = new THREE.PointsMaterial({
      color: 0xffffff,
      size: 0.1, // 입자 크기 조정
      transparent: true,
      opacity: 0.5,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
    });

    // 원형 입자 표현을 위한 알파 맵 설정
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

    // 애니메이션 함수
    const animate = () => {
      particlesMesh.rotation.y += 0.001;
      for (let i = 0; i < particlesCount * 3; i += 3) {
        particlesPosition[i + 1] -= 0.02; // Y 방향으로 떨어지는 속도

        if (particlesPosition[i + 1] < -10) {
          particlesPosition[i + 1] = 10;
        }
      }
      particlesGeometry.attributes.position.needsUpdate = true;
      renderer.render(scene, camera);
      requestAnimationFrame(animate);
    };

    animate();

    const handleResize = () => {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    };
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
      renderer.dispose();
      particlesGeometry.dispose();
      particlesMaterial.dispose();
      mountRef.current?.removeChild(renderer.domElement);
    };
  }, [isSnowing]); // isSnowing 상태가 변경될 때만 실행

  return isSnowing ? (
    <div ref={mountRef} className="fixed inset-0 -z-10" />
  ) : null;
};

export default SnowfallBackground;
