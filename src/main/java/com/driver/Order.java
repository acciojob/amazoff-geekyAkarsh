package com.driver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        this.id = id;
        this.deliveryTime = convertDeliveryTime(deliveryTime);
        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = convertDeliveryTime(deliveryTime);
    }

    public static int convertDeliveryTime(String deliveryTime) {
        String[] time = deliveryTime.split(":");
        int hr = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        return (hr*60) + min;
    }

    public static String convertDeliveryTime(int deliveryTime){

        int HH = deliveryTime/60;
        int MM = deliveryTime%60;
        String hh = String.valueOf(HH);
        String mm = String.valueOf(MM);
        if(hh.length() == 1)
            hh = "0" + hh;
        if(mm.length() == 1)
            mm = "0" + mm;
        return hh+ ":" + mm;
    }

    //    public String getId() {
//        return id;
//    }
//
//    public int getDeliveryTime() {
//        return deliveryTime;
//    }
}
