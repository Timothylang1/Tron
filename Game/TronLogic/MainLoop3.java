package TronLogic;

import Graphics.*;
import Graphics.events.Key;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

public class MainLoop3 {

    // Used for canvas
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 800;

    // Used for starting placement
    private static final int STARTING_CIRCLE_RADIUS = 190; // TO REMOVE
    // private static final int STARTING_CIRCLE_RADIUS = 200;

    // Actual size of the game // CHANGE THESE NUMBERS ----------------------------------
    private static final int GAME_WIDTH = 1600;
    private static final int GAME_HEIGHT = 1600;
    
    private static GameClient game;

    private static int playernum, totalPlayers;
    private static String playerNum;

    // Keeps track of which direction the bike is currently heading (all bikes start going north at the start)
    private int direction = 0;

    private static ArrayList<Bike2> bikers;

    private static GraphicsGroup Lines;

    private static CanvasWindow canvas;

    // Used to help keep the main screen centered on your bike until it dies

    // Used to make it so that when the player loses, it changes what the mouse input buttons do
    private static Eventhandler eventhandler1, eventhandler2;

    public MainLoop3() throws Exception {
        canvas = new CanvasWindow("Tron", WINDOW_WIDTH, WINDOW_HEIGHT);
        bikers = new ArrayList<>();
        Lines = new GraphicsGroup();

        canvas.add(Lines);

        // Handles all client input
        try {
            game = new GameClient();
        }
        catch (Exception e) {
            throw e;
        }

        // Gets player number and total number of players
        playernum = game.getPlayerNum();
        totalPlayers = game.getTotalPlayers();
        // // Used this to help make your bike be in the center of the canvas // TO REMOVE
        double playerAngle = Math.PI * 2 / totalPlayers * playernum;

        // Setsup borders
        // int borderWidth = 15;
        double startingX = WINDOW_WIDTH / 2 - Math.cos(playerAngle) * STARTING_CIRCLE_RADIUS - GAME_WIDTH / 2; // DRAW A PICTURE TO HELP UNDERSTAND
        double startingY = WINDOW_HEIGHT / 2 - Math.sin(playerAngle) * STARTING_CIRCLE_RADIUS - GAME_HEIGHT / 2;
        // double startingX = 0;
        // double startingY = 0;
        int borderWidth = 15;
        setupBorders(startingX, startingY, borderWidth, GAME_HEIGHT);
        setupBorders(startingX, startingY, GAME_WIDTH, borderWidth);
        setupBorders(startingX + GAME_WIDTH - borderWidth, startingY, borderWidth, GAME_HEIGHT);
        setupBorders(startingX, startingY + GAME_HEIGHT - borderWidth, GAME_WIDTH, borderWidth);

        // Creates bikers
        System.out.println("totalplayer = " + totalPlayers);
        for (int counter = 0; counter < totalPlayers; counter++) {
            // Starting coordinates follow a circle
            double angle = Math.PI * 2 / totalPlayers * counter;
            Bike2 biker = new Bike2(STARTING_CIRCLE_RADIUS * Math.cos(angle) - // TO REMOVE
            STARTING_CIRCLE_RADIUS * Math.cos(playerAngle) + WINDOW_WIDTH / 2, 
            STARTING_CIRCLE_RADIUS * Math.sin(angle) - 
            STARTING_CIRCLE_RADIUS * Math.sin(playerAngle) + WINDOW_HEIGHT / 2);
            // Bike2 biker = new Bike2(STARTING_CIRCLE_RADIUS * Math.cos(angle) + WINDOW_WIDTH / 2,
            // STARTING_CIRCLE_RADIUS * Math.sin(angle) + WINDOW_HEIGHT / 2);

            bikers.add(biker);
            canvas.add(biker);
        }

        // Creates a list of possible directions the bike can go (compass)
        List<Character> Directions = List.of('n', 'e', 's', 'w');

        Bike2.changeCanvasDirection('n');

        // Converts player number int into a two digit number
        if (playernum < 10) {
            playerNum = "0" + Integer.toString(playernum);
        }
        else {
            playerNum = Integer.toString(playernum);
        }
        
        // Originally, we only care that the right and left arrows change the direction of the biker, but after your bike is removed, 
        // this expression will change
        eventhandler1 = key -> {
            if (key.equals(Key.RIGHT_ARROW)) {
                if (direction == 3) {
                    direction = 0;
                }
                else {
                    direction += 1;
                }
                game.tosend(playerNum + Directions.get(direction));
            }
            else if (key.equals(Key.LEFT_ARROW)) {
                if (direction == 0) {
                    direction = 3;
                }
                else {
                    direction -= 1;
                }
                game.tosend(playerNum + Directions.get(direction));
            }
        };
        eventhandler2 = key -> {};
        // Upon key press, tell server that the bike wants to change direction
        canvas.onKeyDown((event) -> eventhandler1.HandleEvent(event.getKey()));
        canvas.onKeyUp((event -> eventhandler2.HandleEvent(event.getKey())));
        canvas.animate(() -> {
            // Waits to receive string and process before doing anything else
            // Server sends string a set number of times a second, so it acts like a framerate
            // Thread will pause until it has received a string from the server
            String input = game.getReceived();
            // Checks how long the input is, so code now knows how many times it needs to iterate over
            int counter = input.length() / 3;
                
            // first part is player num (WILL ALWAYS BE TWO DIGITS (ex. 08)), second is direction ('n', 'e', 's', 'w')
            for (int i = 0; i < counter; i++) {
                // Changes the direction of any biker input
                int player = Integer.valueOf(input.substring(i * 3, i * 3 + 2));
                Character direction = input.charAt(i * 3 + 2);
                Bike2 bike = bikers.get(player);
                if (bike.isAlive()) {
                    bike.change(direction);
                    if (bike.equals(bikers.get(playernum))) {
                        Bike2.changeCanvasDirection(direction);
                    }
                }
                else {
                    if (bike.equals(bikers.get(playernum))) {
                        Bike2.changeViewOfCanvas(direction);
                    }
                }
                // If the input is from this player, then change the direction of all of the objects so that the bike remains in the center
                        
            }
            // Moves all the objects in the correct direction except the bikes to keep the center bike centered
            Lines.moveBy(-Bike2.getCanvasDx(), -Bike2.getCanvasDy()); // TO REMOVE
            Bike2.updateTotalMovement();

            // Moves all the alive bikes in their set direction
            for (Bike2 biker : bikers) {
                biker.moveBy();
            }
        });
    }

    private void setupBorders(double x, double y, int width, int height) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setStroked(false);
        Color color = new Color(0, 0, 0);
        rectangle.setFillColor(color);
        Lines.add(rectangle);
    }

    public static GraphicsGroup getLines() {
        return Lines;
    }

    public static void destroyBike(Bike2 bike) {
        canvas.remove(bike);

        // If the bike is this players bike, than we want to change the keyboard inputs
        if (bikers.get(playernum).equals(bike)) {
            // Sends counter string to the last known direction the bike was going so screen stops moving
            sendCounterString();
            eventhandler1 = key -> {
                if (key.equals(Key.RIGHT_ARROW)) {
                    game.tosend(playerNum + 'e');
                }
                else if (key.equals(Key.LEFT_ARROW)) {
                    game.tosend(playerNum + 'w');
                }
                else if (key.equals(Key.UP_ARROW)) {
                    game.tosend(playerNum + 'n');
                }
                else if (key.equals(Key.DOWN_ARROW)) {
                    game.tosend(playerNum + 's');
                }
            };
            eventhandler2 = key -> {
                if (key.equals(Key.RIGHT_ARROW) || key.equals(Key.LEFT_ARROW)) {
                    game.tosend(playerNum + 'a');
                }
                else if (key.equals(Key.UP_ARROW) || key.equals(Key.DOWN_ARROW)) {
                    game.tosend(playerNum + 'b');
                }
            };
        }
    }

    private static void sendCounterString() {
        int dx = Bike2.getCanvasDx();
        if (dx != 0) {
            game.tosend(playerNum + 'a');
        }
        else {
            game.tosend(playerNum + 'b');
        }
    }
}
