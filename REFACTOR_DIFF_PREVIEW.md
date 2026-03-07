# Aperçu des modifications — Commit 1 : com.example → fr.cnrs.lacito.liftgui

## 1. Mapping des packages

| Ancien package | Nouveau package |
|----------------|-----------------|
| `com.example` | `fr.cnrs.lacito.liftgui` |
| `com.example.ui` | `fr.cnrs.lacito.liftgui.ui` |
| `com.example.ui.controls` | `fr.cnrs.lacito.liftgui.ui.controls` |
| `com.example.ui.model` | `fr.cnrs.lacito.liftgui.ui.model` |
| `com.example.core` | `fr.cnrs.lacito.liftgui.core` |
| `com.example.data` | `fr.cnrs.lacito.liftgui.data` |

## 2. Chemins ressources

| Ancien | Nouveau |
|--------|---------|
| `/com/example/ui/MainView.fxml` | `/fr/cnrs/lacito/liftgui/ui/MainView.fxml` |
| `/com/example/ui/app.css` | `/fr/cnrs/lacito/liftgui/ui/app.css` |
| `/com/example/ui/dark.css` | `/fr/cnrs/lacito/liftgui/ui/dark.css` |
| `com.example.ui.messages` (bundle) | `fr.cnrs.lacito.liftgui.ui.messages` |
| `/com/example/data/mock-dictionary.json` | `/fr/cnrs/lacito/liftgui/data/mock-dictionary.json` |

## 3. Fichiers modifiés (diffs textuels)

### dictionary-editor-fx/pom.xml
```diff
-          <mainClass>com.example.MainApp</mainClass>
+          <mainClass>fr.cnrs.lacito.liftgui.MainApp</mainClass>
```

### MainView.fxml (contenu)
```diff
-            fx:controller="com.example.ui.MainController"
+            fx:controller="fr.cnrs.lacito.liftgui.ui.MainController"
```

### MainApp.java (package + import + chemins)
```diff
- package com.example;
+ package fr.cnrs.lacito.liftgui;

- import com.example.ui.I18n;
+ import fr.cnrs.lacito.liftgui.ui.I18n;
...
-             MainApp.class.getResource("/com/example/ui/MainView.fxml"),
+             MainApp.class.getResource("/fr/cnrs/lacito/liftgui/ui/MainView.fxml"),
...
-         scene.getStylesheets().add(MainApp.class.getResource("/com/example/ui/app.css").toExternalForm());
+         scene.getStylesheets().add(MainApp.class.getResource("/fr/cnrs/lacito/liftgui/ui/app.css").toExternalForm());
```

### MainController.java (package + imports + références)
```diff
- package com.example.ui;
+ package fr.cnrs.lacito.liftgui.ui;

- import com.example.core.DictionaryService;
- import com.example.core.LiftOpenException;
- import com.example.ui.controls.*;
+ import fr.cnrs.lacito.liftgui.core.DictionaryService;
+ import fr.cnrs.lacito.liftgui.core.LiftOpenException;
+ import fr.cnrs.lacito.liftgui.ui.controls.*;
...
-                    Platform.runLater(com.example.MainApp::reloadScene);
+                    Platform.runLater(fr.cnrs.lacito.liftgui.MainApp::reloadScene);
...
-            String darkCss = com.example.MainApp.class.getResource("/com/example/ui/dark.css") != null
-                    ? com.example.MainApp.class.getResource("/com/example/ui/dark.css").toExternalForm()
+            String darkCss = fr.cnrs.lacito.liftgui.MainApp.class.getResource("/fr/cnrs/lacito/liftgui/ui/dark.css") != null
+                    ? fr.cnrs.lacito.liftgui.MainApp.class.getResource("/fr/cnrs/lacito/liftgui/ui/dark.css").toExternalForm()
```

### I18n.java
```diff
- package com.example.ui;
+ package fr.cnrs.lacito.liftgui.ui;
...
-     private static final String BUNDLE_BASE = "com.example.ui.messages";
+     private static final String BUNDLE_BASE = "fr.cnrs.lacito.liftgui.ui.messages";
```

### Tous les fichiers sous ui/controls/ (package uniquement)
```diff
- package com.example.ui.controls;
+ package fr.cnrs.lacito.liftgui.ui.controls;
```
(Fichiers : SenseEditor, VariantEditor, TraitEditor, NotableEditor, FieldEditor, MultiTextEditor, NoteEditor, PronunciationEditor, ReversalEditor, RelationEditor, AnnotationEditor, ExtensibleWithFieldEditor, ExtensibleWithoutFieldEditor, EtymologyEditor, ExampleEditor.)

### SenseRow.java
```diff
- package com.example.ui.model;
+ package fr.cnrs.lacito.liftgui.ui.model;
```

### DictionaryService.java, LiftOpenException.java
```diff
- package com.example.core;
+ package fr.cnrs.lacito.liftgui.core;
```

### LiftRepository.java
```diff
- package com.example.data;
+ package fr.cnrs.lacito.liftgui.data;
```

## 4. Déplacements de fichiers

- **Java** : tous les `.java` de `src/main/java/com/example/` → `src/main/java/fr/cnrs/lacito/liftgui/` (même arborescence relative).
- **Ressources** : `src/main/resources/com/example/` → `src/main/resources/fr/cnrs/lacito/liftgui/` (ui/, data/).  
  `lift/demo.lift` reste en `src/main/resources/lift/`.

## 5. Fichiers supprimés (après création des nouveaux)

- Tous les `.java` sous `com/example/`
- Tous les fichiers sous `resources/com/example/`

---

*Ce fichier est un aperçu avant application. Les modifications réelles seront effectuées ensuite.*
