package view;

import controller.WordleController;
import utilities.Guess;
import utilities.INDEX_RESULT;
import utilities.IncorrectGuessException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import static view.Wordle.*;

/**
 * @author Bennett Brixen
 *
 * This is the text implementation of wordle, it uses ascii color coding to display to the user
 *
 * Correct = this letter is in the correct position
 * Incorrect = this letter is not in the word
 * Correct wrong index = this letter is in the word but not in the right place
 * Unguessed = this letter has not been guessed
 *
 * These values are color coded to help the viewer. All the characters as well as their
 * current state is also printed for the user
 */

public class WordleTextView implements Observer {

    // so normal things dont get colored on accident
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * This is the constructor for the text view. The text view acts slightly like an object, which runs the game
     * upon being created. We create this in Wordle.java
     *
     * This allows you to play multiple games by asking again after a game has finished.
     */
    public WordleTextView() {
        System.out.println("RED = INCORRECT");
        System.out.println("GREEN = CORRECT");
        System.out.println("BLUE = CORRECT BUT IN A DIFFERENT PLACE");
        // these values here are for starting the main game loop
        boolean playing = true;
        while (playing) {
            System.out.print("\nEnter a guess: ");

            WordleController controller = new WordleController(wordleLength, maxGuesses, filename);
            controller.addObserver(this);
            Scanner scanner = new Scanner(System.in);

            playGame(controller, scanner);

            System.out.println("\n\nGood game! The word was " + controller.getAnswer() + ".");
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
    public void displayProgress(WordleController controller) {
        Guess[] prog = controller.getProgress();
        String[] guessedCharacters = parseGuessedCharacters(controller.getGuessedCharacters());

        for (Guess guess : prog) {
            String currentGuess = guess.getGuess();
            INDEX_RESULT[] indices = guess.getIndices();

            for (int i = 0; i < guess.getGuess().length(); i++) {
                System.out.print(indices[i].getAsciiColor() + currentGuess.charAt(i) + " " + ANSI_RESET);
            }

            System.out.println();
        }

        // formatting
        System.out.println();

        for (String character : guessedCharacters) {
            System.out.print(character + " " + ANSI_RESET);
        }

        if (!controller.isGameOver())
            System.out.print("\nEnter a guess: ");
    }

    /**
     * this combines the alphabet with the guessed characters from the controller/model
     * it adds the string of ascii color code to each letter in the alphabet and returns
     * each character with its guessed status
     *
     * @param guessedCharacters - the guess status of the alphabet
     * @return - the alphabet colored with ascii color codes
     */
    public String[] parseGuessedCharacters(INDEX_RESULT[] guessedCharacters) {
        String[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        // adds color code to each letter
        for (int i = 0; i < guessedCharacters.length; i++) {
            alphabet[i] = guessedCharacters[i].getAsciiColor() + alphabet[i];
        }
        return alphabet;
    }

    /**
     * This allows you to play 1 game of wordle with the controller.
     *
     * The game calls this in a loop if you want to play another game without rerunning the program.
     *
     * @param controller the controller for the current game
     * @param scanner the scanner for user input
     */
    public void playGame(WordleController controller, Scanner scanner) {
        while (!controller.isGameOver()) {
            String guess;

            while (true) {
                try {

                    guess = scanner.nextLine();
                    controller.makeGuess(guess);
                    break; // we can successfully break now because valid guess

                } catch (IncorrectGuessException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * This is called when the model alerts the controller that there was an update, and then the controller updates the view (this method).
     * It simply calls displayProgress which will print out the information of the entire game so far
     *
     * In this view, @param arg is unused. that is needed inside the gui view
     *
     * @param o - the wordle controller which holds the needed information to be displayed
     * @param arg - the most recent guess into the model
     */
    @Override
    public void update(Observable o, Object arg) {
        displayProgress((WordleController) o);
    }
}
