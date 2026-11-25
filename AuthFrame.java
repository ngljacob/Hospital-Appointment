import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class AuthFrame extends JFrame {

    private final AuthService service = new AuthService();
    private final JTextField usernameField = UiTheme.placeholderField("Username");
    private final JPasswordField passwordField = UiTheme.passwordField("Password");
    private final JPasswordField confirmField = UiTheme.passwordField("Confirm password");
    private final JLabel messageLabel = UiTheme.caption("");
    private final JPanel confirmHost = new JPanel(new BorderLayout(0, 6));

    private JLabel heading;
    private JLabel subtitle;
    private JButton signInTab;
    private JButton signUpTab;
    private JButton submitButton;
    private JButton modeLink;
    private boolean signUpMode;

    public AuthFrame() {
        super("Morpho Medical | Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(buildShell());
        setSize(520, 660);
        setResizable(false);
        setLocationRelativeTo(null);
        setMode(false);
    }

    private JPanel buildShell() {
        JPanel shell = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(UiTheme.OUTER);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setPaint(new GradientPaint(0, 0, new Color(102, 132, 92, 90),
                        getWidth(), getHeight(), new Color(255, 242, 198, 90)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        shell.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel frame = new UiTheme.RoundedPanel(UiTheme.OLIVE, 30);
        frame.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.setLayout(new BorderLayout());
        frame.add(buildCard(), BorderLayout.CENTER);
        shell.add(frame, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildCard() {
        JPanel card = new UiTheme.RoundedPanel(UiTheme.CREAM, 25);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(28, 34, 30, 34));

        javax.swing.JComponent brand = UiTheme.brand(false, UiTheme.DEEP_OLIVE, UiTheme.OLIVE, false);
        brand.setAlignmentX(LEFT_ALIGNMENT);

        heading = UiTheme.heading("Welcome back", 25);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        subtitle = UiTheme.caption("Sign in to continue your care journey.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        content.add(brand);
        content.add(Box.createVerticalStrut(16));
        content.add(heading);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        JPanel tabs = new JPanel(new GridLayout(1, 2, 8, 0));
        tabs.setOpaque(false);
        tabs.setAlignmentX(LEFT_ALIGNMENT);
        tabs.setMaximumSize(new Dimension(410, 40));
        signInTab = new TabButton("Sign in", false);
        signUpTab = new TabButton("Sign up", false);
        signInTab.addActionListener(e -> setMode(false));
        signUpTab.addActionListener(e -> setMode(true));
        tabs.add(signInTab);
        tabs.add(signUpTab);
        content.add(tabs);
        content.add(Box.createVerticalStrut(20));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setAlignmentX(LEFT_ALIGNMENT);

        form.add(UiTheme.caption("Username"));
        form.add(Box.createVerticalStrut(6));
        usernameField.setMaximumSize(new Dimension(410, 40));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(usernameField);
        form.add(Box.createVerticalStrut(14));

        form.add(UiTheme.caption("Password"));
        form.add(Box.createVerticalStrut(6));
        passwordField.setMaximumSize(new Dimension(410, 40));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(passwordField);

        confirmHost.setOpaque(false);
        confirmHost.setAlignmentX(LEFT_ALIGNMENT);
        confirmHost.setMaximumSize(new Dimension(410, 74));
        JPanel confirmInner = new JPanel();
        confirmInner.setLayout(new BoxLayout(confirmInner, BoxLayout.Y_AXIS));
        confirmInner.setOpaque(false);
        confirmInner.add(UiTheme.caption("Confirm password"));
        confirmInner.add(Box.createVerticalStrut(6));
        confirmField.setMaximumSize(new Dimension(410, 40));
        confirmField.setAlignmentX(LEFT_ALIGNMENT);
        confirmInner.add(confirmField);
        confirmHost.add(confirmInner, BorderLayout.CENTER);
        form.add(confirmHost);
        content.add(form);
        content.add(Box.createVerticalStrut(16));

        JPanel messageCard = new UiTheme.RoundedPanel(UiTheme.SAGE, 12);
        messageCard.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        messageCard.setMaximumSize(new Dimension(410, 46));
        messageCard.setAlignmentX(LEFT_ALIGNMENT);
        messageCard.add(messageLabel, BorderLayout.CENTER);
        content.add(messageCard);
        content.add(Box.createVerticalStrut(18));

        submitButton = UiTheme.button("Sign in", UiTheme.DEEP_OLIVE, Color.WHITE);
        submitButton.setAlignmentX(LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(410, 42));
        submitButton.addActionListener(e -> submit());
        content.add(submitButton);
        content.add(Box.createVerticalStrut(14));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.setOpaque(false);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        modeLink = new JButton("New here? Create an account");
        modeLink.setFont(UiTheme.font(12, Font.BOLD));
        modeLink.setForeground(UiTheme.DEEP_OLIVE);
        modeLink.setFocusPainted(false);
        modeLink.setBorderPainted(false);
        modeLink.setContentAreaFilled(false);
        modeLink.setOpaque(false);
        modeLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        modeLink.addActionListener(e -> setMode(!signUpMode));
        footer.add(modeLink);
        content.add(footer);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void setMode(boolean signUp) {
        signUpMode = signUp;
        heading.setText(signUp ? "Create your account" : "Welcome back");
        subtitle.setText(signUp ? "Your first visit starts here." : "Sign in to continue your care journey.");
        submitButton.setText(signUp ? "Create account" : "Sign in");
        modeLink.setText(signUp ? "Already registered? Sign in" : "New here? Create an account");
        confirmHost.setVisible(signUp);
        ((TabButton) signInTab).setActive(!signUp);
        ((TabButton) signUpTab).setActive(signUp);
        messageLabel.setText("");
        revalidate();
        repaint();
    }

    private void submit() {
        char[] password = passwordField.getPassword();
        char[] confirm = confirmField.getPassword();
        boolean signUp = signUpMode;
        String usernameInput = usernameField.getText().trim();
        long start = System.currentTimeMillis();
        LoadingDialog loading = new LoadingDialog(
                signUp ? "Creating your account..." : "Signing you in...");
        setBusy(true);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                if (signUp) {
                    if (!Arrays.equals(password, confirm)) {
                        throw new AuthException("Passwords do not match.");
                    }
                    return service.signUp(usernameInput, password);
                }
                return service.signIn(usernameInput, password);
            }

            @Override
            protected void done() {
                try {
                    String username = get();
                    long remaining = 850 - (System.currentTimeMillis() - start);
                    if (remaining > 0) {
                        Timer timer = new Timer((int) remaining, e -> {
                            ((Timer) e.getSource()).stop();
                            openMain(username, loading);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        openMain(username, loading);
                    }
                } catch (Exception ex) {
                    loading.dispose();
                    setBusy(false);
                    Throwable cause = ex instanceof ExecutionException ? ex.getCause() : ex;
                    messageLabel.setText(cause == null ? "Unable to continue." : cause.getMessage());
                    messageLabel.setForeground(UiTheme.RED);
                } finally {
                    Arrays.fill(password, '\0');
                    Arrays.fill(confirm, '\0');
                }
            }
        };

        worker.execute();
        loading.setVisible(true);
    }

    private void setBusy(boolean busy) {
        usernameField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        confirmField.setEnabled(!busy);
        signInTab.setEnabled(!busy);
        signUpTab.setEnabled(!busy);
        modeLink.setEnabled(!busy);
        submitButton.setEnabled(!busy);
        setCursor(Cursor.getPredefinedCursor(busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void openMain(String username, LoadingDialog loading) {
        MainFrame main = new MainFrame(username);
        dispose();
        main.setVisible(true);
        loading.dispose();
    }

    private static class TabButton extends JButton {

        private boolean active;

        TabButton(String text, boolean active) {
            super(text);
            this.active = active;
            setFont(UiTheme.font(13, Font.BOLD));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? Color.WHITE : UiTheme.DEEP_OLIVE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(active ? UiTheme.DEEP_OLIVE : UiTheme.SAGE);
            g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0,
                    getWidth() - 1, getHeight() - 1, 20, 20));
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
