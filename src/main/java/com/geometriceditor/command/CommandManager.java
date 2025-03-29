package com.geometriceditor.command;

import java.util.Stack;

public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    public void executeCommand(Command cmd) {
        try {
            cmd.execute();
            undoStack.push(cmd);
            redoStack.clear();
        } catch (Exception e) {
            System.err.println("Command execution failed: " + e.getMessage());
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.redo();
            undoStack.push(cmd);
        }
    }
}
