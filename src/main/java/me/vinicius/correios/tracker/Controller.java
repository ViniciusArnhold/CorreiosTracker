package me.vinicius.correios.tracker;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.vinicius.correios.api.Event;
import me.vinicius.correios.api.Rastreamento;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Controller {

    @FXML
    private ComboBox comboBox;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> codeList;

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
    private ListView<String> eventList;

    private Stage primaryStage;

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void initialize() {
        assert codeList != null : "fx:id=\"codeList\" was not injected: check your FXML file 'interface.fxml'.";
        assert vBox != null : "fx:id=\"vBox\" was not injected: check your FXML file 'interface.fxml'.";
        assert hBox != null : "fx:id=\"hBox\" was not injected: check your FXML file 'interface.fxml'.";
        assert bAdd != null : "fx:id=\"bAdd\" was not injected: check your FXML file 'interface.fxml'.";
        assert bView != null : "fx:id=\"bEdit\" was not injected: check your FXML file 'interface.fxml'.";
        assert bDelete != null : "fx:id=\"bDelete\" was not injected: check your FXML file 'interface.fxml'.";
        assert eventList != null : "fx:id=\"eventList\" was not injected: check your FXML file 'interface.fxml'.";
        assert comboBox != null : "fx:id=\"comboBox\" was not injected: check your FXML file 'interface.fxml'.";


        //BEGIN controls option settings
        //ComboBox
        comboBox.setEditable(true);


        //Lists
        codeList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        codeList.setEditable(false);

        eventList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        eventList.setEditable(false);
        eventList.setOnKeyPressed(event -> {
            if (event.isControlDown() && Objects.equals(event.getCode().getName(), "C")) {
                ObservableList rowList = (ObservableList) eventList.getSelectionModel().getSelectedItems();
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
            System.out.println(comboBox.getValue());
            if (comboBox.getValue() != null) {
                String code = comboBox.getValue().toString().toUpperCase();
                Matcher m = Pattern.compile("[A-Z]{2}\\d{9}[A-Z]*").matcher(code); //Quick Match
                if (m.find()) {
                    if (!codeList.getItems().contains(code)) {
                        codeList.getItems().add(0, code);
                        comboBox.setStyle("-fx-focus-color: #338FFF");
                        comboBox.requestFocus();
                    } else {
                        codeList.requestFocus();
                        codeList.scrollTo(code);
                        codeList.getSelectionModel().select(code);
                        codeList.getFocusModel().focus(codeList.getItems().indexOf(code));
                    }
                } else {
                    comboBox.setStyle("-fx-focus-color: red");
                    comboBox.requestFocus();
                }
            } else {
                comboBox.setStyle("-fx-focus-color: red");
                comboBox.requestFocus();
            }
            System.out.println("Clicked on Added");//Debug
        });
        bView.setOnAction(event -> {
            System.out.println("Clicked on View");//Debug
            if (codeList.getItems().size() != 0 && codeList.getSelectionModel().getSelectedItem() != null) {

                eventList.getItems().clear();
                Rastreamento res = new Rastreamento(codeList.getSelectionModel().getSelectedItem());
                try {
                    res.track();
                } catch (IOException e) {
                    eventList.getItems().add("Connection Timed Out: Check your connection.");
                } finally {
                    Event eventsReversed[] = res.getEvents() != null ? res.getEvents() : null;
                    if (eventsReversed != null) {
                        Event events[] = new Event[eventsReversed.length];

                        //Sort the array in chronological order
                        if (events.length == 0) {
                            eventList.getItems().add("Package not found, check you code");
                        } else {
                            for (int i = 0; i < eventsReversed.length; i++) {
                                events[i] = eventsReversed[eventsReversed.length - i - 1];
                            }
                            for (Event e : events) {
                                eventList.getItems().add(0, "");
                                eventList.getItems().add(0, e.getMovement());
                                eventList.getItems().add(0, "Estava em: " + e.getLocal());
                                eventList.getItems().add(0, e.getAction() + " em " + e.getData());
                            }
                        }
                    } else {
                        System.out.println("Events is null");
                    }
                }
            }
        });
        bDelete.setOnAction(event -> {
            System.out.println("Clicked on Delete"); //Debug
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("This will delete the value: " + codeList.getSelectionModel().getSelectedItem());
            alert.setContentText("Are you sure?");

            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                codeList.getItems().remove(codeList.getSelectionModel().getSelectedItem());
            }

        });
        /* END Buttons Action Handlers */

        //ComboBox Action Events
        comboBox.setOnAction(event -> {

        });

        //Primary Stage events
        //Save Properties
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Primary Stage is closing");//Debug
            try {
                FileOutputStream fos = new FileOutputStream("c:/myconfig.property");
                Properties prop = new Properties();

                int index = 0;
                for (String s : codeList.getItems()) {

                    prop.put("me.viniciusarnhold.correios.tracker.codeList", codeList.getItems().get(0).toString());
                    prop.store(fos, "List of codes");
                    fos.flush();

                    fos.close();
                }

            } catch (FileNotFoundException e) {
                eventList.getItems().add(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Load Properties
        primaryStage.setOnShown(event -> {
            System.out.println("Primary Stage is Opening");

            InputStream input = null;
            Properties prop = new Properties();

            try {
                input = new FileInputStream("c:/myconfig.property");
                prop.load(input);
                codeList.getItems().add(prop.getProperty("me.viniciusarnhold.correios.tracker.codeList"));

            } catch (FileNotFoundException e) {
                //First Run, expected
                System.out.println("Properties not found");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        eventList.getItems().add(e.getMessage());
                    }
                }
            }


        });


    }
}
