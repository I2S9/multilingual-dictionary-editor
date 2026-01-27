package org.example;

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
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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

    // DOM
    private Document liftDoc;

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

        loadLiftFromResources("/lift/20240828Lift.lift");

        setupSelection();

        showEntries();
    }

    // =========================================================
    // VIEW SWITCH
    // =========================================================

    @FXML
    private void showEntries() {
        setActive(btnEntries);
        setView(entriesView, sensesView);
        editSubTitleLabel.setText("Entrée sélectionnée");
    }

    @FXML
    private void showSenses() {
        setActive(btnSenses);
        setView(sensesView, entriesView);
        editSubTitleLabel.setText("Sens sélectionné");
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("LIFT (*.lift)", "*.lift"));

        Window owner = entryTable.getScene().getWindow();
        File f = fc.showOpenDialog(owner);
        if (f == null) return;

        try {
            loadLiftFromFile(f.toPath());
            showToast("✅ Fichier chargé : " + f.getName());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d’ouvrir le fichier.\n" + e.getMessage());
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

        // IMPORTANT: prevent sorting when clicking inside the filter field
        tf.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseEvent::consume);
        tf.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);

        return tf;
    }

    // =========================================================
    // FILTERED LISTS (focus stable)
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

        filteredEntries.setPredicate(r -> {
            if (!fId.isEmpty() && !safe(r.id()).contains(fId)) return false;
            if (!fForm.isEmpty() && !safe(r.lemma()).contains(fForm)) return false;
            if (!fCat.isEmpty() && !safe(r.gramCat()).contains(fCat)) return false;
            return true;
        });

        entriesCountLabel.setText(sortedEntries.size() + " entrées");
    }

    private void updateSensePredicate() {
        String fEntry = safe(senseEntryIdFilter.getText());
        String fSense = safe(senseIdFilter.getText());
        String fCat = safe(senseCatFilter.getText());
        String fGloss = safe(senseGlossFilter.getText());

        filteredSenses.setPredicate(r -> {
            if (!fEntry.isEmpty() && !safe(r.entryId()).contains(fEntry)) return false;
            if (!fSense.isEmpty() && !safe(r.senseId()).contains(fSense)) return false;
            if (!fCat.isEmpty() && !safe(r.category()).contains(fCat)) return false;
            if (!fGloss.isEmpty() && !safe(r.gloss()).contains(fGloss)) return false;
            return true;
        });

        sensesCountLabel.setText(sortedSenses.size() + " sens");
    }

    private String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    // =========================================================
    // SELECTION (simple mapping for now)
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
            // simple mapping: category->morphTypeField, gloss->gramCatField (tu renommeras après)
            morphTypeField.setText(n.category());
            gramCatField.setText(n.gloss());
        });
    }

    // =========================================================
    // LOAD LIFT
    // =========================================================

    private void loadLiftFromResources(String resourcePath) {
        masterEntryRows.clear();
        masterSenseRows.clear();
        liftDoc = null;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                showAlert(Alert.AlertType.ERROR, "Fichier introuvable", "Resource introuvable: " + resourcePath);
                return;
            }

            liftDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            liftDoc.getDocumentElement().normalize();
            fillFromDom();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le fichier.\n" + e.getMessage());
        }
    }

    private void loadLiftFromFile(Path path) throws Exception {
        masterEntryRows.clear();
        masterSenseRows.clear();

        liftDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(path));
        liftDoc.getDocumentElement().normalize();

        fillFromDom();
    }

    private void fillFromDom() {
        NodeList entryNodes = liftDoc.getElementsByTagName("entry");

        for (int i = 0; i < entryNodes.getLength(); i++) {
            Element entryEl = (Element) entryNodes.item(i);
            String entryId = entryEl.getAttribute("id");

            String lemma = extractFirstText(entryEl, "lexical-unit", "form", "text");

            String gramCat = "";
            NodeList gramNodes = entryEl.getElementsByTagName("grammatical-info");
            if (gramNodes.getLength() > 0) gramCat = ((Element) gramNodes.item(0)).getAttribute("value");

            String morphType = "";
            NodeList traits = entryEl.getElementsByTagName("trait");
            for (int t = 0; t < traits.getLength(); t++) {
                Element tr = (Element) traits.item(t);
                if ("morph-type".equals(tr.getAttribute("name"))) {
                    morphType = tr.getAttribute("value");
                    break;
                }
            }

            masterEntryRows.add(new EntryRow(entryId, lemma, gramCat, morphType));

            NodeList senses = entryEl.getElementsByTagName("sense");
            for (int s = 0; s < senses.getLength(); s++) {
                Element senseEl = (Element) senses.item(s);
                String senseId = senseEl.getAttribute("id");

                String cat = "";
                NodeList gi = senseEl.getElementsByTagName("grammatical-info");
                if (gi.getLength() > 0) cat = ((Element) gi.item(0)).getAttribute("value");

                String gloss = extractFirstText(senseEl, "gloss", "text");

                masterSenseRows.add(new SenseRow(entryId, senseId, cat, gloss));
            }
        }

        updateEntryPredicate();
        updateSensePredicate();
    }

    private String extractFirstText(Element root, String... tags) {
        Node current = root;
        for (String tag : tags) {
            if (!(current instanceof Element)) return "";
            NodeList list = ((Element) current).getElementsByTagName(tag);
            if (list.getLength() == 0) return "";
            current = list.item(0);
        }
        return current.getTextContent() == null ? "" : current.getTextContent().trim();
    }

    // =========================================================
    // RIGHT FORM (placeholder)
    // =========================================================

    @FXML
    private void applyEdit() {
        showToast("✅ Appliquer (logique édition à compléter)");
    }

    @FXML
    private void saveFile() {
        if (liftDoc == null) return;
        try {
            Path out = Path.of(System.getProperty("user.home"), "written.lift");
            writeDocument(liftDoc, out);
            showToast("✅ Sauvegardé : " + out.getFileName());
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

    private void writeDocument(Document doc, Path outFile) throws Exception {
        Files.createDirectories(outFile.getParent());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        try (OutputStream os = Files.newOutputStream(outFile)) {
            transformer.transform(new DOMSource(doc), new StreamResult(os));
        }
    }

    // =========================================================
    // TOAST + ALERT
    // =========================================================

    private void showToast(String message) {
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
    // MODELS
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
