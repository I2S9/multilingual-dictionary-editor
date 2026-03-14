"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useLanguage } from "@/context/LanguageContext";
import { LanguageSwitcher } from "./LanguageSwitcher";
import { SearchBar } from "./SearchBar";

const navItems = [
  { href: "/", labelKey: "home" as const },
  { href: "/introduction", labelKey: "introduction" as const },
  { href: "/guide-demarrage", labelKey: "guideDemarrage" as const },
  { href: "/architecture", labelKey: "architecture" as const },
  { href: "/installation", labelKey: "installation" as const },
  { href: "/lift-format", labelKey: "liftFormat" as const },
  { href: "/fonctionnalites", labelKey: "fonctionnalites" as const },
  { href: "/category-browser", labelKey: "categoryBrowser" as const },
  { href: "/configuration", labelKey: "configuration" as const },
  { href: "/glossaire", labelKey: "glossaire" as const },
];

export function AppLayout({ children }: { children: React.ReactNode }) {
  const { lang, t } = useLanguage();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    document.documentElement.lang = lang;
  }, [lang]);

  return (
    <div className="flex min-h-screen flex-col">
      {/* Top bar - full width, language selector top right */}
      <header className="flex shrink-0 items-center justify-between border-b-2 border-[#43646a] bg-[#4c6f76] px-4 py-3 shadow-md sm:px-6">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="rounded p-2 text-white hover:bg-white/20 lg:hidden"
            aria-label="Menu"
          >
            <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {sidebarOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
          <Link href="/" className="text-base font-bold text-white sm:text-lg">
            {t.header.appName}
          </Link>
        </div>
        <LanguageSwitcher />
      </header>

      <div className="relative flex flex-1">
        {/* Overlay when sidebar open on mobile */}
        {sidebarOpen && (
          <div
            className="fixed inset-0 z-30 bg-black/50 lg:hidden"
            onClick={() => setSidebarOpen(false)}
            aria-hidden="true"
          />
        )}

        {/* Sidebar - desktop: always visible, mobile: overlay drawer */}
        <aside
          className={`fixed inset-y-0 left-0 z-40 w-72 border-r-2 border-[#43646a] bg-[#4c6f76] p-4 pt-20 shadow-xl transition-transform duration-200 ease-out lg:static lg:z-auto lg:block lg:pt-4 lg:shrink-0 lg:shadow-lg ${
            sidebarOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
          }`}
        >
          <div className="mb-4 px-2">
            <SearchBar />
          </div>
          <nav className="space-y-0.5 overflow-y-auto max-h-[calc(100vh-12rem)]">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                onClick={() => setSidebarOpen(false)}
                className="block rounded-lg px-3 py-2 text-sm text-white/90 hover:bg-white/20 hover:text-white"
              >
                {t.nav[item.labelKey]}
              </Link>
            ))}
          </nav>
        </aside>

        {/* Main content - aligned left, reduced padding on large screens */}
        <main className="min-w-0 flex-1 overflow-auto bg-[#fafbfc] p-4 sm:p-6 lg:px-6 lg:pl-6 lg:pr-12">
          <div className="max-w-4xl">{children}</div>
        </main>
      </div>
    </div>
  );
}
