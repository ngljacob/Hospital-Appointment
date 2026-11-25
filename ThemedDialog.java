import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Window;

@SuppressWarnings("serial")
public abstract class ThemedDialog extends JDialog {

    protected final JPanel body = new JPanel();

    protected ThemedDialog(Window owner, String eyebrow, String title,
                           TcIcons.Kind icon, int width, int height) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(18, 18)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(UiTheme.OUTER);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setPaint(new GradientPaint(0, 0, new Color(102, 132, 92, 70),
                        getWidth(), getHeight(), new Color(255, 242, 198, 65)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel card = new UiTheme.RoundedPanel(UiTheme.CREAM, 28);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setLayout(new BorderLayout());
        card.add(header(eyebrow, title, icon), BorderLayout.NORTH);

        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(20, 26, 22, 26));
        card.add(body, BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        setSize(width, height);
        fitToScreen();
        setLocationRelativeTo(owner);
    }

    protected void fitToScreen() {
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int fitWidth = Math.max(260, Math.min(getWidth(), screen.width - 48));
        int fitHeight = Math.max(240, Math.min(getHeight(), screen.height - 48));
        setSize(fitWidth, fitHeight);
    }

    private JPanel header(String eyebrow, String title, TcIcons.Kind icon) {
        JPanel header = new UiTheme.GradientPanel(UiTheme.OLIVE, UiTheme.DEEP_OLIVE, 28);
        header.setPreferredSize(new Dimension(0, 78));
        header.setLayout(new BorderLayout());

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.X_AXIS));
        if (icon != null) {
            copy.add(new TcIconLabel(icon, 18, Color.WHITE));
            copy.add(Box.createRigidArea(new Dimension(8, 0)));
        }

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(UiTheme.label(eyebrow, 10, Font.BOLD, UiTheme.SCRIPT));
        text.add(Box.createRigidArea(new Dimension(0, 5)));
        text.add(UiTheme.label(title, 20, Font.BOLD, Color.WHITE));
        copy.add(text);

        JButton close = new JButton(icon(TcIcons.Kind.CLOSE, 22)) {
            {
                setPreferredSize(new Dimension(32, 32));
                setMinimumSize(new Dimension(32, 32));
                setMaximumSize(new Dimension(32, 32));
            }
        };
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setOpaque(false);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dispose());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(copy, BorderLayout.CENTER);
        top.add(close, BorderLayout.EAST);
        header.add(top, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(12, 26, 12, 22));
        return header;
    }

    private static Icon icon(TcIcons.Kind kind, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component component, Graphics graphics, int x, int y) {
                Graphics2D g = (Graphics2D) graphics.create();
                TcIcons.paint(g, kind, x, y, size, Color.WHITE, false);
                g.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}
