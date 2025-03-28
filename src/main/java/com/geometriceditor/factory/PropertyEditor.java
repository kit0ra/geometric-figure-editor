package com.geometriceditor.factory;

import java.awt.Color;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.Shape.Point;

public class PropertyEditor {
    // Singleton pattern
    private static PropertyEditor instance;

    private PropertyEditor() {
    }

    public static PropertyEditor getInstance() {
        if (instance == null) {
            instance = new PropertyEditor();
        }
        return instance;
    }

    // Generic property editing methods
    public void setPosition(Shape shape, int x, int y) {
        shape.setPosition(new Point(x, y));
    }

    public void setColor(Shape shape, Color fillColor, Color borderColor) {
        shape.setFillColor(fillColor);
        shape.setBorderColor(borderColor);
    }

    public void setRotation(Shape shape, float rotation) {
        shape.setRotation(rotation);
    }

    // Rectangle-specific properties
    public void setRectangleProperties(Rectangle rectangle,
            int width,
            int height,
            float cornerRadius) {
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setCornerRadius(cornerRadius);
    }

    // Polygon-specific properties
    public void setPolygonProperties(RegularPolygon polygon,
            int sides,
            int sideLength) {
        polygon.setNumberOfSides(sides);
        polygon.setSideLength(sideLength);
    }

    // Create a memento-like mechanism for undo/redo
    public ShapeMemento createMemento(Shape shape) {
        return new ShapeMemento(shape);
    }

    // Memento inner class to store shape state
    public static class ShapeMemento {
        private final Shape.Point position;
        private final Color fillColor;
        private final Color borderColor;
        private final float rotation;

        public ShapeMemento(Shape shape) {
            this.position = new Point(shape.getPosition());
            this.fillColor = shape.getFillColor();
            this.borderColor = shape.getBorderColor();
            this.rotation = shape.getRotation();
        }

        public void restore(Shape shape) {
            shape.setPosition(position);
            shape.setFillColor(fillColor);
            shape.setBorderColor(borderColor);
            shape.setRotation(rotation);
        }
    }
}
