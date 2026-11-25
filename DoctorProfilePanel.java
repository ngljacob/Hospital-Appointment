import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class DoctorProfilePanel extends JPanel {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    private final RecordManager manager;
    private final Runnable onChange;
    private final String patientName;
    private final JPanel portrait;
    private final JPanel namePill;
    private final JLabel nameLabel;
    private final JLabel clinicLabel;
    private final JLabel aboutText;
    private final JLabel feeLabel;
    private final JPanel expandedView;
    private final JPanel collapsedView;
    private boolean collapsed = true;
    private Doctor doctor;
    private LocalDate appointmentDate = LocalDate.now();
    private JButton dateButton;
    private JComboBox<String> timeCombo;
    private JLabel patientsValue;
    private JLabel surgeryValue;
    private JLabel experienceValue;
    private JLabel ratingValue;

    public DoctorProfilePanel(RecordManager manager, Runnable onChange) {
        this("Sheikh", manager, onChange);
    }

    public DoctorProfilePanel(String patientName, RecordManager manager, Runnable onChange) {
        this.manager = manager;
        this.onChange = onChange;
        this.patientName = patientName == null || patientName.trim().isEmpty()
                ? "Sheikh" : patientName.trim();
        this.doctor = new Doctor("Chanace Linnsay Berdin", "Cardiologist, Bacolod City",
                "Cardiology", "09:00 - 17:00", "Available Now", "CL",
                "Compassionate heart specialist focused on preventive care, accurate diagnosis, and steady recovery for every patient.",
                1500, 410, 120, 7, 4.8, UiTheme.SAGE_LIGHT, "graphics/Chanace.jpg");

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(56, 0));

        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout());

        JButton minimizeButton = UiTheme.button("›", UiTheme.SAGE_LIGHT, UiTheme.DEEP_OLIVE);
        minimizeButton.setPreferredSize(new Dimension(30, 30));
        minimizeButton.setToolTipText("Hide doctor details");
        minimizeButton.addActionListener(e -> setCollapsed(true));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 18, 0, 18));
        header.add(minimizeButton);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);

        portrait = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                DoctorArt.paintPortrait(g, getWidth(), getHeight(), doctor);
                g.dispose();
            }

            @Override
            public void doLayout() {
                int width = Math.min(getWidth() - 40, 240);
                namePill.setBounds((getWidth() - width) / 2, getHeight() - 46, width, 52);
            }
        };
        portrait.setOpaque(false);
        portrait.setPreferredSize(new Dimension(298, 220));
        namePill = new UiTheme.RoundedPanel(UiTheme.SAGE_LIGHT, 24);
        namePill.setLayout(new BorderLayout());
        namePill.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        nameLabel = UiTheme.label(doctor.getName(), 15, Font.BOLD, UiTheme.CHARCOAL);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        clinicLabel = UiTheme.label(doctor.getClinic(), 10, Font.PLAIN, UiTheme.MUTED_TEXT);
        clinicLabel.setHorizontalAlignment(JLabel.CENTER);
        namePill.add(nameLabel, BorderLayout.NORTH);
        namePill.add(clinicLabel, BorderLayout.SOUTH);
        portrait.add(namePill);
        top.add(portrait, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        JPanel bio = new JPanel();
        bio.setLayout(new BoxLayout(bio, BoxLayout.Y_AXIS));
        bio.setOpaque(false);
        bio.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 10));

        JLabel aboutHeading = UiTheme.heading("About Doctor:", 15);
        aboutHeading.setHorizontalAlignment(SwingConstants.CENTER);
        aboutHeading.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        bio.add(aboutHeading);
        bio.add(Box.createVerticalStrut(8));
        aboutText = new JLabel("<html><div style='width:200px;text-align:center;line-height:1.35'>"
                + doctor.getAbout() + "</div></html>");
        aboutText.setFont(UiTheme.font(12));
        aboutText.setForeground(UiTheme.MUTED_TEXT);
        aboutText.setHorizontalAlignment(SwingConstants.CENTER);
        aboutText.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        bio.add(aboutText);
        bio.add(Box.createVerticalStrut(18));

        JLabel scheduleTitle = UiTheme.label("Select Your Date & Time", 12,
                Font.BOLD, UiTheme.CHARCOAL);
        scheduleTitle.setHorizontalAlignment(SwingConstants.CENTER);
        scheduleTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        bio.add(scheduleTitle);
        bio.add(Box.createVerticalStrut(8));

        JPanel schedule = new JPanel(new GridLayout(2, 1, 0, 8));
        schedule.setOpaque(false);
        dateButton = new JButton(DATE_FORMAT.format(appointmentDate));
        dateButton.setFont(UiTheme.font(12, Font.BOLD));
        dateButton.setForeground(UiTheme.CHARCOAL);
        dateButton.setHorizontalAlignment(SwingConstants.CENTER);
        dateButton.setFocusPainted(false);
        dateButton.setBorderPainted(false);
        dateButton.setContentAreaFilled(false);
        dateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateButton.setPreferredSize(new Dimension(104, 30));
        dateButton.addActionListener(e -> showDatePicker());
        schedule.add(new TriggerCombo(TcIcons.Kind.CALENDAR, dateButton));

        timeCombo = UiTheme.combo(new String[]{"Select a time"});
        updateTimeChoices();
        timeCombo.setFont(UiTheme.font(12, Font.BOLD));
        timeCombo.setForeground(UiTheme.CHARCOAL);
        timeCombo.setBorder(BorderFactory.createEmptyBorder());
        timeCombo.setOpaque(false);
        timeCombo.setPreferredSize(new Dimension(104, 30));
        timeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list,
                                                          Object value, int index,
                                                          boolean selected,
                                                          boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, selected, hasFocus);
                setFont(UiTheme.font(12, Font.BOLD));
                setForeground(UiTheme.CHARCOAL);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
                return this;
            }
        });
        schedule.add(new TriggerCombo(TcIcons.Kind.CLOCK, timeCombo));
        schedule.setMaximumSize(new Dimension(292, 96));
        schedule.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        bio.add(schedule);
        bio.add(Box.createVerticalStrut(16));

        JPanel location = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        location.setOpaque(false);
        location.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JPanel pinBadge = new UiTheme.RoundedPanel(UiTheme.SAGE_LIGHT, 14);
        pinBadge.setLayout(new BorderLayout());
        pinBadge.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        pinBadge.add(new TcIconLabel(TcIcons.Kind.PIN, 18, UiTheme.DEEP_OLIVE),
                BorderLayout.CENTER);
        pinBadge.setPreferredSize(new Dimension(34, 34));
        location.add(pinBadge);
        location.add(UiTheme.label("Bacolod City", 12, Font.BOLD, UiTheme.CHARCOAL));
        bio.add(location);
        bio.add(Box.createVerticalStrut(16));

        JPanel stats = new JPanel(new GridLayout(1, 4, 6, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(292, 62));
        stats.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        StatBox patients = new StatBox("410", "Patients", UiTheme.SAGE);
        StatBox surgeries = new StatBox("120", "Surgery", UiTheme.BUTTER_DEEP);
        StatBox experience = new StatBox("7ys", "Exp.", UiTheme.SAGE);
        StatBox ratings = new StatBox("4.8", "Ratings", UiTheme.SAGE);
        patientsValue = patients.value;
        surgeryValue = surgeries.value;
        experienceValue = experience.value;
        ratingValue = ratings.value;
        stats.add(patients);
        stats.add(surgeries);
        stats.add(experience);
        stats.add(ratings);
        bio.add(stats);
        bio.add(Box.createVerticalStrut(18));

        JPanel booking = new JPanel(new BorderLayout(0, 10));
        booking.setOpaque(false);
        booking.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        feeLabel = UiTheme.label("₱ 1,500", 20, Font.BOLD, UiTheme.CHARCOAL);
        booking.add(feeLabel, BorderLayout.WEST);
        booking.add(UiTheme.label("fee", 10, Font.PLAIN, UiTheme.MUTED_TEXT),
                BorderLayout.EAST);
        JButton book = UiTheme.button("Book Appointment", UiTheme.DEEP_OLIVE, Color.WHITE);
        book.addActionListener(e -> bookAppointment());
        booking.add(book, BorderLayout.SOUTH);
        bio.add(booking);

        JScrollPane bioScroll = new JScrollPane(bio);
        UiTheme.styleScrollPane(bioScroll);
        bioScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bioScroll.setPreferredSize(new Dimension(220, 300));
        card.add(bioScroll, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(330, 0));
        card.setMinimumSize(new Dimension(300, 0));
        expandedView = card;

        JPanel collapsed = new UiTheme.RoundedPanel(UiTheme.OLIVE, 18);
        collapsed.setLayout(new BorderLayout());
        collapsed.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));
        JButton expand = UiTheme.button("‹", UiTheme.SAGE_LIGHT, UiTheme.DEEP_OLIVE);
        expand.setPreferredSize(new Dimension(36, 36));
        expand.setToolTipText("Show doctor details");
        expand.addActionListener(e -> setCollapsed(false));
        collapsed.add(expand, BorderLayout.NORTH);
        collapsedView = collapsed;

        add(collapsedView, BorderLayout.CENTER);
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        nameLabel.setText(doctor.getName());
        clinicLabel.setText(doctor.getClinic());
        aboutText.setText("<html><div style='width:200px;text-align:center;line-height:1.35'>"
                + doctor.getAbout() + "</div></html>");
        feeLabel.setText(String.format("₱ %,d", (int) doctor.getFee()));
        patientsValue.setText(String.format("%,d", doctor.getPatients()));
        surgeryValue.setText(String.format("%,d", doctor.getSurgeries()));
        experienceValue.setText(doctor.getExperienceYears() + "ys");
        ratingValue.setText(String.format("%.1f", doctor.getRating()));
        updateTimeChoices();
        portrait.repaint();
        setCollapsed(false);
        revalidate();
        repaint();
    }

    @SuppressWarnings("unchecked")
    private void updateTimeChoices() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) timeCombo.getModel();
        model.removeAllElements();
        for (String slot : availabilitySlots()) {
            model.addElement(slot);
        }
        timeCombo.setSelectedIndex(0);
    }

    private String[] availabilitySlots() {
        if (doctor == null
                || doctor.getStatus().toLowerCase(Locale.US).contains("unavailable")) {
            return new String[]{"Unavailable"};
        }
        try {
            String[] parts = doctor.getHours().split("-");
            DateTimeFormatter parse = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime start = LocalTime.parse(parts[0].trim(), parse);
            LocalTime end = LocalTime.parse(parts[1].trim(), parse);
            List<String> slots = new ArrayList<String>();
            LocalTime cursor = start;
            while (cursor.isBefore(end)) {
                slots.add(cursor.format(TIME_FORMAT));
                cursor = cursor.plusMinutes(30);
            }
            if (!slots.isEmpty()) {
                return slots.toArray(new String[0]);
            }
        } catch (RuntimeException ignored) {
        }
        return new String[]{"11:30 AM", "12:00 PM", "01:30 PM"};
    }

    private void setCollapsed(boolean value) {
        if (collapsed == value) {
            return;
        }
        collapsed = value;
        removeAll();
        add(collapsed ? collapsedView : expandedView, BorderLayout.CENTER);
        setPreferredSize(new Dimension(collapsed ? 56 : 330, 0));
        revalidate();
        repaint();
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }

    public void refresh() {
        revalidate();
        repaint();
    }

    private void showDatePicker() {
        JPopupMenu popup = new JPopupMenu();
        DatePicker picker = new DatePicker(appointmentDate, date -> {
            appointmentDate = date;
            dateButton.setText(DATE_FORMAT.format(date));
            popup.setVisible(false);
        });
        popup.add(picker);
        Dimension size = picker.getPreferredSize();
        popup.show(dateButton, dateButton.getWidth() - size.width, dateButton.getHeight());
    }

    private void bookAppointment() {
        String id = nextId();
        try {
            BaseRecord record = manager.buildRecord("Consultation", id, patientName,
                    String.format("%.2f", doctor.getFee()), "Active", doctor.getName());
            manager.addRecord(record);
            new MessageDialog(SwingUtilities.getWindowAncestor(this), "Booking confirmed",
                    "Appointment " + id + " booked with " + doctor.getName()
                            + " on " + DATE_FORMAT.format(appointmentDate)
                            + " at " + timeCombo.getSelectedItem() + ".").setVisible(true);
            onChange.run();
        } catch (InvalidRecordException ex) {
            new MessageDialog(SwingUtilities.getWindowAncestor(this), "Booking error",
                    ex.getMessage()).setVisible(true);
        }
    }

    private String nextId() {
        for (int i = 1; i < 10000; i++) {
            String candidate = String.format("MORPHO-%03d", i);
            try {
                manager.getRecordById(candidate);
            } catch (InvalidRecordException ex) {
                return candidate;
            }
        }
        return "MORPHO-" + System.currentTimeMillis();
    }

    private static class TriggerCombo extends JPanel {

        TriggerCombo(TcIcons.Kind icon, Component control) {
            setLayout(new BorderLayout(6, 0));
            setOpaque(false);
            setBackground(UiTheme.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.LINE, 1, true),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            setPreferredSize(new Dimension(0, 44));
            add(new TcIconLabel(icon, 17, UiTheme.DEEP_OLIVE), BorderLayout.WEST);
            add(control, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class DatePicker extends JPanel {

        private static final DateTimeFormatter MONTH_FORMAT =
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

        private final Consumer<LocalDate> onSelect;
        private final JLabel title;
        private final JPanel days;
        private YearMonth month;
        private LocalDate selected;

        DatePicker(LocalDate selected, Consumer<LocalDate> onSelect) {
            this.selected = selected;
            this.month = YearMonth.from(selected);
            this.onSelect = onSelect;

            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setBackground(UiTheme.CREAM);
            setOpaque(true);
            setPreferredSize(new Dimension(224, 250));

            JButton previous = navButton("‹");
            previous.addActionListener(e -> {
                month = month.minusMonths(1);
                renderDays();
            });
            JButton next = navButton("›");
            next.addActionListener(e -> {
                month = month.plusMonths(1);
                renderDays();
            });

            title = UiTheme.label("", 12, Font.BOLD, UiTheme.DEEP_OLIVE);
            title.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(previous, BorderLayout.WEST);
            header.add(title, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            JPanel weekdays = new JPanel(new GridLayout(1, 7, 2, 0));
            weekdays.setOpaque(false);
            for (String day : new String[]{"S", "M", "T", "W", "T", "F", "S"}) {
                JLabel label = UiTheme.label(day, 10, Font.BOLD, UiTheme.MUTED_TEXT);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                weekdays.add(label);
            }

            days = new JPanel(new GridLayout(0, 7, 2, 2));
            days.setOpaque(false);

            JPanel grid = new JPanel(new BorderLayout(0, 2));
            grid.setOpaque(false);
            grid.add(weekdays, BorderLayout.NORTH);
            grid.add(days, BorderLayout.CENTER);
            add(grid, BorderLayout.CENTER);

            renderDays();
        }

        private JButton navButton(String text) {
            JButton button = new JButton(text);
            button.setFont(UiTheme.font(16, Font.BOLD));
            button.setForeground(UiTheme.DEEP_OLIVE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(28, 26));
            return button;
        }

        private void renderDays() {
            title.setText(month.format(MONTH_FORMAT));
            days.removeAll();

            int leading = month.atDay(1).getDayOfWeek().getValue() % 7;
            for (int i = 0; i < leading; i++) {
                days.add(new JLabel());
            }

            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate date = month.atDay(day);
                JButton button = new JButton(String.valueOf(day));
                button.setFont(UiTheme.font(11, Font.BOLD));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setBackground(date.equals(selected) ? UiTheme.DEEP_OLIVE : UiTheme.SAGE);
                button.setForeground(date.equals(selected) ? Color.WHITE : UiTheme.CHARCOAL);
                button.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
                button.setPreferredSize(new Dimension(28, 28));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.addActionListener(e -> onSelect.accept(date));
                days.add(button);
            }

            days.revalidate();
            days.repaint();
        }
    }

    private static class StatBox extends UiTheme.RoundedPanel {

        final JLabel value;

        StatBox(String valueText, String caption, Color background) {
            super(background, 12);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(7, 3, 7, 3));
            value = UiTheme.label(valueText, 13, Font.BOLD, UiTheme.CHARCOAL);
            value.setHorizontalAlignment(JLabel.CENTER);
            JLabel text = UiTheme.label(caption, 9, Font.BOLD, UiTheme.MUTED_TEXT);
            text.setHorizontalAlignment(JLabel.CENTER);
            add(value, BorderLayout.NORTH);
            add(text, BorderLayout.SOUTH);
        }
    }
}
