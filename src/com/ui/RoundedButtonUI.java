package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RoundedButtonUI extends BasicButtonUI {
    private final int arc;
    private final Color background;
    private final Color borderColor;

    public RoundedButtonUI() {
        this(15, new Color(60, 63, 65), null); // Default radius, dark background, no border
    }

    public RoundedButtonUI(int arc, Color background, Color borderColor) {
        this.arc = arc;
        this.background = background;
        this.borderColor = borderColor;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();

        // Antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(background);
        g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);

        // Border (optional)
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, arc, arc);
        }

        g2.dispose();
        super.paint(g, c);
    }
}
