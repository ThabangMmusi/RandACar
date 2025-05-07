package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RoundedRadioButtonUI extends BasicRadioButtonUI {
    private final int dotSize = 16;
    private final int gap = 8;
    private final Color fillColor =Color.decode("#2ECC71");

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 0; // Left padding
        int y = (c.getHeight() - dotSize) / 2;

        // Outer circle
        Shape outer = new Ellipse2D.Float(x, y, dotSize, dotSize);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(outer);
        // g2.setColor(Color.GRAY);
        // g2.draw(outer);

        // Filled circle if selected
        if (b.isSelected()) {
            g2.setColor(Color.WHITE);
            g2.fill(outer);
            g2.setColor(fillColor);
            int pad = 4;
            g2.fillOval(x + pad, y + pad, dotSize - 2 * pad, dotSize - 2 * pad);
        }

        // Draw label
        g2.setFont(b.getFont());
        g2.setColor(b.getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + dotSize + gap;
        int textY = y + dotSize - (dotSize - fm.getAscent()) / 2 - 2;
        g2.drawString(b.getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton) c;
        FontMetrics fm = b.getFontMetrics(b.getFont());
        int textWidth = fm.stringWidth(b.getText());
        int width = 2 + dotSize + gap + textWidth + 2;
        int height = Math.max(dotSize + 4, fm.getHeight() + 4);
        return new Dimension(width, height);
    }
}
