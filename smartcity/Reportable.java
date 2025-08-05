package smartcity;

import java.util.ArrayList;

public interface Reportable {
    String generateUsageReport();

    ArrayList<Object> getMetrics();
}