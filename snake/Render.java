import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Render extends JPanel {
    public Render(Dimension windowSize) {
        super.setPreferredSize(windowSize);
        super.setBackground(Color.BLACK);
        super.setLayout(null);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Game.GAME.goPaint(g2d);
    }
}
