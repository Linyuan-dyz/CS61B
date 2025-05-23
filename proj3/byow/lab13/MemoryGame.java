package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
    }

    //generate the random string with the length of n.
    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        int randomNum;
        String randomString = "";
        for (int i = 0; i < n; i++) {
            randomNum = RandomUtils.uniform(rand, 26);
            randomString += CHARACTERS[randomNum];
        }
        return randomString;
    }

    /* clears the canvas,
    ** sets the font to be large and bold (size 30 is appropriate),
    ** draws the input string so that it is centered on the canvas,
    ** and then shows the canvas on the screen.
    *
    * first clears the canvas,
    * draws everything necessary for the next frame,
    * and then shows the canvas.
     */
    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(width/2, height/2, s);
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    /*  Each character should be visible on the screen for 1 second
    *   and there should be a brief 0.5 second break between characters where the screen is blank.
    * */
    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        char[] lettersArray = letters.toCharArray();
        StdDraw.enableDoubleBuffering();
        for (int i = 0; i < lettersArray.length; i++) {
            String newChar = Character.toString(lettersArray[i]);

            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(width/2, height/2, newChar);
            StdDraw.show();
            StdDraw.pause(1000);

            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(width/2, height/2, "");
            StdDraw.show();
            StdDraw.pause(500);
        }
    }

    //reads n keystrokes using StdDraw and returns the string corresponding to those keystrokes
    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        Character newChar;
        String resultString = "";
        for (int i = 0; i < n; i++) {
            while (!StdDraw.hasNextKeyTyped()) {}
            newChar = StdDraw.nextKeyTyped();
            resultString += newChar;
        }
        return resultString;
    }

    /*  Start the game at round 1
     *  Display the message “Round: “ followed by the round number in the center of the screen
     *  Generate a random string of length equal to the current round number
     *  Display the random string one letter at a time
     *  Wait for the player to type in a string the same length as the target string
     *  Check to see if the player got it correct
    * */
    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.round = 1;
        this.gameOver = false;
        //TODO: Establish Engine loop
        while (!gameOver) {
            drawFrame("Round:" + round);
            StdDraw.pause(1000);
            String randString = generateRandomString(round);
            flashSequence(randString);
            String inputString = solicitNCharsInput(round);
            drawFrame(randString);
            StdDraw.pause(1000);
            if (randString.equals(inputString)) {
                round += 1;
            } else {
                gameOver = true;
                drawFrame("Game Over! You made it to round:" + round);
            }
        }
    }

}
