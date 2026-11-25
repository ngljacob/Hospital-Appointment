import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class SvgIcon implements Icon, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Pattern PATH_TAG = Pattern.compile(
            "<path\\b([^>]*)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern CIRCLE_TAG = Pattern.compile(
            "<circle\\b([^>]*)>", Pattern.CASE_INSENSITIVE);
    private static final Pattern ATTRIBUTE = Pattern.compile(
            "(d|class|fill)\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern CIRCLE_ATTRIBUTE = Pattern.compile(
            "(cx|cy|r|class|fill)\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_RULE = Pattern.compile(
            "\\.([A-Za-z0-9_-]+)\\s*\\{([^}]*)\\}", Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_FILL = Pattern.compile(
            "\\.([A-Za-z0-9_-]+)\\s*\\{\\s*fill\\s*:\\s*(#[0-9a-fA-F]{3,8}|[a-zA-Z]+)\\s*;?\\s*\\}",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern FILL_NONE = Pattern.compile(
            "\\bfill\\s*:\\s*none", Pattern.CASE_INSENSITIVE);
    private static final Pattern STROKE_COLOR = Pattern.compile(
            "\\bstroke\\s*:\\s*(#[0-9a-fA-F]{3,8}|[a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern STROKE_WIDTH = Pattern.compile(
            "\\bstroke-width\\s*:\\s*(\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN = Pattern.compile(
            "[MmLlHhVvCcSsQqTtAaZz]|[-+]?(?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][-+]?\\d+)?");
    private static final Pattern VIEWBOX = Pattern.compile(
            "<svg\\b[^>]*\\bviewBox\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    private final List<Shape> shapes = new ArrayList<Shape>();
    private final List<Color> shapeColors = new ArrayList<>();
    private final List<Boolean> shapeStrokes = new ArrayList<>();
    private final List<Float> shapeStrokeWidths = new ArrayList<>();
    private final boolean preserveColors;
    private final int size;
    private float scale;
    private float offsetX;
    private float offsetY;
    private Color color = Color.WHITE;
    private Color gradientCenter;
    private Color gradientMiddle;
    private Color gradientEdge;

    public SvgIcon(String path, int size) {
        this(path, size, false);
    }

    public SvgIcon(String path, int size, boolean preserveColors) {
        this.size = size;
        this.scale = size / 512f;
        this.offsetX = 0;
        this.offsetY = 0;
        this.preserveColors = preserveColors;
        load(path);
        if (shapes.isEmpty()) {
            shapes.add(fallback());
            shapeColors.add(null);
            shapeStrokes.add(Boolean.FALSE);
            shapeStrokeWidths.add(Float.valueOf(1f));
        }
    }

    public void setColor(Color color) {
        this.color = color;
        this.gradientCenter = null;
        this.gradientMiddle = null;
        this.gradientEdge = null;
    }

    public void setGradient(Color center, Color middle, Color edge) {
        this.color = edge;
        this.gradientCenter = center;
        this.gradientMiddle = middle;
        this.gradientEdge = edge;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.scale(scale, scale);
        g.translate(offsetX, offsetY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke previousStroke = g.getStroke();
        for (int i = 0; i < shapes.size(); i++) {
            Color original = preserveColors ? shapeColors.get(i) : null;
            if (original != null) {
                g.setColor(original);
            } else if (gradientCenter != null) {
                g.setPaint(new RadialGradientPaint(
                        new Point2D.Float(256f, 256f), 340f,
                        new float[]{0f, 0.55f, 1f},
                        new Color[]{gradientCenter, gradientMiddle, gradientEdge}));
            } else {
                g.setColor(color);
            }
            if (shapeStrokes.get(i)) {
                g.setStroke(new BasicStroke(shapeStrokeWidths.get(i),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(shapes.get(i));
            } else {
                g.fill(shapes.get(i));
            }
        }
        g.setStroke(previousStroke);
        g.dispose();
    }

    private void load(String path) {
        try {
            String svg = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            applyViewBox(svg);
            Map<String, Color> fills = parseStyles(svg);
            Map<String, String> styleBodies = parseStyleBodies(svg);

            Matcher matcher = PATH_TAG.matcher(svg);
            while (matcher.find()) {
                String attributes = matcher.group(1);
                String data = attribute(attributes, "d");
                if (data == null) {
                    continue;
                }
                String className = attribute(attributes, "class");
                String body = styleBody(styleBodies, className);
                Color fill = originalFill(fills, className, attribute(attributes, "fill"));
                boolean stroke = isStrokeStyle(body);
                Color original = stroke ? strokeColor(body) : fill;
                float width = strokeWidth(body);
                for (Path2D shape : parsePath(data)) {
                    addShape(shape, original, stroke, width);
                }
            }

            Matcher circles = CIRCLE_TAG.matcher(svg);
            while (circles.find()) {
                String attributes = circles.group(1);
                String cx = circleAttribute(attributes, "cx");
                String cy = circleAttribute(attributes, "cy");
                String radius = circleAttribute(attributes, "r");
                if (cx == null || cy == null || radius == null) {
                    continue;
                }
                double x = Double.parseDouble(cx);
                double y = Double.parseDouble(cy);
                double r = Double.parseDouble(radius);
                String className = circleAttribute(attributes, "class");
                String body = styleBody(styleBodies, className);
                Color fill = originalFill(fills, className, circleAttribute(attributes, "fill"));
                boolean stroke = isStrokeStyle(body);
                Color original = stroke ? strokeColor(body) : fill;
                addShape(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2),
                        original, stroke, strokeWidth(body));
            }
        } catch (IOException | RuntimeException ignored) {
            shapes.clear();
            shapeColors.clear();
            shapeStrokes.clear();
            shapeStrokeWidths.clear();
        }
    }

    private void addShape(Shape shape, Color original, boolean stroke, float width) {
        shapes.add(shape);
        shapeColors.add(preserveColors ? original : null);
        shapeStrokes.add(Boolean.valueOf(stroke));
        shapeStrokeWidths.add(Float.valueOf(width));
    }

    private void applyViewBox(String svg) {
        Matcher matcher = VIEWBOX.matcher(svg);
        if (!matcher.find()) {
            return;
        }
        List<String> values = new ArrayList<String>();
        Matcher numbers = TOKEN.matcher(matcher.group(1));
        while (numbers.find() && values.size() < 4) {
            values.add(numbers.group());
        }
        if (values.size() < 4) {
            return;
        }
        float minX = Float.parseFloat(values.get(0));
        float minY = Float.parseFloat(values.get(1));
        float width = Float.parseFloat(values.get(2));
        float height = Float.parseFloat(values.get(3));
        float span = Math.max(width, height);
        if (span <= 0) {
            return;
        }
        scale = size / span;
        offsetX = -minX;
        offsetY = -minY;
    }

    private static Map<String, Color> parseStyles(String svg) {
        Map<String, Color> styles = new HashMap<String, Color>();
        Matcher matcher = STYLE_FILL.matcher(svg);
        while (matcher.find()) {
            Color color = parseColor(matcher.group(2));
            if (color != null) {
                styles.put(matcher.group(1), color);
            }
        }
        return styles;
    }

    private static Map<String, String> parseStyleBodies(String svg) {
        Map<String, String> bodies = new HashMap<String, String>();
        Matcher matcher = STYLE_RULE.matcher(svg);
        while (matcher.find()) {
            bodies.put(matcher.group(1), matcher.group(2));
        }
        return bodies;
    }

    private static String styleBody(Map<String, String> bodies, String className) {
        if (className == null) {
            return null;
        }
        for (String name : className.trim().split("\\s+")) {
            String body = bodies.get(name);
            if (body != null) {
                return body;
            }
        }
        return null;
    }

    private static Color originalFill(Map<String, Color> fills, String className, String attributeFill) {
        if (className != null) {
            Color fill = styleBodyFill(fills, className);
            if (fill != null) {
                return fill;
            }
        }
        return parseColor(attributeFill);
    }

    private static Color styleBodyFill(Map<String, Color> fills, String className) {
        for (String name : className.trim().split("\\s+")) {
            Color fill = fills.get(name);
            if (fill != null) {
                return fill;
            }
        }
        return null;
    }

    private static boolean isStrokeStyle(String body) {
        if (body == null) {
            return false;
        }
        if (FILL_NONE.matcher(body).find()) {
            return true;
        }
        Matcher stroke = STROKE_COLOR.matcher(body);
        return stroke.find() && !"none".equalsIgnoreCase(stroke.group(1).trim());
    }

    private static Color strokeColor(String body) {
        if (body == null) {
            return null;
        }
        Matcher matcher = STROKE_COLOR.matcher(body);
        return matcher.find() ? parseColor(matcher.group(1)) : null;
    }

    private static float strokeWidth(String body) {
        if (body != null) {
            Matcher matcher = STROKE_WIDTH.matcher(body);
            if (matcher.find()) {
                return Float.parseFloat(matcher.group(1));
            }
        }
        return 4.5f;
    }

    private static String circleAttribute(String attributes, String name) {
        Matcher matcher = CIRCLE_ATTRIBUTE.matcher(attributes);
        while (matcher.find()) {
            if (name.equalsIgnoreCase(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return null;
    }

    private static String attribute(String attributes, String name) {
        Matcher matcher = ATTRIBUTE.matcher(attributes);
        while (matcher.find()) {
            if (name.equalsIgnoreCase(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return null;
    }

    private static Color parseColor(String value) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.startsWith("#")) {
            String hex = text.substring(1);
            try {
                if (hex.length() == 3) {
                    StringBuilder expanded = new StringBuilder(6);
                    for (int i = 0; i < hex.length(); i++) {
                        expanded.append(hex.charAt(i)).append(hex.charAt(i));
                    }
                    return new Color(Integer.parseInt(expanded.toString(), 16));
                }
                if (hex.length() == 6) {
                    return new Color(Integer.parseInt(hex, 16));
                }
                if (hex.length() == 8) {
                    long rgba = Long.parseLong(hex, 16);
                    int alpha = (int) ((rgba >>> 24) & 0xFF);
                    int rgb = (int) (rgba & 0xFFFFFF);
                    return new Color(rgb | (alpha << 24), true);
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if ("white".equalsIgnoreCase(text)) {
            return Color.WHITE;
        }
        if ("black".equalsIgnoreCase(text)) {
            return Color.BLACK;
        }
        if ("none".equalsIgnoreCase(text)) {
            return null;
        }
        return null;
    }

    private static List<Path2D> parsePath(String data) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN.matcher(data);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        List<Path2D> result = new ArrayList<>();
        Path2D path = new Path2D.Float();
        double cx = 0;
        double cy = 0;
        double sx = 0;
        double sy = 0;
        double lastX2 = 0;
        double lastY2 = 0;
        char command = 0;
        int i = 0;

        while (i < tokens.size()) {
            String token = tokens.get(i);
            if (isCommand(token)) {
                command = token.charAt(0);
                i++;
                if (command == 'z' || command == 'Z') {
                    path.closePath();
                    cx = sx;
                    cy = sy;
                    continue;
                }
            }

            switch (command) {
                case 'M':
                case 'm': {
                    if (!has(tokens, i, 2)) {
                        break;
                    }
                    double x = number(tokens.get(i++));
                    double y = number(tokens.get(i++));
                    if (command == 'm') {
                        x += cx;
                        y += cy;
                    }
                    path.moveTo(x, y);
                    sx = cx = x;
                    sy = cy = y;
                    command = command == 'M' ? 'L' : 'l';
                    break;
                }
                case 'L':
                case 'l': {
                    if (!has(tokens, i, 2)) {
                        break;
                    }
                    double x = number(tokens.get(i++));
                    double y = number(tokens.get(i++));
                    if (command == 'l') {
                        x += cx;
                        y += cy;
                    }
                    path.lineTo(x, y);
                    cx = x;
                    cy = y;
                    break;
                }
                case 'V':
                case 'v': {
                    if (!has(tokens, i, 1)) {
                        break;
                    }
                    double y = number(tokens.get(i++));
                    if (command == 'v') {
                        y += cy;
                    }
                    path.lineTo(cx, y);
                    cy = y;
                    break;
                }
                case 'C':
                case 'c': {
                    if (!has(tokens, i, 6)) {
                        break;
                    }
                    double x1 = number(tokens.get(i++));
                    double y1 = number(tokens.get(i++));
                    double x2 = number(tokens.get(i++));
                    double y2 = number(tokens.get(i++));
                    double x = number(tokens.get(i++));
                    double y = number(tokens.get(i++));
                    if (command == 'c') {
                        x1 += cx;
                        y1 += cy;
                        x2 += cx;
                        y2 += cy;
                        x += cx;
                        y += cy;
                    }
                    path.curveTo(x1, y1, x2, y2, x, y);
                    cx = x;
                    cy = y;
                    lastX2 = x2;
                    lastY2 = y2;
                    break;
                }
                case 'S':
                case 's': {
                    if (!has(tokens, i, 4)) {
                        break;
                    }
                    double x2 = number(tokens.get(i++));
                    double y2 = number(tokens.get(i++));
                    double x = number(tokens.get(i++));
                    double y = number(tokens.get(i++));
                    if (command == 's') {
                        x2 += cx;
                        y2 += cy;
                        x += cx;
                        y += cy;
                    }
                    path.curveTo(2 * cx - lastX2, 2 * cy - lastY2, x2, y2, x, y);
                    cx = x;
                    cy = y;
                    lastX2 = x2;
                    lastY2 = y2;
                    break;
                }
                default:
                    i++;
                    break;
            }
        }

        result.add(path);
        return result;
    }

    private static Shape fallback() {
        Path2D path = new Path2D.Float();
        path.moveTo(150, 90);
        path.lineTo(150, 190);
        path.curveTo(150, 280, 220, 320, 300, 320);
        path.curveTo(380, 320, 440, 260, 440, 180);
        path.lineTo(440, 90);
        path.moveTo(120, 90);
        path.lineTo(180, 90);
        path.moveTo(410, 90);
        path.lineTo(470, 90);
        return path;
    }

    private static boolean has(List<String> tokens, int index, int count) {
        return index + count <= tokens.size();
    }

    private static boolean isCommand(String token) {
        return token.length() == 1 && Character.isLetter(token.charAt(0));
    }

    private static double number(String token) {
        return Double.parseDouble(token);
    }
}
