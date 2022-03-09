package model;

import utilities.Guess;
import utilities.INDEX_RESULT;
import java.util.Arrays;

/**
 * This clas represents the wordle word. It stores the correct answer and the characters that have been guessed.
 * It uses INDEX_RESULT to distinguish between correct/incorrect/unguessed/correct but wrong place.
 * It contains multiple functions like correctLettersCorrectPlaces and correctLettersWrongPlaces which
 * analyze the word character by character returning INDEX_RESULT's for each character in
 * a guess or the guessed characters
 */
public class WordleModel {

	private final String answer;
	private final INDEX_RESULT[] guessedCharacters;

	/**
	 * This creates a wordle model which represents the correct wordle word
	 *
	 * It has useful functions for handling guesses against the word
	 *
	 * @param answer the correct wordle word
	 */
	public WordleModel(String answer) {
		this.answer = answer.toUpperCase();
		guessedCharacters = new INDEX_RESULT[26];
		Arrays.fill(guessedCharacters, INDEX_RESULT.UNGUESSED);
	}


	/**
	 * This returns a list of INDEX_RESULTS for all the semi-correct positions
	 *
	 * It takes in a list of INDEX_RESULTS corresponding to each letter in the guess
	 * This is origionally filled with INCORRECT, and this will put CORRECT_WRONG_INDEX in every
	 * location that has a letter which appears in the word. This will put CORRECT_WRONG_INDEX in
	 * locations which should have CORRECT, which is why we then call correctLettersCorrectPlaces to overwrite these.
	 *
	 * After both functions have called we have properly analyzed the word
	 *
	 * @param guess the string being guessed
	 * @param combination the list of INDEX_RESULTS corresponding to each letter,
	 *                       this will be filled with INCORRECT
	 */
	private void correctLettersWrongPlaces(String guess, INDEX_RESULT[] combination, String tempAnswer) {
		for (int i = 0; i < guess.length(); i++) {
			// we do "" + charAt to implicitly cast to a string for contains()
			if (combination[i] != INDEX_RESULT.CORRECT && tempAnswer.contains("" + guess.charAt(i))) {
				combination[i] = INDEX_RESULT.CORRECT_WRONG_INDEX;
				tempAnswer = tempAnswer.replaceFirst(""+guess.charAt(i), ".");
			}
		}
	}

	/**
	 * This returns a list of INDEX_RESULTS for all the correct positions
	 *
	 * It takes in a list of INDEX_RESULTS corresponding to each letter in the guess, and it
	 * fills it with CORRECT everywhere there is a correct letter, and leaves the other locations unmodified
	 * After this returns, the list will contain a mix of CORRECT, INCORRECT, and CORRECT_WRONG_INDEX
	 * all in the needed locations, so analyzing the correct-ness of the word is finished
	 *
	 * @param guess the string being guessed
	 * @param combination the list of INDEX_RESULTS corresponding to each letter,
	 *                       this will be a mix of INCORRECT and CORRECT_WRONG_INDEX
	 */
	private String correctLettersCorrectPlaces(String guess, INDEX_RESULT[] combination) {
		String tempAnswer = this.answer;
		for (int i = 0; i < answer.length(); i++) {
			if (answer.charAt(i) == guess.charAt(i)) {
				combination[i] = INDEX_RESULT.CORRECT;
				tempAnswer = tempAnswer.replaceFirst(""+guess.charAt(i), ".");
			}
		}
		return tempAnswer;
	}

	/**
	 * Gets the answer of this wordle model
	 *
	 * @return the wordle answer
	 */
	public String getAnswer() {
		/* Return the answer. Used to show the answer at the end of the game. */
		return answer;
	}

	/**
	 * This updates the list of all characters to include what has been guessed
	 *
	 * It keeps track of what value each letter got. This allows the user to see what
	 * letters they have remaining, and what the status of each guessed letter is.
	 *
	 * @param guess the word being guessed (used to access the characters)
	 * @param combination the status of each character as being correct/incorrect/semi-correct within the guess
	 */
	private void updateGuessedCharacters(String guess, INDEX_RESULT[] combination) {
		for (int i = 0; i < guess.length(); i++) {

			// we dont want to overwrite useful information
			if (guessedCharacters[guess.charAt(i) - 'A'] == INDEX_RESULT.CORRECT) continue;
			if (guessedCharacters[guess.charAt(i) - 'A'] == INDEX_RESULT.CORRECT_WRONG_INDEX &&
					combination[i] != INDEX_RESULT.CORRECT) continue;

			// overwrite with better information
			guessedCharacters[guess.charAt(i) - 'A'] = combination[i];
		}
	}

	/**
	 * This returns all the guessed characters (keeping information about that status of each character)
	 *
	 * @return the status of every character (correct/incorrect/ungussed/etc.)
	 */
	public INDEX_RESULT[] getGuessedCharacters() {
		/* Return the guessed characters. */
		return guessedCharacters;
	}

	/**
	 * This handles a guess against the model.
	 *
	 * It analyzes the word to determine the validity of each letter. It uses this to update the
	 * information about the guessed letters and it returns a new guess object
	 * which holds the needed information about the guess.
	 *
	 * @param guess the word being guessed
	 * @return a guess object which holds the parsed information about the guess
	 */
	public Guess handleGuess(String guess) {
		// starting off, every letter is incorrect
		INDEX_RESULT[] combination = new INDEX_RESULT[guess.length()];
		Arrays.fill(combination, INDEX_RESULT.INCORRECT);

		// then fill in the correct ones
		String tempAnswer = correctLettersCorrectPlaces(guess, combination);
		// fill in the semi-right ones first
		correctLettersWrongPlaces(guess, combination, tempAnswer);

		// now combination is a mix of correct, incorrect, and semi-correct
		// so we pass it into update guessed characters so they have accurate information
		updateGuessedCharacters(guess, combination);

		return new Guess(guess, combination, guess.equalsIgnoreCase(this.answer));
	}

}
