"use client";

import { useLanguage } from "@/context/LanguageContext";

export function LanguageSwitcher() {
  const { lang, setLang } = useLanguage();

  return (
    <div className="flex items-center gap-1 rounded-lg border border-white/30 bg-white/10 p-1">
      <button
        onClick={() => setLang("fr")}
        className={`rounded px-3 py-1.5 text-sm font-medium transition-colors ${
          lang === "fr" ? "bg-white text-[#4c6f76]" : "text-white/90 hover:text-white"
        }`}
      >
        FR
      </button>
      <button
        onClick={() => setLang("en")}
        className={`rounded px-3 py-1.5 text-sm font-medium transition-colors ${
          lang === "en" ? "bg-white text-[#4c6f76]" : "text-white/90 hover:text-white"
        }`}
      >
        EN
      </button>
    </div>
  );
}
