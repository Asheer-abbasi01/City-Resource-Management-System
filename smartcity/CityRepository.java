package smartcity;

import java.io.*;
import java.util.ArrayList;

public class CityRepository<T extends CityResource> implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private ArrayList<T> resources;
    private ArrayList<String> resourceIDs;

    public CityRepository() {
        this.resources = new ArrayList<>();
        this.resourceIDs = new ArrayList<>();
    }

    public void add(T resource) {
        if (resourceIDs.contains(resource.getResourceID())) {
            throw new IllegalArgumentException("Resource ID already exists: " + resource.getResourceID());
        }
        resources.add(resource);
        resourceIDs.add(resource.getResourceID());
    }

    public T get(String resourceID) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getResourceID().equals(resourceID)) {
                return resources.get(i);
            }
        }
        return null;
    }

    public void remove(String resourceID) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getResourceID().equals(resourceID)) {
                resources.remove(i);
                resourceIDs.remove(resourceID);
                break;
            }
        }
    }

    public ArrayList<T> getAll() {
        return new ArrayList<>(resources);
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(resources.toArray(new CityResource[0]));
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filename);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            CityResource[] loadedArray = (CityResource[]) ois.readObject();
            resources.clear();
            resourceIDs.clear();
            CityResource.resetMetrics();
            PowerStation.resetEnergyMetrics();
            EmergencyService.resetEmergencyMetrics();
            for (CityResource resource : loadedArray) {
                resources.add((T) resource);
                resourceIDs.add(resource.getResourceID());
                if (resource instanceof PowerStation) {
                    PowerStation ps = (PowerStation) resource;
                    for (Consumer c : ps.getConnectedConsumers()) {
                        PowerStation.addToTotalEnergyConsumed(c.getConsumption());
                    }
                } else if (resource instanceof TransportUnit) {
                    TransportUnit tu = (TransportUnit) resource;
                    CityResource.totalPassengers += tu.getCurrentPassengers();
                }
            }
            CityResource.totalResources = resources.size();
            CityResource.totalEnergyUsage = PowerStation.getTotalEnergyConsumed();
        }
    }
}