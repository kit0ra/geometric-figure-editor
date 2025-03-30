package com.geometriceditor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geometriceditor.model.Shape;

public class RotateCommand implements Command {
    private final List<Shape> shapesToRotate;
    private final float rotationAmount; // Can be relative (degrees to add) or absolute (target degrees)
    private final boolean isAbsoluteRotation; // Flag to distinguish between relative and absolute rotation
    private final Map<Shape, Float> originalRotations; // Store original rotation for undo

    /**
     * Constructor for relative rotation.
     */
    // Removed WhiteboardPanel whiteboard parameter
    public RotateCommand(List<Shape> shapes, float degreesToAdd) {
        this.shapesToRotate = new ArrayList<>(shapes); // Copy list
        this.rotationAmount = degreesToAdd;
        this.isAbsoluteRotation = false;
        this.originalRotations = new HashMap<>();
        // Store original rotations immediately
        shapesToRotate.forEach(shape -> originalRotations.put(shape, shape.getRotation()));
    }

    /**
     * Constructor for absolute rotation.
     */
    // Removed WhiteboardPanel whiteboard parameter
    public RotateCommand(List<Shape> shapes, int targetDegrees) {
        this.shapesToRotate = new ArrayList<>(shapes); // Copy list
        this.rotationAmount = targetDegrees % 360; // Normalize target angle
        this.isAbsoluteRotation = true;
        this.originalRotations = new HashMap<>();
        // Store original rotations immediately
        shapesToRotate.forEach(shape -> originalRotations.put(shape, shape.getRotation()));
    }

    @Override
    public void execute() {
        for (Shape shape : shapesToRotate) {
            if (isAbsoluteRotation) {
                shape.setRotation(rotationAmount); // Set to absolute angle
            } else {
                float currentRotation = shape.getRotation();
                shape.setRotation((currentRotation + rotationAmount) % 360); // Add relative angle
            }
        }
        // Whiteboard repaint is handled by the caller (undo/redo methods in
        // WhiteboardPanel)
    }

    @Override
    public void undo() {
        // Restore original rotations
        for (Shape shape : shapesToRotate) {
            if (originalRotations.containsKey(shape)) {
                shape.setRotation(originalRotations.get(shape));
            }
        }
        // Whiteboard repaint is handled by the caller
    }

    @Override
    public void redo() {
        // Re-apply the rotation
        execute();
        // Whiteboard repaint is handled by the caller
    }
}
