import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class TcIconLabel extends JPanel {

    private final TcIcons.Kind kind;
    private final int size;
    private Color color;
    private boolean filled;

    public TcIconLabel(TcIcons.Kind kind, int size, Color color) {
        this.kind = kind;
        this.size = size;
        this.color = color;
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    public Color getColor() {
        return color;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        TcIcons.paint(g, kind, x, y, size, color, filled);
        g.dispose();
    }
}
