package smartcity;

import java.io.Serializable;
import java.util.ArrayList;

public class ResourceHub implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String hubID;
    private String location;
    private final ArrayList<TransportUnit> transportUnits;

    public ResourceHub(String hubID, String location) {
        this.hubID = hubID;
        this.location = location;
        this.transportUnits = new ArrayList<>();
    }

    public void addTransportUnit(TransportUnit unit) {
        transportUnits.add(unit);
    }

    public ArrayList<TransportUnit> getTransportUnits() {
        return transportUnits;
    }

    public String getHubID() {
        return hubID;
    }

    public String getLocation() {
        return location;
    }
}