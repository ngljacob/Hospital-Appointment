import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final RecordManager manager = new RecordManager();
    private final RecordTablePanel tablePanel = new RecordTablePanel();
    private RecordFormPanel formPanel;
    private DashboardPanel dashboardPanel;
    private final JPanel dashboardSlot = new JPanel(new BorderLayout());
    private final JPanel views = new JPanel(new CardLayout());
    private final List<Runnable> changeListeners = new ArrayList<>();

    private final JLabel statusLabel = UiTheme.caption("");
    private final JLabel clockLabel = UiTheme.label("", 13, Font.BOLD, UiTheme.TEXT);
    private JButton overviewButton;
    private JButton recordsButton;

    public MainFrame() {
        super("MediFlow | Clinic Appointment System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UiTheme.BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setOpaque(false);
        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(buildViews(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);

        Timer clock = new Timer(1000, e -> clockLabel.setText(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        clock.start();
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        addChangeListener(dashboardPanel::refresh);
        setActiveNav(overviewButton);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1060, 650));
        setLocationRelativeTo(null);

        fireDataChanged();
    }

    private JPanel buildHeader() {
        JPanel header = UiTheme.gradient(new Color(12, 20, 42), new Color(22, 45, 72));
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.add(UiTheme.label("MEDIFLOW", 18, Font.BOLD, UiTheme.TEXT), BorderLayout.NORTH);
        brand.add(UiTheme.caption("Hospital appointment & billing intelligence"),
                BorderLayout.SOUTH);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        right.setOpaque(false);

        JPanel badge = new UiTheme.RoundedPanel(new Color(19, 61, 58), 12);
        badge.setLayout(new BorderLayout());
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        badge.add(UiTheme.label("SYSTEM ONLINE", 11, Font.BOLD, UiTheme.GREEN),
                BorderLayout.CENTER);
        right.add(badge);
        right.add(clockLabel);

        header.add(brand, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(12, 18, 34));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 14, 22, 14));

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(UiTheme.label("WORKSPACE", 11, Font.BOLD, UiTheme.MUTED), BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(2, 1, 0, 8));
        nav.setOpaque(false);
        overviewButton = navButton("Overview");
        recordsButton = navButton("Appointments");
        overviewButton.addActionListener(e -> showView("overview"));
        recordsButton.addActionListener(e -> showView("records"));
        nav.add(overviewButton);
        nav.add(recordsButton);
        top.add(nav, BorderLayout.CENTER);
        sidebar.add(top, BorderLayout.NORTH);

        sidebar.add(UiTheme.caption("Private clinic data · stored locally"), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildViews() {
        dashboardPanel = new DashboardPanel(manager);
        dashboardSlot.add(dashboardPanel, BorderLayout.CENTER);
        views.add(dashboardSlot, "overview");

        JPanel recordsView = new JPanel(new BorderLayout(14, 0));
        recordsView.setBackground(UiTheme.BG);
        recordsView.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        formPanel = new RecordFormPanel(manager, tablePanel, this::fireDataChanged);
        recordsView.add(formPanel, BorderLayout.WEST);
        recordsView.add(tablePanel, BorderLayout.CENTER);
        views.add(recordsView, "records");

        return views;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(12, 18, 34));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(UiTheme.caption("MediFlow v1.0"), BorderLayout.EAST);
        return footer;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UiTheme.font(13, Font.BOLD));
        button.setForeground(UiTheme.MUTED);
        button.setBackground(new Color(12, 18, 34));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        button.setPreferredSize(new Dimension(180, 42));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setActiveNav(JButton active) {
        for (JButton button : new JButton[]{overviewButton, recordsButton}) {
            boolean selected = button == active;
            button.setBackground(selected ? new Color(30, 55, 90) : new Color(12, 18, 34));
            button.setForeground(selected ? UiTheme.CYAN : UiTheme.MUTED);
        }
    }

    private void showView(String name) {
        CardLayout layout = (CardLayout) views.getLayout();
        layout.show(views, name);
        setActiveNav("overview".equals(name) ? overviewButton : recordsButton);
    }

    private void fireDataChanged() {
        tablePanel.refresh(manager.getAllRecords());
        statusLabel.setText(String.format(
                "%d appointments   |   %d active   |   ₱%,.2f revenue   |   %d checks",
                manager.getTotalRecords(), manager.getActiveCount(),
                manager.getTotalRevenue(), manager.getValidationChecks()));
        for (Runnable listener : changeListeners) {
            listener.run();
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
}
