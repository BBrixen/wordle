package utilities;

import javafx.scene.paint.Color;

/**
 * @author Bennett Brixen
 *
 * This enum represents the result of a guess for a single index.
 * At any one index, that guess could take on one of four values:
 * 	1. The letter at that index could be incorrect, as in not in the answer word.
 * 	2. The letter could be in the answer word at the same exact index.
 * 	3. The letter could be in the answer word but in a different index.
 * 	4. The letter has not been guessed yet
 * The user of the class may use 'getDescription' to get a printable
 * description of each enum for printing to the console.
 *
 * There is also a colorcode attribute which can be used in a fairly nifty way to apply
 * ASCII color to each element. We store the result of a word analysis in a list of INDEX_RESULTs
 * which means we can loop through the characters and results at the same time and essentially add them
 * together to apply the color coding
 *
 * I used the Javafx Color similarly to the ascii color code, this means that the color of the labels is stored
 * along with the result of the index.
 */
public enum INDEX_RESULT {

	INCORRECT("Incorrect!", "\u001B[31m", Color.rgb(50,50,50)),
	CORRECT("Correct!", "\u001B[32m", Color.GREEN),
	CORRECT_WRONG_INDEX("Correct but wrong location", "\u001B[34m", Color.YELLOW),
	UNGUESSED("???", "\u001B[0m", Color.WHITE); // added this for storing unguessed characters in the list of all characters

	private final String description;
	private final String asciiColor;
	private final Color javafxColor;

	/**
	 * This creates an index result.
	 *
	 * Each index result must have a description along with two different color codes for the different views
	 * @param description - type of index, either incorrect, correct, correct but wrong location, or ??? (for unguessed)
	 * @param ascii - the ascii color code
	 * @param fx - the javafx color code
	 */
	INDEX_RESULT(String description, String ascii, Color fx) {
		this.description = description;
		this.asciiColor = ascii;
		this.javafxColor = fx;
	}

	/**
	 * Returns a description of the enum value.
	 *
	 * @return A string containing the description of the enum value.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns an ASCII colorcode of the enum value
	 *
	 * This is used to color each letter. The color values are added before each letter when printing
	 * Incorrect = red; correct = green; correct wrong position = blue; unguessed = default
	 *
	 * @return the ASCII color code for this type of guess
	 */
	public String getAsciiColor() {
		return this.asciiColor;
	}

	public Color getJavaFXColor() {
		return this.javafxColor;
	}
}
