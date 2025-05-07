package com.listeners;

import com.data.CarData;
import java.util.EventListener;

// Listener interface for car selection events
public interface CarSelectionListener extends EventListener {
    void carSelected(CarData selectedCar);
}