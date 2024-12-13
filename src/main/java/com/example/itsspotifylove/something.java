package com.example.itsspotifylove;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

 class MusicDownloader extends Application {

    private static final String API_KEY = "AIzaSyBzVE5k6xoxz6_56f5Mj-t8PAnZL8Y5Q2Q";
    private TableView<String> resultsTable;
    private TextField searchField;
    private List<String> videoIds;
    private ObservableList<String> downloadedSongs;
    private VBox sidebar;
    private boolean sidebarVisible = false;
    private ComboBox<String> formatComboBox;
    //array list to store credentials:
    private final ArrayList<String[]> credentials = new ArrayList<>();
    private final File credentialsFile = new File("credentials.txt");

    public void start(Stage primaryStage) {

        loadCredentialsFromFile();
        BorderPane bp = new BorderPane();

        Image image = new Image("file:C:/Users/xehib/Downloads/WhatsApp Image 2024-12-11 at 6.00.55 AM.jpeg"); // Update path if necessary
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(800);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        bp.setTop(imageView);
// Center GridPane for login form
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox vbox=new VBox();

        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(new Font("Arial",  25));
        usernameLabel.setStyle(" -fx-text-fill:#FFD700 ; -fx-font-weight: bold;");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle(" -fx-text-fill:#FFD700 ; -fx-font-weight: bold;");
        passwordLabel.setFont(new Font("Arial",  25));
        PasswordField passwordField = new PasswordField();
        vbox.getChildren().addAll(usernameLabel,usernameField,passwordLabel,passwordField );
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold ;");
        loginButton.setPrefHeight(300);
        loginButton.setPrefWidth(150);

        Button saveButton = new Button("Sign up");
        saveButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        saveButton.setPrefHeight(300);
        saveButton.setPrefWidth(100);
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: black; -fx-font-weight: bold;");
        exitButton.setPrefHeight(300);
        exitButton.setPrefWidth(100);

        Label notificationLabel = new Label();
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);



        GridPane buttonPane = new GridPane();
        buttonPane.setHgap(30);
        buttonPane.setVgap(100);
        buttonPane.add(loginButton, 0, 2);
        buttonPane.add(saveButton, 1, 2);
        buttonPane.add(exitButton, 2, 2);
        gridPane.add(buttonPane, 0, 2, 2, 1);


        gridPane.add(notificationLabel, 0, 3, 2, 1);
        notificationLabel.setAlignment(Pos.CENTER);



        bp.setCenter(gridPane);
        bp.getChildren().add(vbox);
        bp.setStyle("-fx-background-color: #000000;");

        //log in button:
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateCredentials(username, password)) {
                notificationLabel.setText("Login successful!");
                // openNewWindow(username);
                openMain(primaryStage);
                //primaryStage.hide();
            } else {
                notificationLabel.setText("Invalid username or password!");
            }
        });
        saveButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                saveCredentialsToFile(username, password);
                notificationLabel.setText("Credentials saved successfully!");
            } else {
                notificationLabel.setText("Username and password cannot be empty!");
            }
        });
        exitButton.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(bp, 800, 600);

        primaryStage.setTitle("Login Application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    private void loadCredentialsFromFile() {
        try {
            if (!credentialsFile.exists()) {
                // If the file doesn't exist, create it
                credentialsFile.createNewFile();
            }


            Scanner scanner = new Scanner(credentialsFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    credentials.add(parts);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading credentials: " + e.getMessage());
        }
    }

    private boolean validateCredentials(String username, String password) {

        for (String[] pair : credentials) {
            if (pair[0].equals(username) && pair[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void saveCredentialsToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            credentials.add(new String[]{username, password}); // Update in-memory list as well
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }

    private void openNewWindow(String username) {

        Stage newStage = new Stage();
        Label welcomeLabel = new Label("Welcome, " + username + "!");
        welcomeLabel.setAlignment(Pos.CENTER);

        Scene scene = new Scene(welcomeLabel, 400, 200);
        newStage.setTitle("Welcome");
        newStage.setScene(scene);
        newStage.show();
    }





    public void openMain(Stage stage) {
        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #121212;");

        // Top: Search bar and button
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #333333;");
        searchField = new TextField();
        searchField.setPromptText("Enter song name...");
        searchField.setStyle("-fx-background-color: #222222; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setPrefWidth(450);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        searchButton.setOnAction(e -> searchSongs());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchSongs();
            }
        });

        topBar.getChildren().addAll(searchField, searchButton);

        // Center: Search results (TableView)
        resultsTable = new TableView<>();
        resultsTable.setPlaceholder(new Label("No results found."));
        resultsTable.setStyle("-fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-font-size: 14px;");

        TableColumn<String, String> titleColumn = new TableColumn<>("Searched Results:");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        titleColumn.setPrefWidth(550);

        resultsTable.getColumns().add(titleColumn);

        // Sidebar for downloaded songs
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #282828; -fx-border-color: #FFD700;");
        sidebar.setPrefWidth(350);

        Label sidebarTitle = new Label("Downloaded Songs");
        sidebarTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFD700; -fx-font-weight: bold;");

        ListView<String> downloadedList = new ListView<>();
        downloadedSongs = FXCollections.observableArrayList();
        downloadedList.setItems(downloadedSongs);
        downloadedList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                String selectedSong = downloadedList.getSelectionModel().getSelectedItem();
                if (selectedSong != null) {
                    // Extract the song title and format (if included in the song name)
                    String songTitle = selectedSong.split(" \\(")[0]; // Get the song title without the format
                    String selectedFormat = formatComboBox.getValue(); // Get the format selected by the user

                    // Construct the file path with the selected format
                    String filePath = "downloads/" + songTitle + "." + selectedFormat.toLowerCase();

                    // Play the song with the appropriate file path and song title
                    playSong(filePath, selectedSong);
                }
            }
        });


        sidebar.getChildren().addAll(sidebarTitle, downloadedList);
        sidebar.setVisible(false);

        // Bottom bar with buttons and combo box
        Button toggleSidebarButton = new Button("Downloads");
        toggleSidebarButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        toggleSidebarButton.setPrefWidth(130);
        toggleSidebarButton.setOnAction(e -> {
            sidebarVisible = !sidebarVisible;
            sidebar.setVisible(sidebarVisible);
        });

        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("MP3 (Audio)", "MP4 (Video)", "WAV");
        formatComboBox.setValue("MP3 (Audio)");
        formatComboBox.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        formatComboBox.setPrefWidth(150);

        Button downloadButton = new Button("Download Selected");
        downloadButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        downloadButton.setOnAction(e -> downloadSelectedSong());

        HBox bottomBar = new HBox(10, toggleSidebarButton, formatComboBox, downloadButton);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setStyle("-fx-background-color: #333333;");

        // Set layout sections
        root.setTop(topBar);
        root.setCenter(resultsTable);
        root.setBottom(bottomBar);
        root.setLeft(sidebar);

        // Scene and stage setup
        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Music Downloader");
        stage.setScene(scene);
        stage.show();
    }

    private void playSong(String filePath, String title) {
        File file = new File(filePath);
        if (!file.exists()) {
            showAlert("Error", "File not found: " + filePath, Alert.AlertType.ERROR);
            return;
        }

        Media media = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        Stage playerStage = new Stage();
        playerStage.setTitle("Playing: " + title);

        BorderPane playerPane = new BorderPane();
        playerPane.setCenter(mediaView);
        playerPane.setStyle("-fx-background-color: black;");

        Scene playerScene = new Scene(playerPane, 800, 600);
        playerStage.setScene(playerScene);
        playerStage.show();

        mediaPlayer.play();

        playerStage.setOnCloseRequest(e -> mediaPlayer.stop());
    }

    private void searchSongs() {
        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            showAlert("Error", "Search box cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        try {
            String searchUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=5&q="
                    + URLEncoder.encode(search, "UTF-8") + "&key=" + API_KEY;

            HttpURLConnection connection = (HttpURLConnection) new URL(searchUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.lines().collect(Collectors.joining());
            in.close();

            List<String> results = parseYouTubeResponse(response);
            if (results.isEmpty()) {
                showAlert("No Results", "No results found for the query.", Alert.AlertType.INFORMATION);
            }

            resultsTable.setItems(FXCollections.observableArrayList(results));

        } catch (Exception e) {
            showAlert("Error", "Failed to fetch results: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<String> parseYouTubeResponse(String jsonResponse) {
        videoIds = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        if (!responseObject.has("items")) {
            return titles;
        }

        JsonArray items = responseObject.getAsJsonArray("items");
        for (JsonElement item : items) {
            JsonObject itemObject = item.getAsJsonObject();
            JsonObject idObject = itemObject.getAsJsonObject("id");
            JsonObject snippetObject = itemObject.getAsJsonObject("snippet");

            if (idObject.has("videoId") && snippetObject.has("title")) {
                String videoId = idObject.get("videoId").getAsString();
                String title = snippetObject.get("title").getAsString();

                videoIds.add(videoId);
                titles.add(title);
            }
        }

        return titles;
    }





    private void downloadSelectedSong() {
        String selectedSong = resultsTable.getSelectionModel().getSelectedItem();
        int selectedIndex = resultsTable.getSelectionModel().getSelectedIndex();

        if (selectedSong == null || selectedIndex < 0) {
            showAlert("Error", "No song selected.", Alert.AlertType.ERROR);
            return;
        }

        String videoId = videoIds.get(selectedIndex);
        String selectedFormat = formatComboBox.getValue();
        String formatFlag;
        String extension;

        switch (selectedFormat) {
            case "MP3 (Audio)":
                formatFlag = "-x --audio-format mp3";
                extension = "mp3";
                break;
            case "MP4 (Video)":
                formatFlag = "-f mp4";
                extension = "mp4";
                break;
            case "WAV":
                formatFlag = "-x --audio-format wav";
                extension = "wav";
                break;
            default:
                showAlert("Error", "Unsupported format selected.", Alert.AlertType.ERROR);
                return; // Exit if an unsupported format is selected.


        }

        String outputPath = "downloads/%(title)s." + extension;

        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp",
                        formatFlag,
                        "https://www.youtube.com/watch?v=" + videoId,
                        "-o", outputPath
                );

                pb.start().waitFor();
                return null;
            }
        };

        downloadTask.setOnSucceeded(e -> {
            downloadedSongs.add(selectedSong + " (" + selectedFormat + ")");
            showAlert("Success", "Downloaded: " + selectedSong, Alert.AlertType.INFORMATION);
        });

        downloadTask.setOnFailed(e -> showAlert("Error", "Download failed.", Alert.AlertType.ERROR));

        new Thread(downloadTask).start();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}