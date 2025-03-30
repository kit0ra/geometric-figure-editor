package com.geometriceditor.model;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

public class RegularPolygon extends Shape {
    private int numberOfSides;
    private int sideLength;
    private int radius;

    // Constructors
    public RegularPolygon() {
        super();
        setPosition(new Point(0, 0));
        this.numberOfSides = 6; // Default hexagon
        this.sideLength = 50;
        calculateRadius();
    }

    public RegularPolygon(int x, int y, int sides, int sideLength) {
        super();
        setPosition(new Point(x, y));
        this.numberOfSides = sides;
        this.sideLength = sideLength;
        calculateRadius();
    }

    public RegularPolygon(RegularPolygon other) {
        super(other);
        setPosition(new Point(other.getX(), other.getY()));
        this.numberOfSides = other.numberOfSides;
        this.sideLength = other.sideLength;
        this.radius = other.radius;
    }

    // Calculate the circumscribed circle radius
    private void calculateRadius() {
        this.radius = (int) (sideLength / (2 * Math.sin(Math.PI / numberOfSides)));
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
        renderer.drawRegularPolygon(g2d, this);

        // Restore the original transform
        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean contains(Point point) {
        int[] xPoints = new int[numberOfSides];
        int[] yPoints = new int[numberOfSides];

        for (int i = 0; i < numberOfSides; i++) {
            double angle = 2 * Math.PI * i / numberOfSides;
            xPoints[i] = (int) (getX() + radius * Math.cos(angle));
            yPoints[i] = (int) (getY() + radius * Math.sin(angle));
        }

        Polygon poly = new Polygon(xPoints, yPoints, numberOfSides);
        return poly.contains(point.x, point.y);
    }

    @Override
    public Shape clone() {
        return new RegularPolygon(this);
    }

    // Getters and Setters
    public int getNumberOfSides() {
        return numberOfSides;
    }

    public void setNumberOfSides(int numberOfSides) {
        this.numberOfSides = numberOfSides;
        calculateRadius();
    }

    public int getSideLength() {
        return sideLength;
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
        calculateRadius();
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public Point getGeometricCenter() {
        return new Point(getX(), getY());
    }

    @Override
    public String toString() {
        return "RegularPolygon{" +
                "id='" + id + '\'' +
                ", position=" + getX() + "," + getY() +
                ", sides=" + numberOfSides +
                ", sideLength=" + sideLength +
                ", rotation=" + rotation +
                '}';
    }
}
