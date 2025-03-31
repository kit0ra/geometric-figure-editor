package com.geometriceditor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; // Added
import java.awt.event.MouseMotionAdapter; // Added
import java.io.IOException; // Added
import java.util.ArrayList; // Added
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction; // Added
import javax.swing.JFrame;
import javax.swing.JMenuItem; // Added
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.geometriceditor.command.AddShapeCommand;
import com.geometriceditor.command.CommandManager; // Import all commands
import com.geometriceditor.command.CompositeCommand;
import com.geometriceditor.command.GroupCommand;
import com.geometriceditor.command.MoveCommand;
import com.geometriceditor.command.RotateCommand; // Import the new command
import com.geometriceditor.command.UngroupCommand;
import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.model.Shape;
import com.geometriceditor.model.ShapeGroup;
import com.geometriceditor.rendering.AWTRenderer;
import com.geometriceditor.rendering.ShapeRenderer;

public class WhiteboardPanel extends JPanel {
    // Constants
    private static final Color SELECTION_COLOR = new Color(0, 120, 215);
    private static final Color SELECTION_FILL = new Color(0, 120, 215, 50);

    // State fields
    private final List<Shape> shapes = new ArrayList<>();
    private final List<Shape> selectedShapes = new ArrayList<>();
    private final CommandManager commandManager = new CommandManager();
    private final ShapeRenderer shapeRenderer = new AWTRenderer(); // Instantiate the renderer
    private final ShapeFactory shapeFactory; // Added ShapeFactory field

    // UI interaction fields
    private Point dragStartPoint; // Where the current drag segment started
    private Point dragOriginPoint; // Where the entire drag operation started
    private int dragTotalDx = 0; // Accumulated dx for the command
    private int dragTotalDy = 0; // Accumulated dy for the command
    private Point selectionStartPoint;
    private java.awt.Rectangle selectionRectangle;
    private boolean isCtrlPressed = false;

    // Grid visibility and properties
    private boolean gridVisible = false;
    private int gridSize = 20; // pixels between grid lines
    private Color gridColor = new Color(200, 200, 200, 100); // Light gray with transparency

    // Rotation center management
    private Point rotationCenterDragStart;
    private boolean isDraggingRotationCenter = false;

    // ==================== CONSTRUCTOR ====================
    public WhiteboardPanel(ShapeFactory shapeFactory) { // Added ShapeFactory parameter
        this.shapeFactory = Objects.requireNonNull(shapeFactory, "ShapeFactory cannot be null"); // Store ShapeFactory
        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setFocusable(true);
        setupInputListeners();
        setupContextMenu();
        setupDragAndDrop(); // Added call to setup D&D
    }

    // ==================== INITIALIZATION ====================
    private void setupInputListeners() {
        addKeyListener(new KeyHandler());
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
    }

    private void setupDragAndDrop() {
        setTransferHandler(new ShapeDropHandler());
    }

    // ==================== SHAPE MANAGEMENT ====================
    public void addShape(Shape shape) {
        if (shape != null) {
            commandManager.executeCommand(new AddShapeCommand(this, shape));
        }
    }

    public void addMultipleShapes(List<Shape> shapes) {
        if (shapes != null && !shapes.isEmpty()) {
            CompositeCommand batch = new CompositeCommand();
            shapes.forEach(shape -> batch.add(new AddShapeCommand(this, shape)));
            commandManager.executeCommand(batch);
        }
    }

    public void directAddShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    public void directRemoveShape(Shape shape) {
        shapes.remove(shape);
        repaint();
    }

    public void directDeselectShape(Shape shape) {
        selectedShapes.remove(shape);
        // No repaint here, assuming the caller (command) will handle repaint
    }

    public void directSelectShape(Shape shape) {
        selectedShapes.clear();
        if (shape != null) {
            selectedShapes.add(shape);
        }
        // No repaint
    }

    public void directSelectShapes(List<Shape> shapesToSelect) {
        selectedShapes.clear();
        if (shapesToSelect != null) {
            selectedShapes.addAll(shapesToSelect);
        }
        // No repaint
    }

    public ShapeGroup directGroupShapes(List<Shape> shapesToGroup) {
        ShapeGroup group = new ShapeGroup();
        shapesToGroup.forEach(group::addShape);
        shapes.removeAll(shapesToGroup);
        shapes.add(group);
        selectedShapes.removeAll(shapesToGroup); // Deselect originals
        // No repaint
        return group;
    }

    public List<Shape> directUngroupShape(ShapeGroup group) {
        List<Shape> children = group.getShapes();
        shapes.addAll(children);
        shapes.remove(group);
        selectedShapes.remove(group); // Deselect group
        // No repaint
        return children;
    }

    public void directRegroupShapes(List<Shape> children, ShapeGroup originalGroup) {
        // Assumes children were added back by directUngroupShape's undo
        shapes.removeAll(children);
        shapes.add(originalGroup);
        selectedShapes.removeAll(children); // Deselect children
        // No repaint
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public void clearShapes() {
        shapes.clear();
        selectedShapes.clear();
        repaint();
    }

    // ==================== SELECTION MANAGEMENT ====================
    public void selectAll() {
        selectedShapes.clear();
        selectedShapes.addAll(shapes);
        repaint();
    }

    public void deselectAll() {
        selectedShapes.clear();
        repaint();
    }

    public void deleteSelected() {
        shapes.removeAll(selectedShapes);
        selectedShapes.clear();
        repaint();
    }

    public void groupSelected() {
        if (selectedShapes.size() > 1) {
            // Create and execute GroupCommand
            GroupCommand groupCmd = new GroupCommand(this, selectedShapes);
            commandManager.executeCommand(groupCmd);
            repaint(); // Repaint after command execution
        }
    }

    public void ungroupSelected() {
        // Create a composite command if multiple groups are selected
        CompositeCommand batchUngroup = new CompositeCommand();
        new ArrayList<>(selectedShapes).stream()
                .filter(ShapeGroup.class::isInstance)
                .map(ShapeGroup.class::cast)
                .forEach(group -> batchUngroup.add(new UngroupCommand(this, group)));

        if (!batchUngroup.isEmpty()) {
            commandManager.executeCommand(batchUngroup);
            repaint(); // Repaint after command execution
        }
    }

    // Removed the old private ungroupShape method as logic is now in UngroupCommand
    // and directUngroupShape

    public void rotateSelectedShapes(int degrees) {
        if (!selectedShapes.isEmpty()) {
            // Explicitly pass a new list copy to the command constructor
            List<Shape> shapesToRotate = new ArrayList<>(selectedShapes);
            RotateCommand rotateCmd = new RotateCommand(shapesToRotate, degrees);
            commandManager.executeCommand(rotateCmd);
            repaint(); // Repaint after command execution
        }
    }

    public void rotateSelectedShapesTo(int degrees) {
        if (!selectedShapes.isEmpty()) {
            // Explicitly pass a new list copy to the command constructor
            List<Shape> shapesToRotate = new ArrayList<>(selectedShapes);
            RotateCommand rotateCmd = new RotateCommand(shapesToRotate, degrees);
            commandManager.executeCommand(rotateCmd);
            repaint(); // Repaint after command execution
        }
    }

    // ==================== COMMAND MANAGEMENT ====================
    public void undo() {
        commandManager.undo();
        repaint();
    }

    public void redo() {
        commandManager.redo();
        repaint();
    }

    // ==================== RENDERING ====================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        enableAntialiasing(g2d);

        // Draw grid first (behind everything)
        if (gridVisible) {
            drawGrid(g2d);
        }

        renderShapes(g2d);
        renderSelections(g2d);
        renderSelectionRectangle(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        Color oldColor = g2d.getColor();
        Stroke oldStroke = g2d.getStroke();

        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(1));

        // Calculate visible grid area
        int width = getWidth();
        int height = getHeight();

        // Draw vertical lines
        for (int x = 0; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }

        // Draw horizontal lines
        for (int y = 0; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }

        g2d.setColor(oldColor);
        g2d.setStroke(oldStroke);
    }

    private void enableAntialiasing(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void renderShapes(Graphics2D g2d) {
        // Use the renderer to draw each shape
        shapes.forEach(shape -> shape.draw(g2d, shapeRenderer));
    }

    private void renderSelections(Graphics2D g2d) {
        for (Shape shape : selectedShapes) {
            new SelectionDecorator(shape).drawSelection(g2d);

            // Draw rotation center marker at geometric center
            com.geometriceditor.model.Shape.Point center = shape.getGeometricCenter();
            g2d.setColor(Color.RED);
            g2d.fillOval(center.x - 4, center.y - 4, 8, 8);
        }
    }

    private void renderSelectionRectangle(Graphics2D g2d) {
        if (selectionRectangle != null) {
            g2d.setColor(SELECTION_FILL);
            g2d.fill(selectionRectangle);
            g2d.setColor(SELECTION_COLOR);
            g2d.draw(selectionRectangle);
        }
    }

    // ==================== INPUT HANDLERS ====================
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            isCtrlPressed = e.getKeyCode() == KeyEvent.VK_CONTROL;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                isCtrlPressed = false;
            }
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            handleMousePress(e.getPoint(), e.isControlDown(), e);
            requestFocusInWindow();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            finalizeDragOrSelection(e.getPoint()); // Use a new method
        }
    }

    private class MouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            handleMouseDrag(e.getPoint());
        }
    }

    // ==================== EVENT HANDLING ====================
    private void handleMousePress(Point point, boolean ctrlDown, MouseEvent e) {
        isCtrlPressed = ctrlDown;
        selectionStartPoint = point;
        Shape clickedShape = findShapeAtPoint(point);

        if (SwingUtilities.isRightMouseButton(e)) {
            // Don't change selection on right click
            return;
        }

        // Check if clicking near rotation center (when single shape selected)
        if (selectedShapes.size() == 1) {
            com.geometriceditor.model.Shape.Point center = selectedShapes.get(0).getRotationCenter();
            if (isPointNear(point, center, 5)) { // 5 pixel radius
                isDraggingRotationCenter = true;
                rotationCenterDragStart = point;
                return;
            }
        }

        if (isCtrlPressed) {
            toggleShapeSelection(clickedShape);
        } else if (clickedShape == null) {
            startRectangleSelection(point);
        } else if (isCtrlPressed && !selectedShapes.isEmpty()) {
            // Ctrl+Click sets rotation center for all selected shapes
            for (Shape shape : selectedShapes) {
                shape.setRotationCenter(new com.geometriceditor.model.Shape.Point(point.x, point.y));
            }
            repaint();
            return;
        } else {
            handleShapeSelection(clickedShape, point); // This sets dragStartPoint
            if (dragStartPoint != null) {
                dragOriginPoint = point; // Store the absolute start of the drag
                dragTotalDx = 0; // Reset accumulators
                dragTotalDy = 0;
            }
        }

        repaint();
    }

    private boolean isPointNear(Point p1, com.geometriceditor.model.Shape.Point center, int radius) {
        return Math.abs(p1.x - center.x) <= radius && Math.abs(p1.y - center.y) <= radius;
    }

    private void handleMouseDrag(Point point) {
        if (isDraggingRotationCenter && !selectedShapes.isEmpty()) {
            // Move rotation center
            int dx = point.x - rotationCenterDragStart.x;
            int dy = point.y - rotationCenterDragStart.y;

            for (Shape shape : selectedShapes) {
                com.geometriceditor.model.Shape.Point currentCenter = shape.getRotationCenter();
                shape.setRotationCenter(new Shape.Point(
                        currentCenter.x + dx,
                        currentCenter.y + dy));
            }
            rotationCenterDragStart = point;
            repaint();
            return; // Don't process shape drag if dragging rotation center
        }

        if (dragStartPoint != null) {
            // Calculate delta for this drag segment
            int dx = point.x - dragStartPoint.x;
            int dy = point.y - dragStartPoint.y;

            // Update total displacement for the command
            dragTotalDx += dx;
            dragTotalDy += dy;

            // Visually move shapes during drag (temporary)
            for (Shape shape : selectedShapes) {
                shape.move(dx, dy);
            }
            dragStartPoint = point; // Update start for the *next* drag segment

        } else if (selectionStartPoint != null) {
            updateSelectionRectangle(point); // Handle rectangle selection drag
        }
        repaint();
    }

    // Renamed and modified from finalizeSelection
    private void finalizeDragOrSelection(Point releasePoint) {
        // Finalize rectangle selection
        if (selectionRectangle != null) {
            selectShapesInRectangle(selectionRectangle);
            selectionRectangle = null;
            selectionStartPoint = null; // Reset selection start
        }

        // Finalize shape drag by creating a command
        if (dragStartPoint != null && (dragTotalDx != 0 || dragTotalDy != 0)) {
            // Important: Before creating the command, revert the temporary visual moves
            // so the command executes from the original position.
            for (Shape shape : selectedShapes) {
                shape.move(-dragTotalDx, -dragTotalDy);
            }

            // Create and execute the command
            // Pass a *copy* of selectedShapes to the command
            MoveCommand moveCmd = new MoveCommand(new ArrayList<>(selectedShapes), dragTotalDx, dragTotalDy);
            commandManager.executeCommand(moveCmd);
        }

        // Reset drag state regardless of whether a command was created
        dragStartPoint = null;
        dragOriginPoint = null;
        dragTotalDx = 0;
        dragTotalDy = 0;
        isDraggingRotationCenter = false; // Also reset rotation drag state

        repaint(); // Repaint to show final state
    }

    // ==================== SELECTION HELPERS ====================
    private void toggleShapeSelection(Shape shape) {
        if (shape != null) {
            if (selectedShapes.contains(shape)) {
                selectedShapes.remove(shape);
            } else {
                selectedShapes.add(shape);
            }
        }
    }

    private void startRectangleSelection(Point point) {
        selectedShapes.clear();
        selectionRectangle = new java.awt.Rectangle(point.x, point.y, 0, 0);
    }

    private void handleShapeSelection(Shape shape, Point point) {
        if (!selectedShapes.contains(shape)) {
            selectedShapes.clear();
            selectedShapes.add(shape);
        }
        // Set dragStartPoint to indicate a drag should begin on next mouseDragged event
        dragStartPoint = point;
        // dragOriginPoint and accumulators are set in handleMousePress
    }

    // Removed moveSelectedShapes method as logic is now in handleMouseDrag and
    // finalizeDragOrSelection

    private void updateSelectionRectangle(Point point) {
        int x = Math.min(selectionStartPoint.x, point.x);
        int y = Math.min(selectionStartPoint.y, point.y);
        selectionRectangle = new java.awt.Rectangle(
                x, y,
                Math.abs(point.x - selectionStartPoint.x),
                Math.abs(point.y - selectionStartPoint.y));
    }

    private Shape findShapeAtPoint(Point point) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(new com.geometriceditor.model.Shape.Point(point.x, point.y))) {
                return shape;
            }
        }
        return null;
    }

    private void selectShapesInRectangle(java.awt.Rectangle rect) {
        shapes.stream()
                .filter(shape -> rect.intersects(getShapeBounds(shape)))
                .filter(shape -> !selectedShapes.contains(shape))
                .forEach(selectedShapes::add);
    }

    // Use BoundingBoxVisitor to get shape bounds
    private java.awt.Rectangle getShapeBounds(Shape shape) {
        if (shape == null) {
            return new java.awt.Rectangle(); // Return empty for null shape
        }
        // Need to import BoundingBoxVisitor
        com.geometriceditor.visitor.BoundingBoxVisitor visitor = new com.geometriceditor.visitor.BoundingBoxVisitor();
        return shape.accept(visitor);
    }

    // ==================== CONTEXT MENU ====================
    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        // Reset rotation center option
        JMenuItem resetCenterItem = new JMenuItem("Reset Rotation Center");
        resetCenterItem.addActionListener(e -> {
            if (!selectedShapes.isEmpty()) {
                for (Shape shape : selectedShapes) {
                    shape.setRotationCenter(new Shape.Point(shape.getPosition()));
                }
                repaint();
            }
        });
        contextMenu.add(resetCenterItem);

        // Add more context menu items as needed
        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(e -> {
            if (selectedShapes.size() == 1) {
                PropertyEditDialog.editShapeProperties(
                        (JFrame) SwingUtilities.getWindowAncestor(this),
                        selectedShapes.get(0));
            }
        });
        contextMenu.add(propertiesItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteSelected());
        contextMenu.add(deleteItem);

        // Add keyboard shortcut for context menu
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0), "showContextMenu");
        getActionMap().put("showContextMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!selectedShapes.isEmpty()) {
                    contextMenu.show(WhiteboardPanel.this,
                            selectedShapes.get(0).getRotationCenter().x,
                            selectedShapes.get(0).getRotationCenter().y);
                }
            }
        });

        setComponentPopupMenu(contextMenu);
    }

    // ==================== GETTERS AND SETTERS ====================
    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedShapes);
    }

    public void setSelectedFillColor(Color color) {
        selectedShapes.forEach(shape -> shape.setFillColor(color));
        repaint();
    }

    public void setSelectedBorderColor(Color color) {
        selectedShapes.forEach(shape -> shape.setBorderColor(color));
        repaint();
    }

    public void setGridVisible(boolean visible) {
        this.gridVisible = visible;
        repaint();
    }

    public void setGridSize(int size) {
        this.gridSize = Math.max(5, size); // Minimum 5px grid size
        if (gridVisible)
            repaint();
    }

    public void setGridColor(Color color) {
        this.gridColor = color;
        if (gridVisible)
            repaint();
    }

    public boolean isGridVisible() {
        return gridVisible;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Color getGridColor() {
        return gridColor;
    }

    // ==================== DRAG AND DROP HANDLER ====================
    private class ShapeDropHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            // Check if the drop location is valid and the data flavor is supported
            if (!support.isDrop()) {
                return false;
            }
            return support.isDataFlavorSupported(TransferableShape.SHAPE_TYPE_FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();
            Point dropPoint = support.getDropLocation().getDropPoint();

            try {
                // Get the shape type string from the transferable
                String shapeType = (String) transferable.getTransferData(TransferableShape.SHAPE_TYPE_FLAVOR);

                // Use the ShapeFactory to create the shape at the drop location
                Shape newShape = null;
                int defaultWidth = 100;
                int defaultHeight = 50;
                int defaultRadius = 50;
                int defaultSides = 6;

                // Adjust position so the shape center is at the drop point
                int x = dropPoint.x;
                int y = dropPoint.y;

                if ("Rectangle".equals(shapeType)) {
                    // Center the rectangle on the drop point
                    x -= defaultWidth / 2;
                    y -= defaultHeight / 2;
                    newShape = shapeFactory.createRectangle(x, y, defaultWidth, defaultHeight);
                } else if ("Polygon".equals(shapeType)) {
                    // RegularPolygon position is already its center
                    newShape = shapeFactory.createRegularPolygon(x, y, defaultSides, defaultRadius);
                }

                // Add the shape using the command pattern
                if (newShape != null) {
                    commandManager.executeCommand(new AddShapeCommand(WhiteboardPanel.this, newShape));
                    return true;
                }

            } catch (UnsupportedFlavorException | IOException e) {
                System.err.println("Error importing shape data: " + e.getMessage());
                e.printStackTrace();
            }

            return false;
        }
    }
}
