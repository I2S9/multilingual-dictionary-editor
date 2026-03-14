"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function IntroductionPage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.introduction.title}</h1>
        <p className="mt-2 text-gray-600">{t.introduction.subtitle}</p>
      </div>

      <p className="leading-relaxed text-gray-600">{t.introduction.intro}</p>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.introduction.goals}</h2>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.introduction.goalsItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.introduction.audience}</h2>
        <p className="leading-relaxed text-gray-600">{t.introduction.audienceText}</p>
      </section>
    </div>
  );
}
