package com.geometriceditor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.geometriceditor.factory.ShapeFactory;
import com.geometriceditor.state.StateManager;

public class MainWindow extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private WhiteboardPanel whiteboard;
    private ToolbarPanel toolbarPanel;
    private ShapeFactory shapeFactory;

    public MainWindow() {
        // Basic window setup
        setTitle("Geometric Figure Editor");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize factory
        shapeFactory = ShapeFactory.getInstance();

        // Create components
        initializeWhiteboard();
        initializeToolbar();
        initializeMenuBar();

        // Center the window
        setLocationRelativeTo(null);

    }

    private void initializeToolbar() {
        toolbarPanel = new ToolbarPanel(shapeFactory);
        toolbarPanel.setWhiteboard(whiteboard);

        // Create a scrollable toolbar container
        JScrollPane toolbarScroll = new JScrollPane(toolbarPanel);
        toolbarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        toolbarScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        toolbarScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel toolbarContainer = new JPanel(new BorderLayout());
        toolbarContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolbarContainer.add(toolbarPanel, BorderLayout.CENTER);

        add(toolbarPanel, BorderLayout.NORTH);
    }

    private void initializeWhiteboard() {
        whiteboard = new WhiteboardPanel();
        add(new JScrollPane(whiteboard), BorderLayout.CENTER);
    }

    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to create a new file?", "New File",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                whiteboard.clearShapes();
            }
        });

        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");
        saveItem.addActionListener(e -> saveToFile());
        loadItem.addActionListener(e -> loadFromFile());
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> whiteboard.undo());

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(e -> whiteboard.redo());

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(e -> {
            if (whiteboard.getSelectedShapes().size() == 1) {
                PropertyEditDialog.editShapeProperties(this, whiteboard.getSelectedShapes().get(0));
            }
        });

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(propertiesItem);
        menuBar.add(editMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");

        // Grid visibility toggle
        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Show Grid");
        gridItem.addActionListener(e -> {
            whiteboard.setGridVisible(gridItem.isSelected());
        });
        viewMenu.add(gridItem);

        // Grid size submenu
        JMenu gridSizeMenu = new JMenu("Grid Size");
        int[] sizes = { 10, 15, 20, 25, 30, 40, 50 };
        for (int size : sizes) {
            JMenuItem sizeItem = new JMenuItem(size + "px");
            sizeItem.addActionListener(e -> whiteboard.setGridSize(size));
            gridSizeMenu.add(sizeItem);
        }
        viewMenu.add(gridSizeMenu);

        // Grid color chooser
        JMenuItem gridColorItem = new JMenuItem("Grid Color");
        gridColorItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                    this,
                    "Choose Grid Color",
                    whiteboard.getGridColor());
            if (newColor != null) {
                whiteboard.setGridColor(newColor);
            }
        });
        viewMenu.add(gridColorItem);

        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

        // Add keyboard shortcuts
        addKeyboardShortcuts();
    }

    public static void main(String[] args) {
        // Ensure UI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                StateManager stateManager = new StateManager();
                stateManager.saveToFile(whiteboard, fileChooser.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                StateManager stateManager = new StateManager();
                stateManager.loadFromFile(whiteboard, fileChooser.getSelectedFile());
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private void addKeyboardShortcuts() {
        // Delete selected shapes with Delete key
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "delete");
        getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whiteboard.deleteSelected();
            }
        });

        // Group/Ungroup with Ctrl+G/Ctrl+Shift+G
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("control G"), "group");
        getRootPane().getActionMap().put("group", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whiteboard.groupSelected();
            }
        });

        getRootPane().getInputMap().put(KeyStroke.getKeyStroke("control shift G"), "ungroup");
        getRootPane().getActionMap().put("ungroup", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whiteboard.ungroupSelected();
            }
        });
    }

}
