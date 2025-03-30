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
        System.out.println("--- Executing RotateCommand ---"); // DEBUG
        for (Shape shape : shapesToRotate) {
            System.out.println("Shape ID: " + shape.getId()); // DEBUG
            float currentRotation = shape.getRotation();
            System.out.println("Current Rotation: " + currentRotation); // DEBUG
            float newRotation;
            if (isAbsoluteRotation) {
                newRotation = rotationAmount;
                System.out.println("Setting Absolute Rotation to: " + newRotation); // DEBUG
            } else {
                newRotation = (currentRotation + rotationAmount) % 360;
                // Handle negative results from modulo if necessary
                if (newRotation < 0) {
                    newRotation += 360;
                }
                System.out.println("Adding Relative Rotation: " + rotationAmount); // DEBUG
                System.out.println("Calculated New Rotation: " + newRotation); // DEBUG
            }
            shape.setRotation(newRotation);
            System.out.println("Rotation set. New shape.getRotation(): " + shape.getRotation()); // DEBUG
        }
        System.out.println("--- Finished RotateCommand ---"); // DEBUG
        // Whiteboard repaint is handled by the caller
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
