package com.geometriceditor.visitor;

import com.geometriceditor.model.Rectangle;
import com.geometriceditor.model.RegularPolygon;
import com.geometriceditor.model.ShapeGroup;

/**
 * Visitor interface for performing operations on different Shape types.
 * The type parameter R represents the return type of the visit methods.
 */
public interface ShapeVisitor<R> {
    R visit(Rectangle rectangle);

    R visit(RegularPolygon polygon);

    R visit(ShapeGroup group);
    // Add methods for other concrete shapes if they exist
}
