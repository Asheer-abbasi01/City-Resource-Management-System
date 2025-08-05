package smartcity;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class ResourceChartPanel extends JPanel {
    private ArrayList<CityResource> resources;

    public ResourceChartPanel() {
        this.resources = new ArrayList<>();
        setPreferredSize(new Dimension(300, 300));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Resource Distribution"));
    }

    public void updateResources(ArrayList<CityResource> resources) {
        this.resources = resources;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        int barWidth = 60;
        int gap = 20;

        int transportCount = 0, powerCount = 0, emergencyCount = 0;
        for (CityResource resource : resources) {
            if (resource instanceof TransportUnit)
                transportCount++;
            else if (resource instanceof PowerStation)
                powerCount++;
            else if (resource instanceof EmergencyService)
                emergencyCount++;
        }

        int maxCount = Math.max(1, Math.max(transportCount, Math.max(powerCount, emergencyCount)));
        double scale = (height - 2 * margin - 30) / (double) maxCount;

        g2d.setColor(Color.BLACK);
        g2d.drawLine(margin, height - margin, margin, margin);
        g2d.drawLine(margin, height - margin, width - margin, height - margin);

        g2d.setColor(Color.GREEN);
        int transportHeight = (int) (transportCount * scale);
        g2d.fillRect(margin + gap, height - margin - transportHeight, barWidth, transportHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Transport", margin + gap, height - margin + 15);
        g2d.drawString(String.valueOf(transportCount), margin + gap + barWidth / 2 - 10,
                height - margin - transportHeight - 5);

        g2d.setColor(Color.BLUE);
        int powerHeight = (int) (powerCount * scale);
        g2d.fillRect(margin + 2 * gap + barWidth, height - margin - powerHeight, barWidth, powerHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Power", margin + 2 * gap + barWidth, height - margin + 15);
        g2d.drawString(String.valueOf(powerCount), margin + 2 * gap + barWidth + barWidth / 2 - 10,
                height - margin - powerHeight - 5);

        g2d.setColor(Color.RED);
        int emergencyHeight = (int) (emergencyCount * scale);
        g2d.fillRect(margin + 3 * gap + 2 * barWidth, height - margin - emergencyHeight, barWidth, emergencyHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Emergency", margin + 3 * gap + 2 * barWidth, height - margin + 15);
        g2d.drawString(String.valueOf(emergencyCount), margin + 3 * gap + 2 * barWidth + barWidth / 2 - 10,
                height - margin - emergencyHeight - 5);

        int legendX = width - 120, legendY = margin;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(legendX - 5, legendY - 5, 110, 80);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 5, legendY - 5, 110, 80);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Legend", legendX, legendY + 10);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.GREEN);
        g2d.fillRect(legendX, legendY + 15, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Transport", legendX + 15, legendY + 24);
        g2d.setColor(Color.BLUE);
        g2d.fillRect(legendX, legendY + 30, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Power", legendX + 15, legendY + 39);
        g2d.setColor(Color.RED);
        g2d.fillRect(legendX, legendY + 45, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Emergency", legendX + 15, legendY + 54);
    }
}