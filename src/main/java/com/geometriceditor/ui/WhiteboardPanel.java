package com.geometriceditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.geometriceditor.model.Shape;

public class WhiteboardPanel extends JPanel {
    private List<Shape> shapes;
    private Shape selectedShape;
    private Point dragStartPoint;

    public WhiteboardPanel() {
        shapes = new ArrayList<>();

        // Enable double buffering for smoother rendering
        setDoubleBuffered(true);

        // Set a white background
        setBackground(Color.WHITE);

        // Add mouse listeners for interaction
        addMouseListeners();
    }

    private void addMouseListeners() {
        // Mouse Listener for selection and dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseRelease(e.getPoint());
            }
        });

        // Mouse Motion Listener for dragging
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e.getPoint());
            }
        });
    }

    private void handleMousePress(Point point) {
        // Find shape under mouse
        selectedShape = findShapeAtPoint(point);

        if (selectedShape != null) {
            // Prepare for dragging
            dragStartPoint = point;
        }
    }

    private void handleMouseDrag(Point point) {
        if (selectedShape != null && dragStartPoint != null) {
            // Calculate drag delta
            int dx = point.x - dragStartPoint.x;
            int dy = point.y - dragStartPoint.y;

            // Move selected shape
            selectedShape.move(dx, dy);

            // Update drag start point
            dragStartPoint = point;

            // Repaint to show movement
            repaint();
        }
    }

    private void handleMouseRelease(Point point) {
        // Reset drag-related variables
        dragStartPoint = null;
    }

    private Shape findShapeAtPoint(Point point) {
        // Reverse iteration to select top-most shape
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(new com.geometriceditor.model.Shape.Point(point.x, point.y))) {
                return shape;
            }
        }
        return null;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all shapes
        for (Shape shape : shapes) {
            shape.draw(g2d);
        }

        // Highlight selected shape
        if (selectedShape != null) {
            drawSelectionBorder(g2d, selectedShape);
        }
    }

    private void drawSelectionBorder(Graphics2D g2d, Shape shape) {
        // Implement selection border drawing
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        // Implement based on shape type
    }
}
