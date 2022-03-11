package view;

import controller.WordleController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.Guess;
import utilities.INDEX_RESULT;
import utilities.IncorrectGuessException;
import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public class WordleGUIView extends Application implements Observer {

	public static final int wordleLength = 5, maxGuesses = 6; // typical wordle
	public String currentWord; // this holds the current word being typed in
	private Stage stage;

	// public because they are used in GraphicsDisplay
	public static final Font mainFont = new Font("Arial", 48);
	private static final int LETTER_SPACING = 80;

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		// setting up wordle things for the first game
		WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
		controller.addObserver(this);
		currentWord = "";

		display(controller);
	}

	public void display(WordleController controller) {
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
		displayProgress(currentWord, controller, progressGroup, guessedCharactersGroup);

		// showing the scene
		Scene scene = new Scene(pane, 1100, 900);
		scene.setOnKeyPressed((event) -> { // setting up keyboard input
			String code = event.getCode().toString();
			if (code.equals("ENTER")) {
				this.enterGuess(controller);
			} else if (code.equals("BACK_SPACE")) {
				int len = this.currentWord.length();
				if (len <= 0) return;
				this.currentWord = this.currentWord.substring(0, len - 1);
				this.display(controller);
			} else if (code.matches("[a-zA-Z]")){
				if (this.currentWord.length() >= this.wordleLength) return;
				this.currentWord += code;
				this.display(controller);
			}
		});
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
	}

	private void displayProgress(String currentWord, WordleController controller, Group progressGroup, Group guessedCharactersGroup) {
		Guess[] allGuesses = controller.getProgress();
		INDEX_RESULT[] guessedCharacters = controller.getGuessedCharacters();

		int y = 1;
		boolean foundEnd = false;
		for (Guess guess : allGuesses) {

			if (guess.getGuess().startsWith("-") && !foundEnd) {
				guess = generateCurrent(currentWord);
				foundEnd = true;
			}

			displayGuess(guess, y * LETTER_SPACING, progressGroup);
			y ++;
		}

		Label remainingCharacters = new Label("Character Status:");
		remainingCharacters.setFont(this.mainFont);
		remainingCharacters.setTextFill(Color.WHITE);
		remainingCharacters.setTranslateX(0);
		remainingCharacters.setTranslateY(0);
		remainingCharacters.setPadding(new Insets(10)); // adding some offset from the top
		guessedCharactersGroup.getChildren().add(remainingCharacters);

		displayGuessedCharacters(guessedCharacters, guessedCharactersGroup, controller);
	}

	private Guess generateCurrent(String currentWord) {
		String filledWord = currentWord;
		INDEX_RESULT[] allUnguessed = new INDEX_RESULT[this.wordleLength];
		for(int i = 0; i < this.wordleLength; i++) {
			if (i >= currentWord.length()) filledWord += "-";
			allUnguessed[i] = INDEX_RESULT.UNGUESSED;
		}

		return new Guess(filledWord, allUnguessed, false);
	}

	private void displayGuess(Guess guess, int y, Group progressGroup) {
		// y is for the height down the page for the guess

		for (int i = 0; i < guess.getGuess().length(); i++) {
			displayLetter(guess.getGuess().charAt(i), guess.getIndices()[i],
					LETTER_SPACING * i, y, (event) -> {}, progressGroup);
		}
	}

	private void displayLetter(char letter, INDEX_RESULT value, double x, int y,
									  EventHandler<? super MouseEvent> eventHandler, Group group) {
		// x and y are for placement of the letter
		String labelString = "" + letter;
		if (letter == '-') labelString = " ";
		if (letter == '>') labelString = "Enter";
		if (letter == '<') labelString = "Del";
		Label label = new Label(labelString);
		label.setPrefSize(LETTER_SPACING*2, LETTER_SPACING); // styling the label
		label.setFont(this.mainFont);
		label.setTextFill(value.getColor());
		label.setTranslateX(x);
		label.setTranslateY(y);

		label.setOnMouseClicked(eventHandler);
		group.getChildren().add(label);
	}

	private void displayGuessedCharacters(INDEX_RESULT[] guessedCharacters, Group guessedCharactersGroup, WordleController controller) {
		// hard coding the qwerty keyboard
		// 1st row
		double x = 1.5;
		int y = 1;
		displayLetterConverter('Q', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('W', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('E', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('R', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('T', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('Y', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('U', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('I', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('O', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('P', guessedCharacters, x, y++, guessedCharactersGroup, controller);

		// 2nd row
		x = 2;
		displayLetterConverter('A', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('S', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('D', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('F', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('G', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('H', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('J', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('K', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('L', guessedCharacters, x, y++, guessedCharactersGroup, controller);

		// 3rd row
		x = 1;
		displayLetter('<', INDEX_RESULT.UNGUESSED, LETTER_SPACING*(x++), LETTER_SPACING*y, (event) -> {
			int len = this.currentWord.length();
			if (len <= 0) return;
			this.currentWord = this.currentWord.substring(0, len - 1);
			this.display(controller);
		}, guessedCharactersGroup); // delete key removes last letter
		x++;

		displayLetterConverter('Z', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('X', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('C', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('V', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('B', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('N', guessedCharacters, x++, y, guessedCharactersGroup, controller);
		displayLetterConverter('M', guessedCharacters, x++, y, guessedCharactersGroup, controller);

		x++;
		displayLetter('>', INDEX_RESULT.UNGUESSED, LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> this.enterGuess(controller), guessedCharactersGroup);
	}

	private void displayLetterConverter(char letter, INDEX_RESULT[] guessedCharacters,
											   double x, int y, Group guessedCharactersGroup, WordleController controller) {
		displayLetter(letter, guessedCharacters[letter - 'A'], LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> {
					if (this.currentWord.length() >= this.wordleLength) return;
					this.currentWord += "" + letter;
					this.display(controller);
				}, guessedCharactersGroup);
	}

	public void enterGuess(WordleController controller) {
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

	public void promptGameOver(WordleController controller) {
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

	private void restartGame() {
		WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
		controller.addObserver(this);
		currentWord = "";

		display(controller);
	}

	private void endGame() {
		stage.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void update(Observable o, Object arg) {
		display((WordleController) arg);
	}
}
