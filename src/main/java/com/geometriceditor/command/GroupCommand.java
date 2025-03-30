package com.geometriceditor.command;

import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;
import com.geometriceditor.ui.WhiteboardPanel;

import java.util.ArrayList;
import java.util.List;

public class GroupCommand implements Command {
    private final WhiteboardPanel whiteboard;
    private final List<Shape> shapesToGroup;
    private ShapeGroup createdGroup;

    public GroupCommand(WhiteboardPanel whiteboard, List<Shape> shapesToGroup) {
        this.whiteboard = whiteboard;
        // Make a copy to avoid issues with concurrent modification or external changes
        this.shapesToGroup = new ArrayList<>(shapesToGroup);
    }

    @Override
    public void execute() {
        if (shapesToGroup.size() > 1) {
            // Use a direct method in WhiteboardPanel to perform the grouping logic
            // This method should handle adding the group, removing originals, and returning
            // the group
            this.createdGroup = whiteboard.directGroupShapes(shapesToGroup);
            // Select the newly created group
            whiteboard.directSelectShape(this.createdGroup);
        }
    }

    @Override
    public void undo() {
        if (createdGroup != null) {
            // Use a direct method in WhiteboardPanel to perform ungrouping
            // This method should handle removing the group, adding originals back
            whiteboard.directUngroupShape(createdGroup);
            // Reselect the original shapes
            whiteboard.directSelectShapes(shapesToGroup);
            createdGroup = null; // Clear the reference as it no longer exists on the whiteboard
        }
    }

    @Override
    public void redo() {
        // Re-execute the grouping logic
        execute();
    }
}
