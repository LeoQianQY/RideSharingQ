package com.example.ridesharingq.objects;

public class DistanceJson {
    private String status;
    private String info;
    private String infocode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public Distance[] getResults() {
        return results;
    }

    public void setResults(Distance[] results) {
        this.results = results;
    }

    private Distance[] results;
}
