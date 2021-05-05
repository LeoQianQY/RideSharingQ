package com.example.ridesharingq.objects;

import java.util.ArrayList;

public class OngoingAccount {
    public String did;

    public String pid;

    public int state;

    public ArrayList<String> bindDriverLst;

    public String pickupName;

    public String dropoffName;

    public int result;

    public String timeExpected;

    public String getTimeExpected() {
        return timeExpected;
    }

    public void setTimeExpected(String timeExpected) {
        this.timeExpected = timeExpected;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<String> getBindDriverLst() {
        return bindDriverLst;
    }

    public void setBindDriverLst(ArrayList<String> bindDriverLst) {
        this.bindDriverLst = bindDriverLst;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public String getDropoffName() {
        return dropoffName;
    }

    public void setDropoffName(String dropoffName) {
        this.dropoffName = dropoffName;
    }

}
