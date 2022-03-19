package view;

import javafx.application.Application;

/**
 * @author Bennett Brixen
 *
 * This is wordle.
 * You can play with n letters via changing the constant variables in this class (check readme)
 * I would have done this through command line arguments but since we are using those to determine what view to use,
 * I did not want to interfere and possibly confuse the grader
 *
 * For gui, supply -gui in the command line, or use no command line arguments
 * For text, supply -text in the command line
 */
public class Wordle {

    public static final int wordleLength = 5, maxGuesses = 6; // typical wordle
    public static final String filename = "Dictionaries/Dictionary" + wordleLength + ".txt";
	
    public static void main(String[] args) {
        if (args.length > 0) {
            String cmd = args[0];
            if (cmd.equals("-text")) new WordleTextView();
            else if (cmd.equals("-gui")) Application.launch(WordleGUIView.class, args);
        }
        else Application.launch(WordleGUIView.class, args);
    }
    
}
