package Shared;

import java.io.Serializable;

public class Period implements Serializable {
    private static final long serialVersionUID =
            -8682408877221548064L;
    String period;
    Float income;

    public Period(String period, Float income) {
        this.period = period;
        this.income = income;
    }

    public String getPeriod() {
        return period;
    }

    public Float getIncome() {
        return income;
    }
}
