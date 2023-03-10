import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

import objs.Apple;
import objs.Piece;
import objs.Snake;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Graphics2D;
import java.util.Scanner;

class Game extends JFrame implements ActionListener, KeyListener {

    private final Boolean MODE = false; // true - WSAD false - control
    public static final Dimension WINDOW_SIZE = new Dimension(600, 800);
    public static final int SECTOR_SIZE = 50; // 12x16
    public static final Game GAME = new Game();
    public static final SaveAsFile SAF = new SaveAsFile();
    private final Font FONT = new Font("Calibri", Font.BOLD, 100);

    private Scanner scan;
    private char control = '0';
    private int step = 0, prevStep = 0;
    private File controlFile;
    private Render render;
    private Timer timer;
    private JButton start, exit;

    private Boolean isStarted = false;

    private Snake snake;
    private Apple apple;

    public Game() {
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setTitle("Snake");
        this.render = new Render(WINDOW_SIZE);
        super.setVisible(true);
        super.add(this.render);
        this.pack();
        this.timer = new Timer(5, this);
        super.addKeyListener(this);
        if (MODE) {
            this.createStartButton();
            this.createExitButton();
            this.render.add(start);
            this.render.add(exit);
        } else {
            this.snake = new Snake();
            this.apple = new Apple();
            this.timer.start();
            super.requestFocus();
            this.isStarted = true;
            Runnable japko = () -> {
                while (true) {
                    if (this.apple.time_life() > 3) {
                        this.apple.newApple();
                    }
                }
            };
            Thread th = new Thread(japko);
            th.start();
        }
    }

    private void checkControlFile() {
        controlFile = new File("control");
        try {
            this.scan = new Scanner(controlFile);
        } catch (FileNotFoundException e) {
            e.getStackTrace();
            System.exit(0);
        }
        try {
            this.control = scan.next().charAt(0);
            this.prevStep = step;
            this.step = scan.nextInt();
        } catch (Exception ex) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.getStackTrace();
            }
            this.checkControlFile();
        }

        if (this.control == 'W') {
            this.snake.setVector(1);
        } else if (this.control == 'A') {
            this.snake.setVector(2);
        } else if (this.control == 'S') {
            this.snake.setVector(3);
        } else if (this.control == 'D') {
            this.snake.setVector(4);
        } else if (this.control == '0') {
            SAF.snakeAndAppleSave(apple, snake);
            SAF.save();
        } else {
            System.out.println("NIEROZPOZNANY ZNAK!");
        }
    }

    private void createStartButton() {
        this.start = new JButton("START");
        this.start.setBounds(50, 100, 500, 200);
        this.start.setFont(FONT);
        this.start.setBackground(Color.WHITE);
        this.start.addActionListener(this);
    }

    private void createExitButton() {
        this.exit = new JButton("EXIT");
        this.exit.setBounds(50, 500, 500, 200);
        this.exit.setFont(FONT);
        this.exit.setBackground(Color.WHITE);
        this.exit.addActionListener(this);
    }

    public static void main(String[] args) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted) {
            if (MODE || (!MODE && this.prevStep < step)) {
                this.vectorControl();
                this.bodyMoving();
                this.eatingControl();
                this.wallDetection();
                SAF.snakeAndAppleSave(apple, snake);
                SAF.save();
                for (int i = 1; i < this.snake.getPieces().size(); i++) {
                    if (this.snake.getPieces().get(i).intersects(this.snake.getPieces().get(0))) {
                        this.gameOver();
                    }
                }
                this.render.repaint();
            }
            if (!MODE) {
                checkControlFile();
            }
        } else {
            if (this.start == e.getSource()) {
                this.isStarted = true;
                this.start.setVisible(false);
                this.exit.setVisible(false);
                this.snake = new Snake();
                while (this.appleInSnake())
                    this.apple.newApple();
                this.timer.start();
                super.requestFocus();
            }
            if (this.exit == e.getSource()) {
                System.exit(0);
            }
        }
    }

    private void wallDetection() {
        if ((this.snake.getPieces().get(0).x < 0) ||
                (this.snake.getPieces().get(0).x >= Game.WINDOW_SIZE.width) ||
                (this.snake.getPieces().get(0).y < 0) ||
                (this.snake.getPieces().get(0).y >= Game.WINDOW_SIZE.height)) {
            this.gameOver();
        }
    }

    private void vectorControl() {
        if (this.snake.getVector() == 1) {
            this.snake.getPieces().get(0).prevPosition.x = this.snake.getPieces().get(0).x;
            this.snake.getPieces().get(0).prevPosition.y = this.snake.getPieces().get(0).y;
            this.snake.getPieces().get(0).y = this.snake.getPieces().get(0).y - SECTOR_SIZE;
            if (this.snake.getPieces().size() > 1)
                if (this.snake.getPieces().get(0).intersects(this.snake.getPieces().get(1))) {
                    this.snake.getPieces().get(0).y = this.snake.getPieces().get(0).y + 2 * SECTOR_SIZE;
                    this.snake.setVector(3);
                }

        } else if (this.snake.getVector() == 2) {
            this.snake.getPieces().get(0).prevPosition.x = this.snake.getPieces().get(0).x;
            this.snake.getPieces().get(0).prevPosition.y = this.snake.getPieces().get(0).y;
            this.snake.getPieces().get(0).x = this.snake.getPieces().get(0).x - SECTOR_SIZE;
            if (this.snake.getPieces().size() > 1)
                if (this.snake.getPieces().get(0).intersects(this.snake.getPieces().get(1))) {
                    this.snake.getPieces().get(0).x = this.snake.getPieces().get(0).x + 2 * SECTOR_SIZE;
                    this.snake.setVector(4);
                }

        } else if (this.snake.getVector() == 3) {
            this.snake.getPieces().get(0).prevPosition.x = this.snake.getPieces().get(0).x;
            this.snake.getPieces().get(0).prevPosition.y = this.snake.getPieces().get(0).y;
            this.snake.getPieces().get(0).y = this.snake.getPieces().get(0).y + SECTOR_SIZE;
            if (this.snake.getPieces().size() > 1)
                if (this.snake.getPieces().get(0).intersects(this.snake.getPieces().get(1))) {
                    this.snake.getPieces().get(0).y = this.snake.getPieces().get(0).y - 2 * SECTOR_SIZE;
                    this.snake.setVector(1);
                }

        } else if (this.snake.getVector() == 4) {
            this.snake.getPieces().get(0).prevPosition.x = this.snake.getPieces().get(0).x;
            this.snake.getPieces().get(0).prevPosition.y = this.snake.getPieces().get(0).y;
            this.snake.getPieces().get(0).x = this.snake.getPieces().get(0).x + SECTOR_SIZE;
            if (this.snake.getPieces().size() > 1)
                if (this.snake.getPieces().get(0).intersects(this.snake.getPieces().get(1))) {
                    this.snake.getPieces().get(0).x = this.snake.getPieces().get(0).x - 2 * SECTOR_SIZE;
                    this.snake.setVector(2);
                }
        }
    }

    private void bodyMoving() {
        for (int i = 1; i < this.snake.getPieces().size(); i++) {
            this.snake.getPieces().get(i).prevPosition.x = this.snake.getPieces().get(i).x;
            this.snake.getPieces().get(i).prevPosition.y = this.snake.getPieces().get(i).y;
            this.snake.getPieces().get(i).x = this.snake.getPieces().get(i - 1).prevPosition.x;
            this.snake.getPieces().get(i).y = this.snake.getPieces().get(i - 1).prevPosition.y;
        }
    }

    private void eatingControl() {
        if (this.snake.getPieces().get(0).x == this.apple.x &&
                this.snake.getPieces().get(0).y == this.apple.y) {
            this.snake.addPiece();
            this.apple.newApple();
            while (this.appleInSnake()) {
                this.apple.newApple();
            }
        }
    }

    private Boolean appleInSnake() {
        for (Piece p : this.snake.getPieces()) {
            if (p.intersects(this.apple)) {
                return true;
            }
        }
        return false;
    }

    private void gameOver() {
        if (MODE) {
            this.timer.stop();
            this.isStarted = false;
            this.render.repaint();
            this.start.setVisible(true);
            this.exit.setVisible(true);
        } else {
            Game.SAF.gameover();
            Game.SAF.save();
            // this.prevStep = 0;
            // this.step = 0;
            // this.control = '0';
            this.snake = new Snake();
            while (this.appleInSnake()) {
                this.apple.newApple();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isStarted) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                this.snake.setVector(1);
            } else if (e.getKeyCode() == KeyEvent.VK_A) {
                this.snake.setVector(2);
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                this.snake.setVector(3);
            } else if (e.getKeyCode() == KeyEvent.VK_D) {
                this.snake.setVector(4);
            }
        }
        if (this.isStarted && e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            this.gameOver();
        } else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void goPaint(Graphics2D g2d) {
        if (isStarted) {
            g2d.setColor(Apple.COLOR);
            g2d.fill(this.apple);
            g2d.setColor(Piece.COLOR);
            for (Piece p : this.snake.getPieces()) {
                g2d.fill(p);
            }
        }
    }
}