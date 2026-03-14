"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function GlossairePage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.glossaire.title}</h1>
        <p className="mt-2 text-gray-600">{t.glossaire.subtitle}</p>
      </div>

      <dl className="space-y-4">
        {t.glossaire.terms.map((item, i) => (
          <div key={i} className="border-b border-gray-200 pb-4 last:border-0">
            <dt className="font-semibold text-[#4c6f76]">{item.term}</dt>
            <dd className="mt-1 text-gray-600">{item.def}</dd>
          </div>
        ))}
      </dl>
    </div>
  );
}
