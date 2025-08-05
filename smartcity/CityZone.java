package smartcity;

import java.io.Serializable;
import java.util.ArrayList;

public class CityZone implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String zoneID;
    private ArrayList<ResourceHub> resourceHubs;
    private ArrayList<PowerStation> powerStations;
    private ArrayList<EmergencyService> emergencyServices;

    public CityZone(String zoneID) {
        this.zoneID = zoneID;
        this.resourceHubs = new ArrayList<>();
        this.powerStations = new ArrayList<>();
        this.emergencyServices = new ArrayList<>();
    }

    public void addResourceHub(ResourceHub hub) {
        resourceHubs.add(hub);
    }

    public void addPowerStation(PowerStation station) {
        powerStations.add(station);
    }

    public void addEmergencyService(EmergencyService service) {
        emergencyServices.add(service);
    }

    public String getZoneID() {
        return zoneID;
    }

    public ArrayList<ResourceHub> getResourceHubs() {
        return resourceHubs;
    }

    public ArrayList<PowerStation> getPowerStations() {
        return powerStations;
    }

    public ArrayList<EmergencyService> getEmergencyServices() {
        return emergencyServices;
    }
}