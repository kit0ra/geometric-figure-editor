package com.geometriceditor;

import javax.swing.SwingUtilities;

import com.geometriceditor.ui.MainWindow;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        // Ensure UI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
