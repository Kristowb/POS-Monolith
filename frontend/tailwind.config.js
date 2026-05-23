/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Palette warna kustom jika ingin digunakan dalam Tailwind utility
        spacex: {
          bg: '#030712',
          card: 'rgba(17, 24, 39, 0.4)',
          border: 'rgba(75, 85, 99, 0.2)',
          accent: '#3b82f6',
        },
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      }
    },
  },
  plugins: [],
};
