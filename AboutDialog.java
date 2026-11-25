import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;

public class AboutDialog extends JDialog {

    private static final String STORY = "Morpho Medical is built around restorative transformation—the science of structure, renewal, and recovery. "
            + "We pair clear diagnostics with approachable primary care so every visit helps you reset, recover, and thrive.";

    public AboutDialog(Window owner) {
        super(owner, "About Morpho Medical", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(UiTheme.OUTER);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setPaint(new GradientPaint(0, 0, new Color(102, 132, 92, 80),
                        getWidth(), getHeight(), new Color(102, 132, 92, 0)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JPanel card = new UiTheme.RoundedPanel(UiTheme.CREAM, 28);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setLayout(new BorderLayout());
        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildBody(), BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);

        setSize(720, 570);
        setMinimumSize(new Dimension(620, 500));
        setLocationRelativeTo(owner);
    }

    private JPanel buildHeader() {
        JPanel header = new UiTheme.GradientPanel(UiTheme.OLIVE, UiTheme.DEEP_OLIVE, 28);
        header.setPreferredSize(new Dimension(0, 140));
        header.setLayout(new BorderLayout());

        JPanel copy = new JPanel();
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.setOpaque(false);
        copy.setBorder(BorderFactory.createEmptyBorder(20, 30, 16, 30));

        java.awt.Component brand = UiTheme.brand(false, UiTheme.SCRIPT, UiTheme.WHITE, true);
        JLabel title = UiTheme.heading("A New Form of Care.", 24);
        title.setForeground(Color.WHITE);
        copy.add(brand);
        copy.add(Box.createVerticalStrut(6));
        copy.add(title);
        header.add(copy, BorderLayout.CENTER);

        JButton close = new JButton("X") {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(255, 255, 255, 55));
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        close.setFont(UiTheme.font(13, Font.BOLD));
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setOpaque(false);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setBorder(BorderFactory.createEmptyBorder());
        close.setPreferredSize(new Dimension(36, 36));
        close.addActionListener(e -> dispose());

        JPanel closeHost = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 22));
        closeHost.setOpaque(false);
        closeHost.add(close);
        header.add(closeHost, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel eyebrow = UiTheme.label("THE MORPHO PROMISE", 11, Font.BOLD, UiTheme.DEEP_OLIVE);
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 10, 0);
        content.add(eyebrow, constraints);

        JPanel lead = new UiTheme.RoundedPanel(UiTheme.BUTTER_DEEP, 18);
        lead.setLayout(new BorderLayout());
        lead.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        lead.setMaximumSize(new Dimension(Integer.MAX_VALUE, 98));
        JLabel leadText = UiTheme.label("<html><div style='width:580px;text-align:justify;line-height:1.35'>"
                + STORY + "</div></html>", 13, Font.PLAIN, UiTheme.CHARCOAL);
        lead.add(leadText, BorderLayout.CENTER);
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 0, 12, 0);
        content.add(lead, constraints);

        JPanel pillars = new JPanel(new GridLayout(1, 3, 8, 0));
        pillars.setOpaque(false);
        pillars.setMaximumSize(new Dimension(Integer.MAX_VALUE, 138));
        pillars.add(pillar("01", "Structure", "Clear diagnostics and a care plan made around you.",
                TcIcons.Kind.HEART, UiTheme.DEEP_OLIVE));
        pillars.add(pillar("02", "Renewal", "Preventive guidance that helps your body reset.",
                TcIcons.Kind.CLOCK, UiTheme.FOREST));
        pillars.add(pillar("03", "Recovery", "Steady support from checkup to full recovery.",
                TcIcons.Kind.CHECK, UiTheme.OLIVE));
        constraints.gridy = 2;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, 0, 0, 0);
        content.add(pillars, constraints);

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tags.setOpaque(false);
        tags.add(tag("Trusted specialists"));
        tags.add(tag("Simple booking"));

        JButton explore = UiTheme.button("Start exploring", UiTheme.DEEP_OLIVE, Color.WHITE);
        explore.setMaximumSize(explore.getPreferredSize());
        explore.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setOpaque(false);
        footer.add(tags, BorderLayout.CENTER);
        footer.add(explore, BorderLayout.EAST);
        constraints.gridy = 3;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.insets = new Insets(10, 0, 0, 0);
        content.add(footer, constraints);

        body.add(content, BorderLayout.CENTER);
        return body;
    }

    private JPanel pillar(String number, String title, String detail, TcIcons.Kind icon, Color accent) {
        JPanel card = new UiTheme.RoundedPanel(UiTheme.WHITE, 18);
        card.setBorder(BorderFactory.createEmptyBorder(11, 10, 11, 10));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel index = UiTheme.label(number, 10, Font.BOLD, UiTheme.MUTED_TEXT);
        index.setAlignmentX(LEFT_ALIGNMENT);
        card.add(index);
        card.add(Box.createVerticalStrut(8));

        JPanel badge = new UiTheme.RoundedPanel(UiTheme.SAGE, 12);
        badge.setLayout(new BorderLayout());
        badge.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        badge.setMaximumSize(new Dimension(36, 36));
        badge.setAlignmentX(LEFT_ALIGNMENT);
        badge.add(new TcIconLabel(icon, 20, accent), BorderLayout.CENTER);
        card.add(badge);
        card.add(Box.createVerticalStrut(8));

        JLabel heading = UiTheme.label(title, 13, Font.BOLD, UiTheme.DEEP_OLIVE);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        JLabel caption = UiTheme.label("<html><div style='width:100px;line-height:1.3'>"
                + detail + "</div></html>", 11, Font.PLAIN, UiTheme.MUTED_TEXT);
        caption.setAlignmentX(LEFT_ALIGNMENT);
        card.add(caption);
        return card;
    }

    private JPanel tag(String text) {
        JPanel chip = new UiTheme.RoundedPanel(UiTheme.SAGE, 14);
        chip.setLayout(new BorderLayout());
        chip.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        chip.add(UiTheme.label(text, 11, Font.BOLD, UiTheme.DEEP_OLIVE), BorderLayout.CENTER);
        return chip;
    }
}
