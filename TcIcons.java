import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public final class TcIcons {

    public enum Kind {
        HOME, SETTINGS, CHAT, HISTORY, CART, HELP, SEARCH,
        CALENDAR, CLOCK, PIN, HEART, STAR, LOGOUT, USER, BELL,
        LOCK, CHECK, CLOSE, BRAIN, GASTRO
    }

    private TcIcons() {
    }

    public static void paint(Graphics2D graphics, Kind kind, int x, int y, int size,
                             Color color, boolean filled) {
        paintInternal(graphics, kind, x, y, size, color, null, filled);
    }

    public static void paintGradient(Graphics2D graphics, Kind kind, int x, int y, int size,
                                     Color center, Color middle, Color edge, boolean filled) {
        Paint gradient = new RadialGradientPaint(new Point2D.Float(12, 12), 12,
                new float[]{0f, 0.5f, 1f}, new Color[]{center, middle, edge});
        paintInternal(graphics, kind, x, y, size, null, gradient, filled);
    }

    private static void paintInternal(Graphics2D graphics, Kind kind, int x, int y, int size,
                                      Color color, Paint paint, boolean filled) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.scale(size / 24.0, size / 24.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (paint != null) {
            g.setPaint(paint);
        } else {
            g.setColor(color);
        }

        switch (kind) {
            case HOME: {
                Path2D house = new Path2D.Double();
                house.moveTo(12, 2.5);
                house.lineTo(21.5, 11);
                house.lineTo(18.5, 11);
                house.lineTo(18.5, 21);
                house.lineTo(14, 21);
                house.lineTo(14, 15.5);
                house.lineTo(10, 15.5);
                house.lineTo(10, 21);
                house.lineTo(5.5, 21);
                house.lineTo(5.5, 11);
                house.lineTo(2.5, 11);
                house.closePath();
                if (filled) {
                    g.fill(house);
                } else {
                    g.draw(house);
                }
                break;
            }
            case SETTINGS: {
                g.drawOval(8, 8, 8, 8);
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    int x1 = (int) Math.round(12 + Math.cos(angle) * 6);
                    int y1 = (int) Math.round(12 + Math.sin(angle) * 6);
                    int x2 = (int) Math.round(12 + Math.cos(angle) * 9.5);
                    int y2 = (int) Math.round(12 + Math.sin(angle) * 9.5);
                    g.drawLine(x1, y1, x2, y2);
                }
                break;
            }
            case CHAT: {
                g.draw(new RoundRectangle2D.Double(2, 3.5, 20, 14, 4, 4));
                Path2D tail = new Path2D.Double();
                tail.moveTo(7.5, 17);
                tail.lineTo(6, 21);
                tail.lineTo(11.5, 17.5);
                g.draw(tail);
                g.drawLine(7, 10, 17, 10);
                break;
            }
            case HISTORY: {
                g.drawOval(3, 3, 18, 18);
                g.drawLine(12, 7, 12, 12);
                g.drawLine(12, 12, 16, 15);
                break;
            }
            case CART: {
                g.drawLine(2, 4, 6, 4);
                g.drawLine(6, 4, 9, 16);
                g.drawLine(9, 16, 20, 16);
                g.drawLine(6, 4, 7, 8);
                g.drawLine(7, 8, 21, 8);
                g.fillOval(9, 18, 3, 3);
                g.fillOval(17, 18, 3, 3);
                break;
            }
            case HELP: {
                if (filled) {
                    g.fill(new RoundRectangle2D.Double(2, 3, 20, 16, 5, 5));
                    g.setColor(new Color(102, 132, 92));
                }
                g.draw(new RoundRectangle2D.Double(2, 3, 20, 16, 5, 5));
                Path2D tail = new Path2D.Double();
                tail.moveTo(8, 19);
                tail.lineTo(7, 22);
                tail.lineTo(11, 19);
                g.draw(tail);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
                FontMetrics metrics = g.getFontMetrics();
                String mark = "?";
                g.drawString(mark, 12 - metrics.stringWidth(mark) / 2.0f, 16);
                break;
            }
            case SEARCH: {
                g.drawOval(3, 3, 13, 13);
                g.drawLine(13, 13, 21, 21);
                break;
            }
            case CALENDAR: {
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 16, 3, 3));
                g.drawLine(3, 10, 21, 10);
                g.drawLine(8, 3, 8, 7);
                g.drawLine(16, 3, 16, 7);
                g.fillOval(7, 13, 2, 2);
                g.fillOval(12, 13, 2, 2);
                g.fillOval(17, 13, 2, 2);
                break;
            }
            case CLOCK: {
                g.drawOval(3, 3, 18, 18);
                g.drawLine(12, 7, 12, 12);
                g.drawLine(12, 12, 16, 15);
                break;
            }
            case PIN: {
                Path2D pin = new Path2D.Double();
                pin.moveTo(12, 22);
                pin.curveTo(6, 15, 4, 12, 4, 9);
                pin.curveTo(4, 5, 7.5, 2, 12, 2);
                pin.curveTo(16.5, 2, 20, 5, 20, 9);
                pin.curveTo(20, 12, 18, 15, 12, 22);
                pin.closePath();
                if (filled) {
                    g.fill(pin);
                    g.setColor(Color.WHITE);
                    g.drawOval(9, 6, 6, 6);
                } else {
                    g.draw(pin);
                    g.drawOval(9, 6, 6, 6);
                }
                break;
            }
            case HEART: {
                Path2D heart = new Path2D.Double();
                heart.moveTo(12, 21);
                heart.curveTo(4, 15, 1, 10, 4, 6);
                heart.curveTo(7, 2, 11, 4, 12, 7);
                heart.curveTo(13, 4, 17, 2, 20, 6);
                heart.curveTo(23, 10, 20, 15, 12, 21);
                heart.closePath();
                if (filled) {
                    g.fill(heart);
                } else {
                    g.draw(heart);
                }
                break;
            }
            case LOGOUT: {
                g.draw(new RoundRectangle2D.Double(3, 2.5, 10, 19, 2.5, 2.5));
                g.drawLine(11, 12, 21, 12);
                g.drawLine(18, 9, 21, 12);
                g.drawLine(18, 15, 21, 12);
                break;
            }
            case USER: {
                g.fillOval(8, 3, 8, 8);
                Path2D body = new Path2D.Double();
                body.moveTo(4, 21);
                body.curveTo(4, 15, 7, 12, 12, 12);
                body.curveTo(17, 12, 20, 15, 20, 21);
                if (filled) {
                    g.fill(body);
                } else {
                    g.draw(body);
                }
                break;
            }
            case BELL: {
                Path2D bell = new Path2D.Double();
                bell.moveTo(5, 17);
                bell.lineTo(19, 17);
                bell.lineTo(17, 14);
                bell.lineTo(17, 10);
                bell.curveTo(17, 6, 15, 4, 12, 4);
                bell.curveTo(9, 4, 7, 6, 7, 10);
                bell.lineTo(7, 14);
                bell.closePath();
                if (filled) {
                    g.fill(bell);
                } else {
                    g.draw(bell);
                }
                g.drawLine(10, 20, 14, 20);
                break;
            }
            case LOCK: {
                g.draw(new RoundRectangle2D.Double(4, 10, 16, 12, 3, 3));
                Path2D shackle = new Path2D.Double();
                shackle.moveTo(8, 10);
                shackle.lineTo(8, 7);
                shackle.curveTo(8, 3.5, 16, 3.5, 16, 7);
                shackle.lineTo(16, 10);
                g.draw(shackle);
                g.fillOval(11, 14, 2, 3);
                break;
            }
            case CHECK: {
                Path2D check = new Path2D.Double();
                check.moveTo(4, 12);
                check.lineTo(10, 18);
                check.lineTo(20, 6);
                g.draw(check);
                break;
            }
            case BRAIN: {
                Path2D left = new Path2D.Double();
                left.moveTo(12, 5);
                left.curveTo(9.5, 2.5, 5.5, 3, 4, 6);
                left.curveTo(2, 8, 2.5, 11, 4, 13);
                left.curveTo(3.5, 16, 6, 19, 9, 19);
                left.curveTo(10.5, 19, 12, 18, 12, 17);
                left.lineTo(12, 5);

                Path2D right = new Path2D.Double();
                right.moveTo(12, 5);
                right.curveTo(14.5, 2.5, 18.5, 3, 20, 6);
                right.curveTo(22, 8, 21.5, 11, 20, 13);
                right.curveTo(20.5, 16, 18, 19, 15, 19);
                right.curveTo(13.5, 19, 12, 18, 12, 17);
                right.lineTo(12, 5);

                g.draw(left);
                g.draw(right);
                g.drawLine(12, 5, 12, 18);
                g.drawLine(7, 7, 9, 9);
                g.drawLine(5, 11, 8, 11);
                g.drawLine(8, 15, 10, 13);
                g.drawLine(17, 7, 15, 9);
                g.drawLine(19, 11, 16, 11);
                g.drawLine(16, 15, 14, 13);
                break;
            }
            case GASTRO: {
                Path2D stomach = new Path2D.Double();
                stomach.moveTo(15, 2.5);
                stomach.lineTo(15, 7);
                stomach.curveTo(17.5, 8.5, 19, 11, 18.5, 14);
                stomach.curveTo(18, 18, 15, 21, 11.5, 20.5);
                stomach.curveTo(8, 20, 6, 17, 6.5, 13.5);
                stomach.curveTo(7, 10, 10, 8.5, 12, 8);
                stomach.curveTo(13.5, 7.5, 14, 6, 14, 3.5);
                stomach.closePath();

                Path2D fold = new Path2D.Double();
                fold.moveTo(9, 12);
                fold.curveTo(11, 10, 14, 11, 15, 13);

                if (filled) {
                    g.fill(stomach);
                    g.setColor(Color.WHITE);
                    g.draw(fold);
                } else {
                    g.draw(stomach);
                    g.draw(fold);
                }
                break;
            }
            case CLOSE: {
                g.drawLine(6, 6, 18, 18);
                g.drawLine(18, 6, 6, 18);
                break;
            }
            case STAR: {
                Path2D star = new Path2D.Double();
                for (int i = 0; i < 10; i++) {
                    double radius = i % 2 == 0 ? 10 : 4.4;
                    double angle = -Math.PI / 2 + i * Math.PI / 5;
                    double px = 12 + Math.cos(angle) * radius;
                    double py = 12 + Math.sin(angle) * radius;
                    if (i == 0) {
                        star.moveTo(px, py);
                    } else {
                        star.lineTo(px, py);
                    }
                }
                star.closePath();
                g.fill(star);
                break;
            }
        }
        g.dispose();
    }
}
