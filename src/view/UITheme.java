package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UITheme – centralized design tokens for all Swing views.
 * Demonstrates OOP: Abstraction (utility class).
 */
public class UITheme {

    // ── Colors (Midnight Professional Dark Theme) ─────────────────────────────
    public static final Color BG_DARK      = new Color(15, 23, 42);   // Slate-900
    public static final Color BG_PANEL     = new Color(30, 41, 59);   // Slate-800
    public static final Color BG_CARD      = new Color(51, 65, 85);   // Slate-700
    public static final Color ACCENT       = new Color(99, 102, 241); // Indigo-500
    public static final Color ACCENT_HOVER = new Color(79, 70, 229); // Indigo-600
    public static final Color TEXT_PRIMARY  = new Color(248, 250, 252); // Slate-50
    public static final Color TEXT_SECONDARY= new Color(148, 163, 184); // Slate-400
    public static final Color SUCCESS       = new Color(34, 197, 94);  // Green-500
    public static final Color DANGER        = new Color(239, 68, 68);  // Red-500
    public static final Color WARNING       = new Color(234, 179, 8);  // Yellow-500
    public static final Color TABLE_HEADER  = new Color(30, 41, 59);   // Slate-800
    public static final Color TABLE_ROW_ALT = new Color(22, 32, 51); 

    // ── Fonts ───────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN, 14);

    // ── Dimensions ──────────────────────────────────────────────────────────────
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT  = 38;
    public static final int BORDER_RADIUS = 12;

    private UITheme() {}

    // ── Factory Methods ──────────────────────────────────────────────────────────

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADER);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, BUTTON_HEIGHT));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(185, 28, 28)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(DANGER); }
        });
        return btn;
    }

    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(SUCCESS);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(21, 128, 61)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(SUCCESS); }
        });
        return btn;
    }

    public static JTextField styledField(String placeholder) {
        JTextField field = new JTextField();
        field.setBackground(new Color(30, 41, 59));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setFont(FONT_INPUT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(200, INPUT_HEIGHT));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        return field;
    }

    public static JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(new Color(30, 41, 59));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setFont(FONT_INPUT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(200, INPUT_HEIGHT));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        return field;
    }

    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Base font
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel bodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    /** Applies theme to any JPanel. */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /** Applies theme to a JTable. */
    public static void styleTable(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(46); 
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(51, 65, 85, 80)); // Very subtle line
        
        // Selection Styling
        table.setSelectionBackground(new Color(99, 102, 241, 40)); 
        table.setSelectionForeground(Color.WHITE);
        
        // Header Styling
        table.getTableHeader().setBackground(BG_PANEL);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));
        
        // Advanced Custom Renderer
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Zebra Striping & Selection Background
                if (isSelected) {
                    c.setBackground(new Color(99, 102, 241, 50));
                    // Indigo left border for selected row (matches sidebar)
                    if (column == 0) {
                        c.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT),
                            BorderFactory.createEmptyBorder(0, 11, 0, 15)
                        ));
                    } else {
                        c.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                    }
                } else {
                    c.setBackground(row % 2 == 0 ? BG_DARK : BG_PANEL);
                    c.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                    
                    // High-Contrast Status Coloring
                    String text = String.valueOf(value);
                    if (text.contains("Low Stock") || text.equals("0")) {
                        c.setForeground(DANGER);
                    } else if (text.contains("OK") || text.contains("✓")) {
                        c.setForeground(SUCCESS);
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                    }
                }
                
                return c;
            }
        });

        table.setBorder(BorderFactory.createEmptyBorder());
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    /** Styles a scroll pane (scrollbar) */
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        
        // Modern thin scrollbars
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(BG_DARK);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 6));
    }

    /** Applies look-and-feel. Call once from main(). */
    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.tabsOverlapBorder", true);
            
            // Tooltip styling
            UIManager.put("ToolTip.background", BG_PANEL);
            UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
            UIManager.put("ToolTip.border", BorderFactory.createLineBorder(ACCENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
