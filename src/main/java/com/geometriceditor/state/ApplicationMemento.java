package com.geometriceditor.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.geometriceditor.model.Shape;

public class ApplicationMemento implements Serializable {
    private final List<Shape> shapes;

    public ApplicationMemento(List<Shape> shapes) {
        this.shapes = new ArrayList<>();
        for (Shape shape : shapes) {
            this.shapes.add(shape.clone());
        }
    }

    public List<Shape> getShapes() {
        return shapes;
    }
}
