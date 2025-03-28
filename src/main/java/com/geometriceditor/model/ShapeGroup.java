package com.geometriceditor.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ShapeGroup extends Shape {
    private List<Shape> shapes;

    public ShapeGroup() {
        super();
        this.shapes = new ArrayList<>();
    }

    public ShapeGroup(ShapeGroup other) {
        super(other);
        this.shapes = new ArrayList<>();
        for (Shape shape : other.shapes) {
            this.shapes.add(shape.clone());
        }
    }

    // Add a shape to the group
    public void addShape(Shape shape) {
        shapes.add(shape);
        // Update group position if needed
        updateGroupPosition();
    }

    // Remove a shape from the group
    public void removeShape(Shape shape) {
        shapes.remove(shape);
        updateGroupPosition();
    }

    // Get all shapes in the group
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    // Update group position based on child shapes
    private void updateGroupPosition() {
        if (shapes.isEmpty())
            return;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (Shape shape : shapes) {
            Point pos = shape.getPosition();
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
        }

        this.position = new Point(minX, minY);
    }

    @Override
    public void draw(Graphics2D g2d) {
        for (Shape shape : shapes) {
            shape.draw(g2d);
        }
    }

    @Override
    public boolean contains(Point point) {
        for (Shape shape : shapes) {
            if (shape.contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Shape clone() {
        return new ShapeGroup(this);
    }

    // Override move to move all contained shapes
    @Override
    public void move(int dx, int dy) {
        for (Shape shape : shapes) {
            shape.move(dx, dy);
        }
        super.move(dx, dy);
    }

    // Override setters to apply to all contained shapes
    @Override
    public void setFillColor(Color fillColor) {
        for (Shape shape : shapes) {
            shape.setFillColor(fillColor);
        }
        super.setFillColor(fillColor);
    }

    @Override
    public void setRotation(float rotation) {
        for (Shape shape : shapes) {
            // Adjust individual shape rotations relative to group rotation
            float relativeDelta = rotation - this.rotation;
            shape.setRotation(shape.getRotation() + relativeDelta);
        }
        super.setRotation(rotation);
    }

    @Override
    public String toString() {
        return "ShapeGroup{" +
                "id='" + id + '\'' +
                ", position=" + position.x + "," + position.y +
                ", shapes=" + shapes.size() +
                '}';
    }
}
