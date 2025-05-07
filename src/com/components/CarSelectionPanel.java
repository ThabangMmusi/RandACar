package com.components;

import com.data.CarData;
import com.listeners.CarSelectionListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CarSelectionPanel extends JPanel {

    private static final Color PANEL_BACKGROUND = new Color(35, 35, 45, 0);
    private static final int HORIZONTAL_GAP = 10;

    private final List<CarData> carDataList;
    private final List<CarPreviewPanel> previewPanels = new ArrayList<>();
    private final JPanel contentPanel = createContentPanel(); // Panel that holds car previews
    private final EventListenerList listenerList = new EventListenerList();
    private int selectedIndex = -1;

    /**
     * Constructor that initializes the car selection panel.
     * @param cars List of cars to display
     */
    public CarSelectionPanel(List<CarData> cars) {
        setOpaque(false);
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout());

        this.carDataList = cars != null ? cars : new ArrayList<>();
        populateContentPanel(); // Adds car previews to the content panel

        JScrollPane scrollPane = createScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Select first car by default if available
        if (!carDataList.isEmpty()) {
            setSelectedIndex(0);
        }
    }

    /**
     * Creates and returns a horizontally laid out content panel for previews.
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    /**
     * Creates and configures a scroll pane for horizontal scrolling.
     */
    private JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // if no vertical scrolling needed
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(PANEL_BACKGROUND);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
    
        // Still allow scrolling via touchpad/mouse by setting scrollbar size to 0
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(CarPreviewPanel.PREVIEW_WIDTH / 2);
    
        return scrollPane;
    }
    
    /**
     * Populates the content panel with car preview panels.
     * Adds mouse click listeners to each for selection.
     */
    private void populateContentPanel() {
        contentPanel.removeAll();
        previewPanels.clear();

        for (int i = 0; i < carDataList.size(); i++) {
            CarData car = carDataList.get(i);
            CarPreviewPanel previewPanel = new CarPreviewPanel(car);
            previewPanels.add(previewPanel);
            contentPanel.add(previewPanel);

            // Add spacing between previews except after the last
            if (i < carDataList.size() - 1) {
                contentPanel.add(Box.createHorizontalStrut(HORIZONTAL_GAP));
            }

            final int index = i;
            previewPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedIndex(index); // Select the clicked car
                }
            });
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Updates the selected index and highlights the corresponding car panel.
     * Fires a selection event to listeners.
     */
    public void setSelectedIndex(int index) {
        if (index < 0 || index >= previewPanels.size() || index == selectedIndex) return;

        // Unselect previously selected panel
        if (selectedIndex >= 0) {
            previewPanels.get(selectedIndex).setSelected(false);
        }

        selectedIndex = index;
        CarPreviewPanel selectedPanel = previewPanels.get(index);
        selectedPanel.setSelected(true);
        contentPanel.scrollRectToVisible(selectedPanel.getBounds());

        fireCarSelected(carDataList.get(index)); // Notify listeners
    }

    /**
     * Gets the currently selected car data.
     */
    public CarData getSelectedCar() {
        return (selectedIndex >= 0 && selectedIndex < carDataList.size()) ? carDataList.get(selectedIndex) : null;
    }

    /**
     * Gets the index of the selected car.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Registers a listener to be notified when a car is selected.
     */
    public void addCarSelectionListener(CarSelectionListener listener) {
        listenerList.add(CarSelectionListener.class, listener);
    }

    /**
     * Removes a previously registered selection listener.
     */
    public void removeCarSelectionListener(CarSelectionListener listener) {
        listenerList.remove(CarSelectionListener.class, listener);
    }

    /**
     * Notifies all registered listeners that a car has been selected.
     */
    private void fireCarSelected(CarData selectedCar) {
        for (Object listener : listenerList.getListenerList()) {
            if (listener instanceof CarSelectionListener) {
                ((CarSelectionListener) listener).carSelected(selectedCar);
            }
        }
    }

    /**
     * Paints the background with a custom transparent color.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(PANEL_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
