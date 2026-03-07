# Plan d'implémentation – Feedback client (fonc.txt)

## 1. Analyse des fonctionnalités demandées par le client

### A. Remarques générales
| Demande | Détail |
|--------|--------|
| **Espace de noms** | Remplacer `com.example.ui` (et packages associés) par `fr.cnrs.lacito.liftgui` |
| **Ranges (sérialisation)** | Vérifier sérialisation ranges/range-elements ; ne pas écraser un fichier XML partagé par plusieurs `range/@href` |
| **Spans multitext** | Clarifier / corriger le sort des `<span>` dans les champs multitext |
| **Logging** | Utiliser le système de log déjà présent dans lift-api pour les erreurs |
| **Liens entre fiches** | Retirer l’ID dans les liens ; afficher uniquement : forme de l’entrée, glose du sens, ou numéro de l’exemple |
| **Liens « aller au parent »** | Depuis les fiches Notes, Variants, Étymologies, Relations : ajouter un lien vers l’objet parent |
| **Category browser (Trait/Field)** | (Optionnel / plus tard) Depuis une ligne Trait/Field, lien vers les objets ayant ce trait/field |

---

### B. Objets (colonne gauche)

#### Remarques générales sur les tableaux
- Alignement des colonnes avec les listes déroulantes → utiliser un **layout Grid**
- Adapter le type de champ : **dropdown à valeur unique** si colonne à valeurs répétées, **champ texte** sinon
- Formulaire « Edit form » entrée : **plus d’espace** pour Nom et Valeur des traits
- **Création d’annotations** : actuellement impossible → à brancher

#### Remarques générales sur les formulaires de droite
- **Sense** : utiliser le **texte du sens comme titre** (en noir) ; si plusieurs langues : séparer par " / " ou empiler avec code langue (grisé)
- **Création** : permettre la création d’**annotations**
- **Champ multitext** : augmenter les **marges gauche/droite** de l’applet « Annotations »
- **Dropdown langues** dans le multitext : **limiter aux langues existantes**, pas de création de nouvelle langue ici
- **Bug** : création d’un exemple depuis le formulaire d’un sens → le texte de l’exemple est conservé mais le champ **« source »** est perdu

#### Entries
- **Tableau** : retirer la colonne **« code »** (ou clarifier son rôle)
- **Formulaire** : lien vers sens → **retirer l’ID**, garder seulement la glose

#### Sense
- **Tableau** : ajouter une première colonne (ou groupe) **« entrée parente »** multitexte (forme de l’entrée)
- **Formulaire** : mettre l’**ID en bas, en gris** (bloc « identity » avec dates création/modification, comme pour Entries)

#### Example
- **Tableau** : colonne(s) **« sens parent »** multitexte (forme du sens)
- **Tableau** : ajouter les **traductions** ; un multitexte par type de traduction → autant de groupes de colonnes que de types dans le dictionnaire

#### Variants
- **Tableau** : colonne(s) **« entrée parent »** multitexte
- **Formulaire** : entrée parent en **multitexte grisé** (non modifiable)

#### Étymologie
- Vérifier que **tous les champs** sont présents (côté lift-api si besoin)
- **Tableau** : ajouter **etymology/form** en colonne(s) multitexte

#### Relation (nouveau tableau)
- **Colonnes** : multitexte pour lexical-unit/form de l’entrée parente ; `relation/@type` ; `relation/ref` en multitexte (forme de l’objet pointé)
- **Traits** : nom "is-primary", "variant-type"
- **Formulaire** : les deux multitextes (parent / réf) en **grisé** (non modifiable)

---

### C. Languages
- **Tableau** : **ne pas afficher** la colonne « parent » (les IDs ne sont pas utiles)
- **Colonne « Parent type »** : quand c’est « form », afficher le **parent du form** (gloss, definition, translation, field, note…) — attention cas des glosses
- **Champ « parent »** dans le formulaire : en faire un **lien** vers l’objet correspondant dans le tableau
- **Filtre** : pas de dropdown pour « Languages », uniquement **champ texte** (valeurs quasi uniques)
- **Formulaire** : **applet multitext** pour éditer le texte

---

### D. Category browser
- **a/** **POS et Grammatical Info** : identiques → **supprimer POS**
- **b/** **Trait** : ne pas mettre la colonne « parent » ; **fusionner les lignes identiques** (même nom+valeur) ; au clic : formulaire sans ID ni « Modify entry », avec bouton **« Accéder aux objets correspondant »** (tableau filtré entries/senses ayant ce trait)
- **c/** **Note types** : ajouter colonne **« Parent Type »** (comme pour Field)
- **d/** **Field, Note types, Translation type, Annotation** : lien **« aller au parent »** dans le formulaire
- **e/** Ajouter une entrée **« Relation type »** (`relation/@type`) : valeurs non modifiables, pas dans Configuration, les lister
- **f/** **Menu « Field configuration »** : le **retirer** ; déplacer sous **Dictionary configuration** : « Manage languages », « Manage annotation type », « Manage translation type », « Manage note type », « Relation type » ; dialog **langues** : corriger la suppression (langue supprimée reste proposée / réapparaît) ; **séparer object-language et meta-language** dans la boîte de dialogue

---

### E. Dictionary configuration
- **Dictionary description** : pas besoin de l’espace à droite avec « Modify entry »
- **« Field definitions »** → renommer en **« Field and traits definitions »**
- **Séparer** : « Dictionary description » et « Field and traits definitions » (et les entrées venant de l’ex-Field configuration) **des** entrées qui sont des ranges
- Créer une **cinquième rubrique « Range » (ou « Taxinomies »)** pour lister les ranges

#### Définition des field et trait (4.1)
- **Option-range** : remplacer le champ texte par un **menu déroulant** listant les ranges existants ; **grisé** sauf si "option.*" est sélectionné dans "data type" **et** que c’est un **Trait** (pas un field)

---

### F. Taxinomies (Ranges) – 5e groupe
- Possibilité de **créer une nouvelle taxinomie**
- **Grouper** par fichier source du range, puis par ordre alphabétique
- **Afficher la hiérarchie** quand les range-elements sont hiérarchiques

---

### G. Création rapide
- Les **sens** ont un **ID « bizarre »** → harmoniser (ex. comme les entrées avec UUID ou format lisible)
- **Bouton d’ajout** à côté du champ recherche : position perçue comme bizarre → déplacer si besoin
- **Retirer le champ recherche** dans la vue création rapide ? (à trancher)

---

## 2. Plan d’implémentation par commits (messages en français)

Chaque étape ci-dessous correspond à **un commit git** à part. Ordre choisi pour limiter les conflits et livrer par blocs cohérents.

---

### Commit 1 — Refactoring : espace de noms com.example → fr.cnrs.lacito.liftgui  
**Objectif** : Renommer les packages `com.example`, `com.example.ui`, `com.example.core`, `com.example.data` en `fr.cnrs.lacito.liftgui` (et sous-packages). Mettre à jour FXML, `module-info` / chemins ressources, et références (MainApp, etc.).

---

### Commit 2 — Utiliser le système de log de lift-api pour les erreurs  
**Objectif** : Remplacer les `printStackTrace()` / messages ad hoc par le mécanisme de logging déjà utilisé dans lift-api (ex. `java.util.logging` ou celui du projet). Logger les erreurs d’ouverture, de sauvegarde et d’UI de façon cohérente.

---

### Commit 3 — Liens entre fiches : afficher forme/glose/numéro sans ID  
**Objectif** : Dans tous les liens « montant » (ex. « back to sense ») et « descendant » (sens depuis entrée, exemples depuis sens), retirer l’affichage de l’ID et n’afficher que la forme de l’entrée, la glose du sens ou le numéro de l’exemple selon le cas.

---

### Commit 4 — Liens « aller au parent » pour Notes, Variants, Étymologies, Relations  
**Objectif** : Depuis les fiches (formulaires de droite) des Notes, Variants, Étymologies et Relations, ajouter un lien cliquable vers la fiche de l’objet parent (entry ou sense selon le cas).

---

### Commit 5 — Tableaux Objets : Grid layout et type de champ (dropdown vs texte)  
**Objectif** : Utiliser un layout de type Grid pour corriger l’alignement des colonnes avec les listes déroulantes. Pour les colonnes à valeurs répétées : dropdown à valeur unique ; pour les autres : champ texte. Appliquer aux tableaux de la section Objets.

---

### Commit 6 — Table Entries : retrait de la colonne « code »  
**Objectif** : Supprimer la colonne « code » du tableau des entrées (ou la remplacer par une colonne utile si le besoin est clarifié).

---

### Commit 7 — Table et formulaire Sense : colonne entrée parente, titre et bloc identity  
**Objectif** : Tableau : ajouter une première colonne (ou groupe) « entrée parente » multitexte (forme de l’entrée). Formulaire : titre = texte du sens (plusieurs langues en " / " ou empilées avec code langue en gris) ; ID déplacé en bas dans un bloc « identity » en gris (avec dates création/modification).

---

### Commit 8 — Table Example : colonne sens parent et colonnes Traduction par type  
**Objectif** : Ajouter colonne(s) « sens parent » multitexte. Ajouter les traductions dans le tableau : un groupe de colonnes multitexte par type de traduction existant dans le dictionnaire. Corriger le bug de perte du champ « source » à la création d’un exemple depuis le formulaire d’un sens.

---

### Commit 9 — Variants : colonne entrée parent et formulaire parent en lecture seule  
**Objectif** : Tableau : colonne(s) « entrée parent » multitexte. Formulaire : afficher l’entrée parent en multitexte grisé (non modifiable).

---

### Commit 10 — Étymologie : champs complets et colonne(s) form multitexte  
**Objectif** : S’assurer que tous les champs d’étymologie sont présents (côté lift-api si nécessaire). Tableau : ajouter etymology/form en colonne(s) multitexte.

---

### Commit 11 — Nouveau tableau Relation et formulaire en lecture seule  
**Objectif** : Ajouter le tableau « Relation » dans Objets. Colonnes : multitexte entrée parente (lexical-unit/form), relation/@type, relation/ref en multitexte (forme de l’objet pointé). Gérer les traits is-primary / variant-type. Formulaire : les deux multitextes (parent et ref) en grisé (non modifiable).

---

### Commit 12 — Formulaires : espace traits, marges Annotations, dropdown langues, création Annotation  
**Objectif** : Formulaire entrée : plus d’espace pour Nom/Valeur des traits. Champ multitext (Annotations) : augmenter marges gauche/droite. Dropdown langues du multitext : limiter aux langues existantes (pas de création ici). Permettre la création d’annotations depuis l’interface.

---

### Commit 13 — Vue Languages : colonnes, Parent type, filtre et formulaire  
**Objectif** : Masquer la colonne « parent » (IDs). Colonne « Parent type » : quand type = « form », afficher le parent du form (gloss, definition, translation, field, note…). Filtre : champ texte uniquement (pas de dropdown). Formulaire : champ parent = lien vers l’objet ; zone d’édition = applet multitext pour le texte.

---

### Commit 14 — Category browser : supprimer POS et corriger Trait  
**Objectif** : Supprimer l’entrée POS (doublon avec Grammatical Info). Trait : retirer colonne « parent » ; fusionner les lignes identiques (nom+valeur) ; formulaire sans ID ni « Modify entry », avec bouton « Accéder aux objets correspondant » ouvrant le tableau filtré (entries/senses ayant ce trait).

---

### Commit 15 — Category browser : Note types, Relation type et liens « aller au parent »  
**Objectif** : Note types : ajouter colonne « Parent Type ». Ajouter l’entrée « Relation type » (relation/@type) dans Category browser. Pour Field, Note types, Translation type, Annotation : ajouter le lien « aller au parent » dans le formulaire.

---

### Commit 16 — Suppression du menu Field configuration et déplacement sous Dictionary configuration  
**Objectif** : Retirer le menu « Field configuration ». Ajouter sous « Dictionary configuration » : Manage languages, Manage annotation type, Manage translation type, Manage note type, Relation type. Dialog langues : corriger la suppression (langue supprimée ne plus réapparaître) et séparer object-language et meta-language.

---

### Commit 17 — Dictionary configuration : libellés, panneaux et 5e rubrique  
**Objectif** : Dictionary description : retirer l’espace à droite « Modify entry ». Renommer « Field definitions » en « Field and traits definitions ». Séparer visuellement description + définitions des ranges. Créer la 5e rubrique « Taxinomies » (ou « Range ») pour lister les ranges.

---

### Commit 18 — Définition field/trait : Option-range en dropdown  
**Objectif** : Remplacer le champ texte « Option-range » par un menu déroulant listant les ranges existants ; actif uniquement si "option.*" est sélectionné dans « data type » et que l’on définit un Trait (grisé sinon).

---

### Commit 19 — Taxinomies : création, groupement et hiérarchie  
**Objectif** : Dans la 5e rubrique Taxinomies : possibilité de créer une nouvelle taxinomie ; grouper par fichier source du range puis par ordre alphabétique ; afficher la hiérarchie des range-elements quand elle existe.

---

### Commit 20 — Création rapide : IDs sens, bouton et champ recherche  
**Objectif** : Harmoniser les IDs des sens (format lisible ou cohérent avec les entrées). Ajuster la position du bouton d’ajout de ligne. Retirer ou déplacer le champ recherche dans la vue « création rapide » selon le choix produit.

---

### Commit 21 (optionnel) — Ranges : sérialisation et fichiers partagés  
**Objectif** : Vérifier/corriger la sérialisation des ranges et range-elements ; s’assurer qu’un fichier XML référencé par plusieurs range/@href n’est pas écrasé plusieurs fois de façon incohérente.

---

### Commit 22 (optionnel) — Spans dans les champs multitext  
**Objectif** : Traiter ou documenter le comportement des `<span>` dans les champs multitext (selon décision technique).

---

## 3. Résumé

- **20 commits principaux** (1–20) couvrent le feedback.
- **2 commits optionnels** (21–22) pour ranges/sérialisation et spans multitext.
- Les messages de commit ci-dessus sont en français ; tu peux les utiliser tels quels pour `git commit -m "..."`.
- Aucune implémentation n’a été faite : ce document sert uniquement de plan.
