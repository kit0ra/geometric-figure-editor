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
        this.numberOfSides = 6; // Default hexagon
        this.sideLength = 50;
        calculateRadius();
    }

    public RegularPolygon(int x, int y, int sides, int sideLength) {
        super();
        this.position = new Point(x, y);
        this.numberOfSides = sides;
        this.sideLength = sideLength;
        calculateRadius();
    }

    public RegularPolygon(RegularPolygon other) {
        super(other);
        this.numberOfSides = other.numberOfSides;
        this.sideLength = other.sideLength;
        this.radius = other.radius;
    }

    // Calculate the circumscribed circle radius
    private void calculateRadius() {
        this.radius = (int) (sideLength / (2 * Math.sin(Math.PI / numberOfSides)));
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Store the original transform
        AffineTransform oldTransform = g2d.getTransform();

        // Apply rotation
        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation),
                    rotationCenter.x,
                    rotationCenter.y);
        }

        // Create polygon points
        int[] xPoints = new int[numberOfSides];
        int[] yPoints = new int[numberOfSides];

        for (int i = 0; i < numberOfSides; i++) {
            double angle = 2 * Math.PI * i / numberOfSides;
            xPoints[i] = (int) (position.x + radius * Math.cos(angle));
            yPoints[i] = (int) (position.y + radius * Math.sin(angle));
        }

        Polygon poly = new Polygon(xPoints, yPoints, numberOfSides);

        // Fill polygon
        g2d.setColor(fillColor);
        g2d.fillPolygon(poly);

        // Draw polygon border
        g2d.setColor(borderColor);
        g2d.drawPolygon(poly);

        // Restore the original transform
        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean contains(Point point) {
        int[] xPoints = new int[numberOfSides];
        int[] yPoints = new int[numberOfSides];

        for (int i = 0; i < numberOfSides; i++) {
            double angle = 2 * Math.PI * i / numberOfSides;
            xPoints[i] = (int) (position.x + radius * Math.cos(angle));
            yPoints[i] = (int) (position.y + radius * Math.sin(angle));
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
    public String toString() {
        return "RegularPolygon{" +
                "id='" + id + '\'' +
                ", position=" + position.x + "," + position.y +
                ", sides=" + numberOfSides +
                ", sideLength=" + sideLength +
                ", rotation=" + rotation +
                '}';
    }
}
