import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

@SuppressWarnings("serial")
public class MessageDialog extends ThemedDialog {

    public MessageDialog(Window owner, String title, String message) {
        super(owner, "MORPHO MEDICAL", title, TcIcons.Kind.CHECK, 440, 360);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        int textWidth = Math.max(220, getWidth() - 88);
        javax.swing.JLabel messageLabel = UiTheme.wrappedLabel(
                message, 14, Font.PLAIN, UiTheme.CHARCOAL, textWidth);
        body.add(Box.createVerticalGlue());
        body.add(messageLabel);
        body.add(Box.createRigidArea(new Dimension(0, 26)));

        javax.swing.JButton okay = UiTheme.button("Okay", UiTheme.DEEP_OLIVE, Color.WHITE);
        okay.setAlignmentX(Component.CENTER_ALIGNMENT);
        okay.addActionListener(e -> dispose());
        body.add(okay);
        body.add(Box.createVerticalGlue());
        pack();
        fitToScreen();
        setLocationRelativeTo(owner);
    }
}
