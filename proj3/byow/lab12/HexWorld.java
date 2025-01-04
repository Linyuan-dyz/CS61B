package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld extends Hexagon{
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    //temporary regard size value as 3.
    private static final int size = 3;

    public HexWorld(int startHight, int startWidth) {
        super(startHight, startWidth);
    }


    private static boolean checkWhetherLegal(Hexagon h, TETile[][] hexTiles) {
        int width = h.startPoint[0];
        int height = h.startPoint[1];
        if (height >= HEIGHT || height - 2 * size < 0) {
            return false;
        }
        if (width - size + 1 < 0 || width + size * 2 - 1 >= WIDTH) {
            return false;
        }
        if (hexTiles[width][height] != Tileset.NOTHING) {
            return false;
        }
        return true;
    }

    private static void drawHexagons(TETile[][] hexTiles, Hexagon currentHexagon) {
        if (checkWhetherLegal(currentHexagon, hexTiles)) {
            currentHexagon.drawHexagon(hexTiles);
            drawHexagons(hexTiles, new Hexagon(currentHexagon.upPoint[0], currentHexagon.upPoint[1]));
            drawHexagons(hexTiles, new Hexagon(currentHexagon.downPoint[0], currentHexagon.downPoint[1]));
            drawHexagons(hexTiles, new Hexagon(currentHexagon.leftUpPoint[0], currentHexagon.leftUpPoint[1]));
            drawHexagons(hexTiles, new Hexagon(currentHexagon.leftDownPoint[0], currentHexagon.leftDownPoint[1]));
            drawHexagons(hexTiles, new Hexagon(currentHexagon.rightUpPoint[0], currentHexagon.rightUpPoint[1]));
            drawHexagons(hexTiles, new Hexagon(currentHexagon.rightDownPoint[0], currentHexagon.rightDownPoint[1]));
        }
    }

    private static void fillWithHexTiles(TETile[][] hexTiles) {
        int startHight = HEIGHT / 2;
        int startWidth = WIDTH / 2;

        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                hexTiles[x][y] = Tileset.NOTHING;
            }
        }

        Hexagon startHexagon = new Hexagon(startWidth, startHight);
        drawHexagons(hexTiles, startHexagon);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        fillWithHexTiles(hexTiles);

        ter.renderFrame(hexTiles);
    }
}
