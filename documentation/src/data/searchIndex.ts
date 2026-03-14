export type SearchEntry = {
  id: string;
  href: string;
  titleFr: string;
  titleEn: string;
  keywordsFr: string[];
  keywordsEn: string[];
};

export const searchIndex: SearchEntry[] = [
  { id: "home", href: "/", titleFr: "Accueil", titleEn: "Home", keywordsFr: ["accueil"], keywordsEn: ["home"] },
  { id: "intro", href: "/introduction", titleFr: "Introduction", titleEn: "Introduction", keywordsFr: ["introduction", "présentation", "projet"], keywordsEn: ["introduction", "overview", "project"] },
  { id: "arch", href: "/architecture", titleFr: "Architecture", titleEn: "Architecture", keywordsFr: ["architecture", "maven", "modules", "lift-api", "dictionary-editor-fx", "structure"], keywordsEn: ["architecture", "maven", "modules", "lift-api", "dictionary-editor-fx", "structure"] },
  { id: "install", href: "/installation", titleFr: "Installation", titleEn: "Installation", keywordsFr: ["installation", "compilation", "lancement", "prérequis", "java", "maven"], keywordsEn: ["installation", "build", "launch", "prerequisites", "java", "maven"] },
  { id: "lift", href: "/lift-format", titleFr: "Format LIFT", titleEn: "LIFT Format", keywordsFr: ["lift", "xml", "entrée", "sens", "exemple", "trait", "lexical"], keywordsEn: ["lift", "xml", "entry", "sense", "example", "trait", "lexical"] },
  { id: "features", href: "/fonctionnalites", titleFr: "Fonctionnalités", titleEn: "Features", keywordsFr: ["fonctionnalités", "navigation", "category browser", "configuration"], keywordsEn: ["features", "navigation", "category browser", "configuration"] },
  { id: "category", href: "/category-browser", titleFr: "Category browser", titleEn: "Category browser", keywordsFr: ["category", "browser", "traits", "annotations", "notes", "grammatical"], keywordsEn: ["category", "browser", "traits", "annotations", "notes", "grammatical"] },
  { id: "config", href: "/configuration", titleFr: "Configuration", titleEn: "Configuration", keywordsFr: ["configuration", "langues", "champs", "ranges"], keywordsEn: ["configuration", "languages", "fields", "ranges"] },
  { id: "glossaire", href: "/glossaire", titleFr: "Glossaire", titleEn: "Glossary", keywordsFr: ["glossaire", "définition", "terme", "lift", "entrée", "sens", "exemple", "trait", "annotation", "méta-langue"], keywordsEn: ["glossary", "definition", "term", "lift", "entry", "sense", "example", "trait", "annotation", "meta-language"] },
  { id: "guide", href: "/guide-demarrage", titleFr: "Guide de démarrage", titleEn: "Getting started", keywordsFr: ["guide", "démarrage", "début", "tutoriel"], keywordsEn: ["guide", "getting started", "beginner", "tutorial"] },
];
