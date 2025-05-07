package com.data;

public class CarData {
    private final String id;
    private final String name;
    private final String model;
    private final String previewImagePath;
    private final String logoPath;
    private final String speed;
    private final String seats;
    private final int price;

    public CarData(String id, String name, String model, String previewImagePath, String logoPath, String speed,
            String seats, int price) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.previewImagePath = previewImagePath;
        this.logoPath = logoPath;
        this.speed = speed;
        this.seats = seats;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getPreviewImagePath() {
        return "assets/cars/" + previewImagePath + ".png";
    }

    public String getLogoPath() {
        return "assets/logos/" + logoPath + ".png";
    }

    public String getSpeed() {
        return speed;
    }

    public String getSeats() {
        return seats;
    }

    public int getPrice() {
        return price;
    }

    public String getGrade() {
        // Map speed to a grade (example logic)
        try {
            int speedValue = Integer.parseInt(speed);
            if (speedValue > 200) {
                return "A"; // Example grade
            } else if (speedValue > 150) {
                return "B";
            } else {
                return "C";
            }
        } catch (NumberFormatException e) {
            // Handle cases where speed is not a valid integer (e.g., return a default
            // grade)
            return "N/A"; // Or some other appropriate default
        }
    }
}