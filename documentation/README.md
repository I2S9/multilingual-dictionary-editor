# Documentation technique - Multilingual Dictionary Editor

Site web de documentation technique du projet, construit avec Next.js, Tailwind CSS et TypeScript.

## Développement

```bash
npm run dev
```

Ouvre [http://localhost:3000](http://localhost:3000).

### Erreur `__webpack_modules__[moduleId] is not a function`

Si cette erreur apparaît, supprimez le cache et relancez :

```bash
npm run dev:fresh
```

Ou manuellement (PowerShell) :

```powershell
Remove-Item -Recurse -Force .next -ErrorAction SilentlyContinue
npm run dev
```

## Build

```bash
npm run build
npm start
```

## Pages

- **Accueil** — Vue d'ensemble
- **Architecture** — Structure du projet, modules Maven
- **Installation** — Prérequis, compilation, lancement
- **Format LIFT** — Structure XML des dictionnaires
- **Fonctionnalités** — Category browser, configuration, outils
