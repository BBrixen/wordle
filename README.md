# Wordle GUI

Due Sunday 3/27 11:59PM

## Project Description
For this project, we will use JavaFX to create a GUI for our Wordle game.
We will construct a new WordleGUIView that is a javafx.application.Application and use VBox, GridPane, Label and possibly other JavaFX objects to display the game. A few images of an example output are included in this repository. It would be good to refer to them as your read through this spec. One displays the GUI after the user has won and dismissed the alert. The other displays the GUI after the user has won but before they have dismissed the alert.

You do not have to follow these images perfectly, in fact you shouldn't because they represent what the minimum expectations for this project are: any person who has played Wordle before should immediately recognize your game and be able to play it without needing to ask any questions. In reality GUI should look better than this as described in the 'Hints' section below.

## Implementation
GUI - I will recommend a strategy to layout your components, but you are free to use whatever you wish. Both the guesses and the guessed characters are Label objects placed inside GridPane objects. These two separate GridPane objects are placed inside of a VBox which is then placed inside of the Scene. It is a good idea to keep a reference to the Label objects in an instance variable so you can refer back to them later and change their appearance (update their background, textColor, etc...).

Event Handling - Add an event handler on the scene like so: scene.setOnKeyReleased(...); This was one of the more annoying parts, since separate KeyEvents are used whether you are typing in characters or action keys like 'delete' and 'enter'. We of course want to catch all of these inputs. Because of that, let me give you some example code to perform the various actions. In the code assume 'ke' is the passed in KeyEvent object:

		// To check if the user pressed delete or backspace (in order to delete a character for a guess)
		ke.getCode().equals(KeyCode.DELETE) || ke.getCode().equals(KeyCode.BACK_SPACE)

		// To check if the user pressed enter (in order to enter a guess)
		ke.getCode().equals(KeyCode.ENTER)

		// To get the character entered from the keyboard
		String input = ke.getCode().getName();

MVC - You are required to use the MVC architectural pattern with at least the below classes:
1.	Wordle – This is the main class. When invoked with a command line argument of “-text”, you will launch the text-oriented UI. When invoked with a command line argument of “-gui” you’ll launch the GUI view. The default will be the GUI view if no command line argument is given. This class should be very short.
2.	WordleGUIView – This is the JavaFX GUI as described above
3.	WordleTextView – This is the UI that we built in project 2 with some slight modifications described below.
4.	WordleController – This class contains all of the game logic, and must be shared by the textual and graphical UIs. You may not call into different controllers from the different UIs and all methods provided must be useful to both front ends.
5.	WordleModel – This class contains all of the game state and must also be shared between the two front ends.

Observer/Observable - For the GUI front end, when the controller changes the model, we’d like to change the view. The model can do this for us, by notifying us when the model changes if our view is an Observer and the model is an Observable. 
Have your model class extend java.util.Observable and have your view class implement java.util.Observer. As a requirement to the Observer interface, you will need to implement the update(Observable o, Object arg) method in the View. This method should change the the label text and background colors to indicate which letters of the guess are in the word. It should also change the guessed characters at the bottom of the screen so the user knows which characters have been guessed. The Observer method: public void update(Observable o, Object arg) passes in the model or an Object arg. 'arg' is typically used to display the most recent changes. You can use the model or 'arg' in order to give the view the data it needs. 


## Changes From Project 2
1.	Controller changes - Since we need the model to hold all the game state we will need to make a few changes. 'progress' should now be stored in the model instead of the controller. Like I said in the first project, the original choice to place it in the Controller was somewhat arbitrary but now we have a reason to place it in the model, because we will want to access the progress in the Observer update() method. That also means we will be removing the methods: public Guess[] getProgress() and public INDEX_RESULT[] getGuessedCharacters() from the controller. Lastly, we changed the return type of the makeGuess function to void like so: public void makeGuess(String guess).
2.	Model changes - We changed the return type of the makeGuess function to void. And now we store the progress in the model as mentioned above. 
3.	WordleTextView changes – This class will need very minimal changes. Since we removed access to the data we need by removing methods in the controller, our text view will have to work the same way as our GUI view (which is how it should work). Our text view will make guesses into the controller, the controller will call the appropriate methods in the model, then the model will call setChanged() and notifyObservers(). These methods will update any observers, in this case, the textView which can then print out the necessary information. Nothing fundamental should change here, just some minor refactoring.

## Other Hints/Tips
1.	GUI programming gets ugly fast. Use great style standards - a minimum of instance variables, and a heavy use of constants. I left a few in the starter code so you could get a feel for it. Another important point is to decompose the separate components of your GUI. This will make editing/updating/debugging so much easier.
2.	Don't forget to add your observers using model.addObserver(this) or a line like it. 
3.	The modal alert that comes on the screen at the end of the game is a JavaFX Alert object. If you look at the documentation you should be able to figure out how to use one. After setting whatever necessary parts of your Alert, you will show it by calling alert.showAndWait(). For the GUI version, it is NOT necessary to allow the user to play again.
4.	Our user input should work exactly like the real Wordle game. You type in the characters you want for that guess, you can also press backspace to delete a character, and press enter when you want to make that your guess.
5.	You need to somehow indicate to the user that their guess is invalid if they type in an invalid guess. I will leave this up to you.
6.	The UI in the pictures and the one I will show you in class is the minimal amount of 'pretty' it should be. In reality you should go farther to make it look better. For instance, the default yellow I used makes it very hard to read the characters. Can you change the borders of the labels to make them look better? What about rounded rectangles? Can you add some snazzy animations? We expect your projects to look/feel professional!
7.	Remember that you need to configure your run configurations in order to run your JavaFX program.


## Requirements
- A main class named Wordle, that launches your view using:
		Application.launch(WordleGUIView.class, args);
	for the GUI version or launches the text version, depending on the commandline argument given
- At least the classes described above, with more as you deem necessary (for instance, Exception classes or other objects you see fit).
- You are not required to use your ArraySet class.
- Javadoc comments on all classes and files, including the classes I provided.
- Finally, you must use one animation in your project!! You can choose what it is. Remember in class we talked about the 8 JavaFX Transition classes. We also talked about TimeLine and KeyFrame objects.

## Submission
As always, the last pushed commit prior to the due date will be graded.
