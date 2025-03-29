package com.geometriceditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;

public class SelectionDecorator {
    private final Shape decoratedShape;
    private static final Color SELECTION_COLOR = new Color(0, 120, 215); // Nice blue selection color
    private static final BasicStroke SELECTION_STROKE = new BasicStroke(
            2,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            1,
            new float[] { 5 },
            0);

    public SelectionDecorator(Shape shape) {
        this.decoratedShape = shape;
    }

    public void drawSelection(Graphics2D g2d) {
        // Save original paint and stroke
        Color originalColor = g2d.getColor();
        java.awt.Stroke originalStroke = g2d.getStroke();

        // Set selection style
        g2d.setColor(SELECTION_COLOR);
        g2d.setStroke(SELECTION_STROKE);

        // Draw selection border based on shape type
        if (decoratedShape instanceof Rectangle) {
            Rectangle rect = (Rectangle) decoratedShape;
            g2d.drawRoundRect(
                    rect.getPosition().x - 3,
                    rect.getPosition().y - 3,
                    rect.getWidth() + 6,
                    rect.getHeight() + 6,
                    (int) rect.getCornerRadius(),
                    (int) rect.getCornerRadius());
        } else if (decoratedShape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) decoratedShape;
            // Calculate bounding box for polygon
            int radius = poly.getRadius();
            g2d.drawOval(
                    poly.getPosition().x - radius - 3,
                    poly.getPosition().y - radius - 3,
                    radius * 2 + 6,
                    radius * 2 + 6);
        }

        // Restore original paint and stroke
        g2d.setColor(originalColor);
        g2d.setStroke(originalStroke);
    }

    public Shape getDecoratedShape() {
        return decoratedShape;
    }
}
