"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { useLanguage } from "@/context/LanguageContext";
import { searchIndex } from "@/data/searchIndex";
import type { Lang } from "@/data/translations";

function search(query: string, lang: Lang) {
  const q = query.trim().toLowerCase();
  if (q.length < 2) return [];
  const titleKey = lang === "fr" ? "titleFr" : "titleEn";
  const keywordsKey = lang === "fr" ? "keywordsFr" : "keywordsEn";
  return searchIndex
    .filter(
      (e) =>
        (e[titleKey] as string).toLowerCase().includes(q) ||
        (e[keywordsKey] as string[]).some((k) => k.toLowerCase().includes(q))
    )
    .slice(0, 8);
}

export function SearchBar() {
  const { lang } = useLanguage();
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<typeof searchIndex>([]);
  const [open, setOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setResults(search(query, lang));
    setOpen(query.length >= 2);
  }, [query, lang]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const titleKey = lang === "fr" ? "titleFr" : "titleEn";
  const placeholder = lang === "fr" ? "Rechercher..." : "Search...";

  return (
    <div ref={containerRef} className="relative w-full max-w-xs">
      <div className="flex items-center gap-2 rounded-lg border border-white/30 bg-white/10 px-3 py-2">
        <svg className="h-4 w-4 shrink-0 text-white/80" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          ref={inputRef}
          type="search"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={placeholder}
          className="w-full bg-transparent text-sm text-white placeholder-white/60 outline-none"
          aria-label={placeholder}
        />
      </div>
      {open && results.length > 0 && (
        <ul className="absolute left-0 right-0 top-full z-50 mt-1 max-h-64 overflow-auto rounded-lg border border-[#43646a] bg-white py-1 shadow-xl">
          {results.map((r) => (
            <li key={r.id}>
              <Link
                href={r.href}
                onClick={() => {
                  setQuery("");
                  setOpen(false);
                }}
                className="block px-3 py-2 text-sm text-gray-800 hover:bg-[#d3e8eb]"
              >
                {r[titleKey] as string}
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
