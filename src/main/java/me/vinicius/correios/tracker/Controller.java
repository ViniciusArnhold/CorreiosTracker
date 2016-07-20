package me.vinicius.correios.tracker;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.vinicius.correios.api.Event;
import me.vinicius.correios.api.Rastreamento;


class Controller {

    @FXML
    private ComboBox comboBox;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> codeListView;

    @FXML
    private VBox vBox;

    @FXML
    private HBox hBox;

    @FXML
    private Button bAdd;

    @FXML
    private Button bView;

    @FXML
    private Button bDelete;

    @FXML
    private ListView<String> eventListView;

    private Stage primaryStage;

    private ObservableList<String> codeList;

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void initialize() {
        assert codeListView != null : "fx:id=\"codeListView\" was not injected: check your FXML file 'interface.fxml'.";
        assert vBox != null : "fx:id=\"vBox\" was not injected: check your FXML file 'interface.fxml'.";
        assert hBox != null : "fx:id=\"hBox\" was not injected: check your FXML file 'interface.fxml'.";
        assert bAdd != null : "fx:id=\"bAdd\" was not injected: check your FXML file 'interface.fxml'.";
        assert bView != null : "fx:id=\"bEdit\" was not injected: check your FXML file 'interface.fxml'.";
        assert bDelete != null : "fx:id=\"bDelete\" was not injected: check your FXML file 'interface.fxml'.";
        assert eventListView != null : "fx:id=\"eventListView\" was not injected: check your FXML file 'interface.fxml'.";
        assert comboBox != null : "fx:id=\"comboBox\" was not injected: check your FXML file 'interface.fxml'.";
        //BEGIN controls option settings
        //ComboBox
        comboBox.setEditable(true);
        //Lists
        codeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        codeListView.setEditable(false);
        eventListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        eventListView.setEditable(false);
        eventListView.setOnKeyPressed(event -> {
            if (event.isControlDown() && Objects.equals(event.getCode().getName(), "C")) {
                ObservableList rowList = (ObservableList) eventListView.getSelectionModel().getSelectedItems();
                StringBuilder clipboardString = new StringBuilder();
                for (Object row : rowList) {
                    if (row == null) {
                        row = "";
                    }
                    clipboardString.append(row);
                    clipboardString.append("\n");
                }
                final ClipboardContent content = new ClipboardContent();
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        });
        //Buttons
        bDelete.setStyle("-fx-base: red;");
        //END controls option settings

        /* BEGIN Buttons Action Handlers */
        bAdd.setOnAction(event -> {
            if (comboBox.getSelectionModel().getSelectedItem() != null) {
                String code = comboBox.getSelectionModel().getSelectedItem().toString().toUpperCase();
                Matcher m = Pattern.compile("[A-Z]{2}\\d{9}[A-Z]{1,3}$").matcher(code); //Quick Match
                if (m.find()) {
                    if (!codeListView.getItems().contains(code)) {
                        codeListView.getItems().add(0, code);
                        comboBox.setStyle("-fx-focus-color: #338FFF");
                        comboBox.requestFocus();
                    } else {
                        codeListView.requestFocus();
                        codeListView.scrollTo(code);
                        codeListView.getSelectionModel().select(code);
                        codeListView.getFocusModel().focus(codeListView.getItems().indexOf(code));
                    }
                } else {
                    comboBox.setStyle("-fx-focus-color: red");
                    comboBox.requestFocus();
                }
            } else {
                comboBox.setStyle("-fx-focus-color: red");
                comboBox.requestFocus();
            }
        });
        bView.setOnAction(event -> {
            if (codeListView.getItems().size() != 0 && codeListView.getSelectionModel().getSelectedItem() != null) {
                eventListView.getItems().clear();
                Rastreamento res = new Rastreamento(codeListView.getSelectionModel().getSelectedItem());
                try {
                    res.track();
                } catch (IOException e) {
                    eventListView.getItems().add("Connection Timed Out: Check your connection.");
                } finally {
                    Event eventsReversed[] = res.getEvents() != null ? res.getEvents() : null;
                    if (eventsReversed != null) {
                        Event events[] = new Event[eventsReversed.length];
                        //Sort the array in chronological order
                        if (events.length == 0) {
                            eventListView.getItems().add("Package not found, check you code");
                        } else {
                            for (int i = 0; i < eventsReversed.length; i++) {
                                events[i] = eventsReversed[eventsReversed.length - i - 1];
                            }
                            for (Event e : events) {
                                eventListView.getItems().add(0, "");
                                eventListView.getItems().add(0, e.getMovement());
                                eventListView.getItems().add(0, "Estava em: " + e.getLocal());
                                eventListView.getItems().add(0, e.getAction() + " em " + e.getData());
                            }
                            //Notification -  For Testing
                            String title = "Rastreamento de: " + codeListView.getSelectionModel().getSelectedItem() + "\n" +
                                    eventsReversed[0].getAction() + " em " + eventsReversed[0].getData();
                            String message = eventsReversed[0].getMovement();
                            Notifications notification = Notifications.SUCCESS;
                            TrayNotification tray = new TrayNotification(title, message, notification);
                            tray.setImage(new Image("http://globalestudio.com.br/loja/image/cache/catalog/PRODUTOS/modulosmodulo-de-frete-correios-opencart-54-746x746.jpg"));
                            tray.setAnimation(Animations.FADE);
                            tray.showAndDismiss(Duration.seconds(3));
                        }
                    } else {
                        System.out.println("Events is null");
                    }
                }
            }
        });
        bDelete.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("This will delete the value: " + codeListView.getSelectionModel().getSelectedItem());
            alert.setContentText("Are you sure?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                codeListView.getItems().remove(codeListView.getSelectionModel().getSelectedItem());
            }

        });
        /* END Buttons Action Handlers */
        //ComboBox Action Events
        comboBox.setOnAction(event -> {
        });
        //Primary Stage events
        //Save Properties
        primaryStage.setOnCloseRequest(event -> {
            Preferences pref = Preferences.userNodeForPackage(getClass());
            pref.putBoolean("notFirstRun", true);
            pref.put("codeList", codeListView.getItems().toString());


        });
        //Load Properties
        primaryStage.setOnShown(event -> {
            Preferences pref = Preferences.userNodeForPackage(getClass());
            //try {pref.clear();} catch (BackingStoreException e) {e.printStackTrace();}//Debug
            if (pref.getBoolean("notFirstRun", false)) { // Preferences Exists
                String str = null;
                str = pref.get("codeList", "");
                if (str != null) {
                    if (str.length() != 0) {
                        Matcher m = Pattern.compile("[A-Z]{2}\\d{9}[A-Z]{1,3}").matcher(str); //This differs from the
                        // first because now we will have more than one tracking code
                        while (m.find()) {
                            comboBox.getItems().add(0, m.group());
                            codeListView.getItems().add(0, m.group());
                        }
                    }
                } else {
                }

            } else { //First Run
                //Only set "notFirstRun" when program is legally closed, avoid errors due to crashes
            }


        });


    }
}
