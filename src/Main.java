import view.LoginView;
import view.UITheme;

import javax.swing.*;

/**
 * Main – Application entry point.
 *
 * Demonstrates OOP:
 *  - Encapsulation  : all fields private with getters/setters in models
 *  - Inheritance    : JPanel subclassing in views
 *  - Abstraction    : controllers hide Firebase logic from views
 *  - Polymorphism   : JTable renderers, SwingWorker generics
 */
public class Main {

    public static void main(String[] args) {
        // Apply cross-platform L&F before creating any component
        UITheme.applyLookAndFeel();

        // Start on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== Inventory Management System Starting ===");
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
