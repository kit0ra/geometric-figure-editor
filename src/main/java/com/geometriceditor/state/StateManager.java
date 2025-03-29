package com.geometriceditor.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.geometriceditor.model.Shape;
import com.geometriceditor.ui.WhiteboardPanel;

public class StateManager {
    public ApplicationMemento save(WhiteboardPanel whiteboard) {
        return new ApplicationMemento(whiteboard.getShapes());
    }

    public void restore(WhiteboardPanel whiteboard, ApplicationMemento memento) {
        whiteboard.clearShapes();
        for (Shape shape : memento.getShapes()) {
            whiteboard.addShape(shape);
        }
    }

    public void saveToFile(WhiteboardPanel whiteboard, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(save(whiteboard));
        }
    }

    public void loadFromFile(WhiteboardPanel whiteboard, File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ApplicationMemento memento = (ApplicationMemento) ois.readObject();
            restore(whiteboard, memento);
        }
    }
}
