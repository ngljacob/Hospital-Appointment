import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("TextField.selectionBackground", UiTheme.VIOLET);
        UIManager.put("TextField.selectionForeground", UiTheme.TEXT);
        UIManager.put("List.selectionBackground", UiTheme.VIOLET);
        UIManager.put("Table.selectionBackground", new java.awt.Color(38, 64, 105));
        UIManager.put("Table.selectionForeground", UiTheme.TEXT);

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
