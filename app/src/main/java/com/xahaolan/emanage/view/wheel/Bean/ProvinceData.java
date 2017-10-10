package com.xahaolan.emanage.view.wheel.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aiodiy on 2017/6/22.
 */

public class ProvinceData implements Serializable {
    private String p;
    private List<CityData> c;

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public List<CityData> getC() {
        return c;
    }

    public void setC(List<CityData> c) {
        this.c = c;
    }
}
