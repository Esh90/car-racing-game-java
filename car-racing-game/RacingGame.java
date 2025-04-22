import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;

class Game extends JPanel {
    int crx, cry;    // location of the crossing
    int car_x, car_y;    // x and y location of user's car
    int speedX, speedY;    
    int nOpponent;      // the number of opponent vehicles in the game
    String imageLoc[]; 
    int lx[], ly[];  // integer arrays used to store the x and y values of the oncoming vehicles
    int score;      
    int highScore;  
    int speedOpponent[]; 
    boolean isFinished; 
    boolean isUp, isDown, isRight, isLeft;  

    private Image backgroundImage;

    public Game() {
        crx = cry = -999;  
        
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) { 
                stopCar(e); 
            }
            public void keyPressed(KeyEvent e) { 
                moveCar(e); 
            }
        });
        setFocusable(true); 
        car_x = car_y = 300;    
        isUp = isDown = isLeft = isRight = false;   
        speedX = speedY = 0;    
        nOpponent = 0; 
        lx = new int[20]; 
        ly = new int[20]; 
        imageLoc = new String[20];
        speedOpponent = new int[20]; 
        isFinished = false; 
        score = 0; 

        loadHighScore();

        backgroundImage = new ImageIcon("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/st_road.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D obj = (Graphics2D) g;
        obj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            obj.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            if (cry >= -499 && crx >= -499)
                obj.drawImage(getToolkit().getImage("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/cross_road.png"), crx, cry, this); // draw another road crossing on window

            obj.drawImage(getToolkit().getImage("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/car_self.png"), car_x, car_y, this);   // draw car on window

            if (isFinished) { 
                obj.drawImage(getToolkit().getImage("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/boom.png"), car_x - 30, car_y - 30, this); // draw explosion image on window at collision to indicate the collision has occurred
            }

            if (this.nOpponent > 0) { 
                for (int i = 0; i < this.nOpponent; i++) { 
                    obj.drawImage(getToolkit().getImage(this.imageLoc[i]), this.lx[i], this.ly[i], this); // draw onto window
                }
            }

            obj.setColor(Color.WHITE);
            obj.setFont(new Font("Arial", Font.BOLD, 20));
            obj.drawString("Score: " + score, getWidth() - 150, 30);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void moveRoad(int count) {
        if (crx == -999 && cry == -999) { 
            if (count % 10 == 0) {  
                crx = 499;      
                cry = 0;
            }
        } else {   
            crx--; 
        }
        if (crx == -499 && cry == 0) { 
            crx = cry = -999;   
        }
        car_x += speedX; 
        car_y += speedY;

        if (car_x < 0)  
            car_x = 0; 

        if (car_x + 93 >= 500) 
            car_x = 500 - 93; 

        if (car_y <= 124)    
            car_y = 124;   

        if (car_y >= 364 - 50) 
            car_y = 364 - 50; 

        for (int i = 0; i < this.nOpponent; i++) { 
            this.lx[i] -= speedOpponent[i]; 
        }

        int index[] = new int[nOpponent];
        for (int i = 0; i < nOpponent; i++) {
            if (lx[i] >= -127) {
                index[i] = 1;
            }
        }
        int c = 0;
        for (int i = 0; i < nOpponent; i++) {
            if (index[i] == 1) {
                imageLoc[c] = imageLoc[i];
                lx[c] = lx[i];
                ly[c] = ly[i];
                speedOpponent[c] = speedOpponent[i];
                c++;
            }
        }

        score += nOpponent - c; 

        if (score > highScore)   
            highScore = score;  

        nOpponent = c;

        // Check for collision

        for (int i = 0; i < nOpponent; i++) { 
            if ((ly[i] >= car_y && ly[i] <= car_y + 46) || (ly[i] + 46 >= car_y && ly[i] + 46 <= car_y + 46)) {   // if the cars collide vertically
                if (car_x + 87 >= lx[i] && !(car_x >= lx[i] + 87)) {  // and if the cars collide horizontally
                    System.out.println("My car : " + car_x + ", " + car_y);
                    System.out.println("Colliding car : " + lx[i] + ", " + ly[i]);
                    playSound("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/DBV6WJA-car-crash.wav");
                    this.finish(); 
                }
            }
        }
    }

    void finish() {
        String str = "";    
        isFinished = true;  
        this.repaint();    
        if (score == highScore && score != 0) 
            str = "\nCongratulations!!! It's a high score";  

        saveHighScore();

        int choice = JOptionPane.showConfirmDialog(this, "Game Over!!!\nYour Score : " + score + "\nHigh Score : " + highScore + str + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            restartGame(); 
        } else {
            System.exit(ABORT); 
        }
    }

    void restartGame() {
        crx = cry = -999;
        car_x = car_y = 300;
        speedX = speedY = 0;
        nOpponent = 0;
        score = 0;
        isFinished = false;
        Arrays.fill(lx, 0);
        Arrays.fill(ly, 0);
        Arrays.fill(imageLoc, null);
        Arrays.fill(speedOpponent, 0);
    }

    public void moveCar(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {   
            isUp = true;
            speedY = -1;     
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) { 
            isDown = true;
            speedY = +1;    
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { 
            isRight = true;
            speedX = 3;    
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
            isLeft = true;
            speedX = -1;    
        }
    }

    public void stopCar(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {   
            isUp = false;
            speedY = 0; 
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {    
            isDown = false;
            speedY = 0; 
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {     
            isLeft = false;
            speedX = 0; 
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {   
            isRight = false;
            speedX = 0; 
        }
    }

    private void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("highscore.txt"))) {
            pw.println(highScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class HomePage extends JPanel implements ActionListener {
    private JFrame mainFrame;
    private JButton startButton;
    private JButton instructionsButton;
    private JButton highScoreButton;
    private JButton exitButton;
    private Image backgroundImage;

    public HomePage(JFrame frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());

        backgroundImage = new ImageIcon("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/CarHomePage.png").getImage();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.gridx = 0;

        Font customFont = loadFont("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/Lobster-Regular.ttf");

        JLabel titleLabel = new JLabel("Ready to Roll on the Road?");
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.insets = new Insets(50, 10, 10, 10); 
        add(titleLabel, gbc);

        startButton = createStyledButton("Start");
        gbc.gridy = 1;
        gbc.gridwidth = 2; 
        gbc.insets = new Insets(20, 10, 10, 10); 
        add(startButton, gbc);

        instructionsButton = createStyledButton("Instructions");
        gbc.gridy = 2;
        add(instructionsButton, gbc);

        highScoreButton = createStyledButton("High Score");
        gbc.gridy = 3;
        add(highScoreButton, gbc);

        exitButton = createStyledButton("Exit");
        gbc.gridy = 4;
        add(exitButton, gbc);
    }

    private Font loadFont(String path) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        Font customFont = loadFont("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/SeymourOne-Regular.ttf");
        button.setFont(customFont.deriveFont(Font.BOLD, 24)); 
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            playSound("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/sounds/button_click.wav");
            mainFrame.getContentPane().removeAll();
            Game game = new Game();
            mainFrame.add(game);
            mainFrame.revalidate();
            mainFrame.repaint();
            game.requestFocusInWindow(); 

            new Thread(() -> {
                int count = 1, c = 1;
                while (!game.isFinished) {
                    game.moveRoad(count);
                    while (c <= 1) {
                        game.repaint();
                        try {
                            Thread.sleep(5);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                        c++;
                    }
                    c = 1;
                    count++;
                    if (game.nOpponent < 4 && count % 200 == 0) {
                        game.imageLoc[game.nOpponent] = "C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/images/car_left_" + ((int) ((Math.random() * 100) % 3) + 1) + ".png";
                        game.lx[game.nOpponent] = game.getWidth();
                        int p = (int) (Math.random() * 100) % 4;
                        if (p == 0) {
                            p = 250;
                        } else if (p == 1) {
                            p = 300;
                        } else if (p == 2) {
                            p = 185;
                        } else {
                            p = 130;
                        }
                        game.ly[game.nOpponent] = p;
                        game.speedOpponent[game.nOpponent] = (int) (Math.random() * 100) % 2 + 2;
                        game.nOpponent++;
                    }
                }
            }).start();
        } else if (e.getSource() == instructionsButton) {
            JOptionPane.showMessageDialog(mainFrame, "Instructions:\n\n1. Use the arrow keys to move your car.\n2. Avoid the opponent cars.\n3. Try to achieve a high score!\n\nGood luck and have fun!");
        } else if (e.getSource() == highScoreButton) {
            displayHighScore();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    private void playSound(String filePath) {
        try {
            File soundFile = new File("C:/Users/BEST BUY COMPUTERS/Desktop/Java2ndSemProj/car racing game/RVVF4QK-perky-game-music.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayHighScore() {
        int highScore = loadHighScore();
        JOptionPane.showMessageDialog(mainFrame, "High Score: " + highScore, "High Score", JOptionPane.INFORMATION_MESSAGE);
    }

    private int loadHighScore() {
        int highScore = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            highScore = 0;
        }
        return highScore;
    }
}

public class RacingGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Car Racing Game");
        HomePage homePage = new HomePage(frame);
        frame.add(homePage);
        frame.setSize(800, 600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}