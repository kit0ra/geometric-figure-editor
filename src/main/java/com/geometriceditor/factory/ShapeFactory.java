package com.geometriceditor.factory;

import java.awt.Color;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;

public class ShapeFactory {
    // Singleton pattern for factory
    private static ShapeFactory instance;

    private ShapeFactory() {
    }

    public static ShapeFactory getInstance() {
        if (instance == null) {
            instance = new ShapeFactory();
        }
        return instance;
    }

    // Create Rectangle
    public Rectangle createRectangle() {
        return new Rectangle();
    }

    public Rectangle createRectangle(int x, int y, int width, int height) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFillColor(Color.BLUE);
        return rectangle;
    }

    // Create Regular Polygon
    public RegularPolygon createRegularPolygon() {
        return new RegularPolygon();
    }

    public RegularPolygon createRegularPolygon(int x, int y, int sides, int sideLength) {
        RegularPolygon polygon = new RegularPolygon(x, y, sides, sideLength);
        polygon.setFillColor(Color.GREEN);
        return polygon;
    }

    // Generic shape cloning method
    public Shape cloneShape(Shape originalShape) {
        return originalShape.clone();
    }
}
