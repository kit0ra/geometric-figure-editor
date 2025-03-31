package com.geometriceditor.command;

/**
 * Interface for listeners that want to be notified after a command
 * has been executed, undone, or redone by the CommandManager.
 */
@FunctionalInterface // Optional, but good practice for single-method interfaces
public interface CommandExecutionListener {
    /**
     * Called after a command operation (execute, undo, redo) completes.
     */
    void commandExecuted();
}
