# Diff — Refactoring complet : com.example → fr.cnrs.lacito.liftgui

## État actuel

- **Code actif** : déjà présent sous `src/main/java/fr/cnrs/lacito/liftgui/` (packages et imports à jour).
- **Ressources** : déjà sous `src/main/resources/fr/cnrs/lacito/liftgui/` et `lift/`.
- **Références** : aucune occurrence de `com.example` ou `/com/example` dans le module `dictionary-editor-fx`.
- **Reste à faire** : supprimer l’arborescence vide `com/example` (et `com` si vide) pour que le package `com.example` n’existe plus.

---

## 1. Déplacements (déjà effectués côté contenu)

| Source (com.example) | Destination (fr.cnrs.lacito.liftgui) |
|----------------------|--------------------------------------|
| `com/example/MainApp.java` | `fr/cnrs/lacito/liftgui/MainApp.java` |
| `com/example/ui/I18n.java` | `fr/cnrs/lacito/liftgui/ui/I18n.java` |
| `com/example/ui/MainController.java` | `fr/cnrs/lacito/liftgui/ui/MainController.java` |
| `com/example/ui/model/SenseRow.java` | `fr/cnrs/lacito/liftgui/ui/model/SenseRow.java` |
| `com/example/ui/controls/*.java` (15 fichiers) | `fr/cnrs/lacito/liftgui/ui/controls/*.java` |
| `com/example/core/DictionaryService.java` | `fr/cnrs/lacito/liftgui/core/DictionaryService.java` |
| `com/example/core/LiftOpenException.java` | `fr/cnrs/lacito/liftgui/core/LiftOpenException.java` |
| `com/example/data/LiftRepository.java` | `fr/cnrs/lacito/liftgui/data/LiftRepository.java` |
| `resources/com/example/ui/MainView.fxml` | `resources/fr/cnrs/lacito/liftgui/ui/MainView.fxml` |
| `resources/com/example/ui/app.css` | `resources/fr/cnrs/lacito/liftgui/ui/app.css` |
| `resources/com/example/ui/messages_*.properties` | `resources/fr/cnrs/lacito/liftgui/ui/` |
| `resources/com/example/data/mock-dictionary.json` | `resources/fr/cnrs/lacito/liftgui/data/` |

---

## 2. Modifications dans les fichiers (déjà appliquées)

### Packages Java
- `package com.example;` → `package fr.cnrs.lacito.liftgui;`
- `package com.example.ui;` → `package fr.cnrs.lacito.liftgui.ui;`
- `package com.example.ui.controls;` → `package fr.cnrs.lacito.liftgui.ui.controls;`
- `package com.example.ui.model;` → `package fr.cnrs.lacito.liftgui.ui.model;`
- `package com.example.core;` → `package fr.cnrs.lacito.liftgui.core;`
- `package com.example.data;` → `package fr.cnrs.lacito.liftgui.data;`

### Imports
- `import com.example.ui.I18n;` → `import fr.cnrs.lacito.liftgui.ui.I18n;`
- `import com.example.core.DictionaryService;` → `import fr.cnrs.lacito.liftgui.core.DictionaryService;`
- `import com.example.core.LiftOpenException;` → `import fr.cnrs.lacito.liftgui.core.LiftOpenException;`
- `import com.example.ui.controls.*;` → `import fr.cnrs.lacito.liftgui.ui.controls.*;`

### Chemins ressources (Java)
- `"/com/example/ui/MainView.fxml"` → `"/fr/cnrs/lacito/liftgui/ui/MainView.fxml"`
- `"/com/example/ui/app.css"` → `"/fr/cnrs/lacito/liftgui/ui/app.css"`
- `"/com/example/ui/dark.css"` → `"/fr/cnrs/lacito/liftgui/ui/dark.css"`

### Bundle i18n
- `BUNDLE_BASE = "com.example.ui.messages"` → `"fr.cnrs.lacito.liftgui.ui.messages"`

### Références qualifiées
- `com.example.MainApp` → `fr.cnrs.lacito.liftgui.MainApp`

### FXML
- `fx:controller="com.example.ui.MainController"` → `fx:controller="fr.cnrs.lacito.liftgui.ui.MainController"`

### pom.xml
- `<mainClass>com.example.MainApp</mainClass>` → `<mainClass>fr.cnrs.lacito.liftgui.MainApp</mainClass>`

---

## 3. Suppression du package com.example (à appliquer)

Suppression des répertoires vides pour qu’il ne reste plus aucun `com.example` :

```
SUPPRIMER (arborescence complète) :
  dictionary-editor-fx/src/main/java/com/example/
  dictionary-editor-fx/src/main/java/com/          (si vide après suppression de example/)
  dictionary-editor-fx/src/main/resources/com/example/
  dictionary-editor-fx/src/main/resources/com/      (si vide après suppression de example/)
```

**Commande équivalente :**
```bash
rm -rf dictionary-editor-fx/src/main/java/com/example
rmdir dictionary-editor-fx/src/main/java/com 2>/dev/null || true
rm -rf dictionary-editor-fx/src/main/resources/com/example
rmdir dictionary-editor-fx/src/main/resources/com 2>/dev/null || true
```

---

## 4. Résumé

- Le contenu a déjà été déplacé et mis à jour sous `fr/cnrs/lacito/liftgui`.
- Il reste uniquement à **supprimer les dossiers `com/example` et `com`** (java + resources) pour finaliser le refactoring.
