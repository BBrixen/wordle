package view;

import controller.WordleController;
import utilities.Guess;
import utilities.INDEX_RESULT;
import utilities.IncorrectGuessException;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * This is an implementation of Wordl (a word game). You must guess a 5 letter word in 6 guesses. Each guess gives you
 * more information about the correct word.
 * Correct = this letter is in the correct position
 * Incorrect = this letter is not in the word
 * Correct wrong index = this letter is in the word but not in the right place
 * Unguessed = this letter has not been guessed
 *
 * These values are color coded to help the viewer. All the characters as well as their
 * current state is also printed for the user
 */

public class WordleTextView implements Observer {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final int wordleLength = 5, maxGuesses = 6; // typical wordle

    /**
     * This is the main wordle game. Run this to play!
     * This allows you to play multiple games by asking again after a game has finished.
     *
     * @param args (unused)
     */
    public static void main(String[] args) {
        System.out.println("RED = INCORRECT");
        System.out.println("GREEN = CORRECT");
        System.out.println("BLUE = CORRECT BUT IN A DIFFERENT PLACE");
        // these values here are for starting the main game loop
        boolean playing = true;
        while (playing) {

            WordleController controller = new WordleController(wordleLength, maxGuesses, "Dictionary.txt");
            Scanner scanner = new Scanner(System.in);

            playGame(controller, scanner);

            System.out.println("Good game! The word was " + controller.getAnswer() + ".");
            System.out.println("Would you like to play again? yes/no");
            String answer = scanner.nextLine();
            if (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("y"))
                playing = false;
        }
    }

    /**
     * This takes the wordle controller (the game controller) and displays its information to the user.
     * This is printed as a grid of previous and empty guesses as well as all characters
     *
     * @param controller the wordle game controller
     */
    public static void displayProgress(WordleController controller) {
        Guess[] prog = controller.getProgress();
        String[] guessedCharacters = parseGuessedCharacters(controller.getGuessedCharacters());

        for (Guess guess : prog) {
            String currentGuess = guess.getGuess();
            INDEX_RESULT[] indices = guess.getIndices();

            for (int i = 0; i < guess.getGuess().length(); i++) {
                System.out.print(indices[i].getColorCode() + currentGuess.charAt(i) + " " + ANSI_RESET);
            }

            System.out.println();
        }

        // formatting
        System.out.println();

        for (String character : guessedCharacters) {
            System.out.print(character + " " + ANSI_RESET);
        }

        // formatting
        System.out.println('\n');
    }

    public static String[] parseGuessedCharacters(INDEX_RESULT[] guessedCharacters) {
        String[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        // adds color code to each letter
        for (int i = 0; i < guessedCharacters.length; i++) {
            alphabet[i] = guessedCharacters[i].getColorCode() + alphabet[i];
        }
        return alphabet;
    }

    /**
     * This allows you to play 1 game of wordle with the controller.
     *
     * Main calls this in a loop if you want to play another game without rerunning the program.
     *
     * @param controller the controller for the current game
     * @param scanner the scanner for user input
     */
    public static void playGame(WordleController controller, Scanner scanner) {
        while (!controller.isGameOver()) {
            String guess;

            while (true) {
                try {

                    System.out.print("Enter a guess: ");
                    guess = scanner.nextLine();
                    controller.makeGuess(guess);
                    break; // we can successfully break now because valid guess

                } catch (IncorrectGuessException e) {
                    System.out.println(e.getMessage());
                }
            }

            displayProgress(controller);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
