import java.io.Serializable;

public class datatypePhone implements sendDataFactory, Serializable {
    private int numberOfPeriod;
    private String[] labels;

    @Override
    public int getInt() {
        return numberOfPeriod;
    }

    String[] getLabels() {
        return labels;
    }
}
