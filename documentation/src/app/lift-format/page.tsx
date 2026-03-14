"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function LiftFormatPage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.liftFormat.title}</h1>
        <p className="mt-2 text-gray-600">{t.liftFormat.subtitle}</p>
      </div>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.liftFormat.introTitle}</h2>
        <p className="leading-relaxed text-gray-600">{t.liftFormat.intro}</p>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.liftFormat.structureTitle}</h2>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.liftFormat.structureItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.liftFormat.elementsTitle}</h2>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.liftFormat.elementsItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.liftFormat.resourcesTitle}</h2>
        <a
          href="https://github.com/sillsdev/lift-standard"
          target="_blank"
          rel="noopener noreferrer"
          className="text-[#4c6f76] hover:text-[#43646a] hover:underline"
        >
          {t.liftFormat.liftStandard}
        </a>
      </section>
    </div>
  );
}
