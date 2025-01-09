package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.RandomWorld;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        /*TODO: Fill out this method so that it run the engine using the input
         passed in as an argument, and return a 2D tile representation of the
         world that would have been drawn if the same inputs had been given
         to interactWithKeyboard().

         See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
         that works for many different input types.
        */
        ter.initialize(WIDTH, HEIGHT);
        char[] inputCharArray = input.toCharArray();
        int seed = getSeed(inputCharArray);
        int index = getFirstPoint(inputCharArray);
        int length = input.length();

        RandomWorld world = new RandomWorld(seed);
        TETile[][] generateWorld = new TETile[WIDTH][HEIGHT];
        world.generateNewWorld(generateWorld);
        ter.renderFrame(generateWorld);

        for(int i = index; i < length; i++) {
            readOperation(inputCharArray[i], generateWorld);
        }

        TETile[][] finalWorldFrame = generateWorld;
        return finalWorldFrame;
    }

    //return the seed number of the input string.
    public int getSeed(char[] input) {
        int seed = 0;
        int i = 1;
        if (input[0] == 'N' || input[0] == 'n') {
            while (input[i] <= '9' && input[i] >= '0') {
                seed *= 10;
                seed += input[i] - '0';
                i += 1;
            }
            return seed;
        }
        return 0;
    }

    //return the index that point to the first instruction.
    public int getFirstPoint(char[] input) {
        int index = 0;
        //index now point to S in "N####S"
        while (input[index] != 's' && input[index] != 'S') {
            index += 1;
        }
        return index + 1;
    }

    private void readOperation(char operation, TETile[][] world) {

    }
}
