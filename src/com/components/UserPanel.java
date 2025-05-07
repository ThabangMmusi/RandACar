package com.components;

import javax.swing.*;

import com.ui.RoundedButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/**
 * A custom JPanel that displays user information with an exit button.
 * This panel can be added to any container in an existing application.
 */
public class UserPanel extends JPanel {
    
    private JLabel userNameLabel;
    private JLabel userHandleLabel;
    private JButton exitButton;
    private ActionListener exitAction;
    
    /**
     * Creates a user panel with the specified exit action.
     * 
     * @param exitAction The action to perform when the exit button is clicked
     */
    public UserPanel(ActionListener exitAction) {
        this.exitAction = exitAction;
        initializePanel();
    }
    
    /**
     * Creates a user panel with a default exit action that does nothing.
     * You should set a proper exit action later using setExitAction().
     */
    public UserPanel() {
        this.exitAction = e -> {};
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Create avatar panel with initials
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular background
                g2.setColor(Color.decode("#2ECC71")); 
                g2.fill(new Ellipse2D.Double(0, 0, 40, 40));
                
                // Draw initials with better positioning
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "TM";
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getHeight();
                int x = (40 - textWidth) / 2;
                int y = ((40 - textHeight) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(40, 40);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(40, 40);
            }
            
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(40, 40);
            }
        };
        avatarPanel.setOpaque(false);
        
        // Create user info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        
        // User name in bold
        userNameLabel = new JLabel("Thabang Mmusi");
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // User handle in blue-gray
        userHandleLabel = new JLabel("@tmmusi");
        userHandleLabel.setForeground(Color.decode("#3C5868"));
        userHandleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        userHandleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userInfoPanel.add(userNameLabel);
        userInfoPanel.add(userHandleLabel);
        
        // Create exit button
        exitButton = new JButton("Exit App");
        exitButton.setFocusPainted(false);
        exitButton.setUI(new RoundedButtonUI(20, Color.red, Color.white));
        exitButton.setForeground(Color.WHITE);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(exitAction);
        
        // Add components to panel
        add(avatarPanel);
        add(Box.createRigidArea(new Dimension(15, 0))); // Spacing
        add(userInfoPanel);
        add(Box.createHorizontalGlue()); // Push exit button to the right
        add(exitButton);
    }
    
    /**
     * Sets the action to perform when the exit button is clicked.
     * 
     * @param exitAction The action to perform
     */
    public void setExitAction(ActionListener exitAction) {
        // Remove existing action listener
        for (ActionListener al : exitButton.getActionListeners()) {
            exitButton.removeActionListener(al);
        }
        
        // Add new action listener
        this.exitAction = exitAction;
        exitButton.addActionListener(exitAction);
    }
    
    /**
     * Sets the user's name displayed in the panel.
     * 
     * @param name The user's name
     */
    public void setUserName(String name) {
        userNameLabel.setText(name);
    }
    
    /**
     * Sets the user's handle displayed in the panel.
     * 
     * @param handle The user's handle
     */
    public void setUserHandle(String handle) {
        userHandleLabel.setText(handle);
    }
}