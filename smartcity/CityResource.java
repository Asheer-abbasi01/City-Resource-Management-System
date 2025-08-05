package smartcity;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class CityResource implements Reportable, Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    protected String resourceID;
    protected String location;
    protected String status;
    protected LocalDateTime lastUpdated;

    protected static int totalResources = 0;
    protected static double totalMaintenanceCost = 0.0;
    protected static int totalPassengers = 0;
    protected static double totalEnergyUsage = 0.0;

    protected static CityRepository<CityResource> repository;

    public CityResource(String resourceID, String location, String status) {
        this.resourceID = resourceID;
        this.location = location;
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
        totalResources++;
    }

    public abstract double calculateMaintenanceCost();

    @Override
    public String toString() {
        return String.format("%s [%s] - %s at %s",
                getClass().getSimpleName(), resourceID, status, location);
    }

    public String getResourceID() {
        return resourceID;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public static int getTotalResources() {
        return totalResources;
    }

    public static double getTotalMaintenanceCost() {
        return totalMaintenanceCost;
    }

    public static int getTotalPassengers() {
        return totalPassengers;
    }

    public static double getTotalEnergyUsage() {
        return totalEnergyUsage;
    }

    public static void setRepository(CityRepository<CityResource> repo) {
        repository = repo;
    }

    public static void resetMetrics() {
        totalResources = 0;
        totalMaintenanceCost = 0.0;
        totalPassengers = 0;
        totalEnergyUsage = 0.0;
    }
}