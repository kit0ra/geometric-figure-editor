package com.geometriceditor.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.model.Shape;

public class ToolbarPanel extends JPanel {
    private ShapeFactory shapeFactory;
    private WhiteboardPanel whiteboard;

    public ToolbarPanel(ShapeFactory shapeFactory) {
        this.shapeFactory = Objects.requireNonNull(shapeFactory, "ShapeFactory cannot be null");
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add toolbar buttons
        addShapeButtons();
        addActionButtons();
    }

    private void addShapeButtons() {
        // Rectangle Button
        JButton rectangleButton = createShapeButton("Rectangle", e -> {
            addShapeToWhiteboard(() -> {
                return shapeFactory.createRectangle(50, 50, 100, 50);
            });
        });
        add(rectangleButton);

        // Polygon Button
        JButton polygonButton = createShapeButton("Polygon", e -> {
            addShapeToWhiteboard(() -> {
                return shapeFactory.createRegularPolygon(50, 50, 6, 50);
            });
        });
        add(polygonButton);
    }

    private void addActionButtons() {
        // Undo Button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener((ActionEvent e) -> {
            if (whiteboard != null) {
                whiteboard.undo();
            } else {
                showWhiteboardNotInitializedMessage();
            }
        });
        add(undoButton);

        // Redo Button
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.redo();
            } else {
                showWhiteboardNotInitializedMessage();
            }
        });
        add(redoButton);

        // Group Button
        JButton groupButton = new JButton("Group");
        groupButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.groupSelectedShapes();
            }
        });
        add(groupButton);

        // Ungroup Button
        JButton ungroupButton = new JButton("Ungroup");
        ungroupButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.ungroupSelectedShape();
            }
        });
        add(ungroupButton);

    }

    private JButton createShapeButton(String name, ActionListener action) {
        JButton button = new JButton(name);
        button.addActionListener(action);
        return button;
    }

    // Method to set the whiteboard (called from MainWindow)
    public void setWhiteboard(WhiteboardPanel whiteboard) {
        this.whiteboard = Objects.requireNonNull(whiteboard, "Whiteboard cannot be null");
    }

    // Generic method to add shape with null safety
    private void addShapeToWhiteboard(ShapeSupplier shapeSupplier) {
        if (whiteboard == null) {
            showWhiteboardNotInitializedMessage();
            return;
        }

        try {
            Shape shape = shapeSupplier.get();
            whiteboard.addShape(shape);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding shape: " + e.getMessage(),
                    "Shape Addition Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show message when whiteboard is not initialized
    private void showWhiteboardNotInitializedMessage() {
        JOptionPane.showMessageDialog(this,
                "Whiteboard has not been initialized. Please set the whiteboard first.",
                "Initialization Error",
                JOptionPane.WARNING_MESSAGE);
    }

    // Functional interface for shape creation
    @FunctionalInterface
    private interface ShapeSupplier {
        Shape get();
    }
}
