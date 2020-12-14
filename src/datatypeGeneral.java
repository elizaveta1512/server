import java.io.Serializable;

public class datatypeGeneral implements sendDataFactory, Serializable {
    private int numberOfPeriod;
    private float income;

    datatypeGeneral(int period, float income)
    {
        numberOfPeriod = period;
        this.income = income;
    }

    @Override
    public int getInt() {
        return numberOfPeriod;
    }

    float getIncome() {
        return income;
    }
}
