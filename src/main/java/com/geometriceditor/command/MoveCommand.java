package com.geometriceditor.command;

import java.util.List;

import com.geometriceditor.model.Shape;

/**
 * Command to move one or more shapes by a given delta (dx, dy).
 */
public class MoveCommand implements Command {

    // Use a copy of the list to prevent issues if the original selection changes
    private final List<Shape> shapesToMove;
    private final int dx;
    private final int dy;
    // Optional: Store original positions if move(-dx, -dy) isn't perfectly
    // reversible
    // private final Map<Shape, Shape.Point> originalPositions;

    /**
     * Creates a command to move shapes.
     *
     * @param shapes The list of shapes to move. A defensive copy is made.
     * @param dx     The horizontal displacement.
     * @param dy     The vertical displacement.
     */
    public MoveCommand(List<Shape> shapes, int dx, int dy) {
        // Store an immutable copy of the shapes list at the time of command creation
        this.shapesToMove = List.copyOf(shapes);
        this.dx = dx;
        this.dy = dy;

        // Example if storing original positions was needed:
        // this.originalPositions = shapes.stream()
        // .collect(Collectors.toMap(s -> s, s -> new Shape.Point(s.getPosition())));
    }

    @Override
    public void execute() {
        // Move each shape by the delta
        shapesToMove.forEach(shape -> shape.move(dx, dy));
    }

    @Override
    public void undo() {
        // Move each shape back by the inverse delta
        shapesToMove.forEach(shape -> shape.move(-dx, -dy));

        // Example if restoring original positions:
        // shapesToMove.forEach(shape ->
        // shape.setPosition(originalPositions.get(shape)));
    }

    @Override
    public void redo() {
        // Redoing a move is the same as executing it
        execute();
    }

    // Removed @Override as it's not in the Command interface
    public String getDescription() {
        if (shapesToMove.isEmpty()) {
            return "Move shapes";
        }
        // Provide a more specific description if possible
        String shapeDesc = shapesToMove.size() == 1 ? shapesToMove.get(0).getClass().getSimpleName()
                : shapesToMove.size() + " shapes";
        return "Move " + shapeDesc + " by (" + dx + ", " + dy + ")";
    }
}
