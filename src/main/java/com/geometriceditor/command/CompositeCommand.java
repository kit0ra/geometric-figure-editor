
package com.geometriceditor.command;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite command that groups multiple commands together
 * and executes them as a single unit for undo/redo operations.
 */
public class CompositeCommand implements Command {
    private final List<Command> commands = new ArrayList<>();

    /**
     * Adds a command to the composite
     *
     * @param command The command to add
     */
    public void add(Command command) {
        commands.add(command);
    }

    /**
     * Executes all commands in the composite
     */
    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    /**
     * Undoes all commands in reverse order
     */
    @Override
    public void undo() {
        // Undo in reverse order
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }

    /**
     * Redoes all commands in original order
     */
    @Override
    public void redo() {
        execute();
    }

    /**
     * @return The number of commands in this composite
     */
    public int size() {
        return commands.size();
    }

    /**
     * @return true if this composite contains no commands
     */
    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
