"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function Home() {
  const { t } = useLanguage();

  return (
    <div className="space-y-8 sm:space-y-10">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-[#4c6f76] sm:text-4xl">
          {t.home.title}
        </h1>
        <p className="mt-2 text-lg text-gray-600 sm:text-xl">{t.home.subtitle}</p>
      </div>

      <section className="prose max-w-none">
        <p className="text-gray-600 leading-relaxed text-base sm:text-lg">{t.home.intro}</p>
        <p className="mt-4 text-gray-600 leading-relaxed">{t.home.intro2}</p>
      </section>

      <section>
        <h2 className="text-xl font-semibold text-gray-800 mb-4">{t.home.docSection}</h2>
        <div className="grid gap-3 sm:grid-cols-2">
          <Link
            href="/introduction"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.introduction}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.introCard}</p>
          </Link>
          <Link
            href="/guide-demarrage"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.guideDemarrage}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.guideCard}</p>
          </Link>
          <Link
            href="/architecture"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.architecture}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.architectureDesc}</p>
          </Link>
          <Link
            href="/installation"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.installation}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.installationDesc}</p>
          </Link>
          <Link
            href="/lift-format"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.liftFormat}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.liftFormatDesc}</p>
          </Link>
          <Link
            href="/fonctionnalites"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.fonctionnalites}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.fonctionnalitesDesc}</p>
          </Link>
          <Link
            href="/category-browser"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.categoryBrowser}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.categoryCard}</p>
          </Link>
          <Link
            href="/configuration"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.configuration}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.configCard}</p>
          </Link>
          <Link
            href="/glossaire"
            className="block rounded-lg border border-gray-200 bg-white p-4 transition-colors hover:border-[#4c6f76]/50 hover:bg-[#d3e8eb]/20"
          >
            <h3 className="font-semibold text-[#4c6f76]">{t.nav.glossaire}</h3>
            <p className="mt-1 text-sm text-gray-600">{t.home.glossaireCard}</p>
          </Link>
        </div>
      </section>

      <section className="rounded-lg border border-[#4c6f76]/20 bg-[#d3e8eb]/10 p-4 sm:p-6">
        <h2 className="text-lg font-semibold text-[#4c6f76]">{t.home.searchSection}</h2>
        <p className="mt-2 text-sm text-gray-600">{t.home.searchDesc}</p>
      </section>
    </div>
  );
}
