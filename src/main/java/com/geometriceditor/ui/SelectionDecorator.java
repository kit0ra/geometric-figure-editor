package com.geometriceditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform; // Import AffineTransform

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.Shape.Point; // Import Point

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
        // Save original state
        Color originalColor = g2d.getColor();
        java.awt.Stroke originalStroke = g2d.getStroke();
        AffineTransform oldTransform = g2d.getTransform(); // Save original transform

        // Apply rotation if necessary
        float rotation = decoratedShape.getRotation();
        if (rotation != 0) {
            Point center = decoratedShape.getGeometricCenter();
            g2d.rotate(Math.toRadians(rotation), center.x, center.y);
        }

        // Set selection style
        g2d.setColor(SELECTION_COLOR);
        g2d.setStroke(SELECTION_STROKE);

        // Draw selection border based on shape type
        if (decoratedShape instanceof Rectangle) {
            Rectangle rect = (Rectangle) decoratedShape;
            // Check if the shape is part of a ShapeGroup
            if (decoratedShape.getParent() == null) {
                g2d.drawRoundRect(
                        rect.getPosition().x - 3,
                        rect.getPosition().y - 3,
                        rect.getWidth() + 6,
                        rect.getHeight() + 6,
                        (int) rect.getCornerRadius(),
                        (int) rect.getCornerRadius());
            }
        } else if (decoratedShape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) decoratedShape;
            // Check if the shape is part of a ShapeGroup
            if (decoratedShape.getParent() == null) {
                // Get the actual geometric center of the polygon
                Point center = poly.getGeometricCenter();

                // Draw a small circle at the center point
                int centerRadius = 3;
                g2d.drawOval(
                        center.x - centerRadius,
                        center.y - centerRadius,
                        centerRadius * 2,
                        centerRadius * 2);
            }

            // Draw the selection border around the polygon
            int radius = poly.getRadius();
            g2d.drawOval(
                    poly.getPosition().x - radius - 3,
                    poly.getPosition().y - radius - 3,
                    radius * 2 + 6,
                    radius * 2 + 6);
        }
        // Add handling for ShapeGroup if needed (e.g., draw bounding box for group
        // selection)
        // else if (decoratedShape instanceof ShapeGroup) { ... }

        // Restore original state
        g2d.setTransform(oldTransform); // Restore original transform
        g2d.setColor(originalColor);
        g2d.setStroke(originalStroke);
    }

    public Shape getDecoratedShape() {
        return decoratedShape;
    }
}
