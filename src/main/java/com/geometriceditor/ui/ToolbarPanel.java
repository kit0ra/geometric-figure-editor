package com.geometriceditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.model.Shape;

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
        // Rectangle Button
        JButton rectangleButton = createShapeButton("Rectangle", e -> {
            addShapeToWhiteboard(() -> {
                return shapeFactory.createRectangle(50, 50, 100, 50);
            });
        });
        add(rectangleButton);

        // Polygon Button
        JButton polygonButton = createShapeButton("Polygon", e -> {
            addShapeToWhiteboard(() -> {
                return shapeFactory.createRegularPolygon(50, 50, 6, 50);
            });
        });
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
    private JButton createShapeButton(String name, ActionListener action) {
        JButton button = new JButton(name);
        button.addActionListener(action);
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
}
