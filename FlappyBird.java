import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 735;
    int boardHeight = 406;

    // Image
    Image bImg;
    Image gImg;
    Image p1Img;
    Image p2Img;

    // Bird (Ghost)
    int birdX = boardHeight / 9;
    int birdY = boardHeight / 2;
    int birdWidth = 64;
    int birdHeight = 54;

    // Start and Restart buttons
    JButton startButton;
    JButton restartButton;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 60;
    int pipeHeight = 300;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
            this.x = boardWidth;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    Random random = new Random();
    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    int score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load Images
        bImg = new ImageIcon(getClass().getResource("./background1.jpeg")).getImage();
        gImg = new ImageIcon(getClass().getResource("./ghost11.png")).getImage();
        p1Img = new ImageIcon(getClass().getResource("./pipe111.png")).getImage();
        p2Img = new ImageIcon(getClass().getResource("./pipe22.png")).getImage();

        // Bird
        bird = new Bird(gImg);
        pipes = new ArrayList<>();

        // Initialize buttons
        startButton = new JButton("START");
        startButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 - 25, 100, 50);
        startButton.addActionListener(e -> startGame());

        restartButton = new JButton("Restart");
        restartButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 - 25, 100, 50);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false); // Initially hidden

        setLayout(null);
        add(startButton);
        add(restartButton);

        // Pipe spawning
        placePipesTimer = new Timer(1500, e -> placepipes());

        // Game loop
        gameLoop = new Timer(1000 / 60, this);
    }

    void startGame() {
        // Start the game logic
        gameStarted = true;
        startButton.setVisible(false);
        placepipes();
        placePipesTimer.start();
        gameLoop.start();
    }

    void restartGame() {
        // Reset game state
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        restartButton.setVisible(false);
        gameLoop.start();
        placePipesTimer.start();
    }

    void placepipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 3;

        Pipe topPipe = new Pipe(p1Img);
        topPipe.y = randomPipeY;
        topPipe.x = boardWidth;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(p2Img);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        bottomPipe.x = boardWidth;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(bImg, 0, 0, boardWidth, boardHeight, null);
        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        // Pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score display
        g.setColor(Color.white);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        if (gameOver) {
            g.drawString("Game Over : " + score, 270, 300);
        } else {
            g.drawString("Score : " + score, 10, 35);
        }
    }

    public void move() {
        if (!gameStarted) return; // No movement until game starts

        // Bird movement and gravity
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // Prevent bird from going off top

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                if (pipe.img == p2Img) {
                    score++;
                }
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
        pipes.removeIf(pipe -> pipe.x + pipe.width < 0);
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
            restartButton.setVisible(true); // Show Restart button when game is over
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                velocityY = -9;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
