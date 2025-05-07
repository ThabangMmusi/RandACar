package com.components;

import com.data.CarData;
import com.ui.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A custom Swing panel to display a preview of a car.
 * Includes rating, performance index, price, and an image.
 */
public class CarPreviewPanel extends RoundedPanel {

    private final CarData carData;
    private boolean isSelected = false;

    // --- Styling Constants ---
    private static final Color DEFAULT_BG = new Color(30, 30, 30);
    private static final Color SELECTED_BG = Color.WHITE;
    private static final Color RATING_BG = new Color(10, 10, 10);
    private static final Color RATING_FG = Color.WHITE;
    private static final Color PI_FG = Color.BLACK;
    private static final Color PRICE_FG_DEFAULT = Color.WHITE;
    private static final Color PRICE_FG_SELECTED = Color.BLACK;

    // --- Dimension Constants ---
    static final int PREVIEW_WIDTH = 220;
    public static final int PREVIEW_HEIGHT = 100;
    private static final int IMAGE_WIDTH = 120;
    private static final int INFO_WIDTH = PREVIEW_WIDTH - IMAGE_WIDTH;

    // --- Font Constants ---
    private static final Font RATING_PI_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font PRICE_FONT = new Font("SansSerif", Font.BOLD, 16);

    // --- UI Components ---
    private JLabel imageLabel;
    private JLabel priceLabel;
    private JLabel ratingLabel;
    private JLabel piLabel;

    /**
     * Constructor to create the preview panel for a given car.
     */
    public CarPreviewPanel(CarData carData) {
        super(18); // Rounded corners
        this.carData = carData;

        // Set fixed size
        setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        setMaximumSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        setMinimumSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));

        setLayout(new BorderLayout(2, 0)); // Gap between text and image
        setOpaque(false);
        setBorderColor(Color.white); // White rounded border

        setupUI(); // Create and layout all child components
        updateSelectionState(); // Set initial state styling
    }

    /**
     * Initializes and arranges the UI components for the preview panel.
     */
    private void setupUI() {
        // --- Left Info Panel (Rating, PI, Price) ---
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // --- Rating & PI Sub-panel ---
        JPanel ratingPiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingPiPanel.setOpaque(false);

        ratingLabel = createInfoBox(carData.getGrade(), RATING_FG, RATING_BG, BorderFactory.createLineBorder(Color.black, 2));
        piLabel = createInfoBox(String.valueOf(carData.getSpeed()), PI_FG, PRICE_FG_DEFAULT, BorderFactory.createLineBorder(Color.black, 2));

        ratingPiPanel.add(ratingLabel);
        ratingPiPanel.add(piLabel);
        ratingPiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Price Label ---
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        currencyFormatter.setMaximumFractionDigits(0);
        priceLabel = new JLabel(currencyFormatter.format(carData.getPrice()));
        priceLabel.setFont(PRICE_FONT);
        priceLabel.setForeground(PRICE_FG_DEFAULT);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(ratingPiPanel);
        infoPanel.add(Box.createVerticalStrut(5)); // Spacer
        infoPanel.add(priceLabel);

        // --- Image Panel ---
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Load and scale car image
        try {
            ImageIcon icon = new ImageIcon(carData.getPreviewImagePath());
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE && icon.getIconWidth() > 0) {
                Image scaledImg = icon.getImage().getScaledInstance(IMAGE_WIDTH - 6, PREVIEW_HEIGHT - 30, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imageLabel.setText("No Image");
                System.err.println("Failed to load preview image: " + carData.getPreviewImagePath());
            }
        } catch (Exception e) {
            imageLabel.setText("Error");
            System.err.println("Error loading preview image '" + carData.getPreviewImagePath() + "': " + e.getMessage());
        }

        // --- Final Layout ---
        add(infoPanel, BorderLayout.CENTER);
        add(imageLabel, BorderLayout.EAST);
    }

    /**
     * Creates a stylized info box (used for rating and PI).
     */
    private JLabel createInfoBox(String text, Color foreground, Color background, Border border) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(RATING_PI_FONT);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(INFO_WIDTH / 2 - 5, 25)); // Half-width of info area
        label.setBorder(border);
        return label;
    }

    /**
     * Sets the selected state and updates visuals accordingly.
     */
    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            updateSelectionState();
        }
    }

    /**
     * Returns whether the panel is currently selected.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Returns the associated CarData object.
     */
    public CarData getCarData() {
        return carData;
    }

    /**
     * Updates background and price color depending on selection.
     */
    private void updateSelectionState() {
        setBackground(isSelected ? SELECTED_BG : DEFAULT_BG);
        priceLabel.setForeground(isSelected ? PRICE_FG_SELECTED : PRICE_FG_DEFAULT);
        repaint();
    }
}
