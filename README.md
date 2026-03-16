# Éditeur de dictionnaires multilingues

Application JavaFX permettant de **créer**, **modifier** et **gérer** des dictionnaires lexicographiques au format **LIFT**, avec support des langues objet et méta-langues.

## Documentation technique

La documentation complète est disponible en ligne :

**https://multilingual-dictionary-editor.vercel.app/**

Elle couvre notamment :
- **Introduction** — Présentation du projet et objectifs
- **Guide de démarrage** — Démarrer en 5 étapes
- **Architecture** — Structure du projet, modules Maven
- **Installation** — Prérequis, compilation et lancement
- **Format LIFT** — Structure XML, entrées, sens, exemples et métadonnées
- **Fonctionnalités** — Category browser, configuration, gestion des langues
- **Glossaire** — Définitions des termes techniques

---

## Stack technique

- **Java** : JDK **21** (compilation en `release 21`)
- **Build** : **Maven 3.x**
- **UI** : **JavaFX 21** (`javafx-controls`, `javafx-fxml`)
- **Format** : **LIFT** (XML)

### Prérequis

- **JDK 21** installé et disponible (`JAVA_HOME` recommandé)
- **Maven** installé

## Installation et lancement

```bash
git clone "https://github.com/I2S9/multilingual-dictionary-editor.git"
cd multilingual-dictionary-editor
```

**Compiler et installer les modules** (obligatoire en premier) :

```bash
mvn install
```

> Sans cette étape, `dictionary-editor-fx` ne trouvera pas les classes de `lift-api`.

**Lancer l'application** :

```bash
mvn javafx:run -pl dictionary-editor-fx
```

**Alternative** (si la commande ci-dessus échoue) :

```bash
mvn install -pl lift-api
mvn -f dictionary-editor-fx/pom.xml javafx:run
```

**Sous Windows** : un script `run-app.bat` est fourni à la racine.

**Tests** :

```bash
mvn test
```

---

## Structure du dépôt

```
multilingual-dictionary-editor/
├── pom.xml                    # Agrégateur Maven (multi-modules)
├── run-app.bat                # Script de lancement Windows
├── README.md
│
├── lift-api/                  # Module bibliothèque LIFT
│   ├── pom.xml
│   ├── src/main/java/         # Modèle de données, parsers/serializers XML
│   │   └── fr/cnrs/lacito/liftapi/
│   │       ├── LiftDictionary.java
│   │       ├── LiftDictionaryLoader.java
│   │       └── model/         # LiftEntry, LiftSense, LiftExample, MultiText, etc.
│   └── src/test/
│       ├── java/              # Tests unitaires
│       └── resources/lift/    # Fichiers LIFT d'exemple
│
├── dictionary-editor-fx/      # Module application JavaFX
│   ├── pom.xml
│   ├── src/main/java/
│   │   └── fr/cnrs/lacito/liftgui/
│   │       ├── MainApp.java           # Point d'entrée
│   │       ├── core/                  # DictionaryService, LiftOpenException
│   │       ├── data/                  # LiftRepository
│   │       ├── ui/
│   │       │   ├── MainController.java
│   │       │   ├── MainView.fxml
│   │       │   ├── I18n.java, Icons.java
│   │       │   └── controls/           # SenseEditor, ExampleEditor, MultiTextEditor, etc.
│   │       └── undo/                  # Commandes undo/redo
│   └── src/main/resources/
│       ├── fr/cnrs/lacito/liftgui/ui/
│       │   ├── MainView.fxml
│       │   ├── app.css
│       │   └── messages_fr.properties, messages_en.properties
│       └── lift/                      # Fichiers LIFT de démo
│
└── documentation/             # Site de documentation (Next.js)
    ├── src/
    │   ├── app/               # Pages (introduction, architecture, installation, etc.)
    │   └── data/              # Traductions FR/EN
    └── package.json
```

### Modules Maven

| Module | Rôle |
|--------|------|
| **lift-api** | Bibliothèque Java pour charger, manipuler et sauvegarder des dictionnaires LIFT. Classes principales : `LiftDictionary`, `LiftEntry`, `LiftSense`, `LiftExample`, `MultiText`, etc. |
| **dictionary-editor-fx** | Application JavaFX : navigation par arbre, tableaux dynamiques, formulaire d'édition, category browser, export CSV. |

---

## Notes

- **Windows** : si Maven affiche « La syntaxe de la commande n'est pas correcte », vérifier que `JAVA_HOME` ne contient pas de saut de ligne.
- **Documentation** : [https://multilingual-dictionary-editor.vercel.app/](https://multilingual-dictionary-editor.vercel.app/)
