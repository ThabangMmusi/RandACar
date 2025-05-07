package com.ui;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedComboBoxUI extends BasicComboBoxUI {
    private final Color borderColor = new Color(180, 180, 180);
    private final Color backgroundColor = Color.WHITE;
    private final int arc = 12;

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton("▼ ");
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setForeground(Color.GRAY);
        return button;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = c.getWidth();
        int height = c.getHeight();

        // Draw background
        Shape roundedRect = new RoundRectangle2D.Float(0, -.4f, width - 1, height+1, arc, arc);
        g2.setColor(backgroundColor);
        g2.fill(roundedRect);

        // Draw border
        g2.setColor(borderColor);
        g2.draw(roundedRect);

        g2.dispose();
        super.paint(g, c);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Remove default painting
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer<? super Object> renderer = comboBox.getRenderer();
        Object value = comboBox.getSelectedItem();
        Component c = renderer.getListCellRendererComponent(
            new JList<>(), // dummy JList to satisfy method signature
            value,
            -1,
            false,
            false
        );
    
        c.setFont(comboBox.getFont());
        c.setForeground(comboBox.getForeground());

        Rectangle r = new Rectangle(bounds);
        r.x += 8; // Left padding
        r.width -= 16; // Account for arrow
    
        SwingUtilities.paintComponent(g, c, comboBox, r);
    }
    
}
