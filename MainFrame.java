import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final Color LOGO_DARK = new Color(23, 92, 86);
    private static final Color LOGO_MID = new Color(50, 118, 94);
    private static final Color LOGO_LIGHT = new Color(64, 131, 98);

    private static final long serialVersionUID = 1L;

    private final RecordManager manager = new RecordManager();
    private final RecordTablePanel tablePanel = new RecordTablePanel();
    private final JPanel dashboardSlot = new JPanel(new BorderLayout());
    private final JPanel views = new JPanel(new CardLayout());
    private final List<Runnable> changeListeners = new ArrayList<>();
    private final String username;

    private RecordFormPanel formPanel;
    private DashboardPanel dashboardPanel;
    private DoctorProfilePanel profilePanel;
    private HistoryPanel historyPanel;
    private NotificationsPanel notificationsPanel;
    private ProfilePanel accountPanel;
    private NavButton homeButton;
    private NavButton appointmentButton;
    private NavButton historyButton;
    private NavButton notificationButton;
    private NavButton profileButton;

    public MainFrame(String username) {
        super("Morpho Medical | Doctor Booking");
        this.username = username == null || username.trim().isEmpty()
                ? "Sheikh" : username.trim();
        setTitle("Morpho Medical | " + this.username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(buildShell());
        setSize(1460, 900);
        setMinimumSize(new Dimension(1100, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        fireDataChanged();
    }

    private JPanel buildShell() {
        JPanel shell = new JPanel(new BorderLayout(18, 18)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(UiTheme.OUTER);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setPaint(new RadialGradientPaint(
                        new Point2D.Float(getWidth() * 0.16f, getHeight() * 0.78f),
                        getWidth() * 0.42f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(102, 132, 92, 90), new Color(102, 132, 92, 0)}));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setPaint(new RadialGradientPaint(
                        new Point2D.Float(getWidth() * 0.86f, getHeight() * 0.18f),
                        getWidth() * 0.34f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 242, 198, 110), new Color(255, 242, 198, 0)}));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        shell.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel frame = new UiTheme.RoundedPanel(UiTheme.OLIVE, 30);
        frame.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.setLayout(new BorderLayout(10, 10));
        frame.add(buildBody(), BorderLayout.CENTER);
        shell.add(frame, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(8, 0));
        body.setOpaque(false);
        body.add(buildNav(), BorderLayout.WEST);

        JPanel center = new UiTheme.RoundedPanel(UiTheme.CREAM, 26);
        center.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        center.setLayout(new BorderLayout());

        dashboardPanel = new DashboardPanel(username, profilePanelRef());
        dashboardSlot.add(dashboardPanel, BorderLayout.CENTER);
        views.add(dashboardSlot, "home");

        JPanel records = new JPanel(new BorderLayout(14, 0));
        records.setOpaque(false);
        records.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        formPanel = new RecordFormPanel(manager, tablePanel, this::fireDataChanged, username);
        records.add(formPanel, BorderLayout.WEST);
        records.add(tablePanel, BorderLayout.CENTER);
        views.add(records, "records");

        historyPanel = new HistoryPanel(manager, username);
        views.add(historyPanel, "history");

        notificationsPanel = new NotificationsPanel(manager, username, this::updateNotificationBadge);
        views.add(notificationsPanel, "notifications");

        accountPanel = new ProfilePanel(username);
        views.add(accountPanel, "profile");

        center.add(views, BorderLayout.CENTER);
        body.add(center, BorderLayout.CENTER);

        profilePanel = new DoctorProfilePanel(username, manager, this::fireDataChanged);
        body.add(profilePanel, BorderLayout.EAST);
        addChangeListener(dashboardPanel::refresh);
        addChangeListener(profilePanel::refresh);
        addChangeListener(historyPanel::refresh);
        addChangeListener(notificationsPanel::refresh);
        return body;
    }

    private java.util.function.Consumer<Doctor> profilePanelRef() {
        return doctor -> {
            if (profilePanel != null) {
                profilePanel.setDoctor(doctor);
            }
        };
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(72, 0));
        nav.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        JComponent brand = UiTheme.brand(true, UiTheme.SCRIPT, Color.WHITE, true);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(brand);
        nav.add(Box.createRigidArea(new Dimension(48, 46)));

        homeButton = new NavButton(loadLogo(), true);
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeButton.setToolTipText("Home");
        appointmentButton = new NavButton(new SvgIcon("graphics/stethoscope.svg", 28), false);
        appointmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        appointmentButton.setToolTipText("Appointments");
        historyButton = new NavButton(TcIcons.Kind.HISTORY, false);
        historyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        historyButton.setToolTipText("Medical history");
        notificationButton = new NavButton(TcIcons.Kind.BELL, false);
        notificationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        notificationButton.setToolTipText("Notifications");

        homeButton.addActionListener(e -> showView("home"));
        appointmentButton.addActionListener(e -> showView("records"));
        historyButton.addActionListener(e -> showView("history"));
        notificationButton.addActionListener(e -> showView("notifications"));

        nav.add(homeButton);
        nav.add(Box.createRigidArea(new Dimension(48, 10)));
        nav.add(appointmentButton);
        nav.add(Box.createRigidArea(new Dimension(48, 10)));
        nav.add(historyButton);
        nav.add(Box.createRigidArea(new Dimension(48, 10)));
        nav.add(notificationButton);
        Component spacer = Box.createVerticalGlue();
        spacer.setMaximumSize(new Dimension(48, Integer.MAX_VALUE));
        nav.add(spacer);

        profileButton = new NavButton(TcIcons.Kind.USER, false);
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileButton.setToolTipText("My profile");
        profileButton.addActionListener(e -> showView("profile"));
        nav.add(profileButton);
        nav.add(Box.createRigidArea(new Dimension(48, 8)));

        NavButton help = new NavButton(TcIcons.Kind.HELP, false);
        help.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.setToolTipText("Support");
        help.addActionListener(e -> new SupportDialog(this).setVisible(true));
        nav.add(help);
        nav.add(Box.createRigidArea(new Dimension(48, 8)));

        NavButton logout = new NavButton(TcIcons.Kind.LOGOUT, false);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setToolTipText("Log out");
        logout.addActionListener(e -> logOut());
        nav.add(logout);
        return nav;
    }

    private void showView(String name) {
        CardLayout layout = (CardLayout) views.getLayout();
        layout.show(views, name);
        homeButton.setActive("home".equals(name));
        appointmentButton.setActive("records".equals(name));
        historyButton.setActive("history".equals(name));
        notificationButton.setActive("notifications".equals(name));
        profileButton.setActive("profile".equals(name));
    }

    private void logOut() {
        new ConfirmDialog(this, "SESSION", "Log out", TcIcons.Kind.LOGOUT,
                "Log out of Morpho Medical?", "Stay here", "Log out",
                this::finishLogout).setVisible(true);
    }

    private void finishLogout() {
        dispose();
        new AuthFrame().setVisible(true);
    }

    private void fireDataChanged() {
        tablePanel.refresh(manager.getAllRecords());
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    private void updateNotificationBadge(int count) {
        if (notificationButton != null) {
            notificationButton.setBadge(count);
        }
    }

    public void setDashboardPanel(JComponent dashboard) {
        dashboardSlot.removeAll();
        dashboardSlot.add(dashboard, BorderLayout.CENTER);
        dashboardSlot.revalidate();
        dashboardSlot.repaint();
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    public RecordManager getManager() {
        return manager;
    }

    private static BufferedImage loadLogo() {
        try {
            return ImageIO.read(new File("graphics/Logo.png"));
        } catch (IOException e) {
            return null;
        }
    }

    private static class NavButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final TcIcons.Kind kind;
        private final SvgIcon svgIcon;
        private final BufferedImage image;
        private final BufferedImage inactiveImage;
        private boolean active;
        private int badge;

        NavButton(TcIcons.Kind kind, boolean active) {
            this(kind, null, null, active);
        }

        NavButton(SvgIcon svgIcon, boolean active) {
            this(null, svgIcon, null, active);
        }

        NavButton(BufferedImage image, boolean active) {
            this(null, null, image, active);
        }

        private NavButton(TcIcons.Kind kind, SvgIcon svgIcon, BufferedImage image, boolean active) {
            this.kind = kind;
            this.svgIcon = svgIcon;
            this.image = image;
            this.inactiveImage = whiteTint(image);
            this.active = active;
            setPreferredSize(new Dimension(64, 64));
            setMinimumSize(new Dimension(64, 64));
            setMaximumSize(new Dimension(64, 64));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder());
        }

        private static BufferedImage whiteTint(BufferedImage source) {
            if (source == null) {
                return null;
            }
            BufferedImage tinted = new BufferedImage(
                    source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tinted.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.setComposite(AlphaComposite.SrcAtop);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, tinted.getWidth(), tinted.getHeight());
            g.dispose();
            return tinted;
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        void setBadge(int count) {
            this.badge = Math.max(0, count);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                int diameter = Math.min(getWidth(), getHeight()) - 4;
                int circleX = (getWidth() - diameter) / 2;
                int circleY = (getHeight() - diameter) / 2;
                g.setColor(new Color(255, 255, 255, 30));
                g.fillOval(circleX, circleY, diameter, diameter);
                g.setColor(UiTheme.SAGE_LIGHT);
                g.setStroke(new BasicStroke(2f));
                g.drawOval(circleX + 1, circleY + 1, diameter - 2, diameter - 2);
            }

            int iconSize = image != null
                    ? (active ? 56 : 50)
                    : (active ? 42 : 32);
            int iconX = (getWidth() - iconSize) / 2;
            int iconY = (getHeight() - iconSize) / 2;
            Color color = Color.WHITE;

            if (image != null) {
                g.drawImage(active ? image : inactiveImage, iconX, iconY, iconSize, iconSize, this);
            } else if (svgIcon != null) {
                double scale = iconSize / (double) svgIcon.getIconWidth();
                Graphics2D iconGraphics = (Graphics2D) g.create();
                iconGraphics.translate(iconX, iconY);
                iconGraphics.scale(scale, scale);
                if (active) {
                    svgIcon.setGradient(LOGO_DARK, LOGO_MID, LOGO_LIGHT);
                } else {
                    svgIcon.setColor(color);
                }
                svgIcon.paintIcon(this, iconGraphics, 0, 0);
                iconGraphics.dispose();
            } else if (active) {
                TcIcons.paintGradient(g, kind, iconX, iconY, iconSize,
                        LOGO_DARK, LOGO_MID, LOGO_LIGHT, false);
            } else {
                TcIcons.paint(g, kind, iconX, iconY, iconSize, color, false);
            }

            if (badge > 0) {
                String text = badge > 99 ? "99+" : Integer.toString(badge);
                int diameter = text.length() > 2 ? 26 : 21;
                int badgeX = getWidth() - diameter - 1;
                int badgeY = 1;
                g.setColor(UiTheme.STATUS_GREEN);
                g.fillOval(badgeX, badgeY, diameter, diameter);
                g.setColor(Color.WHITE);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(text,
                        badgeX + (diameter - metrics.stringWidth(text)) / 2f,
                        badgeY + (diameter - metrics.getHeight()) / 2f + metrics.getAscent());
            }

            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
