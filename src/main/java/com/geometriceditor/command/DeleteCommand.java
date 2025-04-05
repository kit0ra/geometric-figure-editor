package com.geometriceditor.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.geometriceditor.model.Shape;
import com.geometriceditor.ui.WhiteboardPanel;

/**
 * Command to delete one or more shapes from the whiteboard.
 */
public class DeleteCommand implements Command {

    private final WhiteboardPanel whiteboard;
    // Store copies of the shapes to be deleted for undo
    private final List<Shape> shapesToDelete;
    // Store the selection state before deletion for undo
    private List<Shape> previousSelection;

    /**
     * Creates a command to delete shapes.
     *
     * @param whiteboard The whiteboard panel containing the shapes.
     * @param shapes     The list of shapes to delete. A defensive copy is made.
     */
    public DeleteCommand(WhiteboardPanel whiteboard, List<Shape> shapes) {
        this.whiteboard = whiteboard;
        // Store clones for undo, as the original objects will be removed
        this.shapesToDelete = shapes.stream().map(Shape::clone).collect(Collectors.toList());
    }

    @Override
    public void execute() {
        // Store current selection before deleting
        previousSelection = new ArrayList<>(whiteboard.getSelectedShapes());

        // Use whiteboard's direct methods for removal and deselection
        List<String> idsToDelete = shapesToDelete.stream().map(Shape::getId).collect(Collectors.toList());
        List<Shape> actualShapesToRemove = whiteboard.getShapes().stream()
                .filter(s -> idsToDelete.contains(s.getId()))
                .collect(Collectors.toList());

        // Deselect all first to handle cases where deleted items were selected
        whiteboard.deselectAll(); // This repaints, but observer handles final repaint

        // Remove the actual shapes found on the whiteboard
        actualShapesToRemove.forEach(whiteboard::directRemoveShape);
        // Note: directRemoveShape calls repaint, but the final repaint will be
        // triggered by the CommandManager listener notification.
    }

    @Override
    public void undo() {
        // Add the shapes back using direct method
        shapesToDelete.forEach(whiteboard::directAddShape);

        // Restore previous selection state
        whiteboard.directSelectShapes(previousSelection);
        // Note: directAddShape calls repaint, listener handles final repaint.
        // directSelectShapes does not repaint.
    }

    @Override
    public void redo() {
        execute(); // Redoing deletion is the same as executing it
    }

    // Optional: Add getDescription() if needed later for UI hints
    // @Override
    // public String getDescription() {
    // String desc = shapesToDelete.size() == 1 ? "shape" : "shapes";
    // return "Delete " + shapesToDelete.size() + " " + desc;
    // }
}
