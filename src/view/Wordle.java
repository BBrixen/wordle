package view;

public class Wordle {
	
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-text")) WordleTextView.main(args);
        else WordleGUIView.main(args);
    }
    
}
