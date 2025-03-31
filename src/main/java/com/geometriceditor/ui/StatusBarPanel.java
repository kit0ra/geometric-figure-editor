package com.geometriceditor.ui;

import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.geometriceditor.command.CommandExecutionListener;
import com.geometriceditor.command.CommandManager;
import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;

/**
 * A status bar panel to display information like shape counts.
 * It listens to CommandManager to update counts after operations.
 */
public class StatusBarPanel extends JPanel implements CommandExecutionListener {

    private final WhiteboardPanel whiteboard;
    private final JLabel rectangleCountLabel;
    private final JLabel polygonCountLabel;
    private final JLabel totalCountLabel;

    public StatusBarPanel(WhiteboardPanel whiteboard, CommandManager commandManager) {
        this.whiteboard = Objects.requireNonNull(whiteboard, "Whiteboard cannot be null");
        Objects.requireNonNull(commandManager, "CommandManager cannot be null").addListener(this);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        rectangleCountLabel = new JLabel("Rectangles: 0");
        polygonCountLabel = new JLabel("Polygons: 0");
        totalCountLabel = new JLabel("Total: 0");

        add(rectangleCountLabel);
        add(new JSeparator(SwingConstants.VERTICAL)); // Add separators for visual clarity
        add(polygonCountLabel);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(totalCountLabel);

        updateCounts(); // Initial count
    }

    /**
     * Updates the counts displayed on the status bar by iterating
     * through the shapes on the whiteboard. Handles ShapeGroups recursively.
     */
    public void updateCounts() {
        int rectCount = 0;
        int polyCount = 0;
        int totalCount = 0;

        // Use the whiteboard's iterator
        for (Shape shape : whiteboard) {
            ShapeCounter counter = countShapesRecursive(shape);
            rectCount += counter.rectangles;
            polyCount += counter.polygons;
            totalCount += counter.total;
        }

        rectangleCountLabel.setText("Rectangles: " + rectCount);
        polygonCountLabel.setText("Polygons: " + polyCount);
        totalCountLabel.setText("Total: " + totalCount);
    }

    /**
     * Helper method to recursively count shapes within a shape (handling groups).
     */
    private ShapeCounter countShapesRecursive(Shape shape) {
        ShapeCounter counter = new ShapeCounter();
        if (shape instanceof Rectangle) {
            counter.rectangles = 1;
            counter.total = 1;
        } else if (shape instanceof RegularPolygon) {
            counter.polygons = 1;
            counter.total = 1;
        } else if (shape instanceof ShapeGroup) {
            // Recursively count shapes within the group
            for (Shape child : (ShapeGroup) shape) { // Use ShapeGroup's iterator
                ShapeCounter childCounter = countShapesRecursive(child);
                counter.rectangles += childCounter.rectangles;
                counter.polygons += childCounter.polygons;
                counter.total += childCounter.total;
            }
        }
        // Ignore other potential shape types for now
        return counter;
    }

    /**
     * Called by CommandManager after a command is executed, undone, or redone.
     */
    @Override
    public void commandExecuted() {
        // Update the counts when the model might have changed
        updateCounts();
    }

    /**
     * Helper class to hold counts during recursion.
     */
    private static class ShapeCounter {
        int rectangles = 0;
        int polygons = 0;
        int total = 0;
    }
}
