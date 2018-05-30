package com.swg.jalinatm.POJO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

public class DataWrapper implements Serializable {

    private ArrayList<ATM> atmList;

    public DataWrapper(ArrayList<ATM> atmList) {
        this.atmList = atmList;
    }

    public ArrayList<ATM> getAtmList() {
        return atmList;
    }

    public void setAtmList(ArrayList<ATM> atmList) {
        this.atmList = atmList;
    }
}
