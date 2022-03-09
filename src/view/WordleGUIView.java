package view;

import controller.WordleController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.IncorrectGuessException;

/**
 *
 */
public class WordleGUIView extends Application {

	public static final int wordleLength = 5, maxGuesses = 6; // typical wordle
	public static String currentWord; // this holds the current word being typed in
	private static Stage stage;

	// public because they are used in GraphicsDisplay
	public static final Font mainFont = new Font("Arial", 48);

	@Override
	public void start(Stage stage) throws IncorrectGuessException {
		WordleGUIView.stage = stage;
		// setting up wordle things for the first game
		WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
		currentWord = "";

		display(controller);
	}

	public static void display(WordleController controller) {
		// top label
		Label topLabel = new Label("Welcome to Bennett's Wordle");
		topLabel.setFont(mainFont);
		topLabel.setTextFill(Color.WHITE);
		topLabel.setPadding(new Insets(10)); // adding some offset from the top

		// groups for displaying all the letters
		// display variables
		Group progressGroup = new Group(); // this is the grid of letters where guesses are made
		Group guessedCharactersGroup = new Group(); // this is the status of each guessed character

		// adding things to borderpane
		// creating basic display
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: black");
		BorderPane.setAlignment(guessedCharactersGroup, Pos.CENTER);
		pane.setTop(topLabel);
		BorderPane.setAlignment(topLabel, Pos.CENTER);
		pane.setCenter(progressGroup);
		pane.setBottom(guessedCharactersGroup);

		// offloading the bulk of displaying to another class for simplicity
		GraphicsDisplay.displayProgress(currentWord, controller, progressGroup, guessedCharactersGroup);

		// showing the scene
		Scene scene = new Scene(pane, 1100, 900);
		scene.setOnKeyPressed((event) -> { // setting up keyboard input
			String code = event.getCode().toString();
			if (code.equals("ENTER")) {
				WordleGUIView.enterGuess(controller);
			} else if (code.equals("BACK_SPACE")) {
				int len = WordleGUIView.currentWord.length();
				if (len <= 0) return;
				WordleGUIView.currentWord = WordleGUIView.currentWord.substring(0, len - 1);
				WordleGUIView.display(controller);
			} else if (code.matches("[a-zA-Z]")){
				if (WordleGUIView.currentWord.length() >= WordleGUIView.wordleLength) return;
				WordleGUIView.currentWord += code;
				WordleGUIView.display(controller);
			}
		});
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
	}

	public static void enterGuess(WordleController controller) {
		try {
			controller.makeGuess(currentWord);
			currentWord = "";
			display(controller);
		} catch (IncorrectGuessException e) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Wordle");
			a.setContentText(e.getMessage());
			a.setHeaderText("Invalid Guess");
			a.showAndWait();
		}

		if (controller.isGameOver())
			promptGameOver(controller);
	}

	public static void promptGameOver(WordleController controller) {
		Stage stage = new Stage();
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color: black");

		Label label = new Label("The word was " + controller.getAnswer() +
				".\n\nWould you like to\nplay again?\n");
		label.setWrapText(true);
		label.setFont(mainFont);
		label.setTextFill(Color.WHITE);
		pane.setTop(label);

		Label yesButton = new Label("Yes");
		yesButton.setFont(mainFont);
		yesButton.setTextFill(Color.WHITE);
		pane.setCenter(yesButton);
		yesButton.setOnMouseClicked((event) -> {
			stage.close();
			restartGame();
		});

		Label noButton = new Label("No");
		noButton.setFont(mainFont);
		noButton.setTextFill(Color.WHITE);
		pane.setBottom(noButton);
		BorderPane.setAlignment(noButton, Pos.CENTER);
		noButton.setOnMouseClicked((event) -> {
			stage.close();
			endGame();
		});

		Scene scene = new Scene(pane, 550, 400);
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
	}

	private static void restartGame() {
		WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
		currentWord = "";

		display(controller);
	}

	private static void endGame() {
		stage.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
