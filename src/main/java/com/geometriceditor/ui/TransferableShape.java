package com.geometriceditor.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A Transferable implementation for dragging shape types (as Strings)
 * from the toolbar to the whiteboard.
 */
public class TransferableShape implements Transferable {

    // Define a custom DataFlavor for our shape type string
    // Define a custom DataFlavor for our shape type string
    // Using String class as the representation class. No ClassNotFoundException
    // expected here.
    public static final DataFlavor SHAPE_TYPE_FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String", "Shape Type");

    private final String shapeType; // e.g., "Rectangle", "Polygon"

    /**
     * Constructs a TransferableShape.
     *
     * @param shapeType The type of shape being transferred (e.g., "Rectangle").
     */
    public TransferableShape(String shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        // Return the supported DataFlavors
        return new DataFlavor[] { SHAPE_TYPE_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        // Check if the requested flavor is our custom flavor
        return SHAPE_TYPE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        // Provide the data (the shape type string) if the flavor is supported
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return this.shapeType;
    }
}
