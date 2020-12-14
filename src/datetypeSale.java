import java.io.Serializable;
import java.util.Map;

public class datetypeSale implements sendDataFactory, Serializable {
    private int numberOfPeriod;
    private Map<String, Float> results;

    @Override
    public int getInt() {
        return numberOfPeriod;
    }

    Map<String, Float> getResults() {
        return results;
    }
}
