import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;

@SuppressWarnings("serial")
public class NotificationsPanel extends JPanel {

    private final RecordManager manager;
    private final String username;
    private final IntConsumer unreadListener;
    private final Set<String> readIds = new HashSet<>();
    private final DefaultListModel<Notice> model = new DefaultListModel<>();
    private final JList<Notice> list = new JList<>(model);
    private final javax.swing.JLabel unreadLabel =
            UiTheme.label("0 unread", 13, Font.BOLD, UiTheme.DEEP_OLIVE);
    private JButton markUnread;

    public NotificationsPanel(RecordManager manager, String username) {
        this(manager, username, null);
    }

    public NotificationsPanel(RecordManager manager, String username, IntConsumer unreadListener) {
        this.manager = manager;
        this.username = username == null || username.trim().isEmpty() ? "Patient" : username.trim();
        this.unreadListener = unreadListener;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.add(UiTheme.heading("Notifications", 26), BorderLayout.NORTH);
        header.add(UiTheme.caption("Updates about your appointments and medical visits."),
                BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(listCard(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel listCard() {
        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel tools = new JPanel(new BorderLayout());
        tools.setOpaque(false);
        tools.add(unreadLabel, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        markUnread = UiTheme.button("Mark as unread", UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        markUnread.addActionListener(e -> markSelectedUnread());
        JButton markAll = UiTheme.button("Mark all as read", UiTheme.SAGE_LIGHT, UiTheme.CHARCOAL);
        markAll.addActionListener(e -> markAllRead());
        actions.add(markUnread);
        actions.add(markAll);
        tools.add(actions, BorderLayout.EAST);
        card.add(tools, BorderLayout.NORTH);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(104);
        list.setSelectionBackground(UiTheme.SAGE_LIGHT);
        list.setSelectionForeground(UiTheme.CHARCOAL);
        list.setCellRenderer(new NoticeRenderer());
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMarkUnread();
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                Rectangle bounds = list.getCellBounds(index, index);
                if (bounds == null || !bounds.contains(e.getPoint())) {
                    return;
                }
                list.setSelectedIndex(index);
                Notice notice = model.get(index);
                if (!notice.read) {
                    notice.read = true;
                    readIds.add(notice.id);
                    list.repaint();
                    updateUnread();
                    updateMarkUnread();
                }
                showNotice(notice);
            }
        });
        JScrollPane scroll = new JScrollPane(list);
        UiTheme.styleScrollPane(scroll);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        model.clear();
        List<Notice> notices = new ArrayList<>();
        Set<String> currentIds = new HashSet<>();

        for (BaseRecord record : manager.getAllRecords()) {
            if (!username.equalsIgnoreCase(record.getName())) {
                continue;
            }
            String status = record.getStatus();
            String extra = RecordTablePanel.extraOf(record);
            String id = record.getRecordId();
            currentIds.add(id);
            if ("Cancelled".equalsIgnoreCase(status)) {
                notices.add(new Notice(id, "Appointment cancelled",
                        id + " for " + extra + " was cancelled.", UiTheme.RED, record));
            } else if ("Completed".equalsIgnoreCase(status)) {
                notices.add(new Notice(id, "Visit completed",
                        id + " with " + extra + " is now completed.", UiTheme.STATUS_GREEN, record));
            } else {
                notices.add(new Notice(id, "Appointment confirmed",
                        id + " with " + extra + " is booked and active.", UiTheme.DEEP_OLIVE, record));
            }
        }

        if (notices.isEmpty()) {
            currentIds.add("welcome");
            notices.add(new Notice("welcome", "Welcome to Morpho Medical",
                    "Book your first appointment from the dashboard to get updates here.",
                    UiTheme.OLIVE, null));
        }
        readIds.retainAll(currentIds);
        for (Notice notice : notices) {
            notice.read = readIds.contains(notice.id);
            model.addElement(notice);
        }
        list.repaint();
        updateUnread();
        updateMarkUnread();
    }

    private void markAllRead() {
        for (int i = 0; i < model.getSize(); i++) {
            Notice notice = model.get(i);
            notice.read = true;
            readIds.add(notice.id);
        }
        list.repaint();
        updateUnread();
        updateMarkUnread();
    }

    private void markSelectedUnread() {
        Notice notice = list.getSelectedValue();
        if (notice == null) {
            return;
        }
        notice.read = false;
        readIds.remove(notice.id);
        list.repaint();
        updateUnread();
        updateMarkUnread();
    }

    private void updateUnread() {
        int count = 0;
        for (int i = 0; i < model.getSize(); i++) {
            if (!model.get(i).read) {
                count++;
            }
        }
        unreadLabel.setText(count + " unread");
        if (unreadListener != null) {
            unreadListener.accept(count);
        }
    }

    private void updateMarkUnread() {
        if (markUnread == null) {
            return;
        }
        Notice notice = list.getSelectedValue();
        markUnread.setEnabled(notice != null && notice.read);
    }

    private void showNotice(Notice notice) {
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int dialogWidth = Math.max(280, Math.min(520, screen.width - 48));
        int dialogHeight = Math.max(260, Math.min(520, screen.height - 48));
        int textWidth = Math.max(180, dialogWidth - 88);
        ThemedDialog dialog = new ThemedDialog(
                SwingUtilities.getWindowAncestor(this), "APPOINTMENT UPDATE",
                notice.title, TcIcons.Kind.CALENDAR, dialogWidth, dialogHeight) { };
        dialog.body.setLayout(new BoxLayout(dialog.body, BoxLayout.Y_AXIS));
        dialog.body.add(wrappedLabel(notice.detail, 13, Font.PLAIN, UiTheme.CHARCOAL, textWidth));
        dialog.body.add(Box.createVerticalStrut(16));
        if (notice.record != null) {
            BaseRecord record = notice.record;
            addDetail(dialog.body, "Appointment ID", record.getRecordId(), textWidth);
            addDetail(dialog.body, "Type", record.getCategoryType(), textWidth);
            addDetail(dialog.body, "Doctor / Test", RecordTablePanel.extraOf(record), textWidth);
            addDetail(dialog.body, "Patient", record.getName(), textWidth);
            addDetail(dialog.body, "Status", record.getStatus(), textWidth);
            addDetail(dialog.body, "Fee",
                    String.format("₱ %,d", (int) record.getBaseRate()), textWidth);
            addDetail(dialog.body, "Total cost",
                    String.format("₱ %,d", (int) record.calculateTotalCost()), textWidth);
        } else {
            dialog.body.add(UiTheme.caption("No appointment details yet."));
        }
        dialog.body.add(Box.createVerticalGlue());
        JButton close = UiTheme.button("Close", UiTheme.DEEP_OLIVE, Color.WHITE);
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        close.setPreferredSize(new Dimension(120, 36));
        close.setMinimumSize(new Dimension(120, 36));
        close.setMaximumSize(new Dimension(120, 36));
        close.addActionListener(e -> dialog.dispose());
        dialog.body.add(close);
        dialog.pack();
        dialog.fitToScreen();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private static JLabel wrappedLabel(String text, int size, int style,
                                       Color color, int width) {
        return UiTheme.wrappedLabel(text, size, style, color, width);
    }

    private static void addDetail(JPanel body, String labelText, String value, int maxWidth) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        row.add(UiTheme.label(labelText + ":", 12, Font.BOLD, UiTheme.MUTED_TEXT));
        row.add(UiTheme.label(value, 12, Font.PLAIN, UiTheme.CHARCOAL));
        body.add(row);
        body.add(Box.createVerticalStrut(8));
    }

    private static class Notice {

        private final String id;
        private final String title;
        private final String detail;
        private final Color color;
        private final BaseRecord record;
        private boolean read;

        Notice(String id, String title, String detail, Color color, BaseRecord record) {
            this.id = id;
            this.title = title;
            this.detail = detail;
            this.color = color;
            this.record = record;
        }
    }

    private static class NoticeRenderer extends JPanel implements ListCellRenderer<Notice> {

        private Notice notice;

        private final JPanel dot = new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(14, 14));
                setMaximumSize(new Dimension(14, 14));
            }

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(notice == null ? UiTheme.OLIVE : notice.color);
                g.fillOval(0, 0, 14, 14);
                g.dispose();
            }
        };
        private final javax.swing.JLabel title = UiTheme.label("", 14, Font.BOLD, UiTheme.CHARCOAL);
        private final javax.swing.JLabel detail = UiTheme.caption("");

        NoticeRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            detail.setAlignmentX(Component.LEFT_ALIGNMENT);
            text.add(title);
            text.add(Box.createRigidArea(new Dimension(0, 4)));
            text.add(detail);
            add(dot);
            add(Box.createRigidArea(new Dimension(12, 0)));
            add(text);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Notice> source,
                                                      Notice value, int index,
                                                      boolean selected, boolean hasFocus) {
            notice = value;
            setBackground(selected ? UiTheme.SAGE_LIGHT : UiTheme.WHITE);
            setOpaque(true);
            title.setText(value.title);
            int textWidth = Math.max(280, source.getWidth() - 70);
            detail.setText("<html><div style='width:" + textWidth + "px'>" + value.detail
                    + "</div></html>");
            Color textColor = value.read ? UiTheme.MUTED_TEXT : UiTheme.CHARCOAL;
            title.setForeground(textColor);
            detail.setForeground(value.read ? UiTheme.LINE : UiTheme.MUTED_TEXT);
            dot.repaint();
            return this;
        }
    }
}
