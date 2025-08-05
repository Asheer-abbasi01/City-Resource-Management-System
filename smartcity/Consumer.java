package smartcity;

import java.io.Serializable;

public class Consumer implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String consumerID;
    private String type;
    private double consumption;

    public Consumer(String consumerID, String type, double consumption) {
        this.consumerID = consumerID;
        this.type = type;
        this.consumption = consumption;
    }

    public double getConsumption() {
        return consumption;
    }

    public String getType() {
        return type;
    }

    public String getConsumerID() {
        return consumerID;
    }
}