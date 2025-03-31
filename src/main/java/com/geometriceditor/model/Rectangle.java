package com.geometriceditor.model;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Rectangle extends Shape {
    private int width;
    private int height;
    private float cornerRadius;

    // Constructors
    public Rectangle() {
        super();
        this.width = 100;
        this.height = 50;
        this.cornerRadius = 0;
    }

    public Rectangle(int x, int y, int width, int height) {
        super();
        this.position = new Point(x, y);
        this.width = width;
        this.height = height;
        this.cornerRadius = 0;
    }

    public Rectangle(Rectangle other) {
        super(other);
        this.width = other.width;
        this.height = other.height;
        this.cornerRadius = other.cornerRadius;
    }

    @Override
    public void draw(Graphics2D g2d, com.geometriceditor.rendering.ShapeRenderer renderer) {
        // Store the original transform
        AffineTransform oldTransform = g2d.getTransform();

        // Apply rotation
        if (rotation != 0) {
            Point center = getGeometricCenter();
            g2d.rotate(Math.toRadians(rotation), center.x, center.y);
        }

        // Delegate drawing to the renderer
        // Note: The AWTRenderer currently doesn't support cornerRadius.
        // This could be added to the ShapeRenderer interface and implementations if
        // needed.
        renderer.drawRectangle(g2d, this);

        // Restore the original transform
        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean contains(Point point) {
        Rectangle2D rect = new Rectangle2D.Double(
                position.x,
                position.y,
                width,
                height);
        return rect.contains(point.x, point.y);
    }

    @Override
    public Shape clone() {
        return new Rectangle(this);
    }

    // Getters and Setters
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    @Override
    public Point getGeometricCenter() {
        return new Point(position.x + width / 2, position.y + height / 2);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "id='" + id + '\'' +
                ", position=" + position.x + "," + position.y +
                ", width=" + width +
                ", height=" + height +
                ", rotation=" + rotation +
                '}';
    }

    public boolean intersects(Rectangle other) {
        // Create Rectangle2D objects for both rectangles
        Rectangle2D thisRect = new Rectangle2D.Double(
                this.position.x,
                this.position.y,
                this.width,
                this.height);

        Rectangle2D otherRect = new Rectangle2D.Double(
                other.position.x,
                other.position.y,
                other.width,
                other.height);

        // Check for intersection
        return thisRect.intersects(otherRect);
    }

    @Override
    public <R> R accept(com.geometriceditor.visitor.ShapeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
