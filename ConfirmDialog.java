import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

@SuppressWarnings("serial")
public class ConfirmDialog extends ThemedDialog {

    public ConfirmDialog(Window owner, String eyebrow, String title, TcIcons.Kind icon,
                         String message, String cancelText, String confirmText,
                         Runnable onConfirm) {
        super(owner, eyebrow, title, icon, 440, 300);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        int textWidth = Math.max(220, getWidth() - 88);
        JLabel label = UiTheme.wrappedLabel(
                message, 14, Font.PLAIN, UiTheme.CHARCOAL, textWidth);
        body.add(Box.createVerticalGlue());
        body.add(label);
        body.add(Box.createRigidArea(new Dimension(0, 26)));

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancel = UiTheme.button(cancelText, UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        cancel.setMaximumSize(cancel.getPreferredSize());
        cancel.addActionListener(e -> dispose());

        JButton confirm = UiTheme.button(confirmText, UiTheme.DEEP_OLIVE, Color.WHITE);
        confirm.setMaximumSize(confirm.getPreferredSize());
        confirm.addActionListener(e -> {
            dispose();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        actions.add(cancel);
        actions.add(Box.createRigidArea(new Dimension(12, 0)));
        actions.add(confirm);
        body.add(actions);
        body.add(Box.createVerticalGlue());
        pack();
        fitToScreen();
        setLocationRelativeTo(owner);
    }
}
