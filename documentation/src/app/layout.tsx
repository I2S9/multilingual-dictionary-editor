import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { AppShell } from "@/components/AppShell";

const inter = Inter({ subsets: ["latin"], display: "swap" });

export const metadata: Metadata = {
  title: "Documentation technique",
  description: "Documentation technique du projet Multilingual Dictionary Editor",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fr" className="bg-white">
      <body className={`${inter.className} min-h-screen bg-white antialiased`}>
        <AppShell>{children}</AppShell>
      </body>
    </html>
  );
}
