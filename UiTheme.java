import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public final class UiTheme {

    public static final Color OUTER = new Color(181, 198, 172);
    public static final Color OLIVE = new Color(102, 132, 92);
    public static final Color DEEP_OLIVE = new Color(77, 116, 68);
    public static final Color FOREST = new Color(39, 75, 58);
    public static final Color CREAM = new Color(247, 250, 246);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color SAGE = new Color(231, 243, 226);
    public static final Color SAGE_LIGHT = new Color(221, 240, 210);
    public static final Color BUTTER = new Color(255, 242, 198);
    public static final Color BUTTER_DEEP = new Color(255, 241, 194);
    public static final Color SCRIPT = new Color(230, 229, 156);
    public static final Color CHARCOAL = new Color(26, 26, 26);
    public static final Color MUTED_TEXT = new Color(124, 135, 120);
    public static final Color LINE = new Color(216, 228, 211);
    public static final Color GOLD = new Color(224, 176, 62);
    public static final Color STATUS_GREEN = new Color(70, 158, 104);

    public static final Color BG = CREAM;
    public static final Color SURFACE = WHITE;
    public static final Color SURFACE_2 = SAGE;
    public static final Color TEXT = CHARCOAL;
    public static final Color MUTED = MUTED_TEXT;
    public static final Color CYAN = OLIVE;
    public static final Color VIOLET = DEEP_OLIVE;
    public static final Color GREEN = STATUS_GREEN;
    public static final Color AMBER = GOLD;
    public static final Color RED = new Color(201, 111, 98);

    private UiTheme() {
    }

    public static Font font(int size) {
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    public static Font font(int size, int style) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static Font serif(int size, int style) {
        return new Font(Font.SERIF, style, size);
    }

    public static Font script(int size) {
        return new Font("Segoe Script", Font.ITALIC, size);
    }

    public static JComponent brand(boolean compact, Color primary, Color secondary, boolean lightLogo) {
        return new BrandMark(compact, primary, secondary, lightLogo);
    }

    public static JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font(size, style));
        label.setForeground(color);
        return label;
    }

    public static JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(serif(size, Font.BOLD));
        label.setForeground(CHARCOAL);
        return label;
    }

    public static JLabel caption(String text) {
        return label(text, 12, Font.PLAIN, MUTED_TEXT);
    }

    public static JLabel wrappedLabel(String text, int size, int style, Color color, int width) {
        Font font = font(size, style);
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        StringBuilder html = new StringBuilder("<html><div style='text-align:center'>");
        StringBuilder line = new StringBuilder();
        String[] words = text.trim().split("\\s+");
        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (line.length() > 0 && metrics.stringWidth(candidate) > width) {
                html.append(escapeHtml(line.toString())).append("<br>");
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            html.append(escapeHtml(line.toString()));
        }
        html.append("</div></html>");
        JLabel label = label(html.toString(), size, style, color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        return label;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static JButton button(String text, Color background, Color foreground) {
        return new PillButton(text, background, foreground);
    }

    public static JTextField field() {
        return placeholderField("");
    }

    public static javax.swing.JPasswordField passwordField(String hint) {
        PlaceholderPasswordField field = new PlaceholderPasswordField(hint);
        field.setFont(font(13));
        field.setForeground(CHARCOAL);
        field.setCaretColor(DEEP_OLIVE);
        field.setBackground(WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    public static JTextField placeholderField(String hint) {
        PlaceholderField field = new PlaceholderField(hint);
        field.setFont(font(13));
        field.setForeground(CHARCOAL);
        field.setCaretColor(DEEP_OLIVE);
        field.setBackground(WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return field;
    }

    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(font(13));
        combo.setBackground(WHITE);
        combo.setForeground(CHARCOAL);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                                   Object value, int index,
                                                                   boolean selected,
                                                                   boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, selected, hasFocus);
                setBackground(selected ? SAGE_LIGHT : WHITE);
                setForeground(CHARCOAL);
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return combo;
    }

    public static JPanel card() {
        return new RoundedPanel(WHITE, 22);
    }

    public static JPanel gradient(Color start, Color end) {
        return new GradientPanel(start, end, 0);
    }

    public static JPanel gradient(Color start, Color end, int radius) {
        return new GradientPanel(start, end, radius);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CREAM);
        scrollPane.setBackground(CREAM);
    }

    private static class BrandMark extends JPanel {

        private final boolean compact;
        private final Color primary;
        private final Color secondary;
        private final Image logo;
        private final Image lightLogo;

        BrandMark(boolean compact, Color primary, Color secondary, boolean lightLogo) {
            this.compact = compact;
            this.primary = primary;
            this.secondary = secondary;

            ImageIcon icon = new ImageIcon("graphics/Logo.png");
            BufferedImage source = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            Graphics2D sourceGraphics = source.createGraphics();
            sourceGraphics.drawImage(icon.getImage(), 0, 0, 96, 96, null);
            sourceGraphics.dispose();
            this.logo = source;
            this.lightLogo = lightLogo ? tintWhite(source) : source;

            setOpaque(false);
            setPreferredSize(compact ? new Dimension(64, 58) : new Dimension(260, 60));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (compact) {
                g.drawImage(lightLogo, (getWidth() - 30) / 2, 0, 30, 30, this);
                g.setColor(primary);
                g.setFont(font(9, Font.BOLD));
                drawSpacedCentered(g, "MORPHO", getWidth() / 2, 40, 1);
                g.setColor(secondary);
                g.setFont(font(7, Font.BOLD));
                drawSpacedCentered(g, "MEDICAL", getWidth() / 2, 51, 2);
            } else {
                g.drawImage(lightLogo, 2, 4, 48, 48, this);
                g.setColor(primary);
                g.setFont(serif(25, Font.BOLD));
                drawSpaced(g, "MORPHO", 62, 26, 1);
                g.setColor(secondary);
                g.setFont(font(11, Font.BOLD));
                drawSpaced(g, "MEDICAL", 64, 44, 3);
                g.setColor(new Color(secondary.getRed(), secondary.getGreen(),
                        secondary.getBlue(), 120));
                g.fillRoundRect(64, 50, 150, 2, 2, 2);
            }
            g.dispose();
        }

        private static Image tintWhite(BufferedImage source) {
            BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tinted.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.setComposite(AlphaComposite.SrcAtop);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, tinted.getWidth(), tinted.getHeight());
            g.dispose();
            return tinted;
        }

        private void drawSpacedCentered(Graphics2D g, String text, int centerX, int y, int spacing) {
            FontMetrics metrics = g.getFontMetrics();
            int width = spacedWidth(metrics, text, spacing);
            drawSpaced(g, text, centerX - width / 2, y, spacing);
        }

        private void drawSpaced(Graphics2D g, String text, int x, int y, int spacing) {
            FontMetrics metrics = g.getFontMetrics();
            int currentX = x;
            for (char character : text.toCharArray()) {
                g.drawString(String.valueOf(character), currentX, y);
                currentX += metrics.charWidth(character) + spacing;
            }
        }

        private int spacedWidth(FontMetrics metrics, String text, int spacing) {
            int width = spacing * (text.length() - 1);
            for (char character : text.toCharArray()) {
                width += metrics.charWidth(character);
            }
            return width;
        }
    }

    public static class PlaceholderField extends JTextField {

        private final String hint;

        public PlaceholderField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getText().isEmpty()) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(MUTED_TEXT);
                g.setFont(font(13));
                java.awt.Insets insets = getInsets();
                g.drawString(hint, insets.left, getHeight() - insets.bottom - 4);
                g.dispose();
            }
        }
    }

    public static class PlaceholderPasswordField extends javax.swing.JPasswordField {

        private final String hint;

        public PlaceholderPasswordField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getPassword().length == 0) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(MUTED_TEXT);
                g.setFont(font(13));
                java.awt.Insets insets = getInsets();
                g.drawString(hint, insets.left, getHeight() - insets.bottom - 4);
                g.dispose();
            }
        }
    }

    public static class PillButton extends JButton {

        private final Color base;
        private final Color hover;
        private boolean over;

        public PillButton(String text, Color background, Color foreground) {
            super(text);
            this.base = background;
            this.hover = brighten(background);
            setFont(font(13, Font.BOLD));
            setForeground(foreground);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.CENTER);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 13, 16));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    over = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    over = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(40, 60, 40, 28));
            g.fill(new RoundRectangle2D.Float(1, 4, getWidth() - 2, getHeight() - 4, 22, 22));
            g.setColor(over ? hover : base);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 5, 22, 22));
            g.dispose();
            super.paintComponent(graphics);
        }

        private Color brighten(Color color) {
            return new Color(Math.min(255, color.getRed() + 18),
                    Math.min(255, color.getGreen() + 18),
                    Math.min(255, color.getBlue() + 18));
        }
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
            g.setColor(new Color(40, 60, 40, 22));
            g.fill(new RoundRectangle2D.Float(1, 4, getWidth() - 2, getHeight() - 3,
                    radius, radius));
            g.setColor(getBackground());
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 5,
                    radius, radius));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    public static class GradientPanel extends JPanel {

        private final Color start;
        private final Color end;
        private final int radius;

        public GradientPanel(Color start, Color end, int radius) {
            super(new java.awt.BorderLayout());
            this.start = start;
            this.end = end;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            if (radius == 0) {
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1,
                        radius, radius));
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
