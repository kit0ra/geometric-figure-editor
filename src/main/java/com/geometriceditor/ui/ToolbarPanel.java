package com.geometriceditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point; // Added
import java.awt.datatransfer.Transferable; // Added
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener; // Added
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame; // Added
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.model.Shape; // Added

public class ToolbarPanel extends JToolBar {
    private ShapeFactory shapeFactory;
    private WhiteboardPanel whiteboard;

    public ToolbarPanel(ShapeFactory shapeFactory) {
        this.shapeFactory = Objects.requireNonNull(shapeFactory);
        setFloatable(false);
        setRollover(true);
        setBorderPainted(true);

        // Use our custom WrapLayout
        setLayout(new WrapLayout());

        // Add components
        addShapeButtons();
        addSeparator();
        addActionButtons();
        addSeparator();
        addUtilityButtons();
    }

    private void addShapeButtons() {
        // Rectangle Button - Pass the ShapeSupplier directly
        JButton rectangleButton = createShapeButton("Rectangle",
                () -> shapeFactory.createRectangle(50, 50, 100, 50));
        add(rectangleButton);

        // Polygon Button - Pass the ShapeSupplier directly
        JButton polygonButton = createShapeButton("Polygon",
                () -> shapeFactory.createRegularPolygon(50, 50, 6, 50));
        add(polygonButton);
    }

    private void addActionButtons() {
        // Undo Button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener((ActionEvent e) -> {
            if (whiteboard != null) {
                whiteboard.undo();
            } else {
                showWhiteboardNotInitializedMessage();
            }
        });
        add(undoButton);

        // Redo Button
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.redo();
            } else {
                showWhiteboardNotInitializedMessage();
            }
        });
        add(redoButton);

        // Group Button
        JButton groupButton = new JButton("Group");
        groupButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.groupSelected();
            }
        });
        add(groupButton);

        // Ungroup Button
        JButton ungroupButton = new JButton("Ungroup");
        ungroupButton.addActionListener(e -> {
            if (whiteboard != null) {
                whiteboard.ungroupSelected();
            }
        });
        add(ungroupButton);

        // Selection controls
        JButton selectAllBtn = new JButton("Select All");
        selectAllBtn.addActionListener(e -> whiteboard.selectAll());
        add(selectAllBtn);

        JButton deselectBtn = new JButton("Deselect");
        deselectBtn.addActionListener(e -> whiteboard.deselectAll());
        add(deselectBtn);

        // Rotation controls
        JButton rotateLeftBtn = new JButton("Rotate -15°");
        rotateLeftBtn.addActionListener(e -> rotateSelectedShapes(-15));
        add(rotateLeftBtn);

        JButton rotateRightBtn = new JButton("Rotate +15°");
        rotateRightBtn.addActionListener(e -> rotateSelectedShapes(15));
        add(rotateRightBtn);

        // Rotation slider for finer control
        JSlider rotationSlider = new JSlider(-180, 180, 0);
        rotationSlider.setMajorTickSpacing(45);
        rotationSlider.setMinorTickSpacing(15);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setPaintLabels(true);
        rotationSlider.addChangeListener(e -> {
            if (!rotationSlider.getValueIsAdjusting()) {
                rotateSelectedShapesTo(rotationSlider.getValue());
            }
        });
        add(rotationSlider);

    }

    private void addUtilityButtons() {
        // Delete button
        add(createToolButton("Delete", "Delete selected", e -> whiteboard.deleteSelected()));

        // Color controls
        add(createColorButton("Fill", "Set fill color", true));
        add(createColorButton("Border", "Set border color", false));

        // Property edit button
        JButton propertiesBtn = new JButton("Properties");
        propertiesBtn.addActionListener(e -> showPropertiesDialog());
        add(propertiesBtn);
    }

    // helpers
    private JButton createShapeButton(String shapeType, ShapeSupplier shapeSupplier) { // Changed param type
        JButton button = new JButton(shapeType);
        // Remove ActionListener entirely, handler will manage click
        // button.addActionListener(fallbackAction);

        // Pass the button AND the ShapeSupplier to the handler
        ShapeButtonMouseHandler mouseHandler = new ShapeButtonMouseHandler(button, shapeSupplier);
        button.addMouseListener(mouseHandler);
        button.addMouseMotionListener(mouseHandler);

        // Set TransferHandler to provide the shape type data when drag is initiated
        button.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY; // We are copying a shape type to create a new one
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                // Create our custom Transferable with the shape type (button's text)
                return new TransferableShape(shapeType);
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                // Cleanup after drag if needed (not necessary here)
                super.exportDone(source, data, action);
            }
        });

        return button;
    }

    private JButton createColorButton(String text, String tooltip, boolean isFill) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> showColorDialog(isFill));
        button.setMargin(new Insets(2, 5, 2, 5));
        return button;
    }

    private JButton createToolButton(String text, String tooltip, ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.setMargin(new Insets(2, 5, 2, 5));
        return button;
    }

    // Method to set the whiteboard (called from MainWindow)
    public void setWhiteboard(WhiteboardPanel whiteboard) {
        this.whiteboard = Objects.requireNonNull(whiteboard, "Whiteboard cannot be null");
    }

    // Generic method to add shape with null safety
    private void addShapeToWhiteboard(ShapeSupplier shapeSupplier) {
        if (whiteboard == null) {
            showWhiteboardNotInitializedMessage();
            return;
        }

        try {
            Shape shape = shapeSupplier.get();
            whiteboard.addShape(shape);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding shape: " + e.getMessage(),
                    "Shape Addition Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show message when whiteboard is not initialized
    private void showWhiteboardNotInitializedMessage() {
        JOptionPane.showMessageDialog(this,
                "Whiteboard has not been initialized. Please set the whiteboard first.",
                "Initialization Error",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showColorDialog(boolean isFillColor) {
        if (whiteboard == null || whiteboard.getSelectedShapes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No shapes selected", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Color initialColor = isFillColor ? whiteboard.getSelectedShapes().get(0).getFillColor()
                : whiteboard.getSelectedShapes().get(0).getBorderColor();

        Color newColor = JColorChooser.showDialog(this, "Choose Color", initialColor);
        if (newColor != null) {
            if (isFillColor) {
                whiteboard.setSelectedFillColor(newColor);
            } else {
                whiteboard.setSelectedBorderColor(newColor);
            }
        }
    }

    private void showPropertiesDialog() {
        if (whiteboard == null || whiteboard.getSelectedShapes().size() != 1) {
            JOptionPane.showMessageDialog(this,
                    "Select exactly one shape to edit properties",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shape shape = whiteboard.getSelectedShapes().get(0);
        boolean confirmed = PropertyEditDialog.editShapeProperties(
                (JFrame) SwingUtilities.getWindowAncestor(this), shape);

        if (confirmed) {
            whiteboard.repaint();
        }
    }

    private void rotateSelectedShapes(int degrees) {
        if (whiteboard != null) {
            whiteboard.rotateSelectedShapes(degrees);
        }
    }

    private void rotateSelectedShapesTo(int degrees) {
        if (whiteboard != null) {
            whiteboard.rotateSelectedShapesTo(degrees);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 80);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    // Functional interface for shape creation
    @FunctionalInterface
    private interface ShapeSupplier {
        Shape get();
    }

    // Inner class to handle mouse events for shape buttons (click vs drag)
    private static class ShapeButtonMouseHandler extends MouseAdapter implements MouseMotionListener {
        private final JButton button;
        private final ShapeSupplier shapeSupplier; // Store the shape creation logic
        private Point pressPoint;
        private boolean potentiallyDragging = false;
        private static final int DRAG_THRESHOLD = 5; // Pixels threshold to start drag

        public ShapeButtonMouseHandler(JButton button, ShapeSupplier shapeSupplier) { // Accept ShapeSupplier
            this.button = button;
            this.shapeSupplier = shapeSupplier; // Store the supplier
        }

        @Override
        public void mousePressed(MouseEvent e) {
            pressPoint = e.getPoint();
            potentiallyDragging = true; // Flag that a drag might start
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (potentiallyDragging) {
                Point currentPoint = e.getPoint();
                int dx = Math.abs(currentPoint.x - pressPoint.x);
                int dy = Math.abs(currentPoint.y - pressPoint.y);
                // Check if movement exceeds threshold
                if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                    JComponent comp = (JComponent) e.getSource();
                    TransferHandler handler = comp.getTransferHandler();
                    if (handler != null) {
                        // Initiate the drag
                        handler.exportAsDrag(comp, e, TransferHandler.COPY);
                        potentiallyDragging = false; // Drag started, clear flag
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // If potentiallyDragging is still true, it means mouseDragged didn't
            // initiate a drag, so treat it as a click.
            if (potentiallyDragging) {
                // Check if the release is still within the button bounds
                if (button.contains(e.getPoint())) {
                    // Directly execute the shape creation logic for a click
                    try {
                        // Need access to the ToolbarPanel's whiteboard instance.
                        // A bit tricky from a static inner class.
                        // Let's find the ToolbarPanel ancestor.
                        ToolbarPanel toolbar = (ToolbarPanel) SwingUtilities.getAncestorOfClass(ToolbarPanel.class,
                                button);
                        if (toolbar != null) {
                            toolbar.addShapeToWhiteboard(shapeSupplier); // Use the stored supplier
                        } else {
                            System.err.println("Could not find ToolbarPanel ancestor for click action.");
                        }
                    } catch (Exception ex) {
                        System.err.println("Error executing click action: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            // Reset state regardless
            potentiallyDragging = false;
            pressPoint = null;
        }

        // Implement other MouseMotionListener methods (can be empty)
        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
}
