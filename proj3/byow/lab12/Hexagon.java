package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Hexagon {
    private static final Random RANDOM = new Random();

    //temporary regard size value as 3.
    private static final int size = 3;

    protected TETile randonTile;
    protected int[] startPoint = new int[2];
    protected int[] upPoint = new int[2];
    protected int[] downPoint = new int[2];
    protected int[] leftUpPoint = new int[2];
    protected int[] leftDownPoint = new int[2];
    protected int[] rightUpPoint = new int[2];
    protected int[] rightDownPoint = new int[2];

    //the first index represents x, the second index represents y.
    public Hexagon (int startWidth, int startHight) {
        this.randonTile = randomTile();
        this.startPoint[0] = startWidth;
        this.startPoint[1] = startHight;
        this.upPoint[0] = startWidth;
        this.upPoint[1] = startHight + size * 2;
        this.downPoint[0] = startWidth;
        this.downPoint[1] = startHight - size * 2;
        this.leftUpPoint[0] = startWidth - (size * 2 - 1);
        this.leftUpPoint[1] = startHight + size;
        this.leftDownPoint[0] = startWidth - (size * 2 - 1);
        this.leftDownPoint[1] = startHight - size;
        this.rightUpPoint[0] = startWidth + (size * 2 - 1);
        this.rightUpPoint[1] = startHight + size;
        this.rightDownPoint[0] = startWidth + (size * 2 - 1);
        this.rightDownPoint[1] = startHight - size;
    }

    public void drawHexagon(TETile[][] hexTiles) {
        int width = this.startPoint[0];
        int height = this.startPoint[1];
        //draw the front part of the hexagon.
        for (int i = height; i > height - size; i--) {
            for (int j = width + (i - height); j < width - (i - height) + size; j++) {
                hexTiles[j][i] = randonTile;
            }
        }
        //draw the latter part of the hexagon.
        for(int i = height - size; i > height - size * 2; i--) {
            for(int j = width - (i + size * 2 - height) + 1; j < width + (i + size * 2 - height) - 1 + size; j++) {
                hexTiles[j][i] = randonTile;
            }
        }
    }

    /* choose a random kind of tile except NOTHING.
       then use it as the content of the new hexagon.
    */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(11);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOOR;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            case 4: return Tileset.FLOWER;
            case 5: return Tileset.LOCKED_DOOR;
            case 6: return Tileset.UNLOCKED_DOOR;
            case 7: return Tileset.SAND;
            case 8: return Tileset.MOUNTAIN;
            case 9: return Tileset.TREE;
            case 10: return Tileset.AVATAR;
            default: return Tileset.NOTHING;
        }
    }
}