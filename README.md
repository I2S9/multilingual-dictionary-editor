# Éditeur de dictionnaires multilingues

> Application bureau JavaFX permettant de **créer**, **modifier** et **gérer** des dictionnaires lexicographiques au format **LIFT**, avec support des langues objet et méta-langues.

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

**Lancer l'application** (mode développement, **tous les OS**) :

```bash
mvn javafx:run -pl dictionary-editor-fx
```

**Alternative** (si la commande ci-dessus échoue) :

```bash
mvn install -pl lift-api
mvn -f dictionary-editor-fx/pom.xml javafx:run
```

**Tests** :

```bash
mvn test
```

---

## Application bureau native (Windows, Linux, macOS)

Le module `dictionary-editor-fx` peut produire une **application autonome** (runtime Java réduit via **jlink**, puis paquet **jpackage**) : pas besoin d’installer JavaFX ou un JDK complet sur la machine cible pour l’exécuter.

### Prérequis communs

- **JDK 21** (`JAVA_HOME` pointant vers ce JDK)
- **Maven 3.9+**
- Indispensable : **adapter `-Djavafx.platform`** au système sur lequel vous **compilez** (les binaires JavaFX sont spécifiques à l’OS) :

| OS cible (build) | Valeur de `-Djavafx.platform` |
|------------------|-------------------------------|
| Linux x86_64     | `linux`                       |
| Windows x86_64   | `win`                         |
| macOS Apple Silicon | `mac-aarch64`              |
| macOS Intel      | `mac`                         |

### Commande de construction (profil Maven `dist`)

Depuis la racine du dépôt :

```bash
mvn clean package -pl dictionary-editor-fx -am -Pdist -DskipTests \
  -Djavafx.platform=<plateforme> \
  -Djpackage.type=<type>
```

Remplacez `<plateforme>` et `<type>` selon le tableau ci-dessous.

### Windows

| Objectif | `-Djpackage.type` | Résultat / lancement |
|----------|-------------------|----------------------|
| Installeur **MSI** ou **EXE** | `MSI` ou `EXE` | Nécessite **[WiX Toolset](https://wixtoolset.org/)** installé et dans le `PATH`. Fichiers sous `dictionary-editor-fx/target/jpackage/` (ou `jpackage-msi` / `jpackage-exe` si vous utilisez des suffixes comme dans la CI). |
| Dossier portable **sans installeur** | Voir ci-dessous | Utile si WiX n’est pas installé. |

**Exemple avec installeur** (WiX requis) :

```bash
mvn clean package -pl dictionary-editor-fx -am -Pdist -DskipTests \
  -Djavafx.platform=win \
  -Djpackage.type=MSI
```

**Sans WiX — image applicative** : le POM active des options Windows (`--win-menu`) incompatibles avec `app-image`. Après une construction qui a produit le runtime jlink (`dictionary-editor-fx/target/maven-jlink/default/`), exécutez **jpackage** une fois à la main :

```bash
# PowerShell ou cmd, depuis la racine du dépôt
"%JAVA_HOME%\bin\jpackage.exe" ^
  --name DictionaryEditor ^
  --dest dictionary-editor-fx\target\jpackage-app ^
  --type app-image ^
  --app-version 1.0.0 ^
  --runtime-image dictionary-editor-fx\target\maven-jlink\default ^
  --vendor "CNRS LACITO" ^
  --module fr.cnrs.lacito.liftgui/fr.cnrs.lacito.liftgui.MainApp
```

Puis lancez **`dictionary-editor-fx\target\jpackage-app\DictionaryEditor\DictionaryEditor.exe`**.

Si `mvn package … -Pdist` échoue uniquement sur **jpackage** (WiX manquant) alors que le dossier **`dictionary-editor-fx/target/maven-jlink/default`** a bien été créé, la commande `jpackage` ci-dessus suffit pour obtenir l’image portable. Sinon installez **WiX** et utilisez `-Djpackage.type=MSI` ou `EXE`.

### Linux

**Paquet `.deb`** (valeur par défaut du projet pour `jpackage.type`) :

```bash
mvn clean package -pl dictionary-editor-fx -am -Pdist -DskipTests \
  -Djavafx.platform=linux \
  -Djpackage.type=DEB
```

Sur Debian/Ubuntu, des outils du type `fakeroot` peuvent être nécessaires ; la CI installe aussi `rpm` pour certaines étapes. Le `.deb` est généré sous `dictionary-editor-fx/target/jpackage/`. Installation :

```bash
sudo dpkg -i dictionary-editor-fx/target/jpackage/*.deb
```

Ensuite lancez l’application depuis le menu applications ou la commande installée par le paquet.

### macOS

**Image disque `.dmg`** (build sur une machine macOS avec le bon `javafx.platform`) :

```bash
mvn clean package -pl dictionary-editor-fx -am -Pdist -DskipTests \
  -Djavafx.platform=mac-aarch64 \
  -Djpackage.type=DMG
```

Sur Mac Intel, essayez `-Djavafx.platform=mac` si votre JDK/OpenJFX le fournit. Le `.dmg` apparaît sous `dictionary-editor-fx/target/jpackage/`. Ouvrez le DMG et glissez l’application dans **Applications**, puis lancez-la depuis le Launchpad ou le dossier Applications.

### Releases GitHub (binaires précompilés)

Un workflow (`.github/workflows/release.yml`) construit automatiquement les installeurs pour **Linux**, **Windows** et **macOS** lorsqu’un **tag** de version est poussé, par exemple :

```bash
git tag v1.0.0
git push origin v1.0.0
```

Les artefacts sont joints à la **Release** GitHub correspondante (fichiers `.deb`, `.msi`, `.exe`, `.dmg` selon les jobs réussis).

---

## Documentation technique

La documentation complète est disponible en ligne :

**https://multilingual-dictionary-editor.vercel.app/**

Elle couvre notamment :
- **Introduction** : Présentation du projet et objectifs
- **Guide de démarrage** : Démarrer en 5 étapes
- **Architecture** : Structure du projet, modules Maven
- **Installation** : Prérequis, compilation et lancement
- **Format LIFT** : Structure XML, entrées, sens, exemples et métadonnées
- **Fonctionnalités** : Category browser, configuration, gestion des langues
- **Glossaire** : Définitions des termes techniques

## Stack technique

- **Java** : JDK **21** (compilation en `release 21`)
- **Build** : **Maven 3.x**
- **UI** : **JavaFX 21** (`javafx-controls`, `javafx-fxml`)
- **Format** : **LIFT** (XML)

### Prérequis

- **JDK 21** installé et disponible (`JAVA_HOME` recommandé)
- **Maven** installé

## Structure du dépôt

```
multilingual-dictionary-editor/
├── pom.xml                    # Agrégateur Maven (multi-modules)
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
