package com.example.ridesharingq.objects;

import java.util.ArrayList;

public class OrderList {
    private String firstOrder;
    private String secondOrder;
    private String thirdOrder;
    private ArrayList<String> firstList;
    private ArrayList<String> secondList;
    private ArrayList<String> thirdList;

    public ArrayList<String> getFirstList() {
        return firstList;
    }

    public void setFirstList(ArrayList<String> firstList) {
        this.firstList = firstList;
    }

    public ArrayList<String> getSecondList() {
        return secondList;
    }

    public void setSecondList(ArrayList<String> secondList) {
        this.secondList = secondList;
    }

    public ArrayList<String> getThirdList() {
        return thirdList;
    }

    public void setThirdList(ArrayList<String> thirdList) {
        this.thirdList = thirdList;
    }

    public String getFirstOrder() {
        return firstOrder;
    }

    public void setFirstOrder(String firstOrder) {
        this.firstOrder = firstOrder;
    }

    public String getSecondOrder() {
        return secondOrder;
    }

    public void setSecondOrder(String secondOrder) {
        this.secondOrder = secondOrder;
    }

    public String getThirdOrder() {
        return thirdOrder;
    }

    public void setThirdOrder(String thirdOrder) {
        this.thirdOrder = thirdOrder;
    }
}
