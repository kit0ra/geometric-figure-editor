package com.geometriceditor.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;

public class ToolbarPanel extends JPanel {
    private ShapeFactory shapeFactory;
    private WhiteboardPanel whiteboard;

    public ToolbarPanel(ShapeFactory shapeFactory) {
        this.shapeFactory = shapeFactory;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add toolbar buttons
        addShapeButtons();
        addActionButtons();
    }

    private void addShapeButtons() {
        // Rectangle Button
        JButton rectangleButton = createShapeButton("Rectangle", e -> {
            Rectangle rect = shapeFactory.createRectangle(50, 50, 100, 50);
            whiteboard.addShape(rect);
        });
        add(rectangleButton);

        // Polygon Button
        JButton polygonButton = createShapeButton("Polygon", e -> {
            RegularPolygon polygon = shapeFactory.createRegularPolygon(50, 50, 6, 50);
            whiteboard.addShape(polygon);
        });
        add(polygonButton);
    }

    private void addActionButtons() {
        // Undo Button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            // TODO: Implement undo logic
        });
        add(undoButton);

        // Redo Button
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            // TODO: Implement redo logic
        });
        add(redoButton);
    }

    private JButton createShapeButton(String name, ActionListener action) {
        JButton button = new JButton(name);
        button.addActionListener(action);
        return button;
    }

    // Method to set the whiteboard (called from MainWindow)
    public void setWhiteboard(WhiteboardPanel whiteboard) {
        this.whiteboard = whiteboard;
    }
}
