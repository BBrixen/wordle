package utilities;

public class Guess {
	
	private String guess;
	private INDEX_RESULT[] indices;
	private boolean isCorrect;
	
	public Guess(String guess, INDEX_RESULT[] indices, boolean isCorrect) {
		if (guess.length() != indices.length) {
			throw new IllegalArgumentException("The length of the guess and its index results must be equal.");
		}
		this.guess = guess;
		this.indices = indices;
		this.isCorrect = isCorrect;
	}
	
	public String getGuess() {
		return this.guess;
	}
	
	public INDEX_RESULT[] getIndices() {
		return this.indices;
	}
	
	public boolean getIsCorrect() {
		return this.isCorrect;
	}
	
}
