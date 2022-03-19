package view;

import controller.WordleController;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;
import utilities.Guess;
import utilities.INDEX_RESULT;
import utilities.IncorrectGuessException;
import java.util.Observable;
import java.util.Observer;
import static view.Wordle.*;

/**
 * @author Bennett Brixen
 *
 * This is the gui for wordle. it has a grid in the center and the guessed letters at the bottom in qwerty layout
 * The keyboard at the bottom and the del/enter buttons are clickable and work the same as typing.
 * After winning, there is a little bouncing animation for the correct answer. If you dont win, there is no animation
 * If you dont want to start a new game or exit, and instead you want to see your game again,
 * you can exit the "new game" menu by clicking the x in the corner.
 * Error messages are not that pretty, but they are error messages so just like, dont be bad? dont guess anything invalid? lmao get better
 *
 * This class is fairly long because it is a gui.
 * It is split up into 4 sections that roughly follow the chronological order of playing wordle
 * 1. Starting a Game - this sets up the starting variables, and then creates the display
 * 2. Creating the Display - this is a series of functions that build different parts of the screen for an empty wordle
 * 			it will have all the fields to fill out a finished wordle, but most will be unfilled/hidden
 * 3. Updating the Display - this changes fields within the already created display. this saves time so that we
 * 			dont have to recreate things all over again. this section also contains enterguess, because it is something that updates the display
 * 			(and there really wasnt a better place for it)
 * 4. Ending a game - this shows the winning animation (if they win) and then prompts them if they want to play again.
 * 			if so, they will be taken back up to the top and the process will begin again
 * 			otherwise, it will exit the program entirely
 */
public class WordleGUIView extends Application implements Observer {

	// object variables which will update throughout gameplay
	private static String currentWord; // this holds the current word being typed in
	private static Stage stage;
	private static Group progressGroup, guessedCharactersGroup;
	private static Label[] guessedCharactersList;
	private static Label[][] progressLabelGrid;
	private static int curRow, curCol;
	private static Guess mostRecentGuess;

	// variables for gui display (mostly dependent on size of screen)
	// scene size
	private static final int MAIN_SCENE_WIDTH = 1100;
	private static final int MAIN_SCENE_HEIGHT = 900;
	private static final int MINI_SCENE_WIDTH = MAIN_SCENE_WIDTH/2;
	private static final int MINI_SCENE_HEIGHT = MAIN_SCENE_HEIGHT/2;

	// letter size
	private static final int LETTER_SPACING = Math.min(MAIN_SCENE_WIDTH/13, MAIN_SCENE_HEIGHT/13);
	private static final Font MAIN_FONT = new Font("Arial", LETTER_SPACING/1.6);
	private static final Insets LABEL_SPACING = new Insets(10);

	// animations
	private static final int ANIMATION_LENGTH = 250;
	private static final int BOUNCES = 3;
	private static final int BOUNCE_HEIGHT = 50;

	/*-------------------------- Starting a Game --------------------------*/
	/**
	 * This creates the gui, while startGame might create the display and can be used to make a new wordle game, this
	 * is needed to start up the appliation the first time since it creates the stage.
	 * We then call startGame to create a single wordle game
	 *
	 * @param stage - the stage for the gui display
	 */
	@Override
	public void start(Stage stage) {
		WordleGUIView.stage = stage;

		// setting up wordle things for the first game
		startGame();
	}

	/**
	 * This creates a game of wordle. It resets the values of all the game/display variables
	 * It then creates a controller and creates the display for playing
	 */
	private void startGame() {
		progressGroup = new Group(); // this is the grid of letters where guesses are made
		guessedCharactersGroup = new Group(); // this is the status of each guessed character
		guessedCharactersList = new Label[26]; // hard coded 26 for alphabet
		progressLabelGrid = new Label[maxGuesses][wordleLength]; // same size as the progress grid
		curRow = 0;
		curCol = 0;

		// time to make the game
		WordleController controller = new WordleController(wordleLength, maxGuesses, filename);
		controller.addObserver(this);
		currentWord = "";

		createDisplay(controller);
	}


	/*-------------------------- Creating the Display --------------------------*/
	/**
	 * This is the outer most function for making the display. It creates the scene and adds the keypress bindings
	 * It calls helper functions for making the grid and qwerty keyboard and setting up keyboard inputs
	 *
	 * @param controller - this is the controller for this game of wordle
	 */
	private void createDisplay(WordleController controller) {
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
		displayProgress(controller, progressGroup, guessedCharactersGroup);

		// showing the scene
		stage.setScene(createScene(controller, pane));
		stage.setTitle("Wordle");
		stage.show();
	}

	/**
	 * This creates the scene and adds the keybinds to it
	 *
	 * @param controller - the controller for this wordle game (needed for entering a guess)
	 * @param pane - the border pane with the gui for the scene
	 * @return - the scene with keybinds
	 */
	private Scene createScene(WordleController controller, BorderPane pane) {
		Scene scene = new Scene(pane, MAIN_SCENE_WIDTH, MAIN_SCENE_HEIGHT);
		scene.setOnKeyPressed((event) -> { // setting up keyboard input
			if (mostRecentGuess != null && mostRecentGuess.getIsCorrect()) return;
			String code = event.getCode().toString();

			if (code.equals("ENTER")) {

				this.enterGuess(controller);

			} else if (code.equals("BACK_SPACE")) {
				int len = currentWord.length();
				if (len <= 0) return;
				currentWord = currentWord.substring(0, len - 1);
				updateCurrentWord();

			} else if (code.matches("[a-zA-Z]")) {
				if (currentWord.length() >= wordleLength) return;
				currentWord += code;
				updateCurrentWord();
			}
		});
		return scene;
	}

	/**
	 * This creates the basic border pane. It uses helper functions to greate the grid and qwerty keyboard
	 *
	 * @param controller - the controller for this wordle game. this is passed into the qwerty grid for entering guesses
	 * @param progressGroup - grid for wordle guesses
	 * @param guessedCharactersGroup - the grid for qwerty
	 */
	private void displayProgress(WordleController controller, Group progressGroup, Group guessedCharactersGroup) {
		// add every label into the grid, this is all filled with spaces
		for (int i = 0; i < maxGuesses; i++) {
			displayGuess((i+1) * LETTER_SPACING, progressGroup);
		}

		// adding the currently guesses/remaining characters
		Label remainingCharacters = new Label("Character Status:");
		remainingCharacters.setFont(MAIN_FONT);
		remainingCharacters.setTextFill(Color.WHITE);
		remainingCharacters.setTranslateX(0);
		remainingCharacters.setTranslateY(0);
		remainingCharacters.setPadding(LABEL_SPACING); // adding some offset from the top
		guessedCharactersGroup.getChildren().add(remainingCharacters);

		// adding keyboard
		displayGuessedCharacters(guessedCharactersGroup, controller);
	}

	/**
	 * This creates a single guess inside the central grid
	 *
	 * @param y - the height of this guess in the grid
	 * @param progressGroup - the grid of all labels for guessing
	 */
	private void displayGuess(int y, Group progressGroup) {
		for (int i = 0; i < wordleLength; i++) {
			displayLetter(' ', LETTER_SPACING * i, y, (event) -> {}, progressGroup);
		}
	}

	/**
	 * This creates and stores a single letter into the scene. it can create labels for both the qwerty keyboard
	 * and the central grid of guesses. It also binds the press to an event,
	 * (this is used to type a character if you press one in the qwerty keyboard)
	 *
	 * @param letter - the letter inside this label (spaces for the central grid at the start)
	 * @param x - the x coordinate of the label
	 * @param y - the y coordinate of the label
	 * @param eventHandler - the on click action for this label
	 * @param group - the group this should be added to (grid or qwerty)
	 */
	private void displayLetter(char letter, double x, int y,
							   EventHandler<? super MouseEvent> eventHandler, Group group) {
		// x and y are for placement of the letter
		String labelString = "" + letter;
		if (letter == '>') labelString = "Enter";
		if (letter == '<') labelString = "Del";
		Label label = new Label(labelString);
		label.setPrefSize(LETTER_SPACING*2, LETTER_SPACING); // styling the label
		label.setFont(MAIN_FONT);
		label.setTextFill(INDEX_RESULT.UNGUESSED.getJavaFXColor()); // they all start unguessed
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

	/**
	 * This creates the entire qwerty keyboard at the bottom. each button is clickable and works the same as typing
	 *
	 * @param guessedCharactersGroup - the group which holds the labels
	 * @param controller - the controller of the game (needed to enter to entering a guess)
	 */
	private void displayGuessedCharacters(Group guessedCharactersGroup, WordleController controller) {
		// hard coding the qwerty keyboard
		// 1st row
		double x = 1.5;
		int y = 1;
		displayLetterConverter('Q', x++, y, guessedCharactersGroup);
		displayLetterConverter('W', x++, y, guessedCharactersGroup);
		displayLetterConverter('E', x++, y, guessedCharactersGroup);
		displayLetterConverter('R', x++, y, guessedCharactersGroup);
		displayLetterConverter('T', x++, y, guessedCharactersGroup);
		displayLetterConverter('Y', x++, y, guessedCharactersGroup);
		displayLetterConverter('U', x++, y, guessedCharactersGroup);
		displayLetterConverter('I', x++, y, guessedCharactersGroup);
		displayLetterConverter('O', x++, y, guessedCharactersGroup);
		displayLetterConverter('P', x, y++, guessedCharactersGroup);

		// 2nd row
		x = 2;
		displayLetterConverter('A', x++, y, guessedCharactersGroup);
		displayLetterConverter('S', x++, y, guessedCharactersGroup);
		displayLetterConverter('D', x++, y, guessedCharactersGroup);
		displayLetterConverter('F', x++, y, guessedCharactersGroup);
		displayLetterConverter('G', x++, y, guessedCharactersGroup);
		displayLetterConverter('H', x++, y, guessedCharactersGroup);
		displayLetterConverter('J', x++, y, guessedCharactersGroup);
		displayLetterConverter('K', x++, y, guessedCharactersGroup);
		displayLetterConverter('L', x, y++, guessedCharactersGroup);

		// 3rd row
		x = 1;
		displayLetter('<', LETTER_SPACING*(x++), LETTER_SPACING*y, (event) -> {
			if (mostRecentGuess != null && mostRecentGuess.getIsCorrect()) return;
			int len = currentWord.length();
			if (len <= 0) return;
			currentWord = currentWord.substring(0, len - 1);
			updateCurrentWord();
		}, guessedCharactersGroup); // delete key removes last letter
		x++;

		displayLetterConverter('Z', x++, y, guessedCharactersGroup);
		displayLetterConverter('X', x++, y, guessedCharactersGroup);
		displayLetterConverter('C', x++, y, guessedCharactersGroup);
		displayLetterConverter('V', x++, y, guessedCharactersGroup);
		displayLetterConverter('B', x++, y, guessedCharactersGroup);
		displayLetterConverter('N', x++, y, guessedCharactersGroup);
		displayLetterConverter('M', x++, y, guessedCharactersGroup);

		x++;
		displayLetter('>', LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> {
					if (mostRecentGuess != null && mostRecentGuess.getIsCorrect()) return;
					this.enterGuess(controller);
				}, guessedCharactersGroup);
	}

	/**
	 * This function simply serves as a middleground between displayGuessedCharacters and the displayLetter function
	 * Since displayLetter is called so many times, and has complex parameters that depend on the character,
	 * i found it best to create a function which just calls the other function without having to repeat so much data
	 *
	 * this finds the correct guessed character in the list of guessed characters, as well as creating the basic event
	 * handler for clicking the button. before this, @param letter would be repeated multiple times and it was a pain
	 *
	 * @param letter - the letter to be displayed in displayLetter
	 * @param x - the x coord of the letter
	 * @param y - the y coord of the letter
	 * @param guessedCharactersGroup - the qwerty group for displaying
	 */
	private void displayLetterConverter(char letter, double x, int y, Group guessedCharactersGroup) {
		displayLetter(letter,LETTER_SPACING*x, LETTER_SPACING*y,
				(event) -> {
					if (mostRecentGuess != null && mostRecentGuess.getIsCorrect()) return;
					if (currentWord.length() >= wordleLength) return;
					currentWord += "" + letter;
					updateCurrentWord();
				}, guessedCharactersGroup);
	}


	/*-------------------------- Updating the Display --------------------------*/
	/**
	 * This function is called when the model changed. it is called from the controller which passes the update along
	 * it holds the most recent guess to easily update variables
	 *
	 * @param o - the controller, used to update the display and control the wordle game
	 * @param arg - the most recent guess, which is stored in this class when we check if they won
	 */
	@Override
	public void update(Observable o, Object arg) {
		WordleController controller = (WordleController) o;
		mostRecentGuess = (Guess) arg;

		// updating the game with the latest guess
		INDEX_RESULT[] guessedCharacters = controller.getGuessedCharacters();
		for (int i = 0; i < guessedCharacters.length; i++) {
			Label label = guessedCharactersList[i];
			label.setTextFill(guessedCharacters[i].getJavaFXColor());
		}

		for (int i = 0; i < mostRecentGuess.getIndices().length; i++) {
			Label label = progressLabelGrid[curRow][i];
			label.setTextFill(mostRecentGuess.getIndices()[i].getJavaFXColor());
			label.setText(""+mostRecentGuess.getGuess().charAt(i));
		}

		curRow ++;
	}

	/**
	 * This is an update function which updates the UI after the user presses a key.
	 * It takes no variables and returns nothing because all the data change happens upon key press, and then
	 * this is called to display those new updates
	 */
	private void updateCurrentWord() {
		for (int i = 0; i < wordleLength; i++) {
			Label label = progressLabelGrid[curRow][i];
			if (i >= currentWord.length()) label.setText(" ");
			else label.setText(""+currentWord.charAt(i));
		}
	}

	/**
	 * This takes the currently typed word and calls makeGuess into the controller.
	 * It also handles any IncorrectGuessExceptions thrown by attempting an incorrect guess,
	 * which it then uses to display a little error message to the user
	 * @param controller - the controller into which we make guesses
	 */
	private void enterGuess(WordleController controller) {
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

		if (controller.isGameOver()) {
			if (mostRecentGuess.getIsCorrect())
				showAnimation(controller);
			else
				promptGameOver(controller);
		}
	}


	/*-------------------------- Ending a Game --------------------------*/
	/**
	 * This will show the animation upon winning the game. It calls letterJump on each letter in the correct answer
	 * We need the controller to pass into the letterJump animation, which will prompt the user to play again
	 *
	 * @param controller - the controller which is needed inside promptGameOver for the final answer
	 */
	private void showAnimation(WordleController controller) {
		int correctGuessRow = curRow - 1;
		Label[] lastGuessLabels = progressLabelGrid[correctGuessRow];

		for (int i = 0; i < lastGuessLabels.length; i++) {
			Label label = lastGuessLabels[i];
			WordleController tempController = null;

			if (i == lastGuessLabels.length - 1) tempController = controller; // use this to prompt game over
			letterJump(label, tempController);
		}
	}

	/**
	 * This makes a single letter jump up a few times. We play this to make the correct answer bounce on winning
	 *
	 * @param label - the label which will jump up and down
	 * @param controller - the controller for displaying the correct answer in promptGameOver
	 */
	private void letterJump(Label label, WordleController controller) {
		TranslateTransition bounceAnimation = new TranslateTransition();
		bounceAnimation.setDuration(Duration.millis(ANIMATION_LENGTH));
		bounceAnimation.setNode(label);
		bounceAnimation.setByY(-1*BOUNCE_HEIGHT); // multipliled by -1
		bounceAnimation.setCycleCount(2*BOUNCES); // this is doubled because 2 cycles = 1 bounce
		bounceAnimation.setAutoReverse(true);

		// we use this one time on the final label, so that we only create 1 display
		if (controller != null)
			bounceAnimation.setOnFinished(e -> promptGameOver(controller));

		bounceAnimation.play();
	}

	/**
	 * This brings up a new gui which gives the correct answer and asks if they want to play again
	 *
	 * @param controller - the controller which can get us the correct answer
	 */
	private void promptGameOver(WordleController controller) {
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
			stage.close(); // closing this mini menu
			endGame();
		});

		Scene scene = new Scene(pane, MINI_SCENE_WIDTH, MINI_SCENE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
	}

	/**
	 * This closes the stage for the entire application.
	 * we call this when we are ending the wordle application, not a single wordle game
	 */
	private void endGame() {
		stage.close();
	}
}
