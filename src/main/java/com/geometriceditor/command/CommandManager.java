package com.geometriceditor.command;

import java.util.ArrayList; // Added
import java.util.List; // Added
import java.util.Stack;

public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    private final List<CommandExecutionListener> listeners = new ArrayList<>(); // Added listener list

    // --- Listener Management ---

    public void addListener(CommandExecutionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CommandExecutionListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        // Iterate over a copy in case listeners modify the list during notification
        for (CommandExecutionListener listener : new ArrayList<>(listeners)) {
            listener.commandExecuted();
        }
    }

    // --- Command Execution ---

    public void executeCommand(Command cmd) {
        try {
            cmd.execute();
            undoStack.push(cmd);
            redoStack.clear();
            notifyListeners(); // Notify after successful execution
        } catch (Exception e) {
            System.err.println("Command execution failed: " + e.getMessage());
            // Optionally notify listeners even on failure, depending on requirements
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
            notifyListeners(); // Notify after undo
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.redo();
            undoStack.push(cmd);
            notifyListeners(); // Notify after redo
        }
    }
}
