package com.geometriceditor.rendering;

import java.awt.Graphics2D;
import java.awt.Polygon;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;

/**
 * Concrete implementation of ShapeRenderer using AWT Graphics2D.
 */
public class AWTRenderer implements ShapeRenderer {

    @Override
    public void drawRectangle(Graphics2D g, Rectangle rectangle) {
        g.setColor(rectangle.getFillColor()); // Use getFillColor
        g.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        g.setColor(rectangle.getBorderColor()); // Use getBorderColor
        g.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public void drawRegularPolygon(Graphics2D g, RegularPolygon regularPolygon) {
        g.setColor(regularPolygon.getFillColor()); // Use getFillColor
        int sides = regularPolygon.getNumberOfSides(); // Use getNumberOfSides
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];
        double angleStep = 2 * Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            xPoints[i] = (int) (regularPolygon.getX() + regularPolygon.getRadius() * Math.cos(i * angleStep));
            yPoints[i] = (int) (regularPolygon.getY() + regularPolygon.getRadius() * Math.sin(i * angleStep));
        }

        Polygon awtPolygon = new Polygon(xPoints, yPoints, sides);
        g.fillPolygon(awtPolygon);
        g.setColor(regularPolygon.getBorderColor()); // Use getBorderColor
        g.drawPolygon(awtPolygon);
    }

    @Override
    public void drawShapeGroup(Graphics2D g, ShapeGroup group) {
        // A group itself doesn't have a visual representation other than its children
        // The drawing logic for children is handled within the ShapeGroup's draw method
        // which will iterate and call draw on each child, passing this renderer.
        // So, this method might remain empty or draw a bounding box if needed.
        // For now, let's keep it simple.
        for (Shape shape : group.getShapes()) {
            // The Shape's draw method will use the renderer passed to it.
            // We assume Shape.draw(Graphics2D g, ShapeRenderer renderer) exists or will be
            // added.
            // shape.draw(g, this); // This call will happen inside ShapeGroup.draw
        }
        // Optionally draw a bounding box for the group for debugging/selection
        // g.setColor(java.awt.Color.GRAY);
        // g.drawRect(group.getX(), group.getY(), group.getWidth(), group.getHeight());
    }
}
