package smartcity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class CityMapPanel extends JPanel {
    private ArrayList<CityResource> resources;

    public CityMapPanel() {
        this.resources = new ArrayList<>();
        setPreferredSize(new Dimension(300, 300));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createTitledBorder("City Map"));
    }

    public void updateResources(ArrayList<CityResource> resources) {
        this.resources = resources;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        Random rand = new Random(42);
        for (CityResource resource : resources) {
            int x = 40 + rand.nextInt(width - 80);
            int y = 40 + rand.nextInt(height - 80);
            Color color;
            if (resource instanceof TransportUnit)
                color = Color.GREEN.darker();
            else if (resource instanceof PowerStation)
                color = Color.BLUE.darker();
            else if (resource instanceof EmergencyService)
                color = Color.RED.darker();
            else
                color = Color.GRAY;
            g.setColor(color);
            g.fillOval(x, y, 24, 24);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, 24, 24);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(resource.getResourceID(), x - 2, y + 36);
        }
    }
}