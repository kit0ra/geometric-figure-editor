package com.geometriceditor.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * Represents a base abstract shape with common properties and methods.
 */
public abstract class Shape implements Cloneable, Serializable {
    // Basic shape properties
    protected Point position;
    protected Color fillColor;
    protected Color borderColor;
    protected float rotation;
    protected Point rotationCenter;

    // Unique identifier for each shape
    protected String id;

    /**
     * Default constructor
     */
    public Shape() {
        this.position = new Point(0, 0);
        this.fillColor = Color.WHITE;
        this.borderColor = Color.BLACK;
        this.rotation = 0f;
        this.rotationCenter = this.position;
        this.id = java.util.UUID.randomUUID().toString();
    }

    /**
     * Copy constructor
     *
     * @param other Shape to copy
     */
    public Shape(Shape other) {
        this.position = new Point(other.position);
        this.fillColor = other.fillColor;
        this.borderColor = other.borderColor;
        this.rotation = other.rotation;
        this.rotationCenter = new Point(other.rotationCenter);
        this.id = java.util.UUID.randomUUID().toString();
    }

    /**
     * Abstract method to draw the shape
     *
     * @param g2d Graphics2D context
     */
    public abstract void draw(Graphics2D g2d);

    /**
     * Abstract method to check if a point is inside the shape
     *
     * @param point Point to check
     * @return true if point is inside, false otherwise
     */
    public abstract boolean contains(Point point);

    /**
     * Clones the shape
     *
     * @return Cloned shape
     */
    @Override
    public abstract Shape clone();

    // Getters and Setters
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void move(int dx, int dy) {
        this.position.x += dx;
        this.position.y += dy;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Point getRotationCenter() {
        return rotationCenter;
    }

    public void setRotationCenter(Point rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    public Point getGeometricCenter() {
        return new Point(position.x, position.y); // Default to position
    }

    public String getId() {
        return id;
    }

    /**
     * Represents a 2D point
     */
    public static class Point implements Cloneable, Serializable {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(Point other) {
            this.x = other.x;
            this.y = other.y;
        }

        @Override
        public Point clone() {
            try {
                return (Point) super.clone();
            } catch (CloneNotSupportedException e) {
                return new Point(this.x, this.y);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }
}
