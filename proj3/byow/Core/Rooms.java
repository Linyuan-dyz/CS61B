package byow.Core;

public class Rooms {
    private int[] leftUp = new int[2];
    private int[] rightDown = new int[2];
    private int width;
    private int height;
    private int WIDTH;
    private int HEIGHT;

    public Rooms(int width, int height, int leftUpX, int leftUpY, int WIDTH, int HEIGHT) {
        this.width = width;
        this.height = height;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.leftUp[0] = leftUpX;
        this.leftUp[1] = leftUpY;
        this.rightDown[0] = leftUpX + width;
        this.rightDown[1] = leftUpY -height;
    }

    public void fixRooms() {
        //recalculate the width of this room in case of crash the right wall.
        if (rightDown[0] >= WIDTH - 2) {
            this.width = WIDTH - 2 - leftUp[0];
            this.rightDown[0] = WIDTH - 2;
        }
        //recalculate the height of this room in case of crash the bottom line.
        if (rightDown[1] <= 1) {
            this.height = leftUp[1] - 1;
            this.rightDown[1] = 1;
        }
    }

    public int getLeftUpX() {
        return this.leftUp[0];
    }

    public int getLeftUpY() {
        return this.leftUp[1];
    }

    public int getRightDownX() {
        return this.rightDown[0];
    }

    public int getRightDownY() {
        return this.rightDown[1];
    }

    public int getWidth() {return this.width;}

    public int getHeight() {return this.height;}
}
