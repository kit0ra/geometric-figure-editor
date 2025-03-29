package com.geometriceditor.ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.geometriceditor.model.Shape;

public class PropertyEditDialog extends JDialog {
    private Shape shape;
    private JTextField xField, yField;
    private JButton colorFillButton, colorBorderButton;
    private JSpinner rotationSpinner;
    private boolean confirmed = false;

    public PropertyEditDialog(JFrame parent, Shape shape) {
        super(parent, "Edit Shape Properties", true);
        this.shape = shape;

        initComponents();
        setupLayout();
        // setupListeners();
    }

    private void initComponents() {
        // Position fields
        xField = new JTextField(String.valueOf(shape.getPosition().x));
        yField = new JTextField(String.valueOf(shape.getPosition().y));

        // Color buttons
        colorFillButton = new JButton("Fill Color");
        colorFillButton.setBackground(shape.getFillColor());

        colorBorderButton = new JButton("Border Color");
        colorBorderButton.setBackground(shape.getBorderColor());

        // Rotation spinner
        SpinnerModel rotationModel = new SpinnerNumberModel(
                shape.getRotation(), // initial value
                0.0, // min
                360.0, // max
                1.0 // step
        );
        rotationSpinner = new JSpinner(rotationModel);
    }

    private void setupLayout() {
        setLayout(new GridLayout(0, 2, 10, 10));

        add(new JLabel("X Position:"));
        add(xField);

        add(new JLabel("Y Position:"));
        add(yField);

        add(new JLabel("Fill Color:"));
        add(colorFillButton);

        add(new JLabel("Border Color:"));
        add(colorBorderButton);

        add(new JLabel("Rotation:"));
        add(rotationSpinner);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        add(okButton);
        add(cancelButton);

        // Color selection listeners
        colorFillButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                    this,
                    "Choose Fill Color",
                    shape.getFillColor());
            if (newColor != null) {
                colorFillButton.setBackground(newColor);
            }
        });

        colorBorderButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                    this,
                    "Choose Border Color",
                    shape.getBorderColor());
            if (newColor != null) {
                colorBorderButton.setBackground(newColor);
            }
        });

        // OK and Cancel button actions
        okButton.addActionListener(e -> {
            try {
                // Update position
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                shape.setPosition(new Shape.Point(x, y));

                // Update colors
                shape.setFillColor(colorFillButton.getBackground());
                shape.setBorderColor(colorBorderButton.getBackground());

                // Update rotation
                shape.setRotation(((Number) rotationSpinner.getValue()).floatValue());

                confirmed = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid numeric values",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getParent());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // Static method to show property dialog
    public static boolean editShapeProperties(JFrame parent, Shape shape) {
        PropertyEditDialog dialog = new PropertyEditDialog(parent, shape);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}
