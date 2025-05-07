package com.components;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.ui.RoundedPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CarInfoCard extends RoundedPanel {
    private static final int CARD_WIDTH = 125;
    private static final int CARD_HEIGHT = 70;
    private static final int ICON_SIZE = 52;
    private static final int ANIMATION_DURATION = 300; // milliseconds
    private static final int ANIMATION_STEP = 30;

    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();

    public CarInfoCard(String title, String value, String iconPath) {
        super(18);
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setLayout(null);
        setBackground(new Color(40, 40, 50));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setupIcon(iconPath);
        setupValueLabel(value);
        setupTitleLabel(title);

        add(iconLabel);
        add(valueLabel);
        add(titleLabel);
    }

    private void setupIcon(String iconPath) {
        iconLabel.setBounds(10, 10, ICON_SIZE, ICON_SIZE);
        if (iconPath != null && !iconPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(iconPath);
            Image scaled = icon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        }
    }

    private void setupValueLabel(String value) {
        valueLabel.setText(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBounds(76, 10, 100, 20);
    }

    private void setupTitleLabel(String title) {
        titleLabel.setText(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(180, 180, 180));
        titleLabel.setBounds(76, 34, 100, 20);
    }

    public void setValue(String newValue) {
        animateValueChange(newValue);
    }

    public void setValue(int value) {
        setValue(String.valueOf(value));
    }

    private void animateValueChange(String newValue) {
        final Timer fadeOutTimer;
        final Timer fadeInTimer;

        final int steps = ANIMATION_DURATION / ANIMATION_STEP;
        final float alphaStep = 1f / steps;
        fadeInTimer = new Timer(ANIMATION_STEP, new AbstractAction() {
            float alpha = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += alphaStep;
                if (alpha >= 1f) {
                    alpha = 1f;
                    ((Timer) e.getSource()).stop();
                }
                valueLabel.setForeground(applyAlpha(Color.WHITE, alpha));
            }
        });

        fadeOutTimer = new Timer(ANIMATION_STEP, new AbstractAction() {
            float alpha = 1f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= alphaStep;
                if (alpha <= 0f) {
                    valueLabel.setText(newValue);
                    ((Timer) e.getSource()).stop();
                    fadeInTimer.start();
                } else {
                    valueLabel.setForeground(applyAlpha(Color.WHITE, alpha));
                }
            }
        });

        
        fadeOutTimer.start();
    }

    private Color applyAlpha(Color base, float alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.round(alpha * 255));
    }
}
