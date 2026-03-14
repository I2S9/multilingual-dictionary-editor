"use client";

import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";

export default function InstallationPage() {
  const { t } = useLanguage();

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <Link href="/" className="mb-4 inline-block text-sm text-gray-600 hover:text-[#4c6f76]">
          {t.common.back}
        </Link>
        <h1 className="text-2xl font-bold text-[#4c6f76] sm:text-3xl">{t.installation.title}</h1>
        <p className="mt-2 text-gray-600">{t.installation.subtitle}</p>
      </div>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.installation.prereqTitle}</h2>
        <ul className="ml-4 list-disc list-inside space-y-2 text-gray-600">
          {t.installation.prereqItems.map((item, i) => (
            <li key={i}>{item}</li>
          ))}
        </ul>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.installation.buildTitle}</h2>
        <p className="text-gray-600">{t.installation.buildIntro}</p>
        <pre className="overflow-x-auto rounded-lg bg-gray-100 p-4 text-sm text-gray-700">{`mvn install`}</pre>
        <p className="text-sm text-gray-600">{t.installation.buildNote}</p>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.installation.runTitle}</h2>
        <p className="text-gray-600">{t.installation.buildIntro}</p>
        <pre className="overflow-x-auto rounded-lg bg-gray-100 p-4 text-sm text-gray-700">{`mvn -pl dictionary-editor-fx javafx:run`}</pre>
        <p className="text-sm text-gray-600">{t.installation.runNote}</p>
        <pre className="overflow-x-auto rounded-lg bg-gray-100 p-4 text-sm text-gray-700">{`$env:JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21'
mvn -pl dictionary-editor-fx javafx:run`}</pre>
      </section>

      <section className="space-y-4">
        <h2 className="text-xl font-semibold text-gray-800">{t.installation.testTitle}</h2>
        <pre className="overflow-x-auto rounded-lg bg-gray-100 p-4 text-sm text-gray-700">{`mvn test -pl lift-api`}</pre>
      </section>
    </div>
  );
}
