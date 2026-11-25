import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;

@SuppressWarnings("serial")
public class SupportDialog extends ThemedDialog {

    private static final String NUMBER = "1800-MORPHO";

    public SupportDialog(Window owner) {
        super(owner, "WE'RE HERE FOR YOU", "Support", TcIcons.Kind.HELP, 460, 386);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel title = UiTheme.heading("Need a hand?", 24);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = UiTheme.caption("Our care team is one call away.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(Box.createVerticalStrut(4));
        body.add(title);
        body.add(Box.createRigidArea(new Dimension(0, 5)));
        body.add(subtitle);
        body.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel phoneCard = new UiTheme.RoundedPanel(UiTheme.SAGE, 18);
        phoneCard.setLayout(new BoxLayout(phoneCard, BoxLayout.Y_AXIS));
        phoneCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        phoneCard.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));
        phoneCard.setPreferredSize(new Dimension(340, 88));
        phoneCard.setMaximumSize(new Dimension(340, 88));
        JLabel caption = UiTheme.label("24/7 SUPPORT LINE", 10, Font.BOLD, UiTheme.DEEP_OLIVE);
        caption.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel number = UiTheme.label(NUMBER, 24, Font.BOLD, UiTheme.CHARCOAL);
        number.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel note = UiTheme.caption("Average response time: under 2 minutes");
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        phoneCard.add(Box.createVerticalStrut(6));
        phoneCard.add(caption);
        phoneCard.add(Box.createRigidArea(new Dimension(0, 4)));
        phoneCard.add(number);
        phoneCard.add(Box.createRigidArea(new Dimension(0, 4)));
        phoneCard.add(note);
        phoneCard.add(Box.createVerticalStrut(6));
        body.add(phoneCard);
        body.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton copy = UiTheme.button("Copy number", UiTheme.DEEP_OLIVE, Color.WHITE);
        JButton close = UiTheme.button("Close", UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        copy.setMaximumSize(copy.getPreferredSize());
        close.setMaximumSize(close.getPreferredSize());
        copy.addActionListener(e -> copyNumber(copy));
        close.addActionListener(e -> dispose());
        actions.add(copy);
        actions.add(Box.createRigidArea(new Dimension(10, 0)));
        actions.add(close);
        body.add(actions);
        body.add(Box.createVerticalStrut(24));
    }

    private void copyNumber(JButton button) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(NUMBER), null);
            button.setText("Copied!");
            new Timer(1400, e -> button.setText("Copy number")).start();
        } catch (Exception ignored) {
            button.setText("Copy unavailable");
        }
    }
}
