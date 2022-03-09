package controller;

import model.WordleModel;
import utilities.ArraySet;
import utilities.Guess;
import utilities.INDEX_RESULT;
import utilities.IncorrectGuessException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class represents the controller for the model and the game. It contains the model as well as a few
 * variables which dictate how the game runs. It stores the users progress as a list of guesses.
 * This can handle a guess by validating it and then calling the model. It then takes the guess information
 * from the model and stores it as well as determining if the game has concluded.
 */
public class WordleController {

	private final WordleModel model;
	private final int letters;
	private ArraySet<String> allwords;
	private final Guess[] progress;
	private int row;
	private boolean gameOver;

	/**
	 * This creates a controller for a game of wordle
	 *
	 * The controller can make guesses to the model, and return useful information from the model.
	 *
	 * @param letters the number of letters in a guess
	 * @param maxRows the number of guesses allowed, or rows in the game
	 * @param filename the file for the dictionary of all valid words
	 */
	public WordleController (int letters, int maxRows, String filename) {
		// maxRows and letters is for the number of guesses and the letters in each guess
		this.model = new WordleModel(Objects.requireNonNull(selectWord(filename)));
		this.row = 0;
		this.letters = letters;
		this.gameOver = false;
		this.progress = new Guess[maxRows];

		this.fillProgress();
	}

	/**
	 * This fills the progress array with empty guesses
	 */
	private void fillProgress() {
		StringBuilder emptyGuess = new StringBuilder();
		INDEX_RESULT[] unguessed = new INDEX_RESULT[progress.length - 1];

		for (int i = 0; i < letters; i++) {
			emptyGuess.append("-");
			unguessed[i] = INDEX_RESULT.UNGUESSED;
		}
		Guess defaultGuess = new Guess(emptyGuess.toString(), unguessed, false);

		Arrays.fill(progress, defaultGuess);
	}

	/**
	 * Returns if the game is finished or not.
	 *
	 * The game is over if it has been the max number of guesses, or the user guessed the word correctly
	 *
	 * @return true if game is over, false otherwise
	 */
	public boolean isGameOver() {
		return this.gameOver;
	}

	/**
	 * This attempts to make a guess to the model
	 *
	 * We first validate the guess, and if it is valid then we make the guess in the model,
	 * and update our current progress with the data model returns.
	 *
	 * @param guess the string being guessed
	 * @throws IncorrectGuessException if the guess was invalid (too long, not a word, or not in the dictionary)
	 */
	public void makeGuess(String guess) throws IncorrectGuessException {
		guess = guess.toUpperCase(); // the answer is stored as uppercase, so this is too

		// validating and handling
		validGuess(guess);
		Guess guessResult = model.handleGuess(guess);

		progress[row] = guessResult;
		row ++;
		// check if the words are the same or if they have used all their guesses
		if (guessResult.getIsCorrect() || row == progress.length) gameOver = true;
	}

	/**
	 * Gets the answer from the model
	 *
	 * @return the correct answer for this wordle game
	 */
	public String getAnswer() {
		return model.getAnswer();
	}

	/**
	 * Gets the progress of the game so far. This is used to display
	 *
	 * @return a list of guesses which represents the progress
	 */
	public Guess[] getProgress() {
		return progress;
	}

	/**
	 * This gets the list of all characters
	 *
	 * It color codes characters according to if they have not been guessed, are correct,
	 * are incorrect, or are correct in the wrong position
	 *
	 * @return the alphabet color coded to previous guesses
	 */
	public INDEX_RESULT[] getGuessedCharacters() {
		return model.getGuessedCharacters();
	}

	/**
	 * This chooses a random word from the dictionary file
	 *
	 * @param filename the dictionary filename
	 * @return a random word for the game, or null if the file did not exist
	 */
	private String selectWord(String filename) {
		allwords = new ArraySet<>();
		try {
			// read every word into allwords
			Scanner scanner = new Scanner(new File(filename));
			while (scanner.hasNextLine())
				allwords.add(scanner.nextLine().toUpperCase());

			// get a random word
			Random random = new Random();
			int randomIndex = random.nextInt(allwords.size());
			return allwords.get(randomIndex).toUpperCase();

		} catch (FileNotFoundException e) {
			return null; // it didnt exist, so return null (this is handled elsewhere)
		}
	}

	/**
	 * This checks if a guess adheres to our requirements
	 *
	 * It must be [letters] long
	 * Each character must be alphabetic
	 * The string must be a valid word in the dictionary
	 *
	 * @param guess the string we are checking
	 * @throws IncorrectGuessException if the guess breaks any requirements
	 */
	private void validGuess(String guess) throws IncorrectGuessException {
		// this validates the correct length
		if (guess.length() != letters)
			throw new IncorrectGuessException("Guess must be " + letters + " characters long\n");
		if (!guess.matches("[a-zA-Z]+"))
			throw new IncorrectGuessException("Guesses must only contain letters\n");
		if (!allwords.contains(guess))
			throw new IncorrectGuessException("Guess must be a valid word in dictionary\n");
	}
}
