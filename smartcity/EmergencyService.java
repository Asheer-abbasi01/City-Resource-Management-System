package smartcity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.Timer;

public class EmergencyService extends CityResource implements Alertable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String serviceType;
    private int responseTime;
    private int callsHandled;
    private boolean onDuty;

    private static int totalEmergencyResponses = 0;
    private static double averageResponseTime = 0.0;

    public EmergencyService(String resourceID, String location, String status, String serviceType, int responseTime,
            int callsHandled) {
        super(resourceID, location, status);
        this.serviceType = serviceType;
        this.responseTime = responseTime;
        this.callsHandled = callsHandled;
        this.onDuty = true;
    }

    @Override
    public double calculateMaintenanceCost() {
        double baseCost = serviceType.equals("Fire") ? 2000.0 : 1500.0;
        double equipmentCost = 500.0;
        double usageCost = callsHandled * 100.0;
        double maintenanceCost = baseCost + equipmentCost + usageCost;
        totalMaintenanceCost += maintenanceCost;
        return maintenanceCost;
    }

    @Override
    public void sendEmergencyAlert(String message) {
        if (onDuty) {
            setStatus("Responding");
            totalEmergencyResponses++;
            callsHandled++;
            if (SmartCityGUI.getReportArea() != null) {
                SmartCityGUI.getReportArea()
                        .append("EMERGENCY DISPATCH [" + serviceType + " - " + resourceID + "]: " + message +
                                " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }

            Timer timer = new Timer(responseTime * 100, e -> {
                setStatus("Available");
                if (SmartCityGUI.getReportArea() != null) {
                    SmartCityGUI.getReportArea()
                            .append(serviceType + " unit " + resourceID + " completed response at " +
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    @Override
    public boolean isAlertEnabled() {
        return onDuty;
    }

    @Override
    public String generateUsageReport() {
        return String.format(
                "Emergency Service Report [%s]:\n- Type: %s\n- Response Time: %d min\n- Calls Handled: %d\n- Status: %s\n- Total Responses: %d\n- Maintenance Cost: $%.2f",
                resourceID, serviceType, responseTime, callsHandled, status, totalEmergencyResponses,
                calculateMaintenanceCost());
    }

    @Override
    public ArrayList<Object> getMetrics() {
        ArrayList<Object> metrics = new ArrayList<>();
        metrics.add(serviceType);
        metrics.add(responseTime);
        metrics.add(callsHandled);
        return metrics;
    }

    public static void handlePowerOutage(PowerStation station) {
        if (SmartCityGUI.getReportArea() != null) {
            SmartCityGUI.getReportArea()
                    .append("Emergency services alerted for power outage at: " + station.getLocation() +
                            " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
        }
    }

    public String getServiceType() {
        return serviceType;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getCallsHandled() {
        return callsHandled;
    }

    public static int getTotalEmergencyResponses() {
        return totalEmergencyResponses;
    }

    public static void resetEmergencyMetrics() {
        totalEmergencyResponses = 0;
        averageResponseTime = 0.0;
    }
}