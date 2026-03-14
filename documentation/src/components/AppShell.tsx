"use client";

import { LanguageProvider } from "@/context/LanguageContext";
import { AppLayout } from "@/components/AppLayout";

export function AppShell({ children }: { children: React.ReactNode }) {
  return (
    <LanguageProvider>
      <AppLayout>{children}</AppLayout>
    </LanguageProvider>
  );
}
