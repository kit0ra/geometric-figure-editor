package com.geometriceditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.geometriceditor.command.AddShapeCommand;
import com.geometriceditor.command.CommandManager;
import com.geometriceditor.command.CompositeCommand;
import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;

public class WhiteboardPanel extends JPanel {
    private List<Shape> shapes;
    private Shape selectedShape;
    private Point dragStartPoint;
    private CommandManager commandManager = new CommandManager();
    private SelectionDecorator selectionDecorator;
    private List<Shape> selectedShapes = new ArrayList<>(); // For multiple selection
    private Point selectionStartPoint; // For rectangle selection
    private java.awt.Rectangle selectionRectangle; // For rectangle selection
    private boolean isCtrlPressed = false; // Track Ctrl key state

    public WhiteboardPanel() {
        shapes = new ArrayList<>();

        // Enable double buffering
        setDoubleBuffered(true);

        // Set background
        setBackground(Color.WHITE);

        // Make panel focusable for keyboard events
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = false;
                }
            }
        });

        // Add mouse listeners for interaction
        addMouseListeners();

    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isCtrlPressed = e.isControlDown();

                if (isCtrlPressed) {
                    // Ctrl+Click - add to selection
                    Shape clickedShape = findShapeAtPoint(e.getPoint());
                    if (clickedShape != null) {
                        if (selectedShapes.contains(clickedShape)) {
                            selectedShapes.remove(clickedShape); // Deselect if already selected
                        } else {
                            selectedShapes.add(clickedShape); // Add to selection
                        }
                    }
                } else {
                    // Regular click - start selection or drag
                    selectionStartPoint = e.getPoint();
                    Shape clickedShape = findShapeAtPoint(e.getPoint());

                    if (clickedShape == null) {
                        // Clicked on empty space - start rectangle selection
                        selectedShapes.clear();
                        selectionRectangle = new java.awt.Rectangle(
                                e.getX(), e.getY(), 0, 0);
                    } else if (!selectedShapes.contains(clickedShape)) {
                        // Clicked on unselected shape - make it the only selection
                        selectedShapes.clear();
                        selectedShapes.add(clickedShape);
                        dragStartPoint = e.getPoint();
                    } else {
                        // Clicked on already selected shape - prepare for drag
                        dragStartPoint = e.getPoint();
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectionRectangle != null) {
                    // Finalize rectangle selection
                    selectShapesInRectangle(selectionRectangle);
                    selectionRectangle = null;
                }
                dragStartPoint = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStartPoint != null && !selectedShapes.isEmpty()) {
                    // Move all selected shapes
                    int dx = e.getX() - dragStartPoint.x;
                    int dy = e.getY() - dragStartPoint.y;

                    for (Shape shape : selectedShapes) {
                        shape.move(dx, dy);
                    }
                    dragStartPoint = e.getPoint();
                } else if (selectionStartPoint != null) {
                    // Update selection rectangle
                    int x = Math.min(selectionStartPoint.x, e.getX());
                    int y = Math.min(selectionStartPoint.y, e.getY());
                    int width = Math.abs(e.getX() - selectionStartPoint.x);
                    int height = Math.abs(e.getY() - selectionStartPoint.y);

                    selectionRectangle = new java.awt.Rectangle(x, y, width, height);

                }
                repaint();
            }
        });
    }

    private void handleMousePress(Point point) {
        // Find shape under mouse
        selectedShape = findShapeAtPoint(point);

        if (selectedShape != null) {
            // Create selection decorator
            selectionDecorator = new SelectionDecorator(selectedShape);
            // Prepare for dragging
            dragStartPoint = point;
        } else {
            selectionDecorator = null;
        }
        repaint();
    }

    private void handleMouseDrag(Point point) {
        if (dragStartPoint != null && !selectedShapes.isEmpty()) {
            int dx = point.x - dragStartPoint.x;
            int dy = point.y - dragStartPoint.y;

            // Move all selected shapes
            for (Shape shape : selectedShapes) {
                shape.move(dx, dy);
            }
            dragStartPoint = point;
        } else if (selectionStartPoint != null) {
            // Update selection rectangle - use java.awt.Rectangle
            int x = Math.min(selectionStartPoint.x, point.x);
            int y = Math.min(selectionStartPoint.y, point.y);
            int width = Math.abs(point.x - selectionStartPoint.x);
            int height = Math.abs(point.y - selectionStartPoint.y);

            selectionRectangle = new java.awt.Rectangle(x, y, width, height);
        }
        repaint();
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

    public void directRemoveShape(Shape shape) {
        shapes.remove(shape);
        repaint();
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void clearShapes() {
        shapes.clear();
        selectedShape = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable antialiasing
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all shapes
        for (Shape shape : shapes) {
            shape.draw(g2d);
        }

        // Draw selection rectangles
        for (Shape shape : selectedShapes) {
            SelectionDecorator decorator = new SelectionDecorator(shape);
            decorator.drawSelection(g2d);
        }

        // Draw selection rectangle if active
        if (selectionRectangle != null) {
            g2d.setColor(new Color(0, 120, 215, 50)); // Semi-transparent blue
            g2d.fill(selectionRectangle);
            g2d.setColor(new Color(0, 120, 215));
            g2d.draw(selectionRectangle);
        }
    }

    private void drawSelectionBorder(Graphics2D g2d, Shape shape) {
        // Implement selection border drawing
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        // Implement based on shape type
    }

    public void directAddShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    public void addShape(Shape shape) {
        if (shape == null)
            return;
        commandManager.executeCommand(new AddShapeCommand(this, shape));
    }

    public void addMultipleShapes(List<Shape> shapes) {
        if (shapes == null || shapes.isEmpty()) {
            return;
        }

        CompositeCommand batch = new CompositeCommand();
        for (Shape shape : shapes) {
            batch.add(new AddShapeCommand(this, shape));
        }
        commandManager.executeCommand(batch);
    }

    public void undo() {
        commandManager.undo();
        repaint();
    }

    public void redo() {
        commandManager.redo();
        repaint();
    }

    // In WhiteboardPanel.java
    public void groupSelectedShapes() {
        if (selectedShape != null) {
            if (selectedShape instanceof ShapeGroup) {
                // Already a group - add to existing group
                ShapeGroup group = (ShapeGroup) selectedShape;
                // Add logic to add other selected shapes to this group
            } else {
                // Create new group
                ShapeGroup group = new ShapeGroup();
                group.addShape(selectedShape);
                shapes.remove(selectedShape);
                shapes.add(group);
                selectedShape = group;
                repaint();
            }
        }
    }

    public void ungroupSelectedShape() {
        if (selectedShape instanceof ShapeGroup) {
            ShapeGroup group = (ShapeGroup) selectedShape;
            shapes.remove(group);
            shapes.addAll(group.getShapes());
            repaint();
        }
    }

    private void selectShapesInRectangle(java.awt.Rectangle rect) {
        for (Shape shape : shapes) {
            java.awt.Rectangle shapeBounds = getShapeBounds(shape);
            if (rect.intersects(shapeBounds)) {
                if (!selectedShapes.contains(shape)) {
                    selectedShapes.add(shape);
                }
            }
        }
    }

    private java.awt.Rectangle getShapeBounds(Shape shape) {
        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            return new java.awt.Rectangle(
                    rect.getPosition().x,
                    rect.getPosition().y,
                    rect.getWidth(),
                    rect.getHeight());
        } else if (shape instanceof RegularPolygon) {
            RegularPolygon poly = (RegularPolygon) shape;
            int diameter = poly.getRadius() * 2;
            return new java.awt.Rectangle(
                    poly.getPosition().x - poly.getRadius(),
                    poly.getPosition().y - poly.getRadius(),
                    diameter,
                    diameter);
        }
        return new java.awt.Rectangle();
    }

    public void selectAll() {
        selectedShapes.clear();
        selectedShapes.addAll(shapes);
        repaint();
    }

    public void deselectAll() {
        selectedShapes.clear();
        repaint();
    }

    public void deleteSelected() {
        shapes.removeAll(selectedShapes);
        selectedShapes.clear();
        repaint();
    }

    public void groupSelected() {
        if (selectedShapes.size() > 1) {
            ShapeGroup group = new ShapeGroup();
            for (Shape shape : selectedShapes) {
                group.addShape(shape);
            }
            shapes.removeAll(selectedShapes);
            shapes.add(group);
            selectedShapes.clear();
            selectedShapes.add(group);
            repaint();
        }
    }

}
