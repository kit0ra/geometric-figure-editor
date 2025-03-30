package com.geometriceditor.command;

import com.geometriceditor.model.Shape;
import com.geometriceditor.ui.WhiteboardPanel;

// In command/AddShapeCommand.java
public class AddShapeCommand implements Command {
    private final WhiteboardPanel whiteboard;
    private final Shape shape;

    public AddShapeCommand(WhiteboardPanel whiteboard, Shape shape) {
        this.whiteboard = whiteboard;
        this.shape = shape;
    }

    @Override
    public void execute() {
        whiteboard.directAddShape(shape); // Use the direct method
    }

    @Override
    public void undo() {
        whiteboard.directRemoveShape(shape);
        whiteboard.directDeselectShape(shape); // Also remove from selection
    }

    @Override
    public void redo() {
        execute();
    }
}
