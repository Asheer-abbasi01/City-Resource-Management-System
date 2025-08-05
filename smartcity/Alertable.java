package smartcity;

public interface Alertable {
    void sendEmergencyAlert(String message);

    boolean isAlertEnabled();
}