"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function ArchitecturePage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.architecture.title}</h1>
        <p className="mt-2 text-gray-600">{t.architecture.subtitle}</p>
      </div>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.architecture.mavenTitle}</h2>
        <p className="leading-relaxed text-gray-600">{t.architecture.mavenIntro}</p>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          <li>
            <code className="rounded bg-gray-100 px-1.5 py-0.5 text-[#4c6f76]">lift-api</code> — Bibliothèque pour
            lire/écrire des dictionnaires au format LIFT
          </li>
          <li>
            <code className="rounded bg-gray-100 px-1.5 py-0.5 text-[#4c6f76]">dictionary-editor-fx</code> — Application
            JavaFX (interface graphique)
          </li>
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.architecture.liftApiTitle}</h2>
        <p className="leading-relaxed text-gray-600">{t.architecture.liftApiDesc}</p>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.architecture.editorTitle}</h2>
        <p className="leading-relaxed text-gray-600">{t.architecture.editorIntro}</p>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.architecture.editorItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.architecture.techTitle}</h2>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.architecture.techItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>
    </div>
  );
}
