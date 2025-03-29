package com.geometriceditor.io;

import com.geometriceditor.model.Shape;
import com.geometriceditor.ui.WhiteboardPanel;

import java.io.*;
import java.util.List;

public class SerializationManager {
    public static void saveWhiteboard(WhiteboardPanel whiteboard, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // Save the list of shapes
            List<Shape> shapes = whiteboard.getShapes();
            oos.writeObject(shapes);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadWhiteboard(WhiteboardPanel whiteboard, String filePath)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            // Clear existing shapes
            whiteboard.clearShapes();

            // Load and add shapes
            List<Shape> loadedShapes = (List<Shape>) ois.readObject();
            for (Shape shape : loadedShapes) {
                whiteboard.addShape(shape);
            }
        }
    }
}
