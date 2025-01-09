package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.RandomWorld;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static Random random;

    private boolean gameOver = false;
    private int seed;
    private TETile[][] world;
    private RandomWorld randomWorld;

    private int currentX;
    private int currentY;
    private int destinationX;
    private int destinationY;
    private final TETile player = Tileset.AVATAR;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        drawStartFrame();
        readStartOperation();

        generateInitialWorld();

        receiveOperations();
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
        this.seed = getSeed(inputCharArray);
        int index = getFirstPoint(inputCharArray);
        int length = input.length();

        generateInitialWorld();

        for(int i = index; i < length; i++) {
            readOperation(inputCharArray[i]);
        }

        TETile[][] finalWorldFrame = this.world;
        return finalWorldFrame;
    }

    //return the seed number of the input string.
    public int getSeed(char[] input) {
        seed = 0;
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

    public void drawStartFrame() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, 2*HEIGHT/3, "CS61B: THE GAME");
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2, "New Game (N)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 2, "Load Game (L)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 4, "Quit (Q)");
        StdDraw.show();
    }

    public static void drawFrame(String s) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2, s);
        StdDraw.show();
    }

    public void drawTheWorld() {
        ter.renderFrame(world);
    }

    public void readStartOperation() {
        char newChar;
        seed = 0;
        while (!StdDraw.hasNextKeyTyped()) {}
        newChar = StdDraw.nextKeyTyped();

        if (newChar == 'n' || newChar == 'N') {
            drawFrame("Enter Seed :");
            while (true) {
                while (!StdDraw.hasNextKeyTyped()) {}
                newChar = StdDraw.nextKeyTyped();
                if (newChar == 's' || newChar == 'S') {
                    break;
                } else {
                    seed *= 10;
                    seed += newChar - '0';
                    flashSeedDigits();
                }
            }
        }
        //load has not finished.
    }

    public void flashSeedDigits() {
        int seed = this.seed;
        String seedString = String.valueOf(seed);
        drawFrame("Enter Seed :" + seedString);
    }

    public void generateInitialWorld() {
        this.randomWorld = new RandomWorld(seed);
        world = new TETile[WIDTH][HEIGHT];
        randomWorld.generateNewWorld(world);
        drawTheWorld();
        updateCurrentPosition();
        updateDestination();
    }

    public void receiveOperations() {
        char operation;
        while (!gameOver) {
            while (!StdDraw.hasNextKeyTyped()) {}
            operation = StdDraw.nextKeyTyped();
            readOperation(operation);
            gameOver = checkGameOver();
        }
        victory();
    }

    private void readOperation(char operation) {
        if (operation == 'w' || operation == 'W') {
            if (checkMovement(currentX, currentY + 1)) {
                randomWorld.updateWorld(world, currentX, currentY + 1);
            }
        }
        if (operation == 's' || operation == 'S') {
            if (checkMovement(currentX, currentY - 1)) {
                randomWorld.updateWorld(world, currentX, currentY - 1);
            }
        }
        if (operation == 'a' || operation == 'A') {
            if (checkMovement(currentX - 1, currentY)) {
                randomWorld.updateWorld(world, currentX - 1, currentY);
            }
        }
        if (operation == 'd' || operation == 'D') {
            if (checkMovement(currentX + 1, currentY)) {
                randomWorld.updateWorld(world, currentX + 1, currentY);
            }
        }
        updateCurrentPosition();
        drawTheWorld();
    }

    private void updateCurrentPosition() {
        this.currentX = randomWorld.getCurrentX();
        this.currentY = randomWorld.getCurrentY();
    }

    private void updateDestination() {
        this.destinationX = randomWorld.getDestinationX();
        this.destinationY = randomWorld.getDestinationY();
    }

    private boolean checkMovement(int newX, int newY) {
        if (world[newX][newY] == Tileset.GRASS) {
            return true;
        }
        return false;
    }

    private boolean checkGameOver() {
        if (currentX == destinationX && currentY + 1 == destinationY) {
            return true;
        }
        if (currentX == destinationX && currentY - 1 == destinationY) {
            return true;
        }
        if (currentX + 1 == destinationX && currentY == destinationY) {
            return true;
        }
        if (currentX - 1 == destinationX && currentY == destinationY) {
            return true;
        }
        return false;
    }

    private void victory() {
        StdDraw.clear(Color.black);
        StdDraw.picture(WIDTH/2, HEIGHT/2, "C:/Users/pc/Desktop/新建文件夹/微信图片_20250109235646.jpg");
        StdDraw.show();
    }
}
