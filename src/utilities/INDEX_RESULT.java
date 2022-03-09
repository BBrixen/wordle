package utilities;

import javafx.scene.paint.Color;

/**
 * This enum represents the result of a guess for a single index.
 * At any one index, that guess could take on one of three values:
 * 	1. The letter at that index could be incorrect, as in not in the answer word.
 * 	2. The letter could be in the answer word at the same exact index.
 * 	3. The letter could be in the answer word but in a different index.
 * The user of the class may use 'getDescription' to get a printable
 * description of each enum for printing to the console.
 *
 * There is also a colorcode attribute which can be used in a fairly nifty way to apply
 * ASCII color to each element. We store the result of a word analysis in a list of INDEX_RESULTs
 * which means we can loop through the characters and results at the same time and essentially add them
 * together to apply the color coding
 */
public enum INDEX_RESULT {

	INCORRECT("Incorrect!", "\u001B[31m", Color.rgb(40,40,40)),
	CORRECT("Correct!", "\u001B[32m", Color.GREEN),
	CORRECT_WRONG_INDEX("Correct but wrong location", "\u001B[34m", Color.YELLOW),
	UNGUESSED("???", "\u001B[0m", Color.WHITE); // added this for storing unguessed characters in the list of all characters

	private final String description;
	private final String colorCode;
	private final Color color;

	INDEX_RESULT(String description, String colorCode, Color color) {
		this.description = description;
		this.colorCode = colorCode;
		this.color = color;
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
	public String getColorCode() {
		return this.colorCode;
	}

	public Color getColor() {
		return this.color;
	}
}
