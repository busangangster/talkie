import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import viteImagemin from "vite-plugin-imagemin";

export default defineConfig({
  plugins: [
    react(),
    viteImagemin({
      // 최신 버전에서 지원하는 옵션 사용
      gifsicle: {
        optimizationLevel: 7,
      },
      optipng: {
        optimizationLevel: 7,
      },
      pngquant: {
        quality: [0.6, 0.8],
      },
      svgo: {
        plugins: [{ name: "removeViewBox" }],
      },
      webp: {
        quality: 80,
      },
    }),
  ],
});
