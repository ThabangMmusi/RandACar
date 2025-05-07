package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedCheckBoxUI extends BasicCheckBoxUI {
    private final int arc = 8;
    private final int boxSize = 16;
    private final int gap = 8;
    private final Color fillColor =Color.decode("#2ECC71");
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = (c.getHeight() - boxSize) / 2;
        int x = 0; // Left padding

        // Draw box
        Shape box = new RoundRectangle2D.Float(x, y, boxSize, boxSize, arc, arc);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(box);
        // g2.setColor(Color.GRAY);
        // g2.draw(box);

        // Fill if selected
        if (b.isSelected()) {
            g2.setColor(fillColor);
            g2.fill(box);
            g2.setColor(Color.WHITE);
            drawCheck(g2, x, y, boxSize);
        }

        // Draw label
        g2.setColor(b.getForeground());
        g2.setFont(b.getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + boxSize + gap;
        int textY = y + boxSize - (boxSize - fm.getAscent()) / 2 - 2;
        g2.drawString(b.getText(), textX, textY);

        g2.dispose();
    }

    private void drawCheck(Graphics2D g2, int x, int y, int size) {
        int pad = 4;
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x + pad, y + size / 2, x + size / 2, y + size - pad);
        g2.drawLine(x + size / 2, y + size - pad, x + size - pad, y + pad);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton) c;
        FontMetrics fm = b.getFontMetrics(b.getFont());
        int textWidth = fm.stringWidth(b.getText());
        int width = 2 + boxSize + gap + textWidth + 2; // padding + box + gap + text + right padding
        int height = Math.max(boxSize + 4, fm.getHeight() + 4);
        return new Dimension(width, height);
    }
}
