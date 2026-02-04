# GUI pour l'édition d'un dictionnaire multilingue

> Ce projet fournit une application de bureau permettant de **parcourir** et **modifier** un dictionnaire multilingue stocké au format **LIFT**. L’interface est construite en **JavaFX** et s’appuie sur une bibliothèque dédiée (`lift-api`) pour manipuler les données LIFT, avec un module séparé pour l’application graphique (`dictionary-editor-fx`).
>
> Le dépôt est organisé en **2 modules Maven** : - **`lift-api/`** : bibliothèque Java pour charger/manipuler/sauvegarder des dictionnaires LIFT. - **`dictionary-editor-fx/`** : application JavaFX (UI) qui s’appuie sur `lift-api`.

## Stack technique
- **Java** : JDK **21** (compilation en `release 21`)
- **Build** : **Maven**
- **UI** : **JavaFX 21** (`javafx-controls`, `javafx-fxml`)
- **Format** : **LIFT** (XML)

### Prérequis
- **JDK 21** installé et disponible (variable `JAVA_HOME` recommandée)
- **Maven** installé (ou Maven via ton IDE)

## Instructions d'installation

Cloner le dépôt :

```bash
git clone "https://github.com/I2S9/multilingual-dictionary-editor.git"
cd multilingual-dictionary-editor
```

Compiler et exécuter les tests :

```bash
mvn test
```

Compiler uniquement l’app JavaFX (et builder `lift-api` si nécessaire) :

```bash
mvn -pl dictionary-editor-fx -am test
```

Lancer l’interface JavaFX depuis la racine :

```bash
mvn -f dictionary-editor-fx/pom.xml javafx:run
```

Notes Windows
- **Si Maven affiche** “La syntaxe de la commande n’est pas correcte.”, vérifie que `JAVA_HOME` ne contient **pas de saut de ligne** (ça casse `mvn.cmd`).

## Structure du dépôt (résumé)
- **`pom.xml`** : agrégateur (multi-modules)
- **`lift-api/`** : lib LIFT + tests + fichiers LIFT/XML d’exemple en `src/test/resources/lift/`
- **`dictionary-editor-fx/`**
  - `src/main/resources/com/example/ui/` : `MainView.fxml`, `app.css`
  - `src/main/java/com/example/` : point d’entrée `MainApp`