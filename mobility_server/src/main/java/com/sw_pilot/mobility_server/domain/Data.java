package com.sw_pilot.mobility_server.domain;

public class Data {
    private String itemName;
    private int value;
    private String areaName; // 추가: 구역 이름 필드
    private String time;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time) {
        this.time= time;
    }
}
