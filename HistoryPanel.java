import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

@SuppressWarnings("serial")
public class HistoryPanel extends JPanel {

    private final RecordManager manager;
    private final String username;
    private final DefaultListModel<BaseRecord> model = new DefaultListModel<>();
    private final JList<BaseRecord> list = new JList<>(model);
    private final JComboBox<String> filter =
            UiTheme.combo(new String[]{"All", "Active", "Completed", "Cancelled"});
    private final javax.swing.JLabel totalLabel = UiTheme.label("0", 22, Font.BOLD, UiTheme.DEEP_OLIVE);
    private final javax.swing.JLabel activeLabel = UiTheme.label("0", 22, Font.BOLD, UiTheme.OLIVE);
    private final javax.swing.JLabel doneLabel = UiTheme.label("0", 22, Font.BOLD, UiTheme.CHARCOAL);
    private final javax.swing.JLabel expenseLabel =
            UiTheme.label("₱ 0", 22, Font.BOLD, UiTheme.DEEP_OLIVE);

    public HistoryPanel(RecordManager manager, String username) {
        this.manager = manager;
        this.username = username == null || username.trim().isEmpty() ? "Patient" : username.trim();
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.add(UiTheme.heading("Medical History", 26), BorderLayout.NORTH);
        header.add(UiTheme.caption("Your booked visits, lab tests, and their current status."),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(summary(), BorderLayout.NORTH);
        content.add(listCard(), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        refresh();
    }

    private JPanel summary() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(summaryCard(totalLabel, "Total visits", UiTheme.SAGE_LIGHT));
        row.add(Box.createRigidArea(new Dimension(12, 0)));
        row.add(summaryCard(activeLabel, "Active", UiTheme.BUTTER));
        row.add(Box.createRigidArea(new Dimension(12, 0)));
        row.add(summaryCard(doneLabel, "Completed", UiTheme.SAGE));
        row.add(Box.createRigidArea(new Dimension(12, 0)));
        row.add(summaryCard(expenseLabel, "Total expenses", UiTheme.BUTTER));
        row.add(Box.createHorizontalGlue());
        return row;
    }

    private JPanel summaryCard(javax.swing.JLabel value, String title, Color background) {
        JPanel card = UiTheme.card();
        card.setBackground(background);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.setPreferredSize(new Dimension(180, 86));
        card.setMaximumSize(new Dimension(180, 86));
        card.add(value);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(UiTheme.caption(title));
        return card;
    }

    private JPanel listCard() {
        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        javax.swing.JLabel count = UiTheme.caption("0 records");
        javax.swing.JLabel filterLabel = UiTheme.label("Filter", 12, Font.BOLD, UiTheme.MUTED_TEXT);
        JPanel tools = new JPanel(new BorderLayout());
        tools.setOpaque(false);
        tools.add(count, BorderLayout.WEST);
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(filterLabel);
        right.add(Box.createRigidArea(new Dimension(8, 0)));
        filter.setPreferredSize(new Dimension(150, 36));
        right.add(filter);
        tools.add(right, BorderLayout.EAST);
        card.add(tools, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(82);
        list.setSelectionBackground(UiTheme.SAGE_LIGHT);
        list.setSelectionForeground(UiTheme.CHARCOAL);
        list.setCellRenderer(new HistoryCellRenderer());
        JScrollPane scroll = new JScrollPane(list);
        UiTheme.styleScrollPane(scroll);
        card.add(scroll, BorderLayout.CENTER);

        filter.addActionListener(e -> {
            refresh();
            count.setText(model.getSize() + " records");
        });
        return card;
    }

    public void refresh() {
        model.clear();
        int total = 0;
        int active = 0;
        int completed = 0;
        double expenses = 0;
        String choice = String.valueOf(filter.getSelectedItem());

        for (BaseRecord record : manager.getAllRecords()) {
            if (!username.equalsIgnoreCase(record.getName())) {
                continue;
            }
            total++;
            expenses += record.calculateTotalCost();
            String status = record.getStatus();
            if ("Active".equalsIgnoreCase(status)) {
                active++;
            } else if ("Completed".equalsIgnoreCase(status)) {
                completed++;
            }
            if (!"All".equals(choice) && !choice.equalsIgnoreCase(status)) {
                continue;
            }
            model.addElement(record);
        }
        if (model.isEmpty()) {
            model.addElement(null);
        }
        totalLabel.setText(String.valueOf(total));
        activeLabel.setText(String.valueOf(active));
        doneLabel.setText(String.valueOf(completed));
        expenseLabel.setText(String.format("₱ %,d", (int) Math.round(expenses)));
    }

    private static class HistoryCellRenderer extends JPanel implements ListCellRenderer<BaseRecord> {

        private final javax.swing.JLabel title = UiTheme.label("", 14, Font.BOLD, UiTheme.CHARCOAL);
        private final javax.swing.JLabel detail = UiTheme.caption("");
        private final javax.swing.JLabel badge =
                UiTheme.label("", 11, Font.BOLD, UiTheme.DEEP_OLIVE);

        HistoryCellRenderer() {
            setLayout(new BorderLayout(10, 4));
            setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(title);
            text.add(Box.createRigidArea(new Dimension(0, 4)));
            text.add(detail);
            add(text, BorderLayout.CENTER);
            add(badge, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends BaseRecord> source,
                                                      BaseRecord value, int index,
                                                      boolean selected, boolean hasFocus) {
            setBackground(selected ? UiTheme.SAGE_LIGHT : UiTheme.WHITE);
            setOpaque(true);
            if (value == null) {
                title.setText("No visits here yet");
                detail.setText("Book an appointment from the dashboard to see it here.");
                badge.setText("");
                return this;
            }
            title.setText(value.getRecordId() + "  •  " + value.getCategoryType());
            detail.setText(RecordTablePanel.extraOf(value) + "  |  ₱"
                    + String.format("%.2f", value.calculateTotalCost()));
            badge.setText(value.getStatus());
            badge.setBackground(badgeColor(value.getStatus()));
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }

        private static Color badgeColor(String status) {
            if ("Cancelled".equalsIgnoreCase(status)) {
                return UiTheme.RED;
            }
            if ("Completed".equalsIgnoreCase(status)) {
                return UiTheme.SAGE;
            }
            return UiTheme.BUTTER_DEEP;
        }
    }
}
