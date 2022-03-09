package view;

import controller.WordleController;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import utilities.Guess;
import utilities.INDEX_RESULT;

public class GraphicsDisplay {

    private static final int LETTER_SPACING = 80;

    public static void displayProgress(String currentWord, WordleController controller, Group progressGroup, Group guessedCharactersGroup) {
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
        remainingCharacters.setFont(WordleGUIView.mainFont);
        remainingCharacters.setTextFill(Color.WHITE);
        remainingCharacters.setTranslateX(0);
        remainingCharacters.setTranslateY(0);
        remainingCharacters.setPadding(new Insets(10)); // adding some offset from the top
        guessedCharactersGroup.getChildren().add(remainingCharacters);

        displayGuessedCharacters(guessedCharacters, guessedCharactersGroup, controller);
    }

    private static Guess generateCurrent(String currentWord) {
        String filledWord = currentWord;
        INDEX_RESULT[] allUnguessed = new INDEX_RESULT[WordleGUIView.wordleLength];
        for(int i = 0; i < WordleGUIView.wordleLength; i++) {
            if (i >= currentWord.length()) filledWord += "-";
            allUnguessed[i] = INDEX_RESULT.UNGUESSED;
        }

        return new Guess(filledWord, allUnguessed, false);
    }

    private static void displayGuess(Guess guess, int y, Group progressGroup) {
        // y is for the height down the page for the guess

        for (int i = 0; i < guess.getGuess().length(); i++) {
            displayLetter(guess.getGuess().charAt(i), guess.getIndices()[i],
                    LETTER_SPACING * i, y, (event) -> {}, progressGroup);
        }
    }

    private static void displayLetter(char letter, INDEX_RESULT value, double x, int y,
                                      EventHandler<? super MouseEvent> eventHandler, Group group) {
        // x and y are for placement of the letter
        String labelString = "" + letter;
        if (letter == '-') labelString = " ";
        if (letter == '>') labelString = "Enter";
        if (letter == '<') labelString = "Del";
        Label label = new Label(labelString);
        label.setPrefSize(LETTER_SPACING*2, LETTER_SPACING); // styling the label
        label.setFont(WordleGUIView.mainFont);
        label.setTextFill(value.getColor());
        label.setTranslateX(x);
        label.setTranslateY(y);

        label.setOnMouseClicked(eventHandler);
        group.getChildren().add(label);
    }

    private static void displayGuessedCharacters(INDEX_RESULT[] guessedCharacters, Group guessedCharactersGroup, WordleController controller) {
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
            int len = WordleGUIView.currentWord.length();
            if (len <= 0) return;
            WordleGUIView.currentWord = WordleGUIView.currentWord.substring(0, len - 1);
            WordleGUIView.display(controller);
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
                (event) -> WordleGUIView.enterGuess(controller), guessedCharactersGroup);
    }

    private static void displayLetterConverter(char letter, INDEX_RESULT[] guessedCharacters,
                                               double x, int y, Group guessedCharactersGroup, WordleController controller) {
        displayLetter(letter, guessedCharacters[letter - 'A'], LETTER_SPACING*x, LETTER_SPACING*y,
                (event) -> {
                    if (WordleGUIView.currentWord.length() >= WordleGUIView.wordleLength) return;
                    WordleGUIView.currentWord += "" + letter;
                    WordleGUIView.display(controller);
                }, guessedCharactersGroup);
    }

}
