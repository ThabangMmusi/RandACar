package com.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.data.CarData;
import com.ui.*;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;

public class CarBookingPanel extends RoundedPanel {

    // --- Constants ---
    private static final Color PANEL_BACKGROUND = new Color(35, 35, 45, 200);
    private static final Color BUTTON_COLOR = Color.decode("#2ECC71");
    private static final Color TEXT_COLOR_HEADER = Color.WHITE;
    private static final Color TEXT_COLOR_NORMAL = new Color(200, 200, 200);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_SUBHEADER = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("SansSerif", Font.BOLD, 12);
    private static final int PREFERRED_WIDTH = 300;
    private static final int PREFERRED_HEIGHT = 620;
    private static final int HORIZONTAL_GAP = 12;
    private static final int FIXED_OPTION_ROW_HEIGHT = 35;

    // Price map keys
    private static final String ADDON_BABY_SEAT = "Baby Seat";
    private static final String ADDON_ROADSIDE_SUPPORT = "Roadside Support";
    private static final String INS_THEFT = "Theft";
    private static final String INS_DAMAGE_WAIVER = "Damage Waiver";
    private static final String FUEL_FULL_TO_FULL = "Full-to-Full";
    private static final String FUEL_PREPAID = "Prepaid";
    private static final String SELF_DRIVING = "Self-Driving";
    private static final String ASSIGN_DRIVER = "Assign Driver";

    // Static price maps
    private static final Map<String, Integer> ADDON_PRICES = Map.of(
            ADDON_BABY_SEAT, 60,
            ADDON_ROADSIDE_SUPPORT, 40);
    private static final Map<String, Integer> INSURANCE_PRICES = Map.of(
            INS_THEFT, 100,
            INS_DAMAGE_WAIVER, 80);
    private static final Map<String, Integer> FUEL_PRICES = Map.of(
            FUEL_PREPAID, 0,
            FUEL_FULL_TO_FULL, 300);
    private static final Map<String, Integer> DRIVER_PRICES = Map.of(
            SELF_DRIVING, 0,
            ASSIGN_DRIVER, 200);

    // UI components
    private CarData car;
    private final JComboBox<String> durationCombo;
    private final ButtonGroup fuelGroup;
    private final ButtonGroup driverGroup;
    private final JLabel totalLabel;
    private final JButton calcBtn;
    private final JLabel carRentalCostDisplayLabel;

    // State variables
    private int addOnsTotal = 0;
    private int insuranceTotal = 0;
    private int fuelPolicyCost = 0;
    private int driverCost = 0;

    /**
     * Creates a new car booking panel with the specified car data
     * 
     * @param carData The car data to initialize with
     */
    public CarBookingPanel(CarData carData) {
        super(12); // Rounded corners with radius 12
        this.car = carData;

        setLayout(new MigLayout("fillx, wrap 1, insets 15", "[grow]", 
                "[]15[]15[]5[]15[]5[]5[]15[]"));
        setBackground(PANEL_BACKGROUND);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

        // Build UI sections
        add(createBookingTitle(), "growx");

        // Duration section
        carRentalCostDisplayLabel = createLabel("Car Rental: R0");
        durationCombo = createDurationComboBox();
        JPanel durationOuterPanel = createLeftAlignedPanel();
        durationOuterPanel.add(createLabelNormal("Rental Duration:"));
        durationOuterPanel.add(Box.createHorizontalStrut(12));
        durationOuterPanel.add(durationCombo);
        add(createSectionPanel(76, carRentalCostDisplayLabel, durationOuterPanel));

        // Add-ons section
        JLabel addonsCostLabel = createLabel("Add-ons: R0");
        JComponent addOnsGroup = createCheckBoxGroupWithValue(
                new String[]{ ADDON_BABY_SEAT, ADDON_ROADSIDE_SUPPORT }, 
                ADDON_PRICES, addonsCostLabel, "Add-ons", 
                cost -> addOnsTotal = cost);
        add(createSectionPanel(66, addonsCostLabel, addOnsGroup));

        // Insurance section
        JLabel insuranceCostLabel = createLabel("Insurance: R0");
        JComponent insuranceGroup = createCheckBoxGroupWithValue(
                new String[]{ INS_THEFT, INS_DAMAGE_WAIVER }, 
                INSURANCE_PRICES, insuranceCostLabel, "Insurance", 
                cost -> insuranceTotal = cost);
        add(createSectionPanel(66, insuranceCostLabel, insuranceGroup));

        // Fuel policy section
        JLabel fuelCostLabel = createLabel("Fuel Policy: R0");
        fuelGroup = new ButtonGroup();
        JComponent fuelPolicyGroup = createRadioButtonGroupWithValue(
                new String[]{ FUEL_PREPAID, FUEL_FULL_TO_FULL }, 
                fuelGroup, FUEL_PRICES, fuelCostLabel, "Fuel Policy", 
                " - OnceOff", cost -> fuelPolicyCost = cost);
        add(createSectionPanel(66, fuelCostLabel, fuelPolicyGroup));

        // Driver choice section
        JLabel driverCostLabel = createLabel("Driver Choice: R0");
        driverGroup = new ButtonGroup();
        JComponent driverOpGroup = createRadioButtonGroupWithValue(
                new String[]{ SELF_DRIVING, ASSIGN_DRIVER }, 
                driverGroup, DRIVER_PRICES, driverCostLabel, "Driver Choice", 
                "/day", cost -> driverCost = cost);
        add(createSectionPanel(66, driverCostLabel, driverOpGroup));

        add(Box.createVerticalStrut(10)); // Add some space at the bottom
        // Calculation section
        calcBtn = createButton("Calculate Total", 102, 32);
        totalLabel = createTotalLabel();
        add(calcBtn, "center");
        add(totalLabel, "center");

        // Initialize UI state
        setupActionListeners();
        updateCarRentalCostDisplay();
        selectFirstOption(fuelGroup);
        selectFirstOption(driverGroup);
    }

    private String formatCurrency(int value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        formatter.setMaximumFractionDigits(0); // No cents
        return formatter.format(value);
    }
    
    /**
     * Select the first button in a button group
     */
    private void selectFirstOption(ButtonGroup group) {
        if (group.getButtonCount() > 0) {
            group.getElements().nextElement().setSelected(true);
        }
    }

    /**
     * Create a styled button
     */
    private JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(FONT_SUBHEADER);
        button.setPreferredSize(new Dimension(width, height));
        button.setUI(new RoundedButtonUI(20, BUTTON_COLOR, null));
        return button;
    }

    /**
     * Create the total price label
     */
    private JLabel createTotalLabel() {
        JLabel label = new JLabel("Total:");
        label.setFont(FONT_HEADER);
        label.setForeground(TEXT_COLOR_HEADER);
        return label;
    }

    /**
     * Set up action listeners for interactive components
     */
    private void setupActionListeners() {
        calcBtn.addActionListener(e -> calculateTotal());
        durationCombo.addActionListener(e -> updateCarRentalCostDisplay());
    }

    /**
     * Creates a panel with a section title and content
     */
    private JPanel createSectionPanel(int height, JLabel label, JComponent content) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(0, 0, 12, 0)
        ));
        panel.setPreferredSize(new Dimension(280, height));
        panel.setMaximumSize(new Dimension(280, height));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(content);

        return panel;
    }

    /**
     * Creates a styled combo box for selecting rental duration
     */
    private JComboBox<String> createDurationComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[] { "1 Day", "3 Days", "7 Days" });
        combo.setUI(new RoundedComboBoxUI());
        combo.setPreferredSize(new Dimension(144, 32));
        combo.setOpaque(false);
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    /**
     * Creates a left-aligned panel with default gap
     */
    private JPanel createLeftAlignedPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Creates a group of checkboxes with values and callback for cost updates
     */
    private JPanel createCheckBoxGroupWithValue(String[] labels, Map<String, Integer> prices, JLabel costLabel,
            String title, Consumer<Integer> callback) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, 0));
        panel.setOpaque(false);
        List<JCheckBox> checkBoxes = new ArrayList<>(labels.length);

        for (String label : labels) {
            JCheckBox cb = new JCheckBox(label);
            cb.setUI(new RoundedCheckBoxUI());
            cb.setOpaque(false);
            cb.setForeground(Color.WHITE);
            
            checkBoxes.add(cb);
            panel.add(cb);
            cb.addActionListener(e -> updateCostDisplay(checkBoxes, prices, costLabel, title, callback));
        }

        updateCostDisplay(checkBoxes, prices, costLabel, title, callback);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, FIXED_OPTION_ROW_HEIGHT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIXED_OPTION_ROW_HEIGHT));
        return panel;
    }

    /**
     * Creates a group of radio buttons with values and callback for cost updates
     */
    private JPanel createRadioButtonGroupWithValue(String[] options, ButtonGroup group, Map<String, Integer> prices,
            JLabel costLabel, String title, String trailing, Consumer<Integer> callback) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, 0));
        panel.setOpaque(false);

        for (String option : options) {
            JRadioButton rb = new JRadioButton(option);
            rb.setUI(new RoundedRadioButtonUI());
            rb.setOpaque(false);
            rb.setForeground(Color.WHITE);
            
            group.add(rb);
            panel.add(rb);
            rb.addActionListener(e -> {
                int cost = prices.getOrDefault(rb.getText(), 0);
                costLabel.setText(title + ": R" + cost + trailing);
                callback.accept(cost);
            });
        }

        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, FIXED_OPTION_ROW_HEIGHT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIXED_OPTION_ROW_HEIGHT));
        return panel;
    }

    /**
     * Updates the cost display based on selected checkboxes
     */
    private void updateCostDisplay(List<JCheckBox> boxes, Map<String, Integer> prices,
            JLabel costLabel, String title, Consumer<Integer> callback) {

        int total = boxes.stream()
                .filter(AbstractButton::isSelected)
                .mapToInt(box -> prices.getOrDefault(box.getText().trim(), 0))
                .sum();
        costLabel.setText(title + ": " + formatCurrency(total) + "/day");
        callback.accept(total);
    }

    /**
     * Creates the booking title panel at the top
     */
    private RoundedPanel createBookingTitle() {
        RoundedPanel panel = new RoundedPanel(12);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.setBorder(new EmptyBorder(16, 15, 16, 15));

        JLabel bookingLabel = new JLabel("Booking Info", SwingConstants.CENTER);
        bookingLabel.setFont(FONT_HEADER);
        bookingLabel.setForeground(Color.black);
        panel.add(bookingLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a styled label
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBHEADER);
        label.setForeground(TEXT_COLOR_NORMAL);
        return label;
    }
    
    /**
     * Creates a styled normal label
     */
    private JLabel createLabelNormal(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT_COLOR_HEADER);
        return label;
    }

    /**
     * Updates the car rental cost display based on selected duration
     */
    private void updateCarRentalCostDisplay() {
        int basePricePerDay = (car != null) ? car.getPrice() : 0;
        int numberOfDays = getSelectedNumberOfDays();
        int currentCarRentalCost = basePricePerDay * numberOfDays;
        carRentalCostDisplayLabel.setText("Car Rental: " + formatCurrency(currentCarRentalCost));
    }

    /**
     * Gets the number of days based on combo box selection
     */
    private int getSelectedNumberOfDays() {
        return switch (durationCombo.getSelectedIndex()) {
            case 0 -> 1; // "1 Day"
            case 1 -> 3; // "3 Days"
            case 2 -> 7; // "7 Days"
            default -> 1; // Default to 1 day if something goes wrong
        };
    }

    /**
     * Calculates the total booking cost
     */
    private void calculateTotal() {
        int basePricePerDay = (car != null) ? car.getPrice() : 0;
        int numberOfDays = getSelectedNumberOfDays();

        int total = (basePricePerDay * numberOfDays) + 
                    (addOnsTotal * numberOfDays) + 
                    (insuranceTotal * numberOfDays) + 
                    fuelPolicyCost + 
                    (driverCost * numberOfDays);
                    
        totalLabel.setText("Total: " + formatCurrency(total));
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(new CompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));
    }

    /**
     * Updates the panel with data from a newly selected car
     *
     * @param selectedCar The new car data, or null to clear
     */
    public void updateDisplay(CarData selectedCar) {
        this.car = selectedCar;
        
        if (car != null) {
            updateCarRentalCostDisplay();
        } else {
            carRentalCostDisplayLabel.setText("Car Rental: R0");
        }
    }
}