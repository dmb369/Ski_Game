import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;
import javax.swing.JButton;
import java.util.ArrayList; 

public class Game extends JFrame {
    private static final long serialVersionUID = 1L;

    public Game() {
        add(new Board());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("resources/icons/new.ico").getImage());
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("Skiing Game");
        setResizable(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }
}

class Board extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private Timer timer;
    private Timer distanceTimer; 
    private Timer speedTimer;    
    private Timer snowflakeTimer; 

    private Sk Sk;
    private Tr[] trees;
    private Bp[] bumps;
    private Ab[] abominators;

    private int ts = 1;
    private int tc = 7;
    private int bc = 4;
    private boolean over = false;
    private boolean started = false;  // If the game has started

    private JButton s_button;  // Start button
    private JButton r_button;  // Restart button
    private JButton e_button; // Exit button

    private int d = 0; 
    private int increase = 1; 

    // Snowflakes for snowfall animation
     private ArrayList<Sf> snowflakes;  //To store the snowflakes
     private boolean snowfallActive = false;  

    public Board() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);
        setDoubleBuffered(true);
        this.setLayout(null);

        s_button = new JButton("Start Game");
        s_button.setBounds(450, 350, 120, 50);  
        s_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
       
        this.setLayout(null);  // Setting layout to null for absolute positioning
        this.add(s_button);
      
        r_button = new JButton("Restart Game");
        r_button.setBounds(447, 320, 120, 50);  
        r_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        r_button.setVisible(false); // Hiding the button
        this.add(r_button);

        e_button = new JButton("Exit Game");
        e_button.setBounds(448, 370, 120, 50);
        e_button.addActionListener(e -> System.exit(0));
        e_button.setVisible(false);
        this.add(e_button);
 
        timer = new Timer(5, this);
        timer.start();

        distanceTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (started && !over) {
                    d += 1;  // Increase d by 1 meter every 1 second
                    repaint();
                }
            }
        });

        speedTimer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (started && !over) {
                    ts += increase;  // Increase speed
                    repaint();
                }
            }
        });

        
        snowflakes = new ArrayList<>();  
        snowflakeTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (snowfallActive) {
                    // Adding new snowflake randomly on the screen
                    snowflakes.add(new Sf(new Random().nextInt(1024), 0));
                    for (Sf Sf : snowflakes) {
                        Sf.moveDown();
                    }
                    repaint();
                }
            }
        });
    }

    private void startGame() {
        
        s_button.setVisible(false);
        r_button.setVisible(false);
        e_button.setVisible(false); 

        // Initialization
        Sk = new Sk();
        trees = new Tr[tc];
        bumps = new Bp[bc];

        Random rand = new Random();

        // Generating random trees, bumps, and abominators using random method
        for (int i = 0; i < trees.length; i++) {
            int x = rand.nextInt(1024);
            int y = rand.nextInt(768);
            trees[i] = new Tr(x, y);
        }

        for (int i = 0; i < bumps.length; i++) {
            int x = rand.nextInt(1024);
            int y = rand.nextInt(768);
            bumps[i] = new Bp(x, y);
        }

        abominators = new Ab[4];
        for (int i = 0; i < abominators.length; i++) {
            abominators[i] = new Ab(rand.nextInt(1024), rand.nextInt(768));
        }

        over = false;
        started = true;
        distanceTimer.start();  
        speedTimer.start(); 
        snowflakeTimer.stop(); 
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        if (!started) {
            if (!snowfallActive) {
                snowfallActive = true;  
                snowflakeTimer.start(); 
            }
            g2d.setColor(Color.BLUE);
            g2d.setFont(g2d.getFont().deriveFont(48f));
            g2d.drawString("Skiing Game", 365, 280);
            g2d.setColor(Color.RED);
            g2d.setFont(g2d.getFont().deriveFont(24f));
            g2d.drawString("Press 'Start Game' to begin", 355, 330);

            g2d.setColor(Color.BLACK);
          
            for (Sf Sf : snowflakes) {
                g2d.fillOval(Sf.getX(), Sf.getY(), 5, 5);
            }

            return;
        }

        if (over) {
            g2d.setColor(Color.RED);
            g2d.setFont(g2d.getFont().deriveFont(30f));
            g2d.drawString("Game Over!", 425, 300);
            r_button.setVisible(true);
            e_button.setVisible(true);

            // Draw snowflakes for snowfall effect
            g2d.setColor(Color.BLACK);
            for (Sf Sf : snowflakes) {
            g2d.fillOval(Sf.getX(), Sf.getY(), 5, 5);
            }

            // Display the d
            g2d.setColor(Color.BLUE);
            g2d.setFont(g2d.getFont().deriveFont(24f));
            g2d.drawString("Distance Travelled is: " + d + " m", 365, 460);
        }

        else {
            g2d.drawImage(Sk.getImage(), Sk.getX(), Sk.getY(), this);
            
            for (Tr Tr : trees) {
                g2d.drawImage(Tr.getImage(), Tr.getX(), Tr.getY(), this);
            }

            for (Bp Bp : bumps) {
                g2d.drawImage(Bp.getImage(), Bp.getX(), Bp.getY(), this);
            }

            for (Ab abom : abominators) {
                g2d.drawImage(abom.getImage(), abom.getX(), abom.getY(), this);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(g2d.getFont().deriveFont(24f));
            g2d.drawString("Distance: " + d + " m", 20, 30);
        
        }
        
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!started) return; // Skipping if the game has not started

        if (over) {
            snowfallActive = true; // Activating snowfall on game-over screen
            snowflakeTimer.start();
            repaint();
            return;
        }

        Sk.move();

        for (Tr Tr : trees) {
            Tr.moveDown(ts);
            if (Tr.getY() > getHeight()) {
                Tr.resetPosition(new Random().nextInt(1024), 0);
            }

            if (Tr.collidesWith(Sk)) {
                over = true;
                break;
            }
        }

        for (Bp Bp : bumps) {
            Bp.moveDown(ts);
            if (Bp.getY() > getHeight()) {
                Bp.resetPosition(new Random().nextInt(1024), 0);
            }

            if (Bp.collidesWith(Sk)) {
                over = true;
                break;
            }
        }

        for (Ab abom : abominators) {
            abom.moveDown(ts);
            if (abom.getY() > getHeight()) {
                abom.resetPosition(new Random().nextInt(1024), 0);
            }

            if (abom.collidesWith(Sk)) {
                over = true;
                break;
            }
        }

        snowflakeTimer.stop(); 
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            Sk.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Sk.keyPressed(e);
        }
    }

    private void resetGame() {
        d = 0; 
        ts = 1;
        // Hidinh the buttons 
        s_button.setVisible(true);  
        r_button.setVisible(false); 
        e_button.setVisible(false);    

        snowflakes.clear();
        snowfallActive = false;
        snowflakeTimer.stop();

        distanceTimer.stop();
        speedTimer.stop();

        startGame();  
    }
}

class Sf {
    private int x;
    private int y;
    private static final int FALL_SPEED = 2; // Speed at which the Sf falls

    public Sf(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveDown() {
        y += FALL_SPEED;
        // Reset position if the Sf moves off-screen
        if (y > 768) { 
            y = 0;
            x = new java.util.Random().nextInt(1024); // Randomization is used here
        }
    }
}


class Sk {

    private Image up1 = new ImageIcon("resources/sprites/skier7.png").getImage();
    private Image up2 = new ImageIcon("resources/sprites/skier8.png").getImage();
    private Image down = new ImageIcon("resources/sprites/skier1.png").getImage();
    private Image left = new ImageIcon("resources/sprites/skier6.png").getImage();
    private Image right = new ImageIcon("resources/sprites/skier7.png").getImage();

    private byte swifter = 0;
    private Image current = left;
    private int dx;
    private int dy;
    private int x;
    private int y;

    public Sk() {
        x = 512;  // Starting from the middle horizontally
        y = 700;  // Starting from the bottom vertically
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return current;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -1;
            current = left;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 1;
            current = right;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -1;
            swifter = (byte) ((swifter + 1) % 2);
            current = (swifter == 0) ? up1 : up2;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 1;
            current = down;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}

class Tr {
    private Image[] treeImages = {
        new ImageIcon("resources/sprites/tree1.png").getImage(),
        new ImageIcon("resources/sprites/tree2.png").getImage(),
        new ImageIcon("resources/sprites/tree3.png").getImage(),
        new ImageIcon("resources/sprites/tree4.png").getImage(),
        new ImageIcon("resources/sprites/tree5.png").getImage()
    };

    private int x;
    private int y;
    private Image currentImage;

    public Tr(int x, int y) {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        this.currentImage = treeImages[rand.nextInt(treeImages.length)];
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public void resetPosition(int x, int y) {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        this.currentImage = treeImages[rand.nextInt(treeImages.length)];
    }

    public boolean collidesWith(Sk Sk) {
        int skierWidth = Sk.getImage().getWidth(null);
        int skierHeight = Sk.getImage().getHeight(null);
        int treeWidth = currentImage.getWidth(null);
        int treeHeight = currentImage.getHeight(null);

        return x < Sk.getX() + skierWidth && x + treeWidth > Sk.getX() &&
               y < Sk.getY() + skierHeight && y + treeHeight > Sk.getY();
    }

    public Image getImage() {
        return currentImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Bp {
    private Image[] bumpImages = {
        new ImageIcon("resources/sprites/bump1.png").getImage(),
        new ImageIcon("resources/sprites/bump2.png").getImage()
    };

    private Image currentImage;
    private int x;
    private int y;

    public Bp(int x, int y) {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        this.currentImage = bumpImages[rand.nextInt(bumpImages.length)];
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public void resetPosition(int x, int y) {
        this.x = x;
        this.y = y;
        Random rand = new Random();
        this.currentImage = bumpImages[rand.nextInt(bumpImages.length)];
    }

    public boolean collidesWith(Sk Sk) {
        int skierWidth = Sk.getImage().getWidth(null);
        int skierHeight = Sk.getImage().getHeight(null);
        int bumpWidth = currentImage.getWidth(null);
        int bumpHeight = currentImage.getHeight(null);

        return x < Sk.getX() + skierWidth && x + bumpWidth > Sk.getX() &&
               y < Sk.getY() + skierHeight && y + bumpHeight > Sk.getY();
    }

    public Image getImage() {
        return currentImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Ab {
    private Image abomImage = new ImageIcon("resources/sprites/abominator1.png").getImage();
    private int x;
    private int y;

    public Ab(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public void resetPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean collidesWith(Sk Sk) {
        int skierWidth = Sk.getImage().getWidth(null);
        int skierHeight = Sk.getImage().getHeight(null);
        int abomWidth = abomImage.getWidth(null);
        int abomHeight = abomImage.getHeight(null);

        return x < Sk.getX() + skierWidth && x + abomWidth > Sk.getX() &&
               y < Sk.getY() + skierHeight && y + abomHeight > Sk.getY();
    }

    public Image getImage() {
        return abomImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}