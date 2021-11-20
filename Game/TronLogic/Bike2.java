package TronLogic;

import Graphics.GraphicsGroup;
import Graphics.Rectangle;
import java.awt.Color;
import java.util.Random;

public class Bike2 extends Rectangle { //CHANGE TO ICON AFTER ALL TESTS PASS

    // Used for bike direction
    private int dx, dy;

    // Used to modify direction of bike so that the canvas is always centered around the players bike, or they can move their screen after death
    private static int canvasdx;
    private static int canvasdy;
    private static int totalMovementX = 0;
    private static int totalMovementY = 0;

    // Speed of bike
    private static final int SPEED = 5;

    // Bike will be a square with this as it's width and height
    private static final int BIKER_DIMENSIONS = 20;

    // Used in the collisions method
    private static final int RADIUS = BIKER_DIMENSIONS / 2;

    // Half the size plus a little extra to help with checking collisions so that it can't fit between the biker points of collisions
    private static final int LINE_WIDTH = (BIKER_DIMENSIONS + 1) / 2;

    // Create a color for line and for bike
    private Color color;

    // Used to help draw the rectangle that is the current line behind the bike
    private Rectangle currentLine;
    private double x, y;

    // Used to keep track of lines so that once the bike is destroyed, it removes all of the lines
    private GraphicsGroup group;

    private boolean isAlive = true;

    public Bike2(double startX, double startY) {
        super(0, 0, BIKER_DIMENSIONS, BIKER_DIMENSIONS);
        group = new GraphicsGroup();

        // Adds the graphics from this bike to the canvas
        MainLoop3.getLines().add(group);

        setCenter(startX, startY);

        // Used to determine the color at the start
        Random rand = new Random();
        color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        setFillColor(color);

        // Sets the initial direciton to north
        change('n');
    }

    public void change(Character direction) {
        switch (direction) {
            case 'n':
                dy = -SPEED;
                dx = 0;
                break;
            case 'e':
                dx = SPEED;
                dy = 0;
                break;
            case 's':
                dy = SPEED;
                dx = 0;
                break;
            case 'w':
                dx = -SPEED;
                dy = 0;
                break;
        }
        createLine();
    }

    private void createLine() {

        // Creates new rectangle to now follow the bike
        currentLine = new Rectangle(0, 0, LINE_WIDTH, LINE_WIDTH);

        // Set the x and y coordinates so that we can change the size of the rectangle
        x = getCenter().getX();
        y = getCenter().getY();
        currentLine.setCenter(x, y);
        currentLine.setStroked(false);
        currentLine.setFillColor(color);

        // Adds to group that saves all the lines that have been drawn by this bike
        group.add(currentLine);
    }

    public void moveBy() {
        if (isAlive) {
            // Sets up movement for the current line being draw
            x -= canvasdx;
            y -= canvasdy;

            // Moves bike
            super.moveBy(dx - canvasdx, dy - canvasdy);

            // Gets all the values we need to evaluate the next expressions
            double currentX = getCenter().getX();
            double currentY = getCenter().getY();
            GraphicsGroup Lines = MainLoop3.getLines();
            double toCheckPointX = currentX + dx / SPEED * RADIUS;
            double toCheckPointY = currentY + dy / SPEED * RADIUS;

            // Checks for collision with the rest of the lines at the edge of the direction it's currently going
            // There are three points to check to ensure that the line doesn't slip through
            if (Lines.getElementAt(toCheckPointX, toCheckPointY) != null || 
                Lines.getElementAt(toCheckPointX + dy / SPEED * RADIUS, toCheckPointY + dx / SPEED * RADIUS) != null || 
                Lines.getElementAt(toCheckPointX - dy / SPEED * RADIUS, toCheckPointY - dx / SPEED * RADIUS) != null) {
                // If this occurs, then that player lost, so now we begin the process of removing everything related to them from the game

                // Remove from the biker gang
                MainLoop3.destroyBike(this);

                Lines.remove(group);

                // Makes isAlive = false so that it's removed from the aliveBikers list so that it's moveBy() method isn't called
                // Just helps with processing power
                isAlive = false;
            }
            else {
                // Draws line behind it by resizing and recentering it
                // Because the lines graphicsgroup moves, include this to recenter it based on CANVAS center
                currentLine.setSize(Math.abs(currentX - x) + LINE_WIDTH, Math.abs(currentY - y) + LINE_WIDTH);
                currentLine.setCenter((currentX + x) / 2 + totalMovementX, (currentY + y) / 2 + totalMovementY);
            }
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public static void changeCanvasDirection(Character direction) { // TO REMOVE
        switch (direction) {
            case 'n':
                canvasdy = -SPEED;
                canvasdx = 0;
                break;
            case 'e':
                canvasdx = SPEED;
                canvasdy = 0;
                break;
            case 's':
                canvasdy = SPEED;
                canvasdx = 0;
                break;
            case 'w':
                canvasdx = -SPEED;
                canvasdy = 0;
                break;
        }
    }

    public static int getCanvasDx() { // TO REMOVE
        return canvasdx;
    }

    public static int getCanvasDy() {
        return canvasdy;
    }

    public static void updateTotalMovement() {
        totalMovementX += canvasdx;
        totalMovementY += canvasdy;
    }

    public static void changeViewOfCanvas(Character direction) {
        switch (direction) {
            case 'n':
                canvasdy = -SPEED;
                break;
            case 'e':
                canvasdx = SPEED;
                break;
            case 's':
                canvasdy = SPEED;
                break;
            case 'w':
                canvasdx = -SPEED;
                break;
            case 'a':
                canvasdx = 0;
                break;
            case 'b':
                canvasdy = 0;
                break;
        }
    }
}
