package org.example.entity;

import java.io.Serializable;

public class Card  implements Serializable {
    private String id;
    private Integer salaryLV;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer setSalaryLV() {
        return salaryLV;
    }

    public void setSalaryLV(String id) {
        this.salaryLV = salaryLV;
    }
}
