package com.geometriceditor.visitor;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;
// Removed invalid alias import

/**
 * A visitor to calculate the bounding box of a shape.
 * Returns a java.awt.Rectangle representing the bounds.
 */
public class BoundingBoxVisitor implements ShapeVisitor<java.awt.Rectangle> {

    @Override
    public java.awt.Rectangle visit(Rectangle rectangle) {
        // For a non-rotated rectangle, the bounds are simple
        if (rectangle.getRotation() == 0) {
            return new java.awt.Rectangle(
                    rectangle.getPosition().x,
                    rectangle.getPosition().y,
                    rectangle.getWidth(),
                    rectangle.getHeight());
        } else {
            // For rotated rectangle, transform its shape and get bounds
            Rectangle2D rect2D = new Rectangle2D.Double(
                    rectangle.getPosition().x,
                    rectangle.getPosition().y,
                    rectangle.getWidth(),
                    rectangle.getHeight());
            AffineTransform tx = AffineTransform.getRotateInstance(
                    Math.toRadians(rectangle.getRotation()),
                    rectangle.getRotationCenter().x,
                    rectangle.getRotationCenter().y);
            // Use fully qualified name for java.awt.Shape
            java.awt.Shape transformedShape = tx.createTransformedShape(rect2D);
            return transformedShape.getBounds(); // Get bounds of the transformed shape
        }
    }

    @Override
    public java.awt.Rectangle visit(RegularPolygon polygon) {
        // Calculate bounds based on radius (approximation for non-rotated)
        // For rotated, similar transformation logic as Rectangle would be needed
        // if precise rotated bounds are required.
        if (polygon.getRotation() == 0) {
            int radius = polygon.getRadius();
            return new java.awt.Rectangle(
                    polygon.getPosition().x - radius,
                    polygon.getPosition().y - radius,
                    radius * 2,
                    radius * 2);
        } else {
            // Create the AWT Polygon
            int[] xPoints = new int[polygon.getNumberOfSides()];
            int[] yPoints = new int[polygon.getNumberOfSides()];
            int radius = polygon.getRadius();
            for (int i = 0; i < polygon.getNumberOfSides(); i++) {
                double angle = 2 * Math.PI * i / polygon.getNumberOfSides();
                xPoints[i] = (int) (polygon.getPosition().x + radius * Math.cos(angle));
                yPoints[i] = (int) (polygon.getPosition().y + radius * Math.sin(angle));
            }
            java.awt.Polygon awtPoly = new java.awt.Polygon(xPoints, yPoints, polygon.getNumberOfSides());

            // Apply rotation
            AffineTransform tx = AffineTransform.getRotateInstance(
                    Math.toRadians(polygon.getRotation()),
                    polygon.getRotationCenter().x,
                    polygon.getRotationCenter().y);
            // Use fully qualified name for java.awt.Shape
            java.awt.Shape transformedShape = tx.createTransformedShape(awtPoly);
            return transformedShape.getBounds();
        }
    }

    @Override
    public java.awt.Rectangle visit(ShapeGroup group) {
        if (group.getShapes().isEmpty()) {
            // Return an empty rectangle or based on group's position if needed
            return new java.awt.Rectangle(group.getPosition().x, group.getPosition().y, 0, 0);
        }

        // Calculate the union of the bounding boxes of all children
        java.awt.Rectangle totalBounds = null;
        for (Shape child : group.getShapes()) {
            java.awt.Rectangle childBounds = child.accept(this); // Recursively visit children
            if (totalBounds == null) {
                totalBounds = childBounds;
            } else {
                totalBounds = totalBounds.union(childBounds);
            }
        }
        // Should never be null if shapes list is not empty, but check for safety
        return totalBounds != null ? totalBounds
                : new java.awt.Rectangle(group.getPosition().x, group.getPosition().y, 0, 0);
    }
}
