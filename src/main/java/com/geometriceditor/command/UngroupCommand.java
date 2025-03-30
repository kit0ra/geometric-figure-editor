package com.geometriceditor.command;

import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;
import com.geometriceditor.ui.WhiteboardPanel;

import java.util.ArrayList;
import java.util.List;

public class UngroupCommand implements Command {
    private final WhiteboardPanel whiteboard;
    private final ShapeGroup groupToUngroup;
    private List<Shape> originalChildren;

    public UngroupCommand(WhiteboardPanel whiteboard, ShapeGroup groupToUngroup) {
        this.whiteboard = whiteboard;
        this.groupToUngroup = groupToUngroup;
        // Store children immediately in case the group is modified later
        this.originalChildren = new ArrayList<>(groupToUngroup.getShapes());
    }

    @Override
    public void execute() {
        if (groupToUngroup != null) {
            // Use a direct method in WhiteboardPanel to perform ungrouping
            // This method should handle removing the group, adding originals back
            List<Shape> children = whiteboard.directUngroupShape(groupToUngroup);
            // Select the newly ungrouped shapes
            whiteboard.directSelectShapes(children);
        }
    }

    @Override
    public void undo() {
        if (groupToUngroup != null && originalChildren != null && !originalChildren.isEmpty()) {
            // Use a direct method in WhiteboardPanel to re-group the shapes
            // This method needs to remove the children and add the original group back
            whiteboard.directRegroupShapes(originalChildren, groupToUngroup);
            // Select the re-created group
            whiteboard.directSelectShape(groupToUngroup);
        }
    }

    @Override
    public void redo() {
        // Re-execute the ungrouping logic
        execute();
    }
}
