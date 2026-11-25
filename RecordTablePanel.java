import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class RecordTablePanel extends JPanel {

    private static final String[] COLUMNS = {
            "Appointment ID", "Patient", "Type", "Doctor / Test",
            "Fee (₱)", "Total Cost (₱)", "Status"
    };

    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField = UiTheme.field();
    private final JLabel countLabel = UiTheme.caption("0 appointments");
    private List<BaseRecord> allRecords = new ArrayList<>();

    public RecordTablePanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        header.add(UiTheme.label("Appointment Register", 18, Font.BOLD, UiTheme.TEXT),
                BorderLayout.NORTH);

        JPanel tools = new JPanel(new BorderLayout(10, 0));
        tools.setOpaque(false);
        tools.add(countLabel, BorderLayout.WEST);
        searchField.setPreferredSize(new Dimension(250, 36));
        tools.add(searchField, BorderLayout.EAST);
        header.add(tools, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(34);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(38, 64, 105));
        table.setSelectionForeground(UiTheme.TEXT);
        table.setDefaultRenderer(Object.class, new RecordCellRenderer());

        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        UiTheme.styleScrollPane(scroll);
        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
    }

    public void refresh(List<BaseRecord> records) {
        allRecords = new ArrayList<>(records);
        applyFilter();
    }

    private void applyFilter() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        model.setRowCount(0);
        for (BaseRecord r : allRecords) {
            if (!query.isEmpty() && !matches(r, query)) {
                continue;
            }
            model.addRow(new Object[]{
                    r.getRecordId(),
                    r.getName(),
                    r.getCategoryType(),
                    extraOf(r),
                    String.format("%.2f", r.getBaseRate()),
                    String.format("%.2f", r.calculateTotalCost()),
                    r.getStatus()
            });
        }
        int shown = model.getRowCount();
        String noun = shown == 1 ? " appointment" : " appointments";
        countLabel.setText(shown + noun + (query.isEmpty() ? "" : " found"));
    }

    private boolean matches(BaseRecord record, String query) {
        String haystack = (record.getRecordId() + " " + record.getName() + " "
                + record.getCategoryType() + " " + extraOf(record) + " "
                + record.getStatus() + " " + record.getSummaryDetails()).toLowerCase();
        return haystack.contains(query);
    }

    public String getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (String) model.getValueAt(table.convertRowIndexToModel(row), 0);
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public JTable getTable() {
        return table;
    }

    public static String extraOf(BaseRecord r) {
        if (r instanceof ConsultationAppointment) {
            return ((ConsultationAppointment) r).getDoctorName();
        } else if (r instanceof LabTestAppointment) {
            return ((LabTestAppointment) r).getTestType();
        }
        return "";
    }

    private static class RecordCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable source, Object value,
                                                       boolean selected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    source, value, selected, hasFocus, row, column);
            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            label.setFont(UiTheme.font(13));

            if (selected) {
                label.setBackground(new Color(38, 64, 105));
                label.setForeground(UiTheme.TEXT);
            } else {
                label.setBackground(row % 2 == 0 ? UiTheme.SURFACE : UiTheme.SURFACE_2);
                label.setForeground(UiTheme.TEXT);
            }

            String text = value == null ? "" : value.toString();
            if (column == 6) {
                if ("Active".equalsIgnoreCase(text)) {
                    label.setBackground(selected ? new Color(38, 64, 105) : new Color(20, 61, 52));
                    label.setForeground(UiTheme.GREEN);
                } else if ("Completed".equalsIgnoreCase(text)) {
                    label.setBackground(selected ? new Color(38, 64, 105) : new Color(23, 49, 80));
                    label.setForeground(UiTheme.CYAN);
                } else if ("Cancelled".equalsIgnoreCase(text)) {
                    label.setBackground(selected ? new Color(38, 64, 105) : new Color(70, 32, 48));
                    label.setForeground(UiTheme.RED);
                }
                label.setFont(UiTheme.font(12, Font.BOLD));
            } else if (column == 2) {
                label.setForeground("Lab Test".equals(text) ? UiTheme.VIOLET : UiTheme.CYAN);
                label.setFont(UiTheme.font(12, Font.BOLD));
            } else if (column == 5) {
                label.setFont(UiTheme.font(13, Font.BOLD));
            }
            return label;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {

        HeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(JLabel.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean selected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, selected, hasFocus, row, column);
            label.setBackground(new Color(20, 30, 54));
            label.setForeground(UiTheme.MUTED);
            label.setFont(UiTheme.font(11, Font.BOLD));
            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return label;
        }
    }
}
