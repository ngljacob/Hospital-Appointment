import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

@SuppressWarnings("serial")
public class PasswordChangeDialog extends ThemedDialog {

    private final AuthService auth;
    private final String username;
    private final JPasswordField current = UiTheme.passwordField("Current password");
    private final JPasswordField next = UiTheme.passwordField("New password");
    private final JLabel status = UiTheme.caption("");

    public PasswordChangeDialog(Window owner, String username, AuthService auth) {
        super(owner, "ACCOUNT SECURITY", "Change password", TcIcons.Kind.LOCK, 480, 470);
        this.username = username;
        this.auth = auth;
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel hint = UiTheme.caption("Use at least 6 characters. Your new password replaces the old one.");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hint);
        body.add(Box.createRigidArea(new Dimension(0, 22)));

        body.add(fieldRow("Current password", current));
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(fieldRow("New password", next));
        body.add(Box.createRigidArea(new Dimension(0, 12)));

        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        status.setPreferredSize(new Dimension(390, 36));
        status.setMaximumSize(new Dimension(390, 36));
        body.add(status);
        body.add(Box.createVerticalGlue());

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancel = UiTheme.button("Cancel", UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        cancel.setMaximumSize(cancel.getPreferredSize());
        cancel.addActionListener(e -> dispose());
        JButton update = UiTheme.button("Update password", UiTheme.DEEP_OLIVE, Color.WHITE);
        update.setMaximumSize(update.getPreferredSize());
        update.addActionListener(e -> updatePassword());
        actions.add(cancel);
        actions.add(Box.createRigidArea(new Dimension(10, 0)));
        actions.add(update);
        actions.add(Box.createHorizontalGlue());
        body.add(actions);

        DocumentListener clearStatus = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setStatus("", UiTheme.MUTED_TEXT);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setStatus("", UiTheme.MUTED_TEXT);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setStatus("", UiTheme.MUTED_TEXT);
            }
        };
        current.getDocument().addDocumentListener(clearStatus);
        next.getDocument().addDocumentListener(clearStatus);
    }

    private JPanel fieldRow(String title, JPasswordField field) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = UiTheme.label(title, 12, Font.BOLD, UiTheme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(380, 42));
        field.setMaximumSize(new Dimension(380, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(Box.createRigidArea(new Dimension(0, 6)));
        row.add(field);
        return row;
    }

    private void updatePassword() {
        char[] oldPassword = current.getPassword();
        char[] newPassword = next.getPassword();
        try {
            if (newPassword.length < 6) {
                setStatus("New password must be at least 6 characters.", UiTheme.RED);
                return;
            }
            auth.changePassword(username, oldPassword, newPassword);
            current.setText("");
            next.setText("");
            setStatus("Password updated successfully.", UiTheme.STATUS_GREEN);
            new Timer(900, e -> dispose()).start();
        } catch (AuthException e) {
            setStatus(e.getMessage(), UiTheme.RED);
        } finally {
            java.util.Arrays.fill(oldPassword, '\0');
            java.util.Arrays.fill(newPassword, '\0');
        }
    }

    private void setStatus(String text, Color color) {
        status.setText(text);
        status.setForeground(color);
    }
}
