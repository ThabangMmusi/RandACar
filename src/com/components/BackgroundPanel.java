package com.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import com.data.CarData;

public class BackgroundPanel extends JPanel {
    // Constants
    private static final String APP_LOGO_PATH = "assets/logos/RandACar.png";
    private static final String BG_IMAGE_PATH = "assets/images/bg.jpg";

    private static final double LARGE_TEXT_AREA_WIDTH_RATIO = 0.90;
    private static final double LARGE_TEXT_AREA_HEIGHT_RATIO = 0.40;
    private static final double LARGE_TEXT_VERTICAL_CENTER_RATIO = 0.45;
    private static final double MAX_CAR_WIDTH_RATIO = 0.65;
    private static final double MAX_CAR_HEIGHT_RATIO = 0.65;
    private static final double CAR_VERTICAL_CENTER_RATIO = 0.5;

    private static final Color LARGE_TEXT_COLOR = new Color(200, 200, 200, 70);
    private static final int ANIMATION_DELAY = 16; // ~60 FPS
    private static final int CAR_ANIMATION_SPEED = 18;
    private static final float TEXT_FADE_SPEED = 0.05f;
    private static final int INITIAL_CAR_OFFSET = 200;

    // Instance variables
    private Image background;
    private Image logoImage;
    private Image carImage;
    private CarData selectedCar;

    private int carOffsetX = INITIAL_CAR_OFFSET;
    private float textOpacity = 0f;

    private Timer animationTimer;

    /**
     * Creates a new BackgroundPanel
     */
    public BackgroundPanel() {
        setOpaque(false);
        logoImage = loadImage(APP_LOGO_PATH);
        background = loadImage(BG_IMAGE_PATH);
        setupAnimation();
    }

    /**
     * Loads an image from the specified path
     */
    private Image loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImageLoadStatus() == MediaTracker.COMPLETE ? icon.getImage() : null;
    }

    /**
     * Sets the selected car and triggers animation
     */
    public void setSelectedCar(CarData selectedCar) {
        this.selectedCar = selectedCar;
        this.carImage = (selectedCar != null) ? loadImage(selectedCar.getPreviewImagePath()) : null;
        resetAnimation();
    }

    /**
     * Returns the currently selected car
     */
    public CarData getSelectedCar() {
        return selectedCar;
    }

    /**
     * Sets up the animation timer
     */
    private void setupAnimation() {
        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            boolean repaintNeeded = false;
            
            // Fade in text
            if (textOpacity < 1f) {
                textOpacity = Math.min(1f, textOpacity + TEXT_FADE_SPEED);
                repaintNeeded = true;
            }
            
            // Move car
            if (carOffsetX > 0) {
                carOffsetX = Math.max(0, carOffsetX - CAR_ANIMATION_SPEED);
                repaintNeeded = true;
            }
            
            if (!repaintNeeded) {
                animationTimer.stop();
            }
            repaint();
        });
    }

    /**
     * Resets the animation state and starts animation
     */
    private void resetAnimation() {
        textOpacity = 0f;
        carOffsetX = INITIAL_CAR_OFFSET;
        animationTimer.restart();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        setupRendering(g2d);

        // Draw background
        drawBackground(g2d, width, height);
        
        // Effective width for other elements
        int effectiveWidth = width - 50;
        
        // Draw logo
        drawLogo(g2d);
        
        // Draw car model text
        drawCarModelText(g2d, effectiveWidth, height);
        
        // Draw car image
        drawCarImage(g2d, effectiveWidth, height);

        g2d.dispose();
    }

    /**
     * Draws the background image or fallback color
     */
    private void drawBackground(Graphics2D g2d, int width, int height) {
        if (background != null) {
            g2d.drawImage(background, 0, 0, width, height, this);
        } else {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(0, 0, width, height);
        }
    }

    /**
     * Draws the logo in the top-left corner
     */
    private void drawLogo(Graphics2D g2d) {
        if (logoImage != null) {
            Dimension dim = scaleToFit(logoImage.getWidth(this), logoImage.getHeight(this), 132, 86);
            g2d.drawImage(logoImage, 48, 32, dim.width, dim.height, this);
        }
    }

    /**
     * Draws the car model text with animation
     */
    private void drawCarModelText(Graphics2D g2d, int width, int height) {
        if (selectedCar != null && selectedCar.getModel() != null) {
            String model = selectedCar.getModel();
            int maxTextW = (int) (width * LARGE_TEXT_AREA_WIDTH_RATIO);
            int maxTextH = (int) (height * LARGE_TEXT_AREA_HEIGHT_RATIO);
            
            Font font = getAutoSizedFont(g2d, model, "SansSerif", Font.BOLD, maxTextW, maxTextH);
            g2d.setFont(font);

            // Apply fade-in effect
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textOpacity));
            g2d.setColor(LARGE_TEXT_COLOR);

            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D bounds = fm.getStringBounds(model, g2d);
            int textX = (width - (int) bounds.getWidth()) / 2;
            int textY = (int) (height * LARGE_TEXT_VERTICAL_CENTER_RATIO + fm.getAscent() / 2);

            g2d.drawString(model, textX, textY - 200);
            g2d.setComposite(AlphaComposite.SrcOver); // Reset composite
        }
    }

    /**
     * Draws the car image with animation
     */
    private void drawCarImage(Graphics2D g2d, int width, int height) {
        if (carImage != null) {
            int maxCarW = (int) (width * MAX_CAR_WIDTH_RATIO);
            int maxCarH = (int) (height * MAX_CAR_HEIGHT_RATIO);
            
            Dimension dim = scaleToFit(carImage.getWidth(this), carImage.getHeight(this), maxCarW, maxCarH);
            int carX = (width - dim.width) / 2 + carOffsetX - 70;
            int carY = (int) (height * CAR_VERTICAL_CENTER_RATIO - dim.height / 2);
            
            g2d.drawImage(carImage, carX, carY, dim.width, dim.height, this);
        }
    }

    /**
     * Sets up rendering hints for better quality
     */
    private void setupRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    /**
     * Scales dimensions to fit within maximum bounds while preserving aspect ratio
     */
    private Dimension scaleToFit(int originalW, int originalH, int maxW, int maxH) {
        double aspectRatio = (double) originalW / originalH;
        int newW = maxW;
        int newH = (int) (newW / aspectRatio);
        
        if (newH > maxH) {
            newH = maxH;
            newW = (int) (newH * aspectRatio);
        }
        
        return new Dimension(newW, newH);
    }

    /**
     * Gets a font sized to fit the text within the given dimensions
     */
    private Font getAutoSizedFont(Graphics2D g, String text, String fontName, int style, int maxW, int maxH) {
        int size = Math.min(maxH * 2, 400);
        Font font;
        
        do {
            font = new Font(fontName, style, size--);
            FontMetrics metrics = g.getFontMetrics(font);
            
            if (metrics.getHeight() <= maxH && metrics.stringWidth(text) <= maxW) {
                break;
            }
        } while (size > 12);
        
        return font;
    }
}