package com.geometriceditor.ui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor; // Added
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; // Added

/**
 * A Transferable implementation for dragging a list of shape IDs
 * from the whiteboard.
 */
public class TransferableShapeList implements Transferable {

    // Define custom DataFlavors
    public static final DataFlavor SHAPE_ID_LIST_FLAVOR = new DataFlavor(ArrayList.class, "List of Shape IDs");
    public static final DataFlavor DRAG_START_POINT_FLAVOR = new DataFlavor(Point.class, "Drag Start Point");

    private static final DataFlavor[] SUPPORTED_FLAVORS = { SHAPE_ID_LIST_FLAVOR, DRAG_START_POINT_FLAVOR };

    private final List<String> shapeIds;
    private final Point startPoint; // Added start point

    /**
     * Constructs a TransferableShapeList.
     *
     * @param shapeIds   A list containing the unique IDs of the shapes being
     *                   transferred.
     * @param startPoint The point where the drag started on the source component.
     */
    public TransferableShapeList(List<String> shapeIds, Point startPoint) {
        this.shapeIds = new ArrayList<>(shapeIds); // Store copies
        this.startPoint = (Point) startPoint.clone(); // Store clone
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS.clone(); // Return clone
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return Arrays.asList(SUPPORTED_FLAVORS).contains(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (SHAPE_ID_LIST_FLAVOR.equals(flavor)) {
            return new ArrayList<>(this.shapeIds); // Return copy
        } else if (DRAG_START_POINT_FLAVOR.equals(flavor)) {
            return this.startPoint.clone(); // Return clone
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
