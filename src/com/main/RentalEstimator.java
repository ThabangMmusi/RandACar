package com.main;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;

import com.components.CarDisplayPanel;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class RentalEstimator extends JFrame {

    public RentalEstimator() {
        setTitle("RAND A CAR APP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);

        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screen.setFullScreenWindow(this);

        setLayout(new BorderLayout());
        add(new CarDisplayPanel(), BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RentalEstimator().setVisible(true));
    }
}
