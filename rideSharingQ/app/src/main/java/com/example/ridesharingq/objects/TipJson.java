package com.example.ridesharingq.objects;

public class TipJson {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private Tip[] tips;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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

    public Tip[] getTips() {
        return tips;
    }

    public void setTips(Tip[] tips) {
        this.tips = tips;
    }

}
