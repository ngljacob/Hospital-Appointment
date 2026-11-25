import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

@SuppressWarnings("serial")
public class LoadingDialog extends ThemedDialog {

    private final Timer timer;

    public LoadingDialog(String message) {
        super(null, "MORPHO MEDICAL", "Please wait", TcIcons.Kind.HEART, 420, 300);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JComponent brand = UiTheme.brand(false, UiTheme.DEEP_OLIVE, UiTheme.OLIVE, false);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel caption = UiTheme.label(message, 13, Font.PLAIN, UiTheme.MUTED_TEXT);
        caption.setAlignmentX(Component.CENTER_ALIGNMENT);

        ProgressTrack track = new ProgressTrack();
        track.setAlignmentX(Component.CENTER_ALIGNMENT);
        track.setPreferredSize(new Dimension(300, 10));
        track.setMaximumSize(new Dimension(300, 10));

        body.add(Box.createVerticalGlue());
        body.add(brand);
        body.add(Box.createRigidArea(new Dimension(0, 18)));
        body.add(caption);
        body.add(Box.createRigidArea(new Dimension(0, 16)));
        body.add(track);
        body.add(Box.createVerticalGlue());

        timer = new Timer(30, e -> track.repaint());
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void dispose() {
        timer.stop();
        super.dispose();
    }

    private static class ProgressTrack extends JPanel {

        private int phase;

        ProgressTrack() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int radius = h / 2;
            g.clip(new RoundRectangle2D.Double(0, 0, w, h, radius, radius));
            g.setColor(new Color(210, 224, 198));
            g.fillRoundRect(0, 0, w, h, radius, radius);

            int span = Math.max(70, w / 3);
            int x = (phase % (w + span)) - span;
            g.setPaint(new GradientPaint(x, 0, UiTheme.SAGE, x + span, 0, UiTheme.DEEP_OLIVE));
            g.fillRoundRect(x, 0, span, h, radius, radius);
            g.dispose();
            phase += 6;
        }
    }
}
