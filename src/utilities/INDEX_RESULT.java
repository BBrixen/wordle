package utilities;

public enum INDEX_RESULT {
	INCORRECT("Incorrect"),
	CORRECT("Correct"),
	CORRECT_WRONG_INDEX("Correct letter, wrong index");
	
	private String description;
	
	private INDEX_RESULT(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
