package Shared;

import java.io.Serializable;

public class Sale implements Serializable {
    private static final long serialVersionUID =
            6984444017860477703L;
    String label;
    String model;
    String date;
    int amount;

    public Sale(String label, String model, String date, int amount) {
        this.label = label;
        this.model = model;
        this.date = date;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public String getModel() {
        return model;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }
}
