import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DashboardPanel extends JPanel {

    private static BufferedImage knowMoreArt;

    private final Consumer<Doctor> onSelect;
    private final String username;
    private final JTextField searchField = UiTheme.placeholderField("Search Your Doctor");
    private final JPanel doctorList = new JPanel();
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<SpecialtyCard> specialtyCards = new ArrayList<>();
    private final Set<String> favorites = new LinkedHashSet<>();
    private final JLabel filterLabel = UiTheme.caption("All nearby specialists");
    private final JPanel topPanel;
    private final JPanel doctorSection;
    private JLabel sectionTitle;
    private JLabel seeAllLabel;
    private Doctor selected;
    private String activeSpecialty = "All";
    private boolean focusMode;

    public DashboardPanel(Consumer<Doctor> onSelect) {
        this("Sheikh", onSelect);
    }

    public DashboardPanel(String username, Consumer<Doctor> onSelect) {
        this.onSelect = onSelect;
        this.username = username == null || username.trim().isEmpty()
                ? "Sheikh" : username.trim();
        buildDoctors();
        selected = doctors.get(1);

        setLayout(new BorderLayout(0, 16));
        setBackground(UiTheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));

        topPanel = buildTop();
        doctorSection = buildDoctorSection();
        add(topPanel, BorderLayout.NORTH);
        add(doctorSection, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                renderDoctors();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                renderDoctors();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                renderDoctors();
            }
        });
    }

    private void buildDoctors() {
        doctors.add(new Doctor("Chanace Linnsay Berdin", "Cardiologist, Bacolod City",
                 "Cardiology", "09:00 - 17:00", "Available Now", "CL",
                 "Cardiologist focused on preventive heart care and minimally invasive procedures.",
                 1500, 620, 210, 12, 4.8, new Color(169, 195, 158), "graphics/Chanace.jpg"));
        doctors.add(new Doctor("Angel Mae P. Jacob", "Neurologist, Bacolod City",
                 "Neurology", "08:30 - 16:30", "Available Now", "AJ",
                 "Neurologist treating complex conditions with modern diagnostic technology.",
                 1800, 350, 95, 10, 4.7, new Color(198, 218, 232), "graphics/Angel.jpg", 0.07));
        doctors.add(new Doctor("Irish Guañizo", "Gastroenterologist, Bacolod City",
                 "Gastroenterology", "10:00 - 18:00", "Available Now", "IG",
                 "Digestive health specialist with a calm, patient-first approach to treatment.",
                  1200, 480, 140, 8, 4.9, new Color(255, 241, 194), "graphics/Irish.jpg"));
        doctors.add(new Doctor("Kurt Andrie Rillo", "General Practitioner, Bacolod City",
                  "General Checkup", "08:00 - 17:00", "Available Now", "KR",
                  "General practitioner providing routine checkups, preventive care, and referrals.",
                  900, 780, 40, 9, 4.9, new Color(226, 211, 237), "graphics/Kurt.jpg", 0.12));
        doctors.add(new Doctor("Alyssah Faith Bermejo", "Pediatrician, Bacolod City",
                  "Pediatrics", "09:00 - 18:00", "Available Now", "AF",
                  "Pediatrician focused on child wellness, growth monitoring, and family guidance.",
                  1100, 640, 25, 11, 4.8, new Color(248, 215, 224), "graphics/Alyssah.jpg"));
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        SearchBar searchBar = new SearchBar();
        searchBar.setAlignmentX(LEFT_ALIGNMENT);
        top.add(searchBar);
        top.add(Box.createVerticalStrut(8));

        JPanel greeting = new JPanel();
        greeting.setLayout(new BoxLayout(greeting, BoxLayout.Y_AXIS));
        greeting.setOpaque(false);
        greeting.setAlignmentX(LEFT_ALIGNMENT);
        JLabel hello = UiTheme.heading("Hello " + username + "!!", 26);
        hello.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitle = UiTheme.caption("Your health, our priority. Find the right doctor today.");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        greeting.add(hello);
        greeting.add(Box.createVerticalStrut(4));
        greeting.add(subtitle);
        top.add(greeting);
        top.add(Box.createVerticalStrut(8));

        JPanel features = new JPanel(new GridLayout(1, 2, 18, 18));
        features.setOpaque(false);
        features.setAlignmentX(LEFT_ALIGNMENT);
        features.add(buildPromo());
        features.add(buildSpecialties());
        features.setMaximumSize(new Dimension(Integer.MAX_VALUE, features.getPreferredSize().height));
        top.add(features);
        return top;
    }

    private static BufferedImage loadKnowMoreArt() {
        if (knowMoreArt == null) {
            try {
                knowMoreArt = ImageIO.read(new File("graphics/Know More.png"));
            } catch (IOException ignored) {
                knowMoreArt = null;
            }
        }
        return knowMoreArt;
    }

    private JPanel buildPromo() {
        JPanel promo = UiTheme.gradient(new Color(216, 233, 207), UiTheme.BUTTER_DEEP, 22);
        promo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel copy = new JPanel();
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.setOpaque(false);
        JLabel lineOne = UiTheme.heading("Early protection", 21);
        JLabel lineTwo = UiTheme.heading("for your family", 21);
        lineOne.setAlignmentX(LEFT_ALIGNMENT);
        lineTwo.setAlignmentX(LEFT_ALIGNMENT);
        copy.add(lineOne);
        copy.add(lineTwo);
        copy.add(Box.createVerticalStrut(10));
        JButton knowMore = UiTheme.button("Know More", UiTheme.CHARCOAL, Color.WHITE);
        knowMore.setAlignmentX(LEFT_ALIGNMENT);
        knowMore.addActionListener(e -> new AboutDialog(
                SwingUtilities.getWindowAncestor(this)).setVisible(true));
        copy.add(knowMore);

        JPanel art = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                BufferedImage image = loadKnowMoreArt();
                if (image == null) {
                    DoctorArt.paintPromoDoctor(g, getWidth(), getHeight());
                } else {
                    double scale = Math.min((double) getWidth() / image.getWidth(),
                            (double) getHeight() / image.getHeight());
                    int width = (int) Math.ceil(image.getWidth() * scale);
                    int height = (int) Math.ceil(image.getHeight() * scale);
                    g.drawImage(image, (getWidth() - width) / 2,
                            (getHeight() - height) / 2, width, height, null);
                }
                g.dispose();
            }
        };
        art.setOpaque(false);
        art.setPreferredSize(new Dimension(170, 108));

        promo.add(copy, BorderLayout.CENTER);
        promo.add(art, BorderLayout.EAST);
        return promo;
    }

    private JPanel buildSpecialties() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        addSpecialtyCard(grid, createSpecialtyCard("Cardiology", "graphics/Heart.svg"),
                constraints, 0, 0, 1);
        addSpecialtyCard(grid, createSpecialtyCard("Neurology", "graphics/Brain.svg"),
                constraints, 1, 0, 1);
        addSpecialtyCard(grid, createSpecialtyCard("Gastroenterology",
                "graphics/Stomach.svg", 28, 0), constraints, 2, 0, 1);
        addSpecialtyCard(grid, createSpecialtyCard("General Checkup",
                "graphics/stethoscope.svg"), constraints, 0, 1, 1);
        addSpecialtyCard(grid, createSpecialtyCard("Pediatrics", "graphics/Pedia.svg"),
                constraints, 1, 1, 1);
        addSpecialtyCard(grid, createSpecialtyCard("Bookmark", TcIcons.Kind.STAR),
                constraints, 2, 1, 1);
        return grid;
    }

    private void addSpecialtyCard(JPanel grid, SpecialtyCard card,
                                  GridBagConstraints constraints,
                                  int x, int y, int width) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        grid.add(card, constraints);
    }

    private SpecialtyCard createSpecialtyCard(String title, TcIcons.Kind kind) {
        SpecialtyCard card = new SpecialtyCard(title, kind);
        card.setActive(title.equals(activeSpecialty));
        specialtyCards.add(card);
        return card;
    }

    private SpecialtyCard createSpecialtyCard(String title, String svgPath) {
        SpecialtyCard card = new SpecialtyCard(title, svgPath);
        card.setActive(title.equals(activeSpecialty));
        specialtyCards.add(card);
        return card;
    }

    private SpecialtyCard createSpecialtyCard(String title, String svgPath,
                                              int iconSize, int labelGap) {
        SpecialtyCard card = new SpecialtyCard(title, svgPath, iconSize, labelGap);
        card.setActive(title.equals(activeSpecialty));
        specialtyCards.add(card);
        return card;
    }

    private void selectSpecialty(String title) {
        setFocusMode(false);
        activeSpecialty = title.equals(activeSpecialty) ? "All" : title;
        for (SpecialtyCard card : specialtyCards) {
            card.setActive(card.getTitle().equals(activeSpecialty));
        }
        renderDoctors();
        if (!"All".equals(activeSpecialty) && !"Bookmark".equals(activeSpecialty)) {
            for (Doctor doctor : doctors) {
                if (activeSpecialty.equals(doctor.getSpecialty())) {
                    selectDoctor(doctor);
                    break;
                }
            }
        }
    }

    private void toggleFocusMode() {
        activeSpecialty = "All";
        for (SpecialtyCard card : specialtyCards) {
            card.setActive(false);
        }
        setFocusMode(!focusMode);
        renderDoctors();
    }

    private void setFocusMode(boolean focused) {
        if (focusMode == focused) {
            return;
        }
        focusMode = focused;
        topPanel.setVisible(!focused);
        sectionTitle.setText(focused ? "All Doctors" : "Nearby Doctors");
        seeAllLabel.setText(focused ? "Back to overview" : "See all");
        revalidate();
        repaint();
    }

    private JPanel buildDoctorSection() {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        sectionTitle = UiTheme.heading("Nearby Doctors", 21);
        heading.add(sectionTitle, BorderLayout.WEST);
        seeAllLabel = UiTheme.label("See all", 12, Font.BOLD, UiTheme.DEEP_OLIVE);
        seeAllLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        seeAllLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleFocusMode();
            }
        });
        heading.add(seeAllLabel, BorderLayout.EAST);

        JPanel headingWrap = new JPanel(new BorderLayout(0, 4));
        headingWrap.setOpaque(false);
        headingWrap.add(heading, BorderLayout.NORTH);
        headingWrap.add(filterLabel, BorderLayout.SOUTH);
        section.add(headingWrap, BorderLayout.NORTH);

        doctorList.setLayout(new BoxLayout(doctorList, BoxLayout.Y_AXIS));
        doctorList.setOpaque(false);
        renderDoctors();

        JScrollPane scroll = new JScrollPane(doctorList);
        UiTheme.styleScrollPane(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(0, 300));
        scroll.setMinimumSize(new Dimension(0, 200));
        section.add(scroll, BorderLayout.CENTER);
        return section;
    }

    private void renderDoctors() {
        doctorList.removeAll();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        boolean bookmarks = !focusMode && "Bookmark".equals(activeSpecialty);
        sectionTitle.setText(focusMode ? "All Doctors"
                : (bookmarks ? "Bookmarked Doctors" : "Nearby Doctors"));
        filterLabel.setText(focusMode
                ? "All specialists in Bacolod City"
                : (bookmarks ? "Saved specialists"
                    : (("All".equals(activeSpecialty) || "Bookmark".equals(activeSpecialty))
                        ? "All nearby specialists"
                        : activeSpecialty + " specialists nearby")));
        boolean found = false;
        for (Doctor doctor : doctors) {
            if (bookmarks) {
                if (!favorites.contains(doctor.getName())) {
                    continue;
                }
            } else if (!"All".equals(activeSpecialty)
                    && !"Bookmark".equals(activeSpecialty)
                    && !activeSpecialty.equalsIgnoreCase(doctor.getSpecialty())) {
                continue;
            }
            String haystack = (doctor.getName() + " " + doctor.getClinic() + " "
                    + doctor.getSpecialty()).toLowerCase();
            if (!query.isEmpty() && !haystack.contains(query)) {
                continue;
            }
            if (found) {
                doctorList.add(Box.createVerticalStrut(10));
            }
            found = true;
            DoctorCard card = new DoctorCard(doctor, doctor == selected, focusMode);
            card.setAlignmentX(LEFT_ALIGNMENT);
            doctorList.add(card);
        }
        if (!found) {
            JLabel empty = UiTheme.caption(bookmarks
                    ? "No bookmarked doctors yet. Tap a star on a doctor to save them here."
                    : "No doctors match this view. Try another specialty or search.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            doctorList.add(empty);
        }
        doctorList.revalidate();
        doctorList.repaint();
    }

    private void selectDoctor(Doctor doctor) {
        selected = doctor;
        renderDoctors();
        if (onSelect != null) {
            onSelect.accept(doctor);
        }
    }

    public void refresh() {
        renderDoctors();
    }

    private class SearchBar extends JPanel {

        private final JButton searchButton = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, UiTheme.SAGE_LIGHT,
                        getWidth(), getHeight(), UiTheme.DEEP_OLIVE));
                g.fill(new RoundRectangle2D.Float(1, 3, getWidth() - 4, getHeight() - 6, 18, 18));
                TcIcons.paint(g, TcIcons.Kind.SEARCH, 8, 7, 22, Color.WHITE, false);
                g.dispose();
            }
        };

        SearchBar() {
            setLayout(null);
            setOpaque(false);
            setPreferredSize(new Dimension(400, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            searchField.setPreferredSize(new Dimension(340, 36));
            add(searchField);
            searchButton.setFocusPainted(false);
            searchButton.setBorderPainted(false);
            searchButton.setContentAreaFilled(false);
            searchButton.setOpaque(false);
            searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            searchButton.addActionListener(e -> searchField.requestFocusInWindow());
            add(searchButton);
        }

        @Override
        public void doLayout() {
            searchField.setBounds(0, 0, Math.max(120, getWidth() - 52), getHeight());
            searchButton.setBounds(getWidth() - 46, 0, 40, 40);
        }
    }

    private class SpecialtyCard extends UiTheme.RoundedPanel {

        private final String title;
        private final Component icon;
        private final Consumer<Color> iconColor;
        private final JLabel label;
        private boolean active;
        private boolean hover;

        SpecialtyCard(String title, TcIcons.Kind kind) {
            this(title, kind, null, 20, 2);
        }

        SpecialtyCard(String title, String svgPath) {
            this(title, null, svgPath, 20, 2);
        }

        SpecialtyCard(String title, String svgPath, int iconSize, int labelGap) {
            this(title, null, svgPath, iconSize, labelGap);
        }

        private SpecialtyCard(String title, TcIcons.Kind kind, String svgPath,
                              int iconSize, int labelGap) {
            super(UiTheme.SAGE, 18);
            this.title = title;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            setPreferredSize(new Dimension(100, 58));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel host = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            host.setOpaque(false);
            host.setAlignmentX(CENTER_ALIGNMENT);
            host.setMaximumSize(new Dimension(iconSize + 4, iconSize + 4));
            if (svgPath != null) {
                SvgIcon svg = new SvgIcon(svgPath, iconSize);
                svg.setColor(UiTheme.CHARCOAL);
                JLabel svgLabel = new JLabel(svg);
                host.add(svgLabel);
                icon = svgLabel;
                iconColor = svg::setColor;
            } else {
                TcIconLabel tcIcon = new TcIconLabel(kind, iconSize, UiTheme.CHARCOAL);
                host.add(tcIcon);
                icon = tcIcon;
                iconColor = tcIcon::setColor;
            }
            add(Box.createVerticalGlue());
            add(host);
            add(Box.createVerticalStrut(labelGap));

            label = UiTheme.label(title, 12, Font.BOLD, UiTheme.CHARCOAL);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setAlignmentX(CENTER_ALIGNMENT);
            label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            add(label);
            add(Box.createVerticalGlue());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectSpecialty(title);
                }
            });
        }

        String getTitle() {
            return title;
        }

        void setActive(boolean active) {
            this.active = active;
            iconColor.accept(active ? UiTheme.DEEP_OLIVE : UiTheme.CHARCOAL);
            icon.repaint();
            label.setForeground(active ? UiTheme.DEEP_OLIVE : UiTheme.CHARCOAL);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            setBackground(active ? UiTheme.BUTTER_DEEP
                    : (hover ? UiTheme.SAGE_LIGHT : UiTheme.SAGE));
            super.paintComponent(graphics);
        }
    }

    private class DoctorCard extends UiTheme.RoundedPanel {

        private final Doctor doctor;
        private final boolean active;
        private boolean favorite;

        DoctorCard(Doctor doctor, boolean active, boolean expanded) {
            super(active ? UiTheme.DEEP_OLIVE : Color.WHITE, 18);
            this.doctor = doctor;
            this.active = active;
            this.favorite = favorites.contains(doctor.getName());
            int width = expanded ? 800 : 760;
            int height = expanded ? 124 : 108;
            int avatarSize = expanded ? 64 : 58;
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(expanded ? 16 : 12,
                    expanded ? 20 : 16, expanded ? 16 : 12, expanded ? 20 : 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
            setPreferredSize(new Dimension(width, height));

            Color primary = active ? Color.WHITE : UiTheme.CHARCOAL;
            Color secondary = active ? UiTheme.SAGE_LIGHT : UiTheme.MUTED_TEXT;
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = 0;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.fill = GridBagConstraints.NONE;
            constraints.insets = new Insets(0, 6, 0, 6);

            JPanel avatar = new JPanel() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    DoctorArt.paintAvatar(g, 0, 0, Math.min(getWidth(), getHeight()), doctor);
                    g.dispose();
                }
            };
            avatar.setOpaque(false);
            avatar.setPreferredSize(new Dimension(avatarSize, avatarSize));
            avatar.setMaximumSize(new Dimension(avatarSize, avatarSize));
            avatar.setAlignmentY(Component.CENTER_ALIGNMENT);
            constraints.gridx = 0;
            constraints.weightx = 0;
            add(avatar, constraints);

            JPanel schedule = new JPanel();
            schedule.setLayout(new BoxLayout(schedule, BoxLayout.Y_AXIS));
            schedule.setOpaque(false);
            schedule.setAlignmentY(Component.CENTER_ALIGNMENT);
            schedule.setPreferredSize(new Dimension(210, 60));
            JPanel hours = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            hours.setOpaque(false);
            hours.setAlignmentX(CENTER_ALIGNMENT);
            TcIconLabel clock = new TcIconLabel(TcIcons.Kind.CLOCK, 14,
                    active ? UiTheme.SAGE_LIGHT : UiTheme.OLIVE);
            hours.add(clock);
            hours.add(UiTheme.label(doctor.getHours(), 11, Font.PLAIN, secondary));
            hours.add(Box.createHorizontalStrut(6));
            JLabel status = UiTheme.label(doctor.getStatus(), 11, Font.BOLD,
                    active ? UiTheme.SAGE_LIGHT : UiTheme.STATUS_GREEN);
            hours.add(status);
            schedule.add(hours);
            JPanel stars = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
            stars.setOpaque(false);
            stars.setAlignmentX(CENTER_ALIGNMENT);
            for (int i = 0; i < 5; i++) {
                stars.add(new TcIconLabel(TcIcons.Kind.STAR, 12, UiTheme.GOLD));
            }
            schedule.add(stars);
            constraints.gridx = 1;
            constraints.weightx = 0.9;
            add(schedule, constraints);

            JPanel identity = new JPanel();
            identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
            identity.setOpaque(false);
            identity.setAlignmentY(Component.CENTER_ALIGNMENT);
            identity.setPreferredSize(new Dimension(210, 50));
            JLabel nameLabel = UiTheme.label(doctor.getName(),
                    expanded ? 17 : 15, Font.BOLD, primary);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setAlignmentX(CENTER_ALIGNMENT);
            identity.add(nameLabel);
            JLabel specialtyLabel = UiTheme.label(doctor.getClinic(),
                    expanded ? 12 : 11, Font.PLAIN, secondary);
            specialtyLabel.setHorizontalAlignment(JLabel.CENTER);
            specialtyLabel.setAlignmentX(CENTER_ALIGNMENT);
            identity.add(specialtyLabel);
            constraints.gridx = 2;
            constraints.weightx = 1;
            add(identity, constraints);

            JLabel fee = UiTheme.label(String.format("₱ %,d", (int) doctor.getFee()),
                    expanded ? 19 : 17, Font.BOLD, active ? UiTheme.BUTTER : UiTheme.CHARCOAL);
            int feeHeight = fee.getPreferredSize().height;
            fee.setHorizontalAlignment(JLabel.CENTER);
            fee.setAlignmentY(Component.CENTER_ALIGNMENT);
            fee.setPreferredSize(new Dimension(120, feeHeight));
            fee.setMinimumSize(new Dimension(120, feeHeight));
            fee.setMaximumSize(new Dimension(120, feeHeight));
            constraints.gridx = 3;
            constraints.weightx = 0.5;
            add(fee, constraints);

            JButton bookmark = new JButton() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    TcIcons.paint(g, TcIcons.Kind.STAR, 0, 0, getWidth(),
                            favorite ? UiTheme.GOLD : (active ? UiTheme.BUTTER : UiTheme.OLIVE),
                            favorite);
                    g.dispose();
                }
            };
            bookmark.setPreferredSize(new Dimension(22, 22));
            bookmark.setFocusPainted(false);
            bookmark.setBorderPainted(false);
            bookmark.setContentAreaFilled(false);
            bookmark.setOpaque(false);
            bookmark.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            bookmark.setToolTipText(favorite ? "Remove bookmark" : "Bookmark this doctor");
            bookmark.addActionListener(e -> {
                if (favorites.contains(doctor.getName())) {
                    favorites.remove(doctor.getName());
                    favorite = false;
                } else {
                    favorites.add(doctor.getName());
                    favorite = true;
                }
                bookmark.setToolTipText(favorite ? "Remove bookmark" : "Bookmark this doctor");
                bookmark.repaint();
            });
            bookmark.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                }
            });
            JPanel bookmarkHost = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            bookmarkHost.setOpaque(false);
            bookmarkHost.setAlignmentY(Component.CENTER_ALIGNMENT);
            bookmarkHost.add(bookmark);
            constraints.gridx = 4;
            constraints.weightx = 0;
            add(bookmarkHost, constraints);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectDoctor(doctor);
                }
            });
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            if (active) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(150, 160, 145));
                g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 0f, new float[]{5f, 5f}, 0f));
                g.draw(new RoundRectangle2D.Float(-16, 2, getWidth() + 12, getHeight() - 6, 18, 18));
                g.dispose();
            }
            super.paintComponent(graphics);
        }
    }
}
