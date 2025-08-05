package smartcity;

import java.io.Serializable;
import java.util.ArrayList;

public class SmartGrid implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String gridID;
    private ArrayList<PowerStation> powerStations;
    private ArrayList<Consumer> consumers;
    private double totalCapacity;

    public SmartGrid(String gridID) {
        this.gridID = gridID;
        this.powerStations = new ArrayList<>();
        this.consumers = new ArrayList<>();
        this.totalCapacity = 0.0;
    }

    public void addPowerStation(PowerStation station) {
        powerStations.add(station);
        totalCapacity += station.getEnergyOutput();
    }

    public void addConsumer(Consumer consumer) {
        consumers.add(consumer);
        if (!powerStations.isEmpty()) {
            powerStations.get(0).addConsumer(consumer);
        }
    }

    public double getTotalCapacity() {
        return totalCapacity;
    }

    public ArrayList<PowerStation> getPowerStations() {
        return powerStations;
    }

    public ArrayList<Consumer> getConsumers() {
        return consumers;
    }
}