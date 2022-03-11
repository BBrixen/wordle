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
import static view.Wordle.maxGuesses; // wordle game variables
import static view.Wordle.wordleLength;

/**
 *
 */
public class WordleGUIView extends Application implements Observer {

	// object variables which will update throughout gameplay
	private String currentWord; // this holds the current word being typed in
	private Stage stage;
	private Group progressGroup, guessedCharactersGroup;
	private Label[] guessedCharactersList;
	private Label[][] progressLabelGrid;
	private int curRow, curCol;

	// public because they are used in GraphicsDisplay
	public static final Font mainFont = new Font("Arial", 48);
	private static final int LETTER_SPACING = 80;

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		
		// setting up wordle things for the first game
		startGame();
	}

	public void createDisplay(WordleController controller) {
		// top label
		Label topLabel = new Label("Welcome to Bennett's Wordle");
		topLabel.setFont(mainFont);
		topLabel.setTextFill(Color.WHITE);
		topLabel.setPadding(new Insets(10)); // adding some offset from the top

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
				this.update(null, controller);
			} else if (code.matches("[a-zA-Z]")){
				if (this.currentWord.length() >= wordleLength) return;
				this.currentWord += code;
				this.update(null, controller);
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
		remainingCharacters.setFont(mainFont);
		remainingCharacters.setTextFill(Color.WHITE);
		remainingCharacters.setTranslateX(0);
		remainingCharacters.setTranslateY(0);
		remainingCharacters.setPadding(new Insets(10)); // adding some offset from the top
		guessedCharactersGroup.getChildren().add(remainingCharacters);

		displayGuessedCharacters(guessedCharacters, guessedCharactersGroup, controller);
	}

	private Guess generateCurrent(String currentWord) {
		String filledWord = currentWord;
		INDEX_RESULT[] allUnguessed = new INDEX_RESULT[wordleLength];
		for(int i = 0; i < wordleLength; i++) {
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
		label.setFont(mainFont);
		label.setTextFill(value.getColor());
		label.setTranslateX(x);
		label.setTranslateY(y);

		label.setOnMouseClicked(eventHandler);
		group.getChildren().add(label);
		if (group.equals(progressGroup)) {
			progressLabelGrid[curRow][curCol] = label;
			// increase one over
			curCol ++;
			curCol %= wordleLength;
			if (curCol == 0) curRow +=1;
			if (curRow == maxGuesses) curRow = 0;
		} else if (letter != '<' && letter != '>'){

			guessedCharactersList[letter - 'A'] = label;
		}
	}

	private void displayGuessedCharacters(INDEX_RESULT[] guessedCharacters, Group guessedCharactersGroup, WordleController controller) {
		// hard coding the qwerty keyboard
		// 1st row
		double x = 1.5;
		int y = 1;
		displayLetterConverter('Q', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('W', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('E', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('R', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('T', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('Y', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('U', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('I', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('O', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('P', guessedCharacters, x, y++, guessedCharactersGroup);

		// 2nd row
		x = 2;
		displayLetterConverter('A', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('S', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('D', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('F', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('G', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('H', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('J', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('K', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('L', guessedCharacters, x, y++, guessedCharactersGroup);

		// 3rd row
		x = 1;
		displayLetter('<', INDEX_RESULT.UNGUESSED, LETTER_SPACING*(x++), LETTER_SPACING*y, (event) -> {
			int len = this.currentWord.length();
			if (len <= 0) return;
			this.currentWord = this.currentWord.substring(0, len - 1);
		}, guessedCharactersGroup); // delete key removes last letter
		x++;

		displayLetterConverter('Z', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('X', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('C', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('V', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('B', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('N', guessedCharacters, x++, y, guessedCharactersGroup);
		displayLetterConverter('M', guessedCharacters, x++, y, guessedCharactersGroup);

		x++;
		displayLetter('>', INDEX_RESULT.UNGUESSED, LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> this.enterGuess(controller), guessedCharactersGroup);
	}

	private void displayLetterConverter(char letter, INDEX_RESULT[] guessedCharacters,
											   double x, int y, Group guessedCharactersGroup) {
		displayLetter(letter, guessedCharacters[letter - 'A'], LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> {
					if (this.currentWord.length() >= wordleLength) return;
					this.currentWord += "" + letter;
				}, guessedCharactersGroup);
	}

	public void enterGuess(WordleController controller) {
		try {
			controller.makeGuess(currentWord);
			currentWord = "";
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
			startGame();
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

	private void startGame() {
		progressGroup = new Group(); // this is the grid of letters where guesses are made
		guessedCharactersGroup = new Group(); // this is the status of each guessed character
		guessedCharactersList = new Label[26]; // hard coded 26 for alphabet
		progressLabelGrid = new Label[maxGuesses][wordleLength]; // same size as the progress grid
		curRow = 0;
		curCol = 0;

		WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
		controller.addObserver(this);
		currentWord = "";

		createDisplay(controller);
	}

	private void endGame() {
		stage.close();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void update(Observable o, Object arg) {
		WordleController controller = (WordleController) arg;
		if (o == null) {
			updateCurrentWord();
		} else {
			updateGuessedWords(controller);
		}
	}

	public void updateCurrentWord() {
		for (int i = 0; i < wordleLength; i++) {
			Label label = progressLabelGrid[curRow][i];
			if (i >= currentWord.length()) label.setText(" ");
			else label.setText(""+currentWord.charAt(i));
		}
	}

	public void updateGuessedWords(WordleController controller) {
		INDEX_RESULT[] guessedCharacters = controller.getGuessedCharacters();
		for (int i = 0; i < guessedCharacters.length; i++) {
			Label label = guessedCharactersList[i];
			label.setTextFill(guessedCharacters[i].getColor());
		}

		Guess[] guesses = controller.getProgress();
		Guess lastGuess = null;
		for (int i = guesses.length - 1; i >= 0; i--) {
			if (guesses[i].getIndices()[0] != INDEX_RESULT.UNGUESSED) {
				lastGuess = guesses[i];
				break;
			}
		}

		for (int i = 0; i < lastGuess.getIndices().length; i++) {
			Label label = progressLabelGrid[curRow][i];
			label.setTextFill(lastGuess.getIndices()[i].getColor());
			label.setText(""+lastGuess.getGuess().charAt(i));
		}

		curRow ++;
	}
}
