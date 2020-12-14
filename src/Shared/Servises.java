package Shared;

import java.io.Serializable;

public class Servises implements Serializable {
    private static final long serialVersionUID =
            5557573101675282136L;
    String label;
    String model;
    Float price;

    public Servises(String label, String model, Float price) {
        this.label = label;
        this.model = model;
        this.price = price;
    }

    public String getLabel() {
        return label;
    }

    public String getModel() {
        return model;
    }

    public Float getPrice() {
        return price;
    }
}
