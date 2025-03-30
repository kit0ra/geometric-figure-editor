package com.geometriceditor.rendering;

import java.awt.Graphics2D;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.ShapeGroup;

/**
 * Interface defining the rendering operations for different shapes (Bridge
 * Abstraction Implementor).
 */
public interface ShapeRenderer {
    void drawRectangle(Graphics2D g, Rectangle rectangle);

    void drawRegularPolygon(Graphics2D g, RegularPolygon polygon);

    void drawShapeGroup(Graphics2D g, ShapeGroup group);
    // Add methods for other shapes if necessary
}
