package com.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.data.CarData;
import com.ui.RoundedPanel;

import net.miginfocom.swing.MigLayout;
import java.awt.*;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class CarInfoPanel extends RoundedPanel {

    // --- Constants ---
    private static final Color BG_PANEL = new Color(35, 35, 45, 0);
    private static final Color BG_PANEL_SOLID = new Color(35, 35, 45, 200);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_MODEL = new Font("SansSerif", Font.BOLD, 14);

    private static final int PREF_WIDTH = 300;
    private static final int PREF_HEIGHT = 650;

    // --- Fields ---
    private CarData car;
    private JLabel carInfoNameLabel;
    private JLabel carInfoModelLabel;
    private JLabel carInfoLogoLabel;
    private CarInfoCard carInfoSpeedLabel;
    private CarInfoCard carInfoSeatsLabel;

    // --- Constructor ---
    public CarInfoPanel(CarData carData) {
        super(12);
        this.car = carData;
        setLayout(new MigLayout("fillx, wrap 1, insets 15", "[grow]", "[]10[]15[]5[]15[]5[]5[]15[]"));
        setBackground(BG_PANEL);
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

        add(createCarInfoSection(), "growx");

        carInfoSeatsLabel = new CarInfoCard("Seats", String.valueOf(car.getSeats()), "assets/icons/seats.png");
        carInfoSpeedLabel = new CarInfoCard("Speed", car.getSpeed(), "assets/icons/speed.png");

        add(carInfoSeatsLabel);
        add(carInfoSpeedLabel);
    }

    private void animateLabelUpdate(JLabel label, String newText) {
        final Point originalLocation = label.getLocation();
        final int slideDistance = 10;

        Animator fadeOutSlideLeft = new Animator(200);
        fadeOutSlideLeft.addTarget(new PropertySetter(label, "location",
                originalLocation, new Point(originalLocation.x - slideDistance, originalLocation.y)));
        fadeOutSlideLeft.addTarget(new PropertySetter(label, "foreground", label.getForeground(),
                new Color(1f, 1f, 1f, 0f))); // fade out

        fadeOutSlideLeft.addTarget(new TimingTargetAdapter() {
            @Override
            public void end() {
                label.setText(newText);
                Animator fadeInSlideBack = new Animator(200);
                fadeInSlideBack.addTarget(new PropertySetter(label, "location",
                        label.getLocation(), originalLocation));
                fadeInSlideBack.addTarget(new PropertySetter(label, "foreground",
                        new Color(1f, 1f, 1f, 0f), new Color(1f, 1f, 1f, 1f))); // fade in
                fadeInSlideBack.start();
            }
        });

        fadeOutSlideLeft.start();
    }

    private RoundedPanel createCarInfoSection() {
        RoundedPanel panel = new RoundedPanel(12);
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBackground(BG_PANEL_SOLID);
        panel.setBorder(new EmptyBorder(16, 15, 16, 15));

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setOpaque(false);

        carInfoLogoLabel = new JLabel();
        updateCarInfoLogo();
        content.add(carInfoLogoLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow, left]"));
        textPanel.setOpaque(false);

        carInfoNameLabel = new JLabel(car != null ? car.getName() : "N/A");
        carInfoNameLabel.setFont(FONT_TITLE);
        carInfoNameLabel.setForeground(TEXT_WHITE);
        textPanel.add(carInfoNameLabel);

        carInfoModelLabel = new JLabel(car != null ? car.getModel() : "");
        carInfoModelLabel.setFont(FONT_MODEL);
        carInfoModelLabel.setForeground(TEXT_WHITE);
        textPanel.add(carInfoModelLabel);

        content.add(textPanel, BorderLayout.CENTER);
        panel.add(content, BorderLayout.NORTH);

        return panel;
    }

    private void updateCarInfoLogo() {
        if (car != null && car.getLogoPath() != null && !car.getLogoPath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(car.getLogoPath());
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image scaled = icon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                    carInfoLogoLabel.setIcon(new ImageIcon(scaled));
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
            }
        }
        carInfoLogoLabel.setIcon(null);
    }

    public void updateDisplay(CarData newCar) {
        this.car = newCar;
    
        if (car != null) {
            animateLabelUpdate(carInfoNameLabel, car.getName());
            animateLabelUpdate(carInfoModelLabel, car.getModel());
            carInfoSpeedLabel.setValue(car.getSpeed());
            carInfoSeatsLabel.setValue(car.getSeats());
        } else {
            animateLabelUpdate(carInfoNameLabel, "N/A");
            animateLabelUpdate(carInfoModelLabel, "");
            carInfoSpeedLabel.setValue("?");
            carInfoSeatsLabel.setValue("?");
        }
    
        updateCarInfoLogo();
        revalidate();
        repaint();
    }
    
    @Override
    public void setBorder(Border border) {
        super.setBorder(new CompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));
    }

}
