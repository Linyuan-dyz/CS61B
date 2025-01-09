package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class RandomWorld {
    private int seed;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final Random random = new Random();

    public RandomWorld(int seed) {
        this.seed = seed;
    }

    //generate a new random room, if this room crash the lines, fix it.
    private static Rooms generateRooms() {
        int width = random.nextInt(6) + 1;
        int height = random.nextInt(6) + 1;
        int leftUpX = random.nextInt(WIDTH - 2) + 1;
        int leftUpY = random.nextInt(HEIGHT - 2) + 1;
        Rooms newRoom = new Rooms(width, height, leftUpX, leftUpY, WIDTH, HEIGHT);
        newRoom.fixRooms();
        return newRoom;
    }

    //after check the room is not conflicted, fill the world with this room.
    private static void fillTheRoom(Rooms room, TETile[][] world) {
        int leftUpX = room.getLeftUpX();
        int leftUpY = room.getLeftUpY();
        int rightDownX = room.getRightDownX();
        int rightDownY = room.getRightDownY();
        for(int x = leftUpX; x <= rightDownX; x++) {
            for(int y = rightDownY; y <= leftUpY ; y++) {
                world[x][y] = Tileset.GRASS;
            }
        }
    }

    //after check the room is not conflicted, fill the world with the surrounding wall.
    private static void fillTheWall(Rooms room, TETile[][] world) {
        int leftUpX = room.getLeftUpX();
        int leftUpY = room.getLeftUpY();
        int rightDownX = room.getRightDownX();
        int rightDownY = room.getRightDownY();
        for(int x = leftUpX - 1; x <= rightDownX + 1; x++){
            if (world[x][leftUpY + 1] == Tileset.NOTHING) {
                world[x][leftUpY + 1] = Tileset.WALL;
            }
            if (world[x][rightDownY - 1] == Tileset.NOTHING) {
                world[x][rightDownY - 1] = Tileset.WALL;
            }
        }
        for(int y = rightDownY - 1; y <= leftUpY + 1; y++) {
            if (world[leftUpX - 1][y] == Tileset.NOTHING) {
                world[leftUpX - 1][y] = Tileset.WALL;
            }
            if (world[rightDownX + 1][y] == Tileset.NOTHING) {
                world[rightDownX + 1][y] = Tileset.WALL;
            }
        }
    }

    private static int min(int x, int y) {
        if (x >= y) {return y;}
        return x;
    }

    private static int max(int x, int y) {
        if (x >= y) {return x;}
        return y;
    }

    //generate path between two unconnected rooms.
    private static void connectTwoRooms(Rooms room1, Rooms room2, TETile[][] world) {
        //randomly choose the width and height of two rooms, combine them with the leftUp point as the start point.
        int width1 = room1.getWidth();
        int height1 = room1.getHeight();
        int width2 = room2.getWidth();
        int height2 = room2.getHeight();
        int resourceX = room1.getLeftUpX();
        int resourceY = room1.getLeftUpY();
        int targetX = room2.getLeftUpX();
        int targetY = room2.getLeftUpY();
        if (width1 != 0) {
            resourceX += random.nextInt(width1);
        }
        if (height1 != 0) {
            resourceY -= random.nextInt(height1);
        }
        if (width2 != 0) {
            targetX += random.nextInt(width2);
        }
        if (height2 != 0) {
            targetY -= random.nextInt(height2);
        }

        //determine that who is the start one, who is the destination.
        if (resourceX <= targetX) {
            for(int x = resourceX; x <= targetX; x++) {
                world[x][resourceY] = Tileset.GRASS;
                if (x == targetX) {
                    if (resourceY > targetY) {
                        fillThePathWall(x, resourceY, world, "x", "down");
                    } else if (resourceY < targetY) {
                        fillThePathWall(x, resourceY, world, "x", "up");
                    } else {
                        fillThePathWall(x, resourceY, world, "x", null);
                    }
                } else {
                    fillThePathWall(x, resourceY, world, "x", null);
                }
            }
        } else {
            for(int x = targetX; x <= resourceX; x++) {
                world[x][targetY] = Tileset.GRASS;
                if (x == resourceX) {
                    if (resourceY > targetY) {
                        fillThePathWall(x, targetY, world, "x", "up");
                    } else if (resourceY < targetY) {
                        fillThePathWall(x, targetY, world, "x", "down");
                    }
                } else {
                    fillThePathWall(x, targetY, world, "x", null);
                }
            }
        }

        if (resourceY <= targetY) {
            for(int y = resourceY; y < targetY; y++) {
                if (resourceX <= targetX) {
                    world[targetX][y] = Tileset.GRASS;
                    fillThePathWall(targetX, y, world,  "y", null);
                } else {
                    world[resourceX][y] = Tileset.GRASS;
                    fillThePathWall(resourceX, y, world, "y", null);
                }
            }
        } else {
            for(int y = targetY; y < resourceY; y++) {
                if (resourceX <= targetX) {
                    world[targetX][y] = Tileset.GRASS;
                    fillThePathWall(targetX, y, world,  "y", null);
                } else {
                    world[resourceX][y] = Tileset.GRASS;
                    fillThePathWall(resourceX, y, world, "y", null);
                }
            }
        }
    }

    private static void fillThePathWall(int x, int y, TETile[][] world, String way, String direction) {
        //judge whether this is the changing point or not.
        if (direction != null) {
            if (direction.equals("up")) {
                if (world[x + 1][y] == Tileset.GRASS) {
                    world[x - 1][y] = Tileset.WALL;
                    world[x - 1][y - 1] = Tileset.WALL;
                } else {
                    world[x + 1][y] = Tileset.WALL;
                    world[x + 1][y - 1] = Tileset.WALL;
                }
                world[x][y - 1] = Tileset.WALL;
                return;
            }
            if (direction.equals("down")) {
                if (world[x + 1][y] == Tileset.GRASS) {
                    world[x - 1][y] = Tileset.WALL;
                    world[x - 1][y + 1] = Tileset.WALL;
                } else {
                    world[x + 1][y] = Tileset.WALL;
                    world[x + 1][y + 1] = Tileset.WALL;
                }
                world[x][y + 1] = Tileset.WALL;
                return;
            }
        }
        //x represents that this path goes in X line.
        //if there is NOTHING between the path, fill the wall.
        if (way.equals("x")) {
            if (world[x][y - 1] == Tileset.NOTHING) {
                world[x][y - 1] = Tileset.WALL;
            }
            if (world[x][y + 1] == Tileset.NOTHING) {
                world[x][y + 1] = Tileset.WALL;
            }
        }
        if (way.equals("y")) {
            if (world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
            if (world[x + 1][y] == Tileset.NOTHING) {
                world[x + 1][y] = Tileset.WALL;
            }
        }

    }

    /* check whether two rooms are conflicted.
    *  --------------------------------------
    *  |*                                   |
    *  |                                    |
    *  |                                    |
    *  |                                    |
    *  |                                    |
    *  |                                    |
    *  |                                   *|
    *  --------------------------------------
    *  firstly, check whether the up line and bottom line are conflicted or not.
    *  then, check whether the left line and right line are conflicted or not.
    * */
    private static boolean checkRooms(Rooms room, TETile[][] world) {
        int leftUpX = room.getLeftUpX();
        int leftUpY = room.getLeftUpY();
        int rightDownX = room.getRightDownX();
        int rightDownY = room.getRightDownY();
        for(int x = leftUpX - 1; x <= rightDownX + 1; x++){
            if (world[x][leftUpY + 1] != Tileset.NOTHING) {
                return false;
            }
            if (world[x][rightDownY - 1] != Tileset.NOTHING) {
                return false;
            }
        }
        for(int y = rightDownY - 1; y <= leftUpY + 1; y++) {
            if (world[leftUpX - 1][y] != Tileset.NOTHING) {
                return false;
            }
            if (world[rightDownX + 1][y] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

    public static void generateNewWorld(TETile[][] world) {
        //initialize a new world with NOTHING.
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        //generate [150, 200) rooms without hallways.
        int roomNum = random.nextInt(150) + 50;
        Rooms[] roomsArray = new Rooms[100];
        int index = 0;
        for(int num = 0; num <= roomNum; num++) {
            Rooms newRoom = generateRooms();
            if (checkRooms(newRoom, world)) {
                roomsArray[index] = newRoom;
                fillTheRoom(newRoom, world);
                fillTheWall(newRoom, world);
                index += 1;
            }
        }

        for(int i = 1; i < index; i++) {
            connectTwoRooms(roomsArray[i - 1], roomsArray[i], world);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        generateNewWorld(world);

        ter.renderFrame(world);
    }
}
