package org.example.controller;

import fr.cnrs.lacito.liftapi.LiftDictionary;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.example.service.LiftService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import fr.cnrs.lacito.liftapi.model.Form;
import fr.cnrs.lacito.liftapi.model.GrammaticalInfo;
import fr.cnrs.lacito.liftapi.model.LiftEntry;
import fr.cnrs.lacito.liftapi.model.LiftSense;
import fr.cnrs.lacito.liftapi.model.LiftTrait;
import fr.cnrs.lacito.liftapi.model.MultiText;

import fr.cnrs.lacito.liftapi.model.LiftEntry;
import fr.cnrs.lacito.liftapi.model.LiftSense;
import fr.cnrs.lacito.liftapi.model.LiftTrait;

public class MainController {

    // ===== sidebar =====
    @FXML private VBox sidebar;
    private boolean sidebarVisible = true;

    @FXML private Button btnEntries;
    @FXML private Button btnSenses;
    @FXML private Button btnExamples;
    @FXML private Button btnVariants;
    @FXML private Button btnEtymology;

    // ===== top search =====
    @FXML private TextField searchField;

    // ===== views =====
    @FXML private VBox entriesView;
    @FXML private VBox sensesView;

    // ===== tables =====
    @FXML private TableView<EntryRow> entryTable;
    @FXML private TableView<SenseRow> senseTable;

    // ===== counts =====
    @FXML private Label entriesCountLabel;
    @FXML private Label sensesCountLabel;

    // ===== right panel =====
    @FXML private Label editTitleLabel;
    @FXML private Label editSubTitleLabel;

    @FXML private TextField morphTypeField;
    @FXML private TextField gramCatField;

    // ===== master data =====
    private final ObservableList<EntryRow> masterEntryRows = FXCollections.observableArrayList();
    private final ObservableList<SenseRow> masterSenseRows = FXCollections.observableArrayList();

    // ===== filtered =====
    private FilteredList<EntryRow> filteredEntries;
    private SortedList<EntryRow> sortedEntries;

    private FilteredList<SenseRow> filteredSenses;
    private SortedList<SenseRow> sortedSenses;

    // ===== lift-api =====
    private final LiftService liftService = new LiftService();
    private LiftDictionary dictionary;
    private Path currentFile;

    // selection
    private EntryRow selectedEntryRow;
    private SenseRow selectedSenseRow;

    // Filter TextFields (in column headers)
    private TextField entryIdFilter;
    private TextField entryFormFilter;
    private TextField entryCatFilter;

    private TextField senseEntryIdFilter;
    private TextField senseIdFilter;
    private TextField senseCatFilter;
    private TextField senseGlossFilter;

    @FXML
    private void initialize() {
        setupEntryTableWithHeaderFilters();
        setupSenseTableWithHeaderFilters();
        setupFilteringLists();

        searchField.textProperty().addListener((obs, o, n) -> {
            updateEntryPredicate();
            updateSensePredicate();
        });

        setupSelection();

        // ✅ IMPORTANT : attendre que la Scene existe (sinon showToast crash)
        Platform.runLater(() -> {
            loadLiftFromResources("/lift/20240828Lift.lift"); // fichier par défaut
            showEntries();
        });
    }

    private LiftEntry findEntryById(String entryId) {
        if (dictionary == null) return null;
        return dictionary.getLiftDictionaryComponents().getEntryById().get(entryId);
    }

    private void upsertTraitOnEntry(LiftEntry entry, String traitName, String newValue) {
        // supprimer les anciens traits morph-type
        entry.getTraits().removeIf(t -> traitName.equals(t.getName()));
        // ajouter le nouveau
        entry.addTrait(LiftTrait.of(traitName, newValue));
    }

    // =========================================================
    // VIEW SWITCH
    // =========================================================

    @FXML
    private void showEntries() {
        setActive(btnEntries);
        setView(entriesView, sensesView);
        editSubTitleLabel.setText("Entrée sélectionnée");
        updateEntryPredicate();

    }

    @FXML
    private void showSenses() {
        setActive(btnSenses);
        setView(sensesView, entriesView);
        editSubTitleLabel.setText("Sens sélectionné");
        updateSensePredicate(); // <-- force refresh
    }

    private void setView(VBox toShow, VBox toHide) {
        toShow.setVisible(true);
        toShow.setManaged(true);
        toHide.setVisible(false);
        toHide.setManaged(false);
    }

    private void setActive(Button active) {
        Button[] all = {btnEntries, btnSenses, btnExamples, btnVariants, btnEtymology};
        for (Button b : all) if (b != null) b.getStyleClass().remove("nav-item-active");
        if (active != null && !active.getStyleClass().contains("nav-item-active")) {
            active.getStyleClass().add("nav-item-active");
        }
    }

    // =========================================================
    // SIDEBAR + OPEN FILE
    // =========================================================

    @FXML
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
        sidebar.setManaged(sidebarVisible);
    }

    @FXML
    private void openLiftFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Ouvrir un fichier LIFT");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("LIFT (*.lift)", "*.lift")
        );

        Window w = (entryTable != null && entryTable.getScene() != null) ? entryTable.getScene().getWindow() : null;
        File f = fc.showOpenDialog(w);
        if (f == null) return;

        try {
            System.out.println("OPENING: " + f.getAbsolutePath());

            dictionary = liftService.load(f.toPath());
            currentFile = f.toPath();

            fillFromLiftApi();   // ta méthode existante pour remplir les tables
            showToast("✅ Fichier ouvert");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible d’ouvrir le fichier.\n" + e.getMessage()
            );
        }
    }

    // =========================================================
    // TABLES with HEADER FILTERS (inside columns)
    // =========================================================

    private void setupEntryTableWithHeaderFilters() {

        entryIdFilter = createHeaderFilterField("Filtrer ID...");
        entryFormFilter = createHeaderFilterField("Filtrer Forme...");
        entryCatFilter = createHeaderFilterField("Filtrer Catégorie...");

        TableColumn<EntryRow, String> colId = new TableColumn<>();
        colId.setCellValueFactory(d -> d.getValue().idProperty());
        colId.setPrefWidth(260);
        colId.setGraphic(makeHeader("ID", entryIdFilter));

        TableColumn<EntryRow, String> colLemma = new TableColumn<>();
        colLemma.setCellValueFactory(d -> d.getValue().lemmaProperty());
        colLemma.setPrefWidth(250);
        colLemma.setGraphic(makeHeader("Forme", entryFormFilter));

        TableColumn<EntryRow, String> colGram = new TableColumn<>();
        colGram.setCellValueFactory(d -> d.getValue().gramCatProperty());
        colGram.setPrefWidth(260);
        colGram.setGraphic(makeHeader("Catégorie", entryCatFilter));

        entryTable.getColumns().setAll(colId, colLemma, colGram);
        colId.setSortable(false);
        colLemma.setSortable(false);
        colGram.setSortable(false);
    }

    private void setupSenseTableWithHeaderFilters() {
        senseEntryIdFilter = createHeaderFilterField("Filtrer Entry...");
        senseIdFilter = createHeaderFilterField("Filtrer Sense...");
        senseCatFilter = createHeaderFilterField("Filtrer Catégorie...");
        senseGlossFilter = createHeaderFilterField("Filtrer Gloss...");

        TableColumn<SenseRow, String> colEntryId = new TableColumn<>();
        colEntryId.setCellValueFactory(d -> d.getValue().entryIdProperty());
        colEntryId.setPrefWidth(240);
        colEntryId.setGraphic(makeHeader("Entry ID", senseEntryIdFilter));

        TableColumn<SenseRow, String> colSenseId = new TableColumn<>();
        colSenseId.setCellValueFactory(d -> d.getValue().senseIdProperty());
        colSenseId.setPrefWidth(240);
        colSenseId.setGraphic(makeHeader("Sense ID", senseIdFilter));

        TableColumn<SenseRow, String> colCat = new TableColumn<>();
        colCat.setCellValueFactory(d -> d.getValue().categoryProperty());
        colCat.setPrefWidth(200);
        colCat.setGraphic(makeHeader("Catégorie", senseCatFilter));

        TableColumn<SenseRow, String> colGloss = new TableColumn<>();
        colGloss.setCellValueFactory(d -> d.getValue().glossProperty());
        colGloss.setPrefWidth(240);
        colGloss.setGraphic(makeHeader("Gloss", senseGlossFilter));

        senseTable.getColumns().setAll(colEntryId, colSenseId, colCat, colGloss);
        colEntryId.setSortable(false);
        colSenseId.setSortable(false);
        colCat.setSortable(false);
        colGloss.setSortable(false);
    }

    private VBox makeHeader(String title, TextField filterField) {
        Label lbl = new Label(title);
        lbl.getStyleClass().add("header-title");

        VBox box = new VBox(6, lbl, filterField);
        box.setPadding(new Insets(4, 6, 6, 6));
        box.setFillWidth(true);
        return box;
    }

    private TextField createHeaderFilterField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("header-filter");
        tf.setFocusTraversable(true);
        tf.setOnMouseClicked(e -> tf.requestFocus());
        return tf;
    }


    // =========================================================
    // FILTERED LISTS
    // =========================================================

    private void setupFilteringLists() {
        filteredEntries = new FilteredList<>(masterEntryRows, r -> true);
        sortedEntries = new SortedList<>(filteredEntries);
        sortedEntries.comparatorProperty().bind(entryTable.comparatorProperty());
        entryTable.setItems(sortedEntries);

        entryIdFilter.textProperty().addListener((obs, o, n) -> updateEntryPredicate());
        entryFormFilter.textProperty().addListener((obs, o, n) -> updateEntryPredicate());
        entryCatFilter.textProperty().addListener((obs, o, n) -> updateEntryPredicate());

        filteredSenses = new FilteredList<>(masterSenseRows, r -> true);
        sortedSenses = new SortedList<>(filteredSenses);
        sortedSenses.comparatorProperty().bind(senseTable.comparatorProperty());
        senseTable.setItems(sortedSenses);

        senseEntryIdFilter.textProperty().addListener((obs, o, n) -> updateSensePredicate());
        senseIdFilter.textProperty().addListener((obs, o, n) -> updateSensePredicate());
        senseCatFilter.textProperty().addListener((obs, o, n) -> updateSensePredicate());
        senseGlossFilter.textProperty().addListener((obs, o, n) -> updateSensePredicate());
    }

    private void updateEntryPredicate() {
        String fId = safe(entryIdFilter.getText());
        String fForm = safe(entryFormFilter.getText());
        String fCat = safe(entryCatFilter.getText());

        String global = safe(searchField.getText()); // <-- nouveau

        filteredEntries.setPredicate(r -> {
            String id = safe(r.id());
            String form = safe(r.lemma());
            String cat = safe(r.gramCat());

            // Filtre global (barre du haut)
            if (!global.isEmpty() && !(id.contains(global) || form.contains(global) || cat.contains(global))) {
                return false;
            }

            // Filtres colonnes
            if (!fId.isEmpty() && !id.contains(fId)) return false;
            if (!fForm.isEmpty() && !form.contains(fForm)) return false;
            if (!fCat.isEmpty() && !cat.contains(fCat)) return false;

            return true;
        });

        entriesCountLabel.setText(sortedEntries.size() + " entrées");
    }

    private void updateSensePredicate() {
        String fEntry = safe(senseEntryIdFilter.getText());
        String fSense = safe(senseIdFilter.getText());
        String fCat = safe(senseCatFilter.getText());
        String fGloss = safe(senseGlossFilter.getText());

        String global = safe(searchField.getText()); // <-- global

        filteredSenses.setPredicate(r -> {
            String entryId = safe(r.entryId());
            String senseId = safe(r.senseId());
            String cat = safe(r.category());
            String gloss = safe(r.gloss());

            // Global search (barre en haut)
            if (!global.isEmpty() && !(entryId.contains(global)
                    || senseId.contains(global)
                    || cat.contains(global)
                    || gloss.contains(global))) {
                return false;
            }

            // Column filters
            if (!fEntry.isEmpty() && !entryId.contains(fEntry)) return false;
            if (!fSense.isEmpty() && !senseId.contains(fSense)) return false;
            if (!fCat.isEmpty() && !cat.contains(fCat)) return false;
            if (!fGloss.isEmpty() && !gloss.contains(fGloss)) return false;

            return true;
        });

        sensesCountLabel.setText(sortedSenses.size() + " sens");
    }


    private String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    // =========================================================
    // SELECTION
    // =========================================================

    private void setupSelection() {
        entryTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedEntryRow = n;
            if (n == null) {
                morphTypeField.clear();
                gramCatField.clear();
                return;
            }
            morphTypeField.setText(n.morphType());
            gramCatField.setText(n.gramCat());
        });

        senseTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedSenseRow = n;
            if (n == null) {
                morphTypeField.clear();
                gramCatField.clear();
                return;
            }
            // placeholder mapping
            morphTypeField.setText(n.category());
            gramCatField.setText(n.gloss());
        });
    }

    // =========================================================
    // LOAD LIFT (lift-api)
    // =========================================================

    private void loadLiftFromResources(String resourcePath) {
        masterEntryRows.clear();
        masterSenseRows.clear();
        dictionary = null;
        currentFile = null;

        try {
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                showAlert(Alert.AlertType.ERROR, "Fichier introuvable", "Resource introuvable: " + resourcePath);
                return;
            }

            // ✅ Si on est en IDE, le resource est un vrai fichier => on sauvegarde directement dessus
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                Path p = Path.of(url.toURI());
                loadLiftFromPath(p);        // currentFile = p
                showToast("📄 Fichier chargé (mode fichier) : " + p.getFileName());
                return;
            }

            // ✅ Si on est en JAR, on ne peut pas écrire dans le JAR => on passe par un temp
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                Path tmp = Files.createTempFile("lift-", ".lift");
                Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);

                loadLiftFromPath(tmp);
                currentFile = null; // force Save As (car ce temp n'est pas le "vrai" fichier source)
                showToast("📦 Chargé depuis JAR (lecture seule) : Save As obligatoire");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le fichier.\n" + e.getMessage());
        }
    }

    private void loadLiftFromPath(Path path) throws Exception {
        masterEntryRows.clear();
        masterSenseRows.clear();

        currentFile = path;
        dictionary = liftService.load(path);

        fillFromLiftApi();
        updateEntryPredicate();
        updateSensePredicate();
    }
    private String firstText(MultiText mt) {
        if (mt == null) return "";
        return mt.getForms().stream()
                .findFirst()
                .map(Form::toString)
                .orElse("");
    }

    private String traitValue(java.util.List<LiftTrait> traits, String name) {
        if (traits == null) return "";
        for (LiftTrait t : traits) {
            if (name.equals(t.getName())) {
                return t.getValue();
            }
        }
        return "";
    }
    private String stripTags(String s) {
        if (s == null) return "";
        return s.replaceAll("<[^>]+>", "").trim();
    }

    private void fillFromLiftApi() {
        masterEntryRows.clear();
        masterSenseRows.clear();

        if (dictionary == null) {
            entriesCountLabel.setText("0 entrées");
            sensesCountLabel.setText("0 sens");
            return;
        }

        var comps = dictionary.getLiftDictionaryComponents();
        var entries = comps.getAllEntries();

        for (LiftEntry e : entries) {
            String entryId = e.getId().orElse("");

            // lemma: première forme disponible
            String lemma = stripTags(firstText(e.getForms()));

            // morph-type: trait côté entry
            String morphType = traitValue(e.getTraits(), "morph-type");

            // gramCat (catégorie) : la lib met grammatical-info surtout dans les senses
            // donc on prend le grammatical-info du 1er sense si présent
            String gramCat = "";
            if (!e.getSenses().isEmpty()) {
                LiftSense s0 = e.getSenses().get(0);
                gramCat = s0.getGrammaticalInfo()
                        .map(GrammaticalInfo::getGramInfoValue)
                        .orElse("");
            }

            masterEntryRows.add(new EntryRow(entryId, lemma, gramCat, morphType));

            // senses
            for (LiftSense s : e.getSenses()) {
                String senseId = s.getId().orElse("");

                String cat = s.getGrammaticalInfo()
                        .map(GrammaticalInfo::getGramInfoValue)
                        .orElse("");

                String gloss = stripTags(firstText(s.getGloss()));

                masterSenseRows.add(new SenseRow(entryId, senseId, cat, gloss));
            }
        }

        entriesCountLabel.setText(masterEntryRows.size() + " entrées");
        sensesCountLabel.setText(masterSenseRows.size() + " sens");
    }

    // =========================================================
    // RIGHT FORM
    // =========================================================

    @FXML
    private void applyEdit() {
        if (dictionary == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun fichier", "Charge un fichier .lift d’abord.");
            return;
        }

        // On applique uniquement si une entrée est sélectionnée dans la vue Entrées
        if (!entriesView.isVisible() || selectedEntryRow == null) {
            showAlert(Alert.AlertType.INFORMATION, "Sélection", "Sélectionne une entrée dans le tableau.");
            return;
        }

        String entryId = selectedEntryRow.id();
        LiftEntry entry = findEntryById(entryId);
        if (entry == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Entrée introuvable: " + entryId);
            return;
        }

        String newMorph = morphTypeField.getText() == null ? "" : morphTypeField.getText().trim();
        String newGram = gramCatField.getText() == null ? "" : gramCatField.getText().trim();

        // 1) morph-type (trait sur entry)
        if (!newMorph.isEmpty()) {
            upsertTraitOnEntry(entry, "morph-type", newMorph);
        } else {
            // si vide => on supprime le trait
            entry.getTraits().removeIf(t -> "morph-type".equals(t.getName()));
        }

        // 2) grammatical-info (dans le 1er sense, car c’est comme ça que tu l’affiches)
        if (!entry.getSenses().isEmpty()) {
            LiftSense s0 = entry.getSenses().get(0);
            if (!newGram.isEmpty()) {
                s0.setGrammaticalInfo(newGram);
            } else {
                s0.clearGrammaticalInfo();
            }

        }

        // 3) Rafraîchir les tables (simple et fiable)
        String keepSelected = entryId;
        fillFromLiftApi();
        updateEntryPredicate();
        updateSensePredicate();

        // Reselection
        for (EntryRow r : masterEntryRows) {
            if (r.id().equals(keepSelected)) {
                entryTable.getSelectionModel().select(r);
                entryTable.scrollTo(r);
                break;
            }
        }

        showToast("✅ Modifications appliquées");
    }


    @FXML
    private void saveFile() {
        if (dictionary == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun fichier", "Charge un fichier .lift d’abord.");
            return;
        }

        // ✅ Appliquer automatiquement avant de sauvegarder
        if (entriesView.isVisible() && selectedEntryRow != null) {
            applyEdit();
        }

        try {
            System.out.println("SAVE currentFile = " + currentFile);

            // ✅ si currentFile existe -> on écrase le même fichier
            if (currentFile != null) {
                dictionary.save(currentFile.toFile());
                showToast("✅ Sauvegardé : " + currentFile.toAbsolutePath());
                return;
            }

            // ✅ Sinon Save As
            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer sous...");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("LIFT (*.lift)", "*.lift"));

            Window w = (entryTable != null && entryTable.getScene() != null) ? entryTable.getScene().getWindow() : null;
            File out = fc.showSaveDialog(w);
            if (out == null) return;

            dictionary.save(out);
            currentFile = out.toPath();
            showToast("✅ Sauvegardé : " + out.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de sauvegarder.\n" + e.getMessage());
        }
    }


    @FXML
    private void cancelEdit() {
        morphTypeField.clear();
        gramCatField.clear();
    }

    // =========================================================
    // TOAST + ALERT
    // =========================================================

    private void showToast(String message) {
        // ✅ évite crash si la scène n’est pas encore prête
        if (entryTable == null || entryTable.getScene() == null || entryTable.getScene().getWindow() == null) {
            System.out.println("[TOAST] " + message);
            return;
        }

        Window owner = entryTable.getScene().getWindow();

        Label text = new Label(message);
        text.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600;");

        HBox box = new HBox(text);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle("""
        -fx-background-color: rgba(20, 20, 20, 0.92);
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0, 0, 6);
    """);

        Popup popup = new Popup();
        popup.getContent().add(box);
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.show(owner);

        Platform.runLater(() -> {
            double margin = 18;
            double x = owner.getX() + owner.getWidth() - box.getWidth() - margin;
            double y = owner.getY() + owner.getHeight() - box.getHeight() - margin;
            popup.setX(x);
            popup.setY(y);
        });

        box.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(120), box);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition wait = new PauseTransition(Duration.seconds(1.4));
        wait.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), box);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> popup.hide());
            fadeOut.play();
        });
        wait.play();
    }


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        DialogPane pane = a.getDialogPane();
        pane.setMinWidth(420);
        a.showAndWait();
    }

    // =========================================================
    // MODELS (tu pourras les déplacer dans org.example.model)
    // =========================================================

    public static class EntryRow {
        private final SimpleStringProperty id = new SimpleStringProperty("");
        private final SimpleStringProperty lemma = new SimpleStringProperty("");
        private final SimpleStringProperty gramCat = new SimpleStringProperty("");
        private final SimpleStringProperty morphType = new SimpleStringProperty("");

        public EntryRow(String id, String lemma, String gramCat, String morphType) {
            this.id.set(id);
            this.lemma.set(lemma);
            this.gramCat.set(gramCat);
            this.morphType.set(morphType);
        }

        public SimpleStringProperty idProperty() { return id; }
        public SimpleStringProperty lemmaProperty() { return lemma; }
        public SimpleStringProperty gramCatProperty() { return gramCat; }

        public String id() { return id.get(); }
        public String lemma() { return lemma.get(); }
        public String gramCat() { return gramCat.get(); }
        public String morphType() { return morphType.get(); }
    }

    public static class SenseRow {
        private final SimpleStringProperty entryId = new SimpleStringProperty("");
        private final SimpleStringProperty senseId = new SimpleStringProperty("");
        private final SimpleStringProperty category = new SimpleStringProperty("");
        private final SimpleStringProperty gloss = new SimpleStringProperty("");

        public SenseRow(String entryId, String senseId, String category, String gloss) {
            this.entryId.set(entryId);
            this.senseId.set(senseId);
            this.category.set(category);
            this.gloss.set(gloss);
        }

        public SimpleStringProperty entryIdProperty() { return entryId; }
        public SimpleStringProperty senseIdProperty() { return senseId; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleStringProperty glossProperty() { return gloss; }

        public String entryId() { return entryId.get(); }
        public String senseId() { return senseId.get(); }
        public String category() { return category.get(); }
        public String gloss() { return gloss.get(); }
    }
}
