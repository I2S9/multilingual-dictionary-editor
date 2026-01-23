package org.example;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainController {

    @FXML private VBox sidebar;

    @FXML private TableView<EntryRow> entryTable;

    @FXML private TextField morphTypeField;
    @FXML private TextField gramCatField;

    private boolean sidebarVisible = true;

    private final ObservableList<EntryRow> rows = FXCollections.observableArrayList();

    // DOM en mémoire
    private Document liftDoc;

    // entrée sélectionnée dans le DOM
    private Element selectedEntryEl;

    // ligne sélectionnée dans la table
    private EntryRow selectedRow;

    @FXML
    private void initialize() {
        setupEntryTable();
        loadLiftFromResources("/lift/20240828Lift.lift");
        setupSelection();
    }

    private void setupEntryTable() {
        TableColumn<EntryRow, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setPrefWidth(260);

        TableColumn<EntryRow, String> colLemma = new TableColumn<>("Forme");
        colLemma.setCellValueFactory(data -> data.getValue().lemmaProperty());
        colLemma.setPrefWidth(250);

        TableColumn<EntryRow, String> colGram = new TableColumn<>("Catégorie");
        colGram.setCellValueFactory(data -> data.getValue().gramCatProperty());
        colGram.setPrefWidth(260);

        entryTable.getColumns().setAll(colId, colLemma, colGram);
        entryTable.setItems(rows);
    }

    private void setupSelection() {
        entryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedRow = newV;
            selectedEntryEl = null;

            if (newV == null || liftDoc == null) {
                morphTypeField.clear();
                gramCatField.clear();
                return;
            }

            // retrouver l'élément <entry> dans le DOM par id
            NodeList entryNodes = liftDoc.getElementsByTagName("entry");
            for (int i = 0; i < entryNodes.getLength(); i++) {
                Element e = (Element) entryNodes.item(i);
                if (newV.id().equals(e.getAttribute("id"))) {
                    selectedEntryEl = e;
                    break;
                }
            }

            morphTypeField.setText(newV.morphType());
            gramCatField.setText(newV.gramCat());
        });
    }

    private void loadLiftFromResources(String resourcePath) {
        rows.clear();
        liftDoc = null;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Fichier introuvable dans resources: " + resourcePath);
                return;
            }

            liftDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            liftDoc.getDocumentElement().normalize();

            NodeList entryNodes = liftDoc.getElementsByTagName("entry");

            for (int i = 0; i < entryNodes.getLength(); i++) {
                Element entryEl = (Element) entryNodes.item(i);

                String id = entryEl.getAttribute("id");

                // lexical-unit / form / text
                String lemma = "";
                NodeList lexUnits = entryEl.getElementsByTagName("lexical-unit");
                if (lexUnits.getLength() > 0) {
                    Element lex = (Element) lexUnits.item(0);
                    NodeList forms = lex.getElementsByTagName("form");
                    if (forms.getLength() > 0) {
                        Element form = (Element) forms.item(0);
                        NodeList texts = form.getElementsByTagName("text");
                        if (texts.getLength() > 0) {
                            lemma = texts.item(0).getTextContent().trim();
                        }
                    }
                }

                // grammatical-info value="..."
                String gramCat = "";
                NodeList gramNodes = entryEl.getElementsByTagName("grammatical-info");
                if (gramNodes.getLength() > 0) {
                    Element g = (Element) gramNodes.item(0);
                    gramCat = g.getAttribute("value");
                }

                // trait name="morph-type"
                String morphType = "";
                NodeList traits = entryEl.getElementsByTagName("trait");
                for (int t = 0; t < traits.getLength(); t++) {
                    Element tr = (Element) traits.item(t);
                    if ("morph-type".equals(tr.getAttribute("name"))) {
                        morphType = tr.getAttribute("value");
                        break;
                    }
                }

                rows.add(new EntryRow(id, lemma, gramCat, morphType));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== BUTTONS =====================

    @FXML
    private void saveEntry() {
        if (selectedEntryEl == null || selectedRow == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionne une entrée d'abord.");
            return;
        }

        try {
            String newMorph = morphTypeField.getText() == null ? "" : morphTypeField.getText().trim();
            String newGram  = gramCatField.getText() == null ? "" : gramCatField.getText().trim();

            // 1) modifier le DOM
            upsertMorphTypeTrait(selectedEntryEl, newMorph);
            upsertGrammaticalInfo(selectedEntryEl, newGram);

            // 2) écrire un fichier de sortie (safe)
            Path out = Path.of(System.getProperty("user.home"), "written.lift");
            writeDocument(liftDoc, out);

            // 3) mettre à jour la table
            selectedRow.setGramCat(newGram);
            selectedRow.setMorphType(newMorph);
            entryTable.refresh();

            // 4) toast + clear
            showToast("✅ Sauvegardé : written.lift");

            entryTable.getSelectionModel().clearSelection();
            selectedEntryEl = null;
            selectedRow = null;
            morphTypeField.clear();
            gramCatField.clear();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer.\n" + ex.getMessage());
        }
    }

    @FXML
    private void cancelEdit() {
        if (selectedRow == null) {
            morphTypeField.clear();
            gramCatField.clear();
            return;
        }
        morphTypeField.setText(selectedRow.morphType());
        gramCatField.setText(selectedRow.gramCat());
    }

    @FXML
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
        sidebar.setManaged(sidebarVisible);
    }

    // ===================== DOM UPDATE HELPERS =====================

    private void upsertGrammaticalInfo(Element entryEl, String value) {
        NodeList gramNodes = entryEl.getElementsByTagName("grammatical-info");
        Element gramEl;

        if (gramNodes.getLength() > 0) {
            gramEl = (Element) gramNodes.item(0);
        } else {
            gramEl = entryEl.getOwnerDocument().createElement("grammatical-info");
            entryEl.appendChild(gramEl);
        }
        gramEl.setAttribute("value", value);
    }

    private void upsertMorphTypeTrait(Element entryEl, String value) {
        NodeList traits = entryEl.getElementsByTagName("trait");
        Element morphTrait = null;

        for (int i = 0; i < traits.getLength(); i++) {
            Element tr = (Element) traits.item(i);
            if ("morph-type".equals(tr.getAttribute("name"))) {
                morphTrait = tr;
                break;
            }
        }

        if (morphTrait == null) {
            morphTrait = entryEl.getOwnerDocument().createElement("trait");
            morphTrait.setAttribute("name", "morph-type");
            entryEl.appendChild(morphTrait);
        }

        morphTrait.setAttribute("value", value);
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

    // ===================== SMALL POPUP (BOTTOM RIGHT) =====================

    private void showToast(String message) {
        Window owner = entryTable.getScene().getWindow();

        javafx.scene.control.Label text = new javafx.scene.control.Label(message);
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

    // ===================== SIMPLE ALERT (NO APP CSS) =====================

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);

        // IMPORTANT: on n'applique PAS ton styles.css ici (sinon Alert devient géant)
        DialogPane pane = a.getDialogPane();
        pane.setMinWidth(420);

        a.showAndWait();
    }

    // ===================== MODEL =====================

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
        public String gramCat() { return gramCat.get(); }
        public String morphType() { return morphType.get(); }

        public void setGramCat(String v) { gramCat.set(v); }
        public void setMorphType(String v) { morphType.set(v); }
    }
}
