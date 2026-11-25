import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class RecordFormPanel extends JPanel {

    private final RecordManager manager;
    private final RecordTablePanel tablePanel;
    private final Runnable onChange;

    private final JTextField idField = UiTheme.field();
    private final JTextField nameField = UiTheme.field();
    private final JTextField rateField = UiTheme.field();
    private final JTextField extraField = UiTheme.field();
    private final JComboBox<String> typeCombo;
    private final JComboBox<String> statusCombo;
    private final JLabel extraLabel = UiTheme.caption("Doctor Name");
    private final JLabel messageLabel = UiTheme.caption("Ready to record a new appointment.");

    private String selectedId;
    private boolean loading;

    public RecordFormPanel(RecordManager manager, RecordTablePanel tablePanel, Runnable onChange) {
        this.manager = manager;
        this.tablePanel = tablePanel;
        this.onChange = onChange;
        this.typeCombo = UiTheme.combo(new String[]{"Consultation", "Lab Test"});
        this.statusCombo = UiTheme.combo(new String[]{"Active", "Completed", "Cancelled"});

        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(330, 0));

        JPanel card = UiTheme.card();
        card.setLayout(new BorderLayout(0, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UiTheme.label("Appointment Console", 18, java.awt.Font.BOLD, UiTheme.TEXT),
                BorderLayout.NORTH);
        header.add(UiTheme.caption("Create, update, and retire patient appointments"),
                BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 4, 0);
        c.fill = GridBagConstraints.NONE;

        addRow(fields, c, 0, UiTheme.caption("Appointment ID"), idField);
        addRow(fields, c, 1, UiTheme.caption("Patient Name"), nameField);
        addRow(fields, c, 2, UiTheme.caption("Appointment Type"), typeCombo);
        addRow(fields, c, 3, UiTheme.caption("Base Fee"), rateField);
        addRow(fields, c, 4, UiTheme.caption("Status"), statusCombo);
        addRow(fields, c, 5, extraLabel, extraField);
        card.add(fields, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 10));
        footer.setOpaque(false);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        JButton addButton = UiTheme.button("Add", UiTheme.CYAN, UiTheme.BG);
        JButton updateButton = UiTheme.button("Update", UiTheme.VIOLET, UiTheme.TEXT);
        JButton deleteButton = UiTheme.button("Delete", UiTheme.RED, UiTheme.BG);
        JButton clearButton = UiTheme.button("Clear", UiTheme.SURFACE_2, UiTheme.TEXT);
        addButton.addActionListener(e -> addRecord());
        updateButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());
        clearButton.addActionListener(e -> clearForm());
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        footer.add(buttons, BorderLayout.CENTER);

        JPanel messageCard = new UiTheme.RoundedPanel(UiTheme.SURFACE_2, 12);
        messageCard.add(messageLabel, BorderLayout.CENTER);
        footer.add(messageCard, BorderLayout.SOUTH);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        typeCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.LINE, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        statusCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.LINE, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        typeCombo.addActionListener(e -> extraLabel.setText(
                "Consultation".equals(typeCombo.getSelectedItem()) ? "Doctor Name" : "Test Type"));

        tablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || loading) {
                return;
            }
            String id = tablePanel.getSelectedId();
            if (id == null) {
                return;
            }
            try {
                loadRecord(manager.getRecordById(id));
            } catch (InvalidRecordException ignored) {
                setMessage("Selected appointment is no longer available.", UiTheme.AMBER);
            }
        });
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row,
                        JComponent label, JComponent field) {
        c.gridy = row;
        c.gridwidth = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 4, 8);
        panel.add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 6, 0);
        panel.add(field, c);
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
    }

    private void loadRecord(BaseRecord record) {
        loading = true;
        selectedId = record.getRecordId();
        idField.setText(record.getRecordId());
        idField.setEditable(false);
        nameField.setText(record.getName());
        rateField.setText(String.format("%.2f", record.getBaseRate()));
        statusCombo.setSelectedItem(record.getStatus());

        if (record instanceof ConsultationAppointment) {
            typeCombo.setSelectedItem("Consultation");
            extraLabel.setText("Doctor Name");
            extraField.setText(((ConsultationAppointment) record).getDoctorName());
        } else {
            typeCombo.setSelectedItem("Lab Test");
            extraLabel.setText("Test Type");
            extraField.setText(((LabTestAppointment) record).getTestType());
        }
        setMessage("Editing " + selectedId + ". Update or delete when ready.", UiTheme.CYAN);
        loading = false;
    }

    private BaseRecord buildFromForm() throws InvalidRecordException {
        return manager.buildRecord(
                (String) typeCombo.getSelectedItem(),
                idField.getText(),
                nameField.getText(),
                rateField.getText(),
                (String) statusCombo.getSelectedItem(),
                extraField.getText());
    }

    private void addRecord() {
        try {
            BaseRecord record = buildFromForm();
            manager.addRecord(record);
            clearForm();
            setMessage("Appointment " + record.getRecordId() + " added successfully.", UiTheme.GREEN);
            onChange.run();
        } catch (InvalidRecordException ex) {
            setMessage(ex.getMessage(), UiTheme.RED);
        }
    }

    private void updateRecord() {
        if (selectedId == null) {
            setMessage("Select an appointment from the table first.", UiTheme.AMBER);
            return;
        }
        try {
            BaseRecord updated = buildFromForm();
            manager.updateRecord(updated);
            clearForm();
            setMessage("Appointment " + updated.getRecordId() + " updated.", UiTheme.GREEN);
            onChange.run();
        } catch (InvalidRecordException ex) {
            setMessage(ex.getMessage(), UiTheme.RED);
        }
    }

    private void deleteRecord() {
        if (selectedId == null) {
            setMessage("Select an appointment from the table first.", UiTheme.AMBER);
            return;
        }
        String target = selectedId;
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete appointment " + target + "?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            manager.deleteRecord(target);
            clearForm();
            setMessage("Appointment " + target + " deleted.", UiTheme.GREEN);
            onChange.run();
        } catch (InvalidRecordException ex) {
            setMessage(ex.getMessage(), UiTheme.RED);
        }
    }

    private void clearForm() {
        loading = true;
        selectedId = null;
        idField.setText("");
        idField.setEditable(true);
        nameField.setText("");
        rateField.setText("");
        extraField.setText("");
        typeCombo.setSelectedItem("Consultation");
        statusCombo.setSelectedItem("Active");
        tablePanel.clearSelection();
        setMessage("Ready to record a new appointment.", UiTheme.MUTED);
        loading = false;
    }

    private void setMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }
}
