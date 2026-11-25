import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public final class DoctorArt {

    private static final Color SKIN = new Color(231, 185, 154);
    private static final Color HAIR = new Color(91, 68, 54);
    private static final Color SCRUBS = new Color(199, 221, 186);
    private static final Color SCRUBS_DARK = new Color(158, 190, 145);
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    private DoctorArt() {
    }

    public static void paintAvatar(Graphics2D graphics, int x, int y, int size, Doctor doctor) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.scale(size / 100.0, size / 100.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new Ellipse2D.Double(0, 0, 100, 100));
        BufferedImage photo = loadPhoto(doctor.getPhotoPath());
        if (photo != null) {
            drawCover(g, photo, 100, 100, doctor.getPhotoOffsetY());
            g.dispose();
            return;
        }
        g.setColor(doctor.getAccent());
        g.fillRect(0, 0, 100, 100);
        g.setColor(new Color(255, 255, 255, 55));
        g.fill(new Ellipse2D.Double(-15, -20, 75, 75));
        g.setColor(SCRUBS);
        g.fill(new Ellipse2D.Double(8, 62, 84, 66));
        g.setColor(SCRUBS_DARK);
        g.fill(new RoundRectangle2D.Double(40, 56, 20, 20, 8, 8));
        g.setColor(SKIN);
        g.fill(new Ellipse2D.Double(34, 22, 32, 42));
        g.setColor(HAIR);
        g.fill(new Arc2D.Double(32, 17, 36, 28, 0, 180, Arc2D.CHORD));
        g.setColor(new Color(60, 50, 45));
        g.fillOval(43, 40, 3, 3);
        g.fillOval(55, 40, 3, 3);
        g.drawLine(48, 52, 53, 52);
        g.dispose();
    }

    public static void paintPortrait(Graphics2D graphics, int width, int height, Doctor doctor) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage photo = loadPhoto(doctor.getPhotoPath());
        if (photo != null) {
            drawCover(g, photo, width, height, doctor.getPhotoOffsetY());
            int overlayTop = (int) (height * 0.74);
            g.setColor(new Color(255, 255, 255, 52));
            g.fillRect(0, overlayTop, width, height - overlayTop);
            g.dispose();
            return;
        }

        g.scale(width / 330.0, height / 300.0);
        g.setColor(new Color(250, 247, 235));
        g.fillRect(0, 0, 330, 300);
        g.setColor(new Color(39, 75, 58));
        g.fill(new Ellipse2D.Double(-55, 25, 170, 150));
        g.setColor(new Color(169, 195, 158));
        g.fill(new Ellipse2D.Double(215, -35, 150, 145));
        g.setColor(new Color(255, 241, 194));
        g.fill(new Ellipse2D.Double(250, 165, 120, 120));
        g.setColor(new Color(221, 240, 210));
        g.fill(new Ellipse2D.Double(-25, 195, 120, 120));

        g.setColor(SCRUBS_DARK);
        g.fill(new Ellipse2D.Double(68, 240, 205, 150));
        g.setColor(SCRUBS);
        g.fill(new RoundRectangle2D.Double(112, 215, 106, 95, 36, 36));
        g.setColor(SKIN);
        g.fill(new RoundRectangle2D.Double(148, 200, 34, 42, 12, 12));
        g.fill(new Ellipse2D.Double(113, 112, 104, 125));

        g.setColor(HAIR);
        g.fill(new Arc2D.Double(108, 98, 114, 92, 0, 180, Arc2D.CHORD));
        g.setColor(WHITE());
        g.fill(new Arc2D.Double(108, 94, 114, 72, 0, 180, Arc2D.CHORD));
        g.setColor(new Color(230, 230, 225));
        g.draw(new Arc2D.Double(108, 94, 114, 72, 0, 180, Arc2D.CHORD));

        g.setColor(new Color(62, 50, 45));
        g.fillOval(141, 165, 7, 5);
        g.fillOval(183, 165, 7, 5);
        g.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND,
                java.awt.BasicStroke.JOIN_ROUND));
        g.drawLine(155, 195, 176, 195);
        g.setColor(new Color(39, 75, 58));
        g.draw(new Arc2D.Double(126, 205, 78, 62, 200, 140, Arc2D.OPEN));
        g.fillOval(122, 247, 11, 11);
        g.fillOval(199, 247, 11, 11);
        g.setColor(new Color(255, 255, 255));
        g.fillOval(125, 250, 5, 5);
        g.fillOval(202, 250, 5, 5);
        g.dispose();
    }

    public static void paintPromoDoctor(Graphics2D graphics, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.scale(width / 170.0, height / 180.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(HAIR);
        g.fill(new Ellipse2D.Double(44, 6, 78, 92));
        g.setColor(SKIN);
        g.fill(new Ellipse2D.Double(54, 20, 58, 72));
        g.setColor(HAIR);
        g.fill(new Arc2D.Double(50, 8, 66, 52, 0, 180, Arc2D.CHORD));
        g.setColor(new Color(62, 50, 45));
        g.fillOval(70, 50, 4, 4);
        g.fillOval(92, 50, 4, 4);
        g.drawLine(77, 68, 88, 68);

        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Double(32, 86, 104, 94, 26, 26));
        g.setColor(new Color(245, 245, 242));
        g.fill(new RoundRectangle2D.Double(78, 86, 14, 80, 4, 4));
        g.setColor(new Color(39, 75, 58));
        g.draw(new Arc2D.Double(52, 90, 64, 58, 195, 150, Arc2D.OPEN));
        g.fillOval(48, 140, 10, 10);
        g.fillOval(110, 140, 10, 10);
        g.dispose();
    }

    private static BufferedImage loadPhoto(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        if (IMAGE_CACHE.containsKey(path)) {
            return IMAGE_CACHE.get(path);
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException ignored) {
            image = null;
        }
        IMAGE_CACHE.put(path, image);
        return image;
    }

    private static void drawCover(Graphics2D g, BufferedImage image, int width, int height,
                                  double offsetY) {
        double scale = Math.max((double) width / image.getWidth(),
                (double) height / image.getHeight());
        int drawWidth = (int) Math.ceil(image.getWidth() * scale);
        int drawHeight = (int) Math.ceil(image.getHeight() * scale);
        int x = (width - drawWidth) / 2;
        int y = (height - drawHeight) / 2 + (int) Math.round(height * offsetY);
        if (y > 0) {
            y = 0;
        }
        g.drawImage(image, x, y, drawWidth, drawHeight, null);
    }

    private static Color WHITE() {
        return new Color(255, 255, 255);
    }
}
