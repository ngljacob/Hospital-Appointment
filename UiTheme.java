import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public final class UiTheme {

    public static final Color BG = new Color(9, 14, 28);
    public static final Color SURFACE = new Color(17, 25, 45);
    public static final Color SURFACE_2 = new Color(23, 33, 58);
    public static final Color LINE = new Color(45, 59, 92);
    public static final Color TEXT = new Color(235, 242, 255);
    public static final Color MUTED = new Color(148, 163, 184);
    public static final Color CYAN = new Color(34, 211, 238);
    public static final Color VIOLET = new Color(139, 92, 246);
    public static final Color GREEN = new Color(52, 211, 153);
    public static final Color AMBER = new Color(251, 191, 36);
    public static final Color RED = new Color(251, 113, 133);

    private UiTheme() {
    }

    public static Font font(int size) {
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    public static Font font(int size, int style) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font(size, style));
        label.setForeground(color);
        return label;
    }

    public static JLabel caption(String text) {
        return label(text, 12, Font.PLAIN, MUTED);
    }

    public static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(font(13, Font.BOLD));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(132, 38));
        return button;
    }

    public static JTextField field() {
        JTextField field = new JTextField();
        field.setFont(font(13));
        field.setForeground(TEXT);
        field.setCaretColor(CYAN);
        field.setBackground(SURFACE_2);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(font(13));
        combo.setBackground(SURFACE_2);
        combo.setForeground(TEXT);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                                   Object value, int index,
                                                                   boolean selected,
                                                                   boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, selected, hasFocus);
                setBackground(selected ? VIOLET : SURFACE_2);
                setForeground(selected ? TEXT : TEXT);
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return combo;
    }

    public static JPanel card() {
        return new RoundedPanel(SURFACE, 18);
    }

    public static JPanel gradient(Color start, Color end) {
        return new GradientPanel(start, end);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setBackground(BG);
    }

    public static class RoundedPanel extends JPanel {

        private final int radius;

        public RoundedPanel(Color background, int radius) {
            super(new java.awt.BorderLayout());
            this.radius = radius;
            setBackground(background);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getBackground());
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1,
                    radius, radius));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    public static class GradientPanel extends JPanel {

        private final Color start;
        private final Color end;

        public GradientPanel(Color start, Color end) {
            super(new java.awt.BorderLayout());
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 24));
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
