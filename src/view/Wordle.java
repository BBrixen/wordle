package view;

public class Wordle {
	
    public static void main(String[] args) {
        if (args.length > 0) {
            String cmd = args[0];
            if (cmd.equals("-text")) new WordleTextView();
            else if (cmd.equals("-gui")) WordleGUIView.main(args);
        }
        else WordleGUIView.main(args);
    }
    
}
