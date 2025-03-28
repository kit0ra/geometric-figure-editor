package com.geometriceditor.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.geometriceditor.factory.ShapeFactory;

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

        // IMPORTANT: Set the whiteboard for the toolbar AFTER whiteboard is created
        toolbarPanel.setWhiteboard(whiteboard);

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
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        // Ensure UI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
