import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final RecordManager manager;
    private final KpiCard totalCard = new KpiCard("Total Appointments", UiTheme.CYAN);
    private final KpiCard activeCard = new KpiCard("Active Cases", UiTheme.GREEN);
    private final KpiCard revenueCard = new KpiCard("Net Revenue", UiTheme.VIOLET);
    private final KpiCard checksCard = new KpiCard("Validation Checks", UiTheme.AMBER);
    private final DonutChart donut = new DonutChart();
    private final JPanel legend = new JPanel();
    private final JPanel activityList = new JPanel();

    public DashboardPanel(RecordManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(18, 18));
        setBackground(UiTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.label("Operations Overview", 24, Font.BOLD, UiTheme.TEXT),
                BorderLayout.NORTH);
        header.add(UiTheme.caption("A live snapshot of clinic performance and billing health"),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(18, 18));
        body.setOpaque(false);

        JPanel kpis = new JPanel(new java.awt.GridLayout(1, 4, 14, 14));
        kpis.setOpaque(false);
        kpis.add(totalCard);
        kpis.add(activeCard);
        kpis.add(revenueCard);
        kpis.add(checksCard);
        body.add(kpis, BorderLayout.NORTH);

        JPanel insights = new JPanel(new java.awt.GridLayout(1, 2, 14, 14));
        insights.setOpaque(false);
        insights.add(buildCategoryCard());
        insights.add(buildActivityCard());
        body.add(insights, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildCategoryCard() {
        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(12, 12));
        card.add(UiTheme.label("Service Mix", 16, Font.BOLD, UiTheme.TEXT), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(donut, BorderLayout.WEST);

        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setOpaque(false);
        JScrollPane legendScroll = new JScrollPane(legend);
        UiTheme.styleScrollPane(legendScroll);
        center.add(legendScroll, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityCard() {
        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(12, 12));
        card.add(UiTheme.label("Recent Activity", 16, Font.BOLD, UiTheme.TEXT), BorderLayout.NORTH);

        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);
        JScrollPane scroll = new JScrollPane(activityList);
        UiTheme.styleScrollPane(scroll);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        int total = manager.getTotalRecords();
        int active = manager.getActiveCount();
        double revenue = manager.getTotalRevenue();
        int checks = manager.getValidationChecks();

        totalCard.setValue(String.valueOf(total), "appointments in register", total == 0 ? 0 : 1);
        activeCard.setValue(String.valueOf(active), "cases in progress", total == 0 ? 0 : active / (double) total);
        revenueCard.setValue(String.format("₱%,.2f", revenue), "excluding cancelled visits",
                Math.min(1.0, revenue / 5000.0));
        checksCard.setValue(String.valueOf(checks), "integrity checks run", Math.min(1.0, checks / 20.0));

        Map<String, Integer> counts = manager.getCountByCategory();
        donut.setData(counts);
        refreshLegend(counts);
        refreshActivity();
        revalidate();
        repaint();
    }

    private void refreshLegend(Map<String, Integer> counts) {
        legend.removeAll();
        if (counts.isEmpty()) {
            legend.add(UiTheme.caption("No services recorded yet."));
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(240, 28));

            JPanel swatch = new JPanel() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(colorFor(entry.getKey()));
                    g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));
                    g.dispose();
                }
            };
            swatch.setPreferredSize(new Dimension(12, 12));
            swatch.setOpaque(false);

            JPanel left = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
            left.setOpaque(false);
            left.add(swatch);
            left.add(UiTheme.caption(entry.getKey()));
            row.add(left, BorderLayout.CENTER);
            row.add(UiTheme.label(String.valueOf(entry.getValue()), 13, Font.BOLD, UiTheme.TEXT),
                    BorderLayout.EAST);
            legend.add(row);
        }
        legend.revalidate();
        legend.repaint();
    }

    private void refreshActivity() {
        activityList.removeAll();
        List<BaseRecord> records = new ArrayList<>(manager.getAllRecords());
        if (records.isEmpty()) {
            JPanel empty = new UiTheme.RoundedPanel(UiTheme.SURFACE_2, 12);
            empty.add(UiTheme.caption("No appointments yet. Add one from the Appointment Console."),
                    BorderLayout.CENTER);
            empty.setMaximumSize(new Dimension(400, 60));
            activityList.add(empty);
        } else {
            int start = Math.max(0, records.size() - 5);
            for (int i = records.size() - 1; i >= start; i--) {
                BaseRecord record = records.get(i);
                JPanel row = new UiTheme.RoundedPanel(UiTheme.SURFACE_2, 12);
                row.setMaximumSize(new Dimension(500, 58));
                JLabel title = UiTheme.label(record.getName() + "  ·  " + record.getRecordId(),
                        13, Font.BOLD, UiTheme.TEXT);
                JLabel detail = UiTheme.caption(record.getSummaryDetails());
                JPanel stack = new JPanel(new BorderLayout());
                stack.setOpaque(false);
                stack.add(title, BorderLayout.NORTH);
                stack.add(detail, BorderLayout.SOUTH);
                row.add(stack, BorderLayout.CENTER);
                activityList.add(row);
                activityList.add(javax.swing.Box.createVerticalStrut(8));
            }
        }
        activityList.revalidate();
        activityList.repaint();
    }

    private Color colorFor(String category) {
        if ("Consultation".equals(category)) {
            return UiTheme.CYAN;
        }
        if ("Lab Test".equals(category)) {
            return UiTheme.VIOLET;
        }
        return UiTheme.MUTED;
    }

    private static class KpiCard extends UiTheme.RoundedPanel {

        private final JLabel valueLabel = UiTheme.label("0", 26, Font.BOLD, UiTheme.TEXT);
        private final JLabel hintLabel = UiTheme.caption("");
        private final ProgressBar bar;

        KpiCard(String title, Color accent) {
            super(UiTheme.SURFACE, 16);
            setLayout(new BorderLayout(0, 8));
            add(UiTheme.label(title.toUpperCase(), 11, Font.BOLD, accent), BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
            JPanel bottom = new JPanel(new BorderLayout(0, 8));
            bottom.setOpaque(false);
            bottom.add(hintLabel, BorderLayout.NORTH);
            bar = new ProgressBar(accent);
            bar.setPreferredSize(new Dimension(0, 6));
            bottom.add(bar, BorderLayout.SOUTH);
            add(bottom, BorderLayout.SOUTH);
        }

        void setValue(String value, String hint, double ratio) {
            valueLabel.setText(value);
            hintLabel.setText(hint);
            bar.setRatio(ratio);
            bar.repaint();
        }
    }

    private static class ProgressBar extends JPanel {

        private final Color accent;
        private double ratio;

        ProgressBar(Color accent) {
            this.accent = accent;
            setOpaque(false);
        }

        void setRatio(double ratio) {
            this.ratio = Math.max(0.0, Math.min(1.0, ratio));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(UiTheme.SURFACE_2);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 4, 4));
            if (ratio > 0) {
                g.setColor(accent);
                g.fill(new RoundRectangle2D.Float(0, 0,
                        Math.max(6, (int) (getWidth() * ratio)), getHeight() - 1, 4, 4));
            }
            g.dispose();
        }
    }

    private static class DonutChart extends JPanel {

        private final List<Map.Entry<String, Integer>> slices = new ArrayList<>();

        DonutChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(190, 190));
        }

        void setData(Map<String, Integer> counts) {
            slices.clear();
            slices.addAll(counts.entrySet());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            int total = 0;
            for (Map.Entry<String, Integer> slice : slices) {
                total += slice.getValue();
            }

            if (total == 0) {
                g.setColor(UiTheme.LINE);
                g.draw(new Ellipse2D.Double(x, y, size, size));
            } else {
                double start = -90;
                for (Map.Entry<String, Integer> slice : slices) {
                    double angle = 360.0 * slice.getValue() / total;
                    g.setColor(colorForStatic(slice.getKey()));
                    g.fill(new Arc2D.Double(x, y, size, size, start, angle, Arc2D.PIE));
                    start += angle;
                }
            }

            int hole = (int) (size * 0.58);
            g.setColor(UiTheme.SURFACE);
            g.fill(new Ellipse2D.Double(x + (size - hole) / 2.0, y + (size - hole) / 2.0,
                    hole, hole));

            String caption = total == 1 ? "APPOINTMENT" : "APPOINTMENTS";
            g.setColor(UiTheme.MUTED);
            g.setFont(UiTheme.font(10, Font.BOLD));
            drawCentered(g, caption, getHeight() / 2 - 16, getWidth());
            g.setColor(UiTheme.TEXT);
            g.setFont(UiTheme.font(28, Font.BOLD));
            drawCentered(g, String.valueOf(total), getHeight() / 2 + 10, getWidth());
            g.dispose();
        }

        private void drawCentered(Graphics2D g, String text, int baseline, int width) {
            java.awt.FontMetrics metrics = g.getFontMetrics();
            g.drawString(text, (width - metrics.stringWidth(text)) / 2, baseline);
        }

        private Color colorForStatic(String category) {
            if ("Consultation".equals(category)) {
                return UiTheme.CYAN;
            }
            if ("Lab Test".equals(category)) {
                return UiTheme.VIOLET;
            }
            return UiTheme.MUTED;
        }
    }
}
