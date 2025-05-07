package com.components;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import com.data.CarData;
import com.listeners.CarSelectionListener;

// CarDisplayPanel IS the BackgroundPanel
public class CarDisplayPanel extends BackgroundPanel {

    private CarBookingPanel carBookingPanel; // Reference to the info panel
    private CarInfoPanel carInfoPanel; // Reference to the info panel
    private CarSelectionPanel selectionPanel; // Reference to the selection panel

    public CarDisplayPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // --- Car Data Initialization ---
        List<CarData> myCars = createSampleCarData();

        // --- Car Selection Panel (SOUTH) ---
        selectionPanel = new CarSelectionPanel(myCars);
        selectionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // --- Car Booking Panel (EAST) ---
        setSelectedCar(myCars.get(0)); // Set the initial selected car
        carBookingPanel = new CarBookingPanel(getSelectedCar()); // Pass initial data

        // --- Car Info Panel (WEST) ---
        carInfoPanel = new CarInfoPanel(getSelectedCar()); // Pass initial data
        carInfoPanel.setBorder(new EmptyBorder(50, 0, 0, 0)); // Add padding to the left

        // --- Add Components to Layout ---

        UserPanel userPanel = new UserPanel(e -> System.exit(0));
        // userPanel.setBorder(new EmptyBorder(0, 0, 8, 0)); // Add padding to the left
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setOpaque(false); // Make the panel transparent
        eastPanel.setPreferredSize(carBookingPanel.getPreferredSize());
        eastPanel.add(carBookingPanel, BorderLayout.SOUTH); // Add the booking panel to the east panel
        eastPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER); // Add some space at the bottom
        eastPanel.add(userPanel, BorderLayout.NORTH); // Add the user panel to the east panel
        add(eastPanel, BorderLayout.EAST); // Takes full height on the right
        add(carInfoPanel, BorderLayout.WEST); // Takes full height on the right
        add(selectionPanel, BorderLayout.SOUTH); // Takes full width at the bottom

        // --- Listener Setup ---
        selectionPanel.addCarSelectionListener(new CarSelectionListener() {
            @Override
            public void carSelected(CarData selectedCar) {
                if (selectedCar != null) {
                    // Update BackgroundPanel elements (managed by BackgroundPanel superclass)
                    setSelectedCar(selectedCar);

                    // Update the CarInfoPanel AND booking with the new data
                    updateCarInfoPanel();
                    updateBookingPanel();
                }
            }
        });

        // --- Set Initial Selection ---
        if (!myCars.isEmpty()) {
            selectionPanel.setSelectedIndex(0); // This will trigger the listener above
        } else {
            updateCarInfoPanel();
            updateBookingPanel();
        }
    }

    /**
     * Helper method to create sample car data.
     * 
     * @return List of CarData objects.
     */
    private List<CarData> createSampleCarData() {
        List<CarData> cars = new ArrayList<>();
        cars.add(new CarData("gle", "Mercedes Benz", "GLE 2024", "benz_gle", "mercedes_benz", "240", "4", 9500));
        cars.add(new CarData("rover_b", "Range Rover", "Vogue 2024", "land_rover_white", "land_rover", "140", "4",
                1000));
        cars.add(new CarData("clc", "Mercedes Benz", "CL 2013", "mercedes_clc", "mercedes_benz", "240", "4", 6500));
        cars.add(new CarData("golfr", "Volkswagen", "GTI-R 2025", "golf_r", "vw", "280", "4", 2500));
        cars.add(new CarData("bmwm3", "BMW M3", "M3 Ultra", "bmw_m3", "bmw", "245", "2", 2000));
        cars.add(new CarData("golf8", "Volkswagen", "GTI-8 2025", "golf_8", "vw", "280", "4", 2500));
        cars.add(new CarData("bmwm2", "BMW M2", "M2 Ultra", "bmw_m2", "bmw", "195", "2", 1600));

        return cars;
    }

    /**
     * Updates the CarInfoPanel with data from the selected CarData object.
     * The selected car data, or null to clear/reset.
     */
    private void updateCarInfoPanel() {
        if (carInfoPanel != null) {
            carInfoPanel.updateDisplay(getSelectedCar());
        }
    }

    private void updateBookingPanel() {
        if (carBookingPanel != null) {
            carBookingPanel.updateDisplay(getSelectedCar());
        }
    }
}