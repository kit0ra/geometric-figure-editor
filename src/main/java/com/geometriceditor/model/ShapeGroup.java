package com.geometriceditor.model;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ShapeGroup extends Shape {
    private List<Shape> shapes = new ArrayList<>();

    public ShapeGroup() {
        super();
    }

    public void addShape(Shape shape) {
        shape.setParent(this);
        shapes.add(shape);
        recalculateBounds();
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        recalculateBounds();
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    private void recalculateBounds() {
        if (shapes.isEmpty())
            return;

        // Find the bounding box of all shapes
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Shape shape : shapes) {
            Point pos = shape.getPosition();
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            // You might need to add methods to get shape dimensions in your concrete shape
            // classes
        }

        // Set the group's position to the top-left of the bounding box
        this.position = new Point(minX, minY);
    }

    @Override
    public void draw(Graphics2D g2d, com.geometriceditor.rendering.ShapeRenderer renderer) {
        // Optionally call the renderer for the group itself (e.g., draw bounding box)
        renderer.drawShapeGroup(g2d, this);

        // Draw all shapes in the group, passing the renderer down
        for (Shape shape : shapes) {
            shape.draw(g2d, renderer); // Pass the renderer
        }
    }

    @Override
    public boolean contains(Point point) {
        // Check if point is contained in any shape in the group
        return shapes.stream().anyMatch(shape -> shape.contains(point));
    }

    @Override
    public Shape clone() {
        ShapeGroup clonedGroup = new ShapeGroup();
        for (Shape shape : shapes) {
            clonedGroup.addShape(shape.clone());
        }
        return clonedGroup;
    }

    // Override move to move all shapes in the group
    @Override
    public void move(int dx, int dy) {
        shapes.forEach(shape -> shape.move(dx, dy));
        super.move(dx, dy);
        recalculateBounds();
    }
}
