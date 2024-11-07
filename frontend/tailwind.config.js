/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      backgroundPosition: {
        "center-top": "center top",
      },
      keyframes: {
        "slide-up": {
          "0%": { transform: "scaleY(0)", opacity: "0" },
          "100%": { transform: "scaleY(1)", opacity: "1" },
        },
        "slide-down": {
          "0%": { transform: "scaleY(1)", opacity: "1" },
          "100%": { transform: "scaleY(0)", opacity: "0" },
        },
      },
      animation: {
        "slide-up": "slide-up 300ms ease-out forwards",
        "slide-down": "slide-down 300ms ease-out forwards",
      },
    },
  },
  plugins: [],
};


