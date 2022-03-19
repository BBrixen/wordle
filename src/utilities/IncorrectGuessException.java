package utilities;

/**
 * @author Bennett Brixen
 * This is a new esception solely for bad guesses. It is thrown in the controller and handled in the view.
 *
 * A guess is invalid if it is:
 * too long or too short
 * non-alphabetic
 * not a valid word in the dictionary
 */
public class IncorrectGuessException extends Exception {

    /**
     * This creates an IncorrectGuessException with the provided error message
     * @param errorMessage a description about the error
     */
    public IncorrectGuessException(String errorMessage) {
        super(errorMessage);
    }
}
