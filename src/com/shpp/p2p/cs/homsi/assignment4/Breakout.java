package com.shpp.p2p.cs.homsi.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Breakout extends WindowProgram {

    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 700;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static int NTURNS = 3;

    /**
     * Paddle, Ball, Brick
     */
    private GRect paddle;
    private GOval ball;
    private GRect brick;

    /**
     * Random for ball initial velocity
     */
    private static final RandomGenerator randomGenerator = RandomGenerator.getInstance();
    // set x,y velocity
    double vx = randomGenerator.nextDouble(1.0, 3.0);
    private double vy;

    /**
     * Label for restart text
     */
    private GLabel restartGame;

    /**
     * Amount of broken bricks in game
     */
    private int brokenBricks;

    /**
     * Game speed, more you add, slower it runs
     */
    private static final int GAME_SPEED = 10;

    /**
     * Temp velocity value storage for game pause
     */
    private static double VX_P = 0;
    private static double VY_P = 0;

    /**
     * Keyboard listener states
     */
    private static boolean paused = false;

    public void run() {
        startGame();
    }

    //Method in which we call methods that create paddle, blocks, balls etc.
    private void startGame() {
        //Draw tools to play
        createPaddle();
        createBricks();

        //Start up info
        waitForUser();
        //Draw ball
        createBall();
        //A little pause before the ball runs
        pause(800);
        //Run game
        moveBall();

    }

    /**
     * Method waitForUser: in this method we load tools such as listeners and show the tutorial for user
     * and then remove it from screen
     */
    private void waitForUser() {
        loadTools();
        showTutorial();
    }

    /**
     * Draw game start tutorial
     */
    private void showTutorial() {
        GLabel info = createLabel("Click on this screen to start the game!");
        info.setColor(Color.BLUE);

        GLabel rules = createLabel("The paddle will follow your mouse");
        rules.move(0, 60);
        rules.setColor(Color.BLUE);

        GLabel pause = createLabel("Press 'P' button to pause the game");
        pause.move(0, 120);
        pause.setColor(Color.BLUE);
        //Click to start the game
        waitForClick();
        //Clean up game start banners
        remove(info);
        remove(rules);
        remove(pause);
    }

    /**
     * Method createBricks: in this method we draw bricks on the top of the screen
     */
    private void createBricks() {
        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {

                createBrick(i, j);
                //Set row color, every 2 rows
                int rowNum = i % 10;

                if (rowNum < 2) {
                    brick.setColor(Color.RED);
                }
                if (rowNum >= 2 && rowNum < 4) {
                    brick.setColor(Color.ORANGE);
                }
                if (rowNum >= 4 && rowNum < 6) {
                    brick.setColor(Color.YELLOW);
                }
                if (rowNum >= 6 && rowNum < 8) {
                    brick.setColor(Color.GREEN);
                }
                if (rowNum >= 8) {
                    brick.setColor(Color.CYAN);
                }
                //Number of broken bricks
                brokenBricks = 0;
            }
        }

    }

    /**
     * Method moveBall: in this method we move the ball by random angle.
     */
    private void moveBall() {
        vy = 3;
        //Random angle for game start
        if (randomGenerator.nextBoolean(0.5)) {
            vx = -vx;
        }
        while (true) {

            //Bounce off x-axis
            if (ball.getX() < 0 || ball.getX() + BALL_RADIUS > WIDTH - BALL_RADIUS) {
                vx = -vx;
            }
            //Bounce off y-axis,
            if (ball.getY() < 0) {
                vy = -vy;
            } else {
                //Check for collisions with paddle
                paddleBrickBounce();
            }
            checkIfLose();

            //Move ball
            ball.move(vx, vy);

            //Speed of the game
            pause(GAME_SPEED);
        }

    }

    /**
     * Method checkIfLose: in this method we check, if we lost the game or not.
     * Otherwise we start the game again
     */
    private void checkIfLose() {
        if (ball.getY() > HEIGHT + BALL_RADIUS) {
            //Lose a turn
            NTURNS--;

            //Use this statement, if we have 2 turns.
            if (NTURNS == 2) {
                restartGame = createLabel("Don't be sad! " + "You have: " + NTURNS + " turns remaining");
                restartGame.setColor(Color.decode("#0C90F7"));
            }
            //Use this statement, if we have 1 turns.
            if (NTURNS == 1) {
                restartGame = createLabel("Oh no! " + "You have: " + NTURNS + " turn remaining");
                restartGame.move(40, 0);
                restartGame.setColor(Color.decode("#0C90F7"));
            }
            //Use this statement, if we have 0 turns.
            if (NTURNS == 0) {
                //Game over label
                GLabel gameOver = createLabel("YOU LOST!");
                gameOver.setColor(Color.RED);
                loadMusic("src/sounds/you lost.wav");
                pause(6000);
                removeAll();
                //Reset bricks broken count
                brokenBricks = 0;
                //Reset turns
                NTURNS = 3;
                // restartGame game
                startGame();
            }
            pause(4000);

            //Remove label
            remove(restartGame);

            //Set ball to center location
            ball.setLocation((WIDTH / 2.0) - (BALL_RADIUS / 2.0), (HEIGHT / 2.0) - (BALL_RADIUS / 2.0));
        }
    }

    /**
     * Method paddleBrickBounce: in this method we check the collision of paddle and bricks
     */
    private void paddleBrickBounce() {

        /*  Balls corners collision  */

        //As soon as you find an object at one of these points,
        // you can declare that the bullet has collided with the object.

        GObject downRightCollider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);

        GObject upLeftCollider = getElementAt(ball.getX(), ball.getY());

        GObject upRightCollider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());

        GObject downLeftCollider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);

        //We look at all possible angles of the ball, if some part touches the paddle, then we change the coordinate.
        if (upLeftCollider == paddle || upRightCollider == paddle || downLeftCollider == paddle || downRightCollider == paddle) {

            //Set angles for trajectory(fixed the bag, when the ball can slide on paddle)
            double offset = ball.getX() - (paddle.getX() + (PADDLE_WIDTH / 2.0));
            if (offset <= -30)
                vx = -3;
            else if (offset <= -20)
                vx = -2;
            else if (offset <= -10)
                vx = -1;
            else if (offset == 0)
                vx = -vx;
            else if (offset < 10)
                vx = 1;
            else if (offset < 20)
                vx = 2;
            else if (offset <= 30)
                vx = 3;

            if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2 * BALL_RADIUS
                    && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2 * BALL_RADIUS + BRICK_SEP) {
                vy = -vy;
            }
        }
        //Removes bricks if balls touches the bricks
        else if (upLeftCollider != null) {
            remove(upLeftCollider);
            vy = -vy;
            brokenBricks++;
        } else if (upRightCollider != null) {
            remove(upRightCollider);
            vy = -vy;
            brokenBricks++;
        } else if (downLeftCollider != null) {
            remove(downLeftCollider);
            vy = -vy;
            brokenBricks++;
        } else if (downRightCollider != null) {
            remove(downRightCollider);
            vy = -vy;
            brokenBricks++;
        }
        //Wins the game if all bricks is broken.
        if (brokenBricks == NBRICK_ROWS * NBRICKS_PER_ROW) {
            //Shows message
            GLabel win = createLabel("You won!");
            win.setColor(Color.RED);
            loadMusic("src/sounds/you win.wav");
            pause(5000);
            //Removes all broken bricks and so on
            removeAll();

            //Reset bricks broken count
            brokenBricks = 0;
            //Reset turns
            NTURNS = 3;
            //Starts the game again
            startGame();
        }
    }


    /**
     * Method "createPaddle": simple method that creates one rectangular(createPaddle), which adjust to ovals center.
     */
    private void createPaddle() {
        int x = (WIDTH / 2) - (PADDLE_WIDTH / 2);
        paddle = new GRect(x, HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setColor(Color.BLACK);
        paddle.setFilled(true);
        add(paddle);
    }

    /**
     * Method "createLabel": simple method that creates label.
     */
    private GLabel createLabel(String text) {
        GLabel gLabel = new GLabel(text, (WIDTH / 2.0) - (BALL_RADIUS / 2.0), HEIGHT / 2.0);
        gLabel.move(-gLabel.getWidth(), 0);
        gLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        gLabel.setColor(Color.BLACK);
        add(gLabel);
        return gLabel;
    }

    /**
     * Create Brick
     */
    private void createBrick(int i, int j) {
        int x = j * (BRICK_WIDTH + BRICK_SEP);
        int y = (i * (BRICK_HEIGHT + BRICK_SEP)) + BRICK_Y_OFFSET;
        brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        brick.setFilled(true);
        add(brick);
    }

    /**
     * Create ball
     */
    private void createBall() {
        int x = (WIDTH / 2) - (BALL_RADIUS / 2);
        ball = new GOval(x, (HEIGHT / 2.0) - (BALL_RADIUS / 2.0), BALL_RADIUS, BALL_RADIUS);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);
    }

    /**
     * Create mouseMove log and set boundaries for paddle
     */
    public void mouseMoved(MouseEvent mouseEvent) {
        if ((mouseEvent.getX() < getWidth() - PADDLE_WIDTH / 2) && (mouseEvent.getX() > PADDLE_WIDTH / 2)) {
            paddle.setLocation(mouseEvent.getX() - PADDLE_WIDTH / 2.0,
                    getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
        }
    }

    private void loadMusic(String text) {
        double[] backGroundM = StdAudio.read(text);
        StdAudio.play(speedUp(backGroundM));
    }

    private double[] speedUp(double[] backGroundM) {
        double[] result = new double[backGroundM.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = backGroundM[i * 2];
        }
        return result;
    }

    /**
     * Loads event listeners, mouse listener and sets canvas color.
     */
    private void loadTools() {
        //Add event listeners to use pause(nice thing when you test the game)
        addKeyListeners();
        //Mouse listener to move the paddle with your mouse
        addMouseListeners();

        //Set background's color
        setBackground(Color.decode("#D3E6FC"));
    }

    /**
     * Pause game by stopping ball
     */
    public void pauseGame() {
        if (paused) {
            VX_P = vx;
            VY_P = vy;
            vx = 0;
            vy = 0;
        } else {
            vx = VX_P;
            vy = VY_P;
        }
    }

    /**
     * Read the key that will be pressed and pause the game
     */
    public void keyPressed(KeyEvent e) {
        //Get key pressed
        int ex = e.getKeyCode();

        //Pause game
        if (ex == KeyEvent.VK_P) {
            paused = !paused;
            pauseGame();
        }
    }

}
