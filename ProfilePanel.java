import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;

@SuppressWarnings("serial")
public class ProfilePanel extends JPanel {

    private final String username;
    private final AuthService auth = new AuthService();
    private final ProfileService profiles = new ProfileService();
    private final JTextField fullName = UiTheme.field();
    private final JTextField email = UiTheme.field();
    private final JTextField phone = UiTheme.field();
    private JPanel avatarPanel;
    private JLabel initialsLabel;

    public ProfilePanel(String username) {
        this.username = username == null || username.trim().isEmpty() ? "Patient" : username.trim();
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        ProfileService.Details details = profiles.load(this.username);
        fullName.setText(details.fullName.isEmpty() ? this.username : details.fullName);
        email.setText(details.email);
        phone.setText(details.phone);

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.add(UiTheme.heading("My Profile", 26), BorderLayout.NORTH);
        header.add(UiTheme.caption("Manage your personal details and account security."),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(0, 20));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.X_AXIS));
        avatarPanel = avatar();
        identity.add(avatarPanel);
        identity.add(Box.createRigidArea(new Dimension(18, 0)));

        JPanel identityText = new JPanel();
        identityText.setOpaque(false);
        identityText.setLayout(new BoxLayout(identityText, BoxLayout.Y_AXIS));
        initialsLabel = UiTheme.label(initials(), 22, Font.BOLD, UiTheme.CHARCOAL);
        identityText.add(initialsLabel);
        identityText.add(Box.createRigidArea(new Dimension(0, 4)));
        identityText.add(UiTheme.label("@" + this.username, 13, Font.PLAIN, UiTheme.MUTED_TEXT));
        identityText.add(Box.createRigidArea(new Dimension(0, 8)));
        JLabel status = UiTheme.label("  Active patient account  ", 11, Font.BOLD,
                UiTheme.DEEP_OLIVE);
        status.setOpaque(true);
        status.setBackground(UiTheme.SAGE_LIGHT);
        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        identityText.add(status);
        identity.add(identityText);
        identity.add(Box.createHorizontalGlue());
        card.add(identity, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        fullName.setPreferredSize(new Dimension(340, 42));
        email.setPreferredSize(new Dimension(340, 42));
        phone.setPreferredSize(new Dimension(340, 42));
        fullName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateInitials();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateInitials();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateInitials();
            }
        });

        addRow(form, 0, "Full name", fullName);
        addRow(form, 1, "Email address", email);
        addRow(form, 2, "Phone number", phone);
        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        JButton save = UiTheme.button("Save changes", UiTheme.DEEP_OLIVE, Color.WHITE);
        save.addActionListener(e -> saveDetails());
        JButton password = UiTheme.button("Change password", UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        password.addActionListener(e -> changePassword());
        actions.add(save);
        actions.add(Box.createRigidArea(new Dimension(10, 0)));
        actions.add(password);
        actions.add(Box.createHorizontalGlue());
        card.add(actions, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
    }

    private JPanel avatar() {
        return new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(104, 104));
                setMaximumSize(new Dimension(104, 104));
            }

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(UiTheme.SAGE_LIGHT);
                g.fillOval(0, 0, 104, 104);
                g.setColor(UiTheme.DEEP_OLIVE);
                g.fillOval(7, 7, 90, 90);
                g.setColor(Color.WHITE);
                g.setFont(UiTheme.font(30, Font.BOLD));
                String text = initials();
                java.awt.FontMetrics metrics = g.getFontMetrics();
                g.drawString(text, 52 - metrics.stringWidth(text) / 2f,
                        52 + metrics.getAscent() / 2f - 2);
                g.dispose();
            }
        };
    }

    private void addRow(JPanel form, int row, String title, JTextField field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        labelConstraints.insets = new Insets(0, 0, 14, 18);
        form.add(UiTheme.label(title, 13, Font.BOLD, UiTheme.TEXT), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(0, 0, 14, 0);
        form.add(field, fieldConstraints);
    }

    private void updateInitials() {
        if (initialsLabel != null) {
            initialsLabel.setText(initials());
        }
        if (avatarPanel != null) {
            avatarPanel.repaint();
        }
    }

    private String initials() {
        String source = fullName.getText().trim();
        if (source.isEmpty()) {
            source = username;
        }
        String[] words = source.trim().split("\\s+");
        if (words.length == 0 || words[0].isEmpty()) {
            return "?";
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(words[0].charAt(0)));
        if (words.length > 1) {
            result.append(Character.toUpperCase(words[words.length - 1].charAt(0)));
        }
        return result.toString();
    }

    private void saveDetails() {
        String mail = email.getText().trim();
        if (!mail.isEmpty() && !mail.contains("@")) {
            showMessage("Enter a valid email address, or leave it blank.", "Profile");
            return;
        }
        ProfileService.Details details = new ProfileService.Details();
        details.fullName = fullName.getText().trim();
        details.email = mail;
        details.phone = phone.getText().trim();
        try {
            profiles.save(username, details);
            showMessage("Your profile has been saved.", "Profile");
        } catch (IOException e) {
            showMessage("Could not save your profile. Please try again.", "Profile");
        }
    }

    private void changePassword() {
        new PasswordChangeDialog(SwingUtilities.getWindowAncestor(this), username, auth)
                .setVisible(true);
    }

    private void showMessage(String message, String title) {
        new MessageDialog(SwingUtilities.getWindowAncestor(this), title, message)
                .setVisible(true);
    }
}
