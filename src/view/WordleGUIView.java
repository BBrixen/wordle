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

import static view.Wordle.*;

/**
 *
 */
public class WordleGUIView extends Application implements Observer {

	// object variables which will update throughout gameplay
	private static String currentWord; // this holds the current word being typed in
	private static Stage stage;
	private static Group progressGroup, guessedCharactersGroup;
	private static Label[] guessedCharactersList;
	private static Label[][] progressLabelGrid;
	private static int curRow, curCol;

	// variables for gui display (mostly dependent on size of screen)
	private static final int MAIN_SCENE_WIDTH = 1100;
	private static final int MAIN_SCENE_HEIGHT = 900;
	private static final int MINI_SCENE_WIDTH = MAIN_SCENE_WIDTH/2;
	private static final int MINI_SCENE_HEIGHT = MAIN_SCENE_HEIGHT/2;
	private static final int LETTER_SPACING = Math.min(MAIN_SCENE_WIDTH/13, MAIN_SCENE_HEIGHT/13);
	private static final Font MAIN_FONT = new Font("Arial", LETTER_SPACING/1.6);
	private static final Insets LABEL_SPACING = new Insets(10);

	@Override
	public void start(Stage stage) {
		WordleGUIView.stage = stage;
		
		// setting up wordle things for the first game
		startGame();
	}

	public void createDisplay(WordleController controller) {
		// top label
		Label topLabel = new Label("Welcome to Bennett's Wordle");
		topLabel.setFont(MAIN_FONT);
		topLabel.setTextFill(Color.WHITE);
		topLabel.setPadding(LABEL_SPACING); // adding some offset from the top

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

		// adding keyboard inputs to the scene
		Scene scene = new Scene(pane, MAIN_SCENE_WIDTH, MAIN_SCENE_HEIGHT);
		scene.setOnKeyPressed((event) -> { // setting up keyboard input
			String code = event.getCode().toString();

			if (code.equals("ENTER")) {
				this.enterGuess(controller);

			} else if (code.equals("BACK_SPACE")) {
				int len = currentWord.length();
				if (len <= 0) return;
				currentWord = currentWord.substring(0, len - 1);
				this.update(null, controller);

			} else if (code.matches("[a-zA-Z]")) {
				if (currentWord.length() >= wordleLength) return;
				currentWord += code;
				this.update(null, controller);
			}
		});

		// showing the scene
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
	}

	private void displayProgress(String currentWord, WordleController controller, Group progressGroup, Group guessedCharactersGroup) {
		Guess[] allGuesses = controller.getProgress();
		INDEX_RESULT[] guessedCharacters = controller.getGuessedCharacters();

		boolean foundEnd = false;
		for (int i = 0; i < allGuesses.length; i++) {
			Guess guess = allGuesses[i];
			// adding the currently typed word to the display
			if (guess.getGuess().startsWith("-") && !foundEnd) {
				guess = generateCurrent(currentWord);
				foundEnd = true;
			}

			displayGuess(guess, (i+1) * LETTER_SPACING, progressGroup);
		}

		// adding the currently guesses/remaining characters
		Label remainingCharacters = new Label("Character Status:");
		remainingCharacters.setFont(MAIN_FONT);
		remainingCharacters.setTextFill(Color.WHITE);
		remainingCharacters.setTranslateX(0);
		remainingCharacters.setTranslateY(0);
		remainingCharacters.setPadding(LABEL_SPACING); // adding some offset from the top
		guessedCharactersGroup.getChildren().add(remainingCharacters);

		displayGuessedCharacters(guessedCharacters, guessedCharactersGroup, controller);
	}

	private Guess generateCurrent(String currentWord) {
		String filledWord = currentWord; // needs to be wordleLength long, with - filling any missing characters
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
		label.setFont(MAIN_FONT);
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
			int len = currentWord.length();
			if (len <= 0) return;
			currentWord = currentWord.substring(0, len - 1);
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
					if (currentWord.length() >= wordleLength) return;
					currentWord += "" + letter;
					this.update(null, controller);
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
				".\n\nWould you like to play again?\n");
		label.setWrapText(true);
		label.setFont(MAIN_FONT);
		label.setTextFill(Color.WHITE);
		pane.setTop(label);

		Label yesButton = new Label("Yes");
		yesButton.setFont(MAIN_FONT);
		yesButton.setTextFill(Color.WHITE);
		pane.setCenter(yesButton);
		yesButton.setOnMouseClicked((event) -> {
			stage.close();
			startGame();
		});

		Label noButton = new Label("No");
		noButton.setFont(MAIN_FONT);
		noButton.setTextFill(Color.WHITE);
		pane.setBottom(noButton);
		BorderPane.setAlignment(noButton, Pos.CENTER);
		noButton.setOnMouseClicked((event) -> {
			stage.close();
			endGame();
		});

		Scene scene = new Scene(pane, MINI_SCENE_WIDTH, MINI_SCENE_HEIGHT);
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

		WordleController controller = new WordleController(wordleLength, maxGuesses, filename);
		controller.addObserver(this);
		currentWord = "";

		createDisplay(controller);
	}

	private void endGame() {
		stage.close();
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
