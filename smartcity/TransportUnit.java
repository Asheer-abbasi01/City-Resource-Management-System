package smartcity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransportUnit extends CityResource {
    private static final long serialVersionUID = 1941305537978736861L;
    private String vehicleType;
    private int passengerCapacity;
    private double fuelConsumptionRate;
    private int currentPassengers;

    public TransportUnit(String resourceID, String location, String status, String vehicleType, int passengerCapacity,
            double fuelConsumptionRate) {
        super(resourceID, location, status);
        this.vehicleType = vehicleType;
        this.passengerCapacity = passengerCapacity;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.currentPassengers = 0;
    }

    @Override
    public double calculateMaintenanceCost() {
        double baseCost = vehicleType.equals("Bus") ? 500.0 : 800.0;
        double fuelCost = fuelConsumptionRate * 3.5;
        double usageCost = passengerCapacity * 0.5;
        double maintenanceCost = baseCost + fuelCost + usageCost;
        totalMaintenanceCost += maintenanceCost;
        totalPassengers += currentPassengers;
        return maintenanceCost;
    }

    @Override
    public String generateUsageReport() {
        return String.format(
                "Transport Report [%s]:\n- Type: %s\n- Passenger Capacity: %d\n- Current Passengers: %d\n- Fuel Consumption: %.2f L\n- Maintenance Cost: $%.2f",
                resourceID, vehicleType, passengerCapacity, currentPassengers, fuelConsumptionRate,
                calculateMaintenanceCost());
    }

    @Override
    public ArrayList<Object> getMetrics() {
        ArrayList<Object> metrics = new ArrayList<>();
        metrics.add(passengerCapacity);
        metrics.add(fuelConsumptionRate);
        metrics.add(vehicleType);
        metrics.add(currentPassengers);
        return metrics;
    }

    public void setCurrentPassengers(int currentPassengers) {
        this.currentPassengers = Math.min(currentPassengers, passengerCapacity);
    }

    public void adjustRouteBasedOnTraffic(int change) {
        int newPassengers = Math.max(0, Math.min(passengerCapacity, currentPassengers + change));
        setCurrentPassengers(newPassengers);
        String statusUpdate = (change > 0) ? "Increased traffic" : (change < 0) ? "Reduced traffic" : "Stable traffic";
        if (SmartCityGUI.getReportArea() != null) {
            SmartCityGUI.getReportArea()
                    .append("TRAFFIC UPDATE: " + resourceID + " - " + statusUpdate + ", Passengers: " + newPassengers +
                            " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
        }
    }

    public int getCurrentPassengers() {
        return currentPassengers;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public double getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }
}