package com.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.data.CarData;
import com.ui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.miginfocom.swing.MigLayout;

public class CarBookingPanel extends RoundedPanel {

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

    private static final String ADDON_BABY_SEAT = "Baby Seat";
    private static final String ADDON_ROADSIDE_SUPPORT = "Roadside Support";
    private static final String INS_THEFT = "Theft";
    private static final String INS_DAMAGE_WAIVER = "Damage Waiver";
    private static final String FUEL_FULL_TO_FULL = "Full-to-Full";
    private static final String FUEL_PREPAID = "Prepaid";
    private static final String SELF_DRIVING = "Self-Driving";
    private static final String ASSIGN_DRIVER = "Assign Driver";

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

    private CarData car;
    private final JComboBox<String> durationCombo;
    private final ButtonGroup fuelGroup;
    private final ButtonGroup driverGroup;
    private final JLabel totalLabel;
    private final JButton calcBtn;
    private final JLabel carRentalCostDisplayLabel;

    private final JLabel addonsCostLabel;
    private final JLabel insuranceCostLabel;
    private final JLabel fuelCostLabel;
    private final JLabel driverCostLabel;

    private List<JCheckBox> actualAddonsCheckBoxes;
    private List<JCheckBox> actualInsuranceCheckBoxes;

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
        super(12);
        this.car = carData;

        setLayout(new MigLayout("fillx, wrap 1, insets 15", "[grow]",
                "[]15[]15[]5[]15[]5[]5[]15[]"));
        setBackground(PANEL_BACKGROUND);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

        add(createBookingTitle(), "growx");

        carRentalCostDisplayLabel = createLabel("Car Rental: R0");
        durationCombo = createDurationComboBox();
        JPanel durationOuterPanel = createLeftAlignedPanel();
        durationOuterPanel.add(createLabelNormal("Rental Duration:"));
        durationOuterPanel.add(Box.createHorizontalStrut(12));
        durationOuterPanel.add(durationCombo);
        add(createSectionPanel(76, carRentalCostDisplayLabel, durationOuterPanel));

        this.addonsCostLabel = createLabel("Add-ons: R0");
        this.actualAddonsCheckBoxes = new ArrayList<>();
        JComponent addOnsGroup = createCheckBoxGroupWithValue(
                new String[]{ADDON_BABY_SEAT, ADDON_ROADSIDE_SUPPORT},
                ADDON_PRICES, this.addonsCostLabel, "Add-ons",
                cost -> addOnsTotal = cost, this.actualAddonsCheckBoxes);
        add(createSectionPanel(66, this.addonsCostLabel, addOnsGroup));

        this.insuranceCostLabel = createLabel("Insurance: R0");
        this.actualInsuranceCheckBoxes = new ArrayList<>();
        JComponent insuranceGroup = createCheckBoxGroupWithValue(
                new String[]{INS_THEFT, INS_DAMAGE_WAIVER},
                INSURANCE_PRICES, this.insuranceCostLabel, "Insurance",
                cost -> insuranceTotal = cost, this.actualInsuranceCheckBoxes);
        add(createSectionPanel(66, this.insuranceCostLabel, insuranceGroup));

        this.fuelCostLabel = createLabel("Fuel Policy: R0");
        fuelGroup = new ButtonGroup();
        JComponent fuelPolicyGroup = createRadioButtonGroupWithValue(
                new String[]{FUEL_PREPAID, FUEL_FULL_TO_FULL},
                fuelGroup, FUEL_PRICES, this.fuelCostLabel, "Fuel Policy",
                " - OnceOff", cost -> fuelPolicyCost = cost);
        add(createSectionPanel(66, this.fuelCostLabel, fuelPolicyGroup));

        this.driverCostLabel = createLabel("Driver Choice: R0");
        driverGroup = new ButtonGroup();
        JComponent driverOpGroup = createRadioButtonGroupWithValue(
                new String[]{SELF_DRIVING, ASSIGN_DRIVER},
                driverGroup, DRIVER_PRICES, this.driverCostLabel, "Driver Choice",
                "/day", cost -> driverCost = cost);
        add(createSectionPanel(66, this.driverCostLabel, driverOpGroup));

        add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        calcBtn = createButton("Calculate Total", 102, 32);
        totalLabel = createTotalLabel();
        add(calcBtn, "center");
        add(totalLabel, "center");

        setupActionListeners();
        updateCarRentalCostDisplay();
        selectFirstOption(fuelGroup);
        selectFirstOption(driverGroup);
    }

    /**
     * Formats an integer value into a currency string.
     * @param value The integer value to format.
     * @return A string representing the value in the default currency format with no cents.
     */
    private String formatCurrency(int value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        formatter.setMaximumFractionDigits(0);
        return formatter.format(value);
    }

    /**
     * Selects the first button in a given ButtonGroup.
     * If the selected button is a JRadioButton, its action listeners are triggered
     * to ensure initial label updates.
     * @param group The ButtonGroup from which to select the first button.
     */
    private void selectFirstOption(ButtonGroup group) {
        if (group.getButtonCount() > 0) {
            AbstractButton button = group.getElements().nextElement();
            button.setSelected(true);
            if (button instanceof JRadioButton) {
                 ActionEvent initialEvent = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, button.getActionCommand());
                 for(java.awt.event.ActionListener al : button.getActionListeners()){
                     al.actionPerformed(initialEvent);
                 }
            }
        }
    }

    /**
     * Retrieves the currently selected AbstractButton from a ButtonGroup.
     * @param group The ButtonGroup to check.
     * @return The selected AbstractButton, or null if no button is selected.
     */
    private AbstractButton getSelectedButton(ButtonGroup group) {
        for (AbstractButton button : Collections.list(group.getElements())) {
            if (button.isSelected()) {
                return button;
            }
        }
        return null;
    }


    /**
     * Creates a styled JButton with specified text, width, and height.
     * @param text The text to display on the button.
     * @param width The preferred width of the button.
     * @param height The preferred height of the button.
     * @return A new styled JButton.
     */
    private JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(FONT_SUBHEADER);
        button.setPreferredSize(new Dimension(width, height));
        button.setUI(new RoundedButtonUI(20, BUTTON_COLOR, null));
        return button;
    }

    /**
     * Creates a styled JLabel for displaying the total price.
     * @return A new styled JLabel initialized for the total.
     */
    private JLabel createTotalLabel() {
        JLabel label = new JLabel("Total:");
        label.setFont(FONT_HEADER);
        label.setForeground(TEXT_COLOR_HEADER);
        return label;
    }

    /**
     * Sets up action listeners for interactive components like the calculate button
     * and the duration combo box.
     */
    private void setupActionListeners() {
        calcBtn.addActionListener(e -> calculateTotal());
        durationCombo.addActionListener(e -> {
            updateCarRentalCostDisplay();

            if (this.actualAddonsCheckBoxes != null && this.addonsCostLabel != null) {
                updateCostDisplay(this.actualAddonsCheckBoxes, ADDON_PRICES, this.addonsCostLabel, "Add-ons", cost -> addOnsTotal = cost);
            }

            if (this.actualInsuranceCheckBoxes != null && this.insuranceCostLabel != null) {
                updateCostDisplay(this.actualInsuranceCheckBoxes, INSURANCE_PRICES, this.insuranceCostLabel, "Insurance", cost -> insuranceTotal = cost);
            }

            AbstractButton selectedDriverButton = getSelectedButton(driverGroup);
            if (selectedDriverButton != null && this.driverCostLabel != null) {
                String optionText = selectedDriverButton.getText();
                int costPerDay = DRIVER_PRICES.getOrDefault(optionText, 0);
                driverCost = costPerDay;

                int numberOfDays = getSelectedNumberOfDays();
                int totalCalculatedDriverCost = costPerDay * numberOfDays;
                String title = "Driver Choice";
                String driverTrailing = "/day";

                if (costPerDay == 0) {
                    this.driverCostLabel.setText(title + ": R0");
                } else {
                    if (numberOfDays > 1 && costPerDay > 0) {
                        this.driverCostLabel.setText(title + ": R" + totalCalculatedDriverCost + " (R" + costPerDay + driverTrailing + ")");
                    } else {
                        this.driverCostLabel.setText(title + ": R" + totalCalculatedDriverCost);
                    }
                }
            }
        });
    }

    /**
     * Creates a JPanel to represent a section with a title label and content.
     * The panel has a bottom border and fixed preferred/maximum height.
     * @param height The preferred and maximum height of the section panel.
     * @param label The JLabel to use as the title for this section.
     * @param content The JComponent to display as the content of this section.
     * @return A new JPanel configured as a section.
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
     * Creates a styled JComboBox for selecting rental duration.
     * @return A new styled JComboBox with predefined duration options.
     */
    private JComboBox<String> createDurationComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"1 Day", "3 Days", "7 Days"});
        combo.setUI(new RoundedComboBoxUI());
        combo.setPreferredSize(new Dimension(144, 32));
        combo.setOpaque(false);
        combo.setForeground(Color.BLACK);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    /**
     * Creates a JPanel with a left-aligned FlowLayout and no opaque background.
     * @return A new JPanel configured for left alignment.
     */
    private JPanel createLeftAlignedPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Creates a JPanel containing a group of JCheckBoxes.
     * It populates a provided list with the created checkboxes and sets up
     * action listeners to update a cost label and a callback when selections change.
     * @param labels Array of strings for checkbox labels.
     * @param prices Map of label strings to their integer prices.
     * @param costLabel JLabel to display the calculated cost of selected checkboxes.
     * @param title Title string for the cost label.
     * @param callback Consumer to accept the total base cost of selected items.
     * @param checkBoxesListToPopulate List to be populated with the created JCheckBoxes.
     * @return A JPanel containing the group of checkboxes.
     */
    private JPanel createCheckBoxGroupWithValue(String[] labels, Map<String, Integer> prices, JLabel costLabel,
                                                String title, Consumer<Integer> callback, List<JCheckBox> checkBoxesListToPopulate) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, 0));
        panel.setOpaque(false);

        for (String label : labels) {
            JCheckBox cb = new JCheckBox(label);
            cb.setUI(new RoundedCheckBoxUI());
            cb.setOpaque(false);
            cb.setForeground(Color.WHITE);

            checkBoxesListToPopulate.add(cb);
            panel.add(cb);
            cb.addActionListener(e -> updateCostDisplay(checkBoxesListToPopulate, prices, costLabel, title, callback));
        }

        updateCostDisplay(checkBoxesListToPopulate, prices, costLabel, title, callback);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, FIXED_OPTION_ROW_HEIGHT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIXED_OPTION_ROW_HEIGHT));
        return panel;
    }

    /**
     * Creates a JPanel containing a group of JRadioButtons.
     * Radio buttons are added to a provided ButtonGroup. Action listeners update
     * a cost label and a callback when a radio button is selected.
     * @param options Array of strings for radio button labels.
     * @param group ButtonGroup to which the radio buttons will be added.
     * @param prices Map of option strings to their integer prices.
     * @param costLabel JLabel to display the cost of the selected option.
     * @param title Title string for the cost label.
     * @param trailing String to append to the cost label (e.g., "/day", " - OnceOff").
     * @param callback Consumer to accept the base cost of the selected item.
     * @return A JPanel containing the group of radio buttons.
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
                int numberOfDays = getSelectedNumberOfDays();
                int displayTotalCost = trailing.equals("/day") ? cost * numberOfDays : cost;

                if (cost == 0) {
                    costLabel.setText(title + ": R0");
                } else if (trailing.equals("/day")) {
                    if (numberOfDays > 1 && cost > 0) {
                         costLabel.setText(title + ": R" + displayTotalCost + " (R" + cost + trailing + ")");
                    } else {
                         costLabel.setText(title + ": R" + displayTotalCost);
                    }
                } else {
                    costLabel.setText(title + ": R" + cost + trailing);
                }
                callback.accept(cost);
            });
        }

        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, FIXED_OPTION_ROW_HEIGHT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIXED_OPTION_ROW_HEIGHT));
        return panel;
    }

    /**
     * Updates a cost display JLabel based on selected JCheckBoxes from a list.
     * The display includes the total cost for the selected number of days and,
     * if applicable, the base per-day cost.
     * @param boxes List of JCheckBoxes to evaluate.
     * @param prices Map of checkbox text to their integer prices (per day).
     * @param costLabel JLabel to update with the calculated cost.
     * @param title Title string for the cost label.
     * @param callback Consumer to accept the total base per-day cost of selected items.
     */
    private void updateCostDisplay(List<JCheckBox> boxes, Map<String, Integer> prices,
                                   JLabel costLabel, String title, Consumer<Integer> callback) {
        String trailing = "/day";
        int baseTotalPerDay = boxes.stream()
                .filter(AbstractButton::isSelected)
                .mapToInt(box -> prices.getOrDefault(box.getText().trim(), 0))
                .sum();

        int numberOfDays = getSelectedNumberOfDays();
        int totalCalculatedCost = baseTotalPerDay * numberOfDays;

        if (baseTotalPerDay == 0) {
            costLabel.setText(title + ": R0");
        } else if (numberOfDays > 1 && baseTotalPerDay > 0) {
            costLabel.setText(title + ": R" + totalCalculatedCost + " (R" + baseTotalPerDay + trailing + ")");
        } else {
            costLabel.setText(title + ": R" + totalCalculatedCost);
        }
        callback.accept(baseTotalPerDay);
    }

    /**
     * Creates a RoundedPanel for the booking title section.
     * @return A new RoundedPanel configured as the booking title.
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
     * Creates a styled JLabel with sub-header font and normal text color.
     * @param text The text for the label.
     * @return A new styled JLabel.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBHEADER);
        label.setForeground(TEXT_COLOR_NORMAL);
        return label;
    }

    /**
     * Creates a styled JLabel with normal font and header text color.
     * @param text The text for the label.
     * @return A new styled JLabel.
     */
    private JLabel createLabelNormal(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT_COLOR_HEADER);
        return label;
    }

    /**
     * Updates the display of the car rental cost based on the selected car's price
     * and the selected number of days.
     */
    private void updateCarRentalCostDisplay() {
        int basePricePerDay = (car != null) ? car.getPrice() : 0;
        int numberOfDays = getSelectedNumberOfDays();
        int currentCarRentalCost = basePricePerDay * numberOfDays;
        carRentalCostDisplayLabel.setText("Car Rental: " + formatCurrency(currentCarRentalCost));
    }

    /**
     * Gets the number of days selected in the duration JComboBox.
     * @return The number of days (1, 3, or 7), defaulting to 1.
     */
    private int getSelectedNumberOfDays() {
        return switch (durationCombo.getSelectedIndex()) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 7;
            default -> 1;
        };
    }

    /**
     * Calculates the total booking cost based on car rental, addons, insurance,
     * fuel policy, and driver costs, multiplied by the number of days where applicable.
     * Updates the totalLabel with the formatted total cost.
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

    /**
     * Overrides the setBorder method to add a compound border with an
     * additional empty border for padding.
     * @param border The original border to be set.
     */
    @Override
    public void setBorder(Border border) {
        super.setBorder(new CompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));
    }

    /**
     * Updates the panel with data from a newly selected car.
     * If a car is selected, its rental cost is updated. If no car is selected (null),
     * relevant cost displays and state variables are reset.
     *
     * @param selectedCar The new car data, or null to clear.
     */
    public void updateDisplay(CarData selectedCar) {
        this.car = selectedCar;

        if (car != null) {
            updateCarRentalCostDisplay();
        } else {
            carRentalCostDisplayLabel.setText("Car Rental: R0");
            if (addonsCostLabel != null) addonsCostLabel.setText("Add-ons: R0");
            if (insuranceCostLabel != null) insuranceCostLabel.setText("Insurance: R0");
            if (driverCostLabel != null) driverCostLabel.setText("Driver Choice: R0");
            if (fuelCostLabel != null) fuelCostLabel.setText("Fuel Policy: R0");
            addOnsTotal = 0;
            insuranceTotal = 0;
            driverCost = 0;
            fuelPolicyCost = 0;
            totalLabel.setText("Total:");
        }
    }
}