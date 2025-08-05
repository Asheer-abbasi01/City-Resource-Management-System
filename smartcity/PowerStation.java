package smartcity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PowerStation extends CityResource implements Alertable {
    private static final long serialVersionUID = 1941305537978736861L;
    private double energyOutput;
    private String powerType;
    private boolean alertEnabled;
    private final ArrayList<Consumer> connectedConsumers;

    private static double totalEnergyConsumed = 0.0;

    public static void addToTotalEnergyConsumed(double amount) {
        totalEnergyConsumed += amount;
    }

    public PowerStation(String resourceID, String location, String status, double energyOutput, String powerType) {
        super(resourceID, location, status);
        this.energyOutput = energyOutput;
        this.powerType = powerType;
        this.alertEnabled = true;
        this.connectedConsumers = new ArrayList<>();
        totalEnergyUsage += energyOutput;
    }

    @Override
    public double calculateMaintenanceCost() {
        double baseRate = powerType.equals("Solar") ? 0.05 : 0.08;
        double usageCost = energyOutput * baseRate * (connectedConsumers.size() * 0.01);
        double maintenanceCost = energyOutput * baseRate + usageCost;
        totalMaintenanceCost += maintenanceCost;
        return maintenanceCost;
    }

    @Override
    public void sendEmergencyAlert(String message) {
        if (alertEnabled) {
            if (SmartCityGUI.getReportArea() != null) {
                SmartCityGUI.getReportArea().append("POWER ALERT [" + resourceID + "]: " + message + " at " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }
            EmergencyService.handlePowerOutage(this);
        }
    }

    @Override
    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    @Override
    public String generateUsageReport() {
        return String.format(
                "Power Station Report [%s]:\n- Type: %s\n- Output: %.2f MW\n- Consumers: %d\n- Total Consumed: %.2f MW\n- Maintenance Cost: $%.2f",
                resourceID, powerType, energyOutput, connectedConsumers.size(), getTotalEnergyConsumed(),
                calculateMaintenanceCost());
    }

    @Override
    public ArrayList<Object> getMetrics() {
        ArrayList<Object> metrics = new ArrayList<>();
        metrics.add(energyOutput);
        metrics.add(powerType);
        metrics.add(connectedConsumers.size());
        metrics.add(getTotalEnergyConsumed());
        return metrics;
    }

    public void addConsumer(Consumer consumer) {
        connectedConsumers.add(consumer);
        totalEnergyConsumed += consumer.getConsumption();
    }

    public void simulateOutage() {
        setStatus("Outage");
        sendEmergencyAlert("Power outage detected! Emergency response required.");
        if (repository != null) {
            ArrayList<CityResource> allResources = repository.getAll();
            for (CityResource resource : allResources) {
                if (resource instanceof EmergencyService) {
                    EmergencyService service = (EmergencyService) resource;
                    if (service.getStatus().equals("Available") && isNearby(service)) {
                        service.sendEmergencyAlert("Respond to power outage at " + getLocation());
                    }
                }
            }
        }
    }

    private boolean isNearby(EmergencyService service) {
        return getLocation().equals(service.getLocation()) || Math.random() < 0.5;
    }

    public double getEnergyOutput() {
        return energyOutput;
    }

    public String getPowerType() {
        return powerType;
    }

    public static double getTotalEnergyConsumed() {
        return totalEnergyConsumed;
    }

    public static void resetEnergyMetrics() {
        totalEnergyConsumed = 0.0;
    }

    public ArrayList<Consumer> getConnectedConsumers() {
        return connectedConsumers;
    }
}