package view;

public class Wordle {
	
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-text")) new WordleTextView();
        else WordleGUIView.main(args);
    }
    
}
