package com.swg.jalinatm.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by user on 6/4/2018.
 */

public class CurrencyFormatter {

    public static String format(double input){
        NumberFormat formatter = new DecimalFormat("#.###");
        return formatter.format(input);
    }
}
