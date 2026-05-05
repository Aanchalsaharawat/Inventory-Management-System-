package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * IconFactory – generates flat colored module icons because Swing on Windows
 * does not render colored emojis. Each icon is a rounded square with a white
 * silhouette drawn via Java2D.
 */
public class IconFactory {

    // Module accent colours (matched to the screenshot feel)
    private static final Color C_DASHBOARD = new Color(99, 102, 241);   // Indigo
    private static final Color C_PRODUCTS  = new Color(212, 163, 115);  // Tan
    private static final Color C_SUPPLIERS = new Color(100, 116, 139);  // Slate
    private static final Color C_BILLING   = new Color(59, 130, 246);   // Blue
    private static final Color C_STOCK     = new Color(34, 197, 94);    // Green
    private static final Color C_REPORTS   = new Color(203, 213, 225);  // Light Slate
    private static final Color C_USERS     = new Color(139, 92, 246);   // Violet

    public static ImageIcon getIcon(String module, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = 2;
        RoundRectangle2D bg = new RoundRectangle2D.Float(pad, pad, size - pad * 2, size - pad * 2, size * 0.22f, size * 0.22f);

        switch (module) {
            case "Dashboard":
                g2.setColor(C_DASHBOARD);
                g2.fill(bg);
                drawHome(g2, size);
                break;
            case "Products":
                g2.setColor(C_PRODUCTS);
                g2.fill(bg);
                drawBox(g2, size);
                break;
            case "Suppliers":
                g2.setColor(C_SUPPLIERS);
                g2.fill(bg);
                drawFactory(g2, size);
                break;
            case "Billing":
                g2.setColor(C_BILLING);
                g2.fill(bg);
                drawCart(g2, size);
                break;
            case "Stock":
                g2.setColor(C_STOCK);
                g2.fill(bg);
                drawChart(g2, size);
                break;
            case "Reports":
                g2.setColor(C_REPORTS);
                g2.fill(bg);
                drawDocument(g2, size);
                break;
            case "Users":
                g2.setColor(C_USERS);
                g2.fill(bg);
                drawPerson(g2, size);
                break;
            default:
                g2.setColor(UITheme.BG_CARD);
                g2.fill(bg);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    /* ── White silhouette helpers ─────────────────────────────────────────── */

    private static void drawHome(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2;
        int w = s * 5 / 10, h = s * 4 / 10;
        int x = cx - w / 2, y = cy - h / 2 + 2;
        // body
        g2.fillRect(x, y + h / 3, w, h * 2 / 3);
        // door
        g2.setColor(C_DASHBOARD);
        g2.fillRect(cx - w / 6, y + h / 2, w / 3, h / 2);
        g2.setColor(Color.WHITE);
        // roof
        Polygon roof = new Polygon();
        roof.addPoint(cx, y - h / 6);
        roof.addPoint(x - w / 8, y + h / 3);
        roof.addPoint(x + w + w / 8, y + h / 3);
        g2.fillPolygon(roof);
    }

    private static void drawBox(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2;
        int w = s * 5 / 10, h = s * 5 / 10;
        int x = cx - w / 2, y = cy - h / 2;
        g2.drawRoundRect(x, y, w, h, 4, 4);
        // tape
        g2.drawLine(cx - w / 6, y, cx + w / 6, y);
        g2.drawLine(cx, y, cx, y + h / 3);
    }

    private static void drawFactory(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2 + 1;
        int w = s * 6 / 10, h = s * 5 / 10;
        int x = cx - w / 2, y = cy - h / 2;
        // three building rects
        int bw = w / 4;
        g2.fillRect(x, y + h / 3, bw, h * 2 / 3);
        g2.fillRect(x + bw + 2, y + h / 5, bw, h * 4 / 5);
        g2.fillRect(x + (bw + 2) * 2, y, bw, h);
        // smoke puffs
        g2.fillOval(x + (bw + 2) * 2 + bw / 4, y - 4, 4, 4);
        g2.fillOval(x + (bw + 2) * 2 + bw / 4 + 2, y - 7, 3, 3);
    }

    private static void drawCart(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2 + 1;
        int w = s * 5 / 10, h = s * 4 / 10;
        int x = cx - w / 2, y = cy - h / 2;
        // basket
        g2.drawLine(x, y + h / 3, x + w, y + h / 3);
        g2.drawLine(x + w, y + h / 3, x + w * 3 / 4, y + h);
        g2.drawLine(x + w * 3 / 4, y + h, x + w / 4, y + h);
        g2.drawLine(x + w / 4, y + h, x, y + h / 3);
        // handle
        g2.drawLine(x + w, y + h / 3, x + w, y);
        g2.drawLine(x + w, y, x + w * 2 / 3, y);
        // wheel
        g2.drawOval(cx - 3, y + h + 1, 5, 5);
    }

    private static void drawChart(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2 + 1;
        int w = s * 5 / 10, h = s * 5 / 10;
        int x = cx - w / 2, y = cy - h / 2;
        int bw = w / 5;
        int gap = (w - bw * 3) / 2;
        // three bars
        g2.fillRect(x, y + h * 2 / 3, bw, h / 3);
        g2.fillRect(x + bw + gap, y + h / 3, bw, h * 2 / 3);
        g2.fillRect(x + (bw + gap) * 2, y, bw, h);
    }

    private static void drawDocument(Graphics2D g2, int s) {
        g2.setColor(UITheme.BG_DARK);
        int cx = s / 2, cy = s / 2;
        int w = s * 5 / 10, h = s * 6 / 10;
        int x = cx - w / 2, y = cy - h / 2;
        g2.fillRect(x, y, w, h);
        // folded corner
        Polygon fold = new Polygon();
        fold.addPoint(x + w * 2 / 3, y);
        fold.addPoint(x + w, y + h / 3);
        fold.addPoint(x + w * 2 / 3, y + h / 3);
        g2.fillPolygon(fold);
        // lines
        g2.setColor(UITheme.TEXT_SECONDARY);
        int ly = y + h / 2;
        for (int i = 0; i < 3; i++) {
            g2.drawLine(x + 4, ly + i * 5, x + w - 4, ly + i * 5);
        }
    }

    private static void drawPerson(Graphics2D g2, int s) {
        g2.setColor(Color.WHITE);
        int cx = s / 2, cy = s / 2;
        // head
        int r = s * 2 / 10;
        g2.fillOval(cx - r / 2, cy - r - 1, r, r);
        // shoulders
        g2.fillArc(cx - s * 3 / 10, cy + r / 2 - 1, s * 6 / 10, s * 5 / 10, 0, 180);
    }
}
