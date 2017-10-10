package com.xahaolan.emanage.view.wheel.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aiodiy on 2017/6/22.
 */

public class CityData implements Serializable {
    private String n;
    private List<CountyData> a;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<CountyData> getA() {
        return a;
    }

    public void setA(List<CountyData> a) {
        this.a = a;
    }
}
