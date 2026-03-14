"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function GuideDemarragePage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.guideDemarrage.title}</h1>
        <p className="mt-2 text-gray-600">{t.guideDemarrage.subtitle}</p>
      </div>

      <ol className="ml-4 list-decimal list-inside space-y-3 text-gray-600">
        {t.guideDemarrage.steps.map((step, i) => (
          <li key={i} className="pl-2">
            {step}
          </li>
        ))}
      </ol>

      <p className="text-sm text-gray-500">
        <Link href="/installation" className="text-[#4c6f76] hover:underline">
          → {t.nav.installation}
        </Link>
      </p>
    </div>
  );
}
