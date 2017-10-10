package com.xahaolan.emanage.view.wheel.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aiodiy on 2017/6/22.
 */

public class AddressData implements Serializable {
    private List<ProvinceData> citylist;

    public List<ProvinceData> getCitylist() {
        return citylist;
    }

    public void setCitylist(List<ProvinceData> citylist) {
        this.citylist = citylist;
    }
}
