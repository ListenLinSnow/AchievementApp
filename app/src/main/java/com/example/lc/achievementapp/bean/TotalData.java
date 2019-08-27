package com.example.lc.achievementapp.bean;

public class TotalData {

    private int type;
    private String typeContent;
    private int sum;

    public TotalData(int type, String typeContent, int sum) {
        this.type = type;
        this.typeContent = typeContent;
        this.sum = sum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(String typeContent) {
        this.typeContent = typeContent;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
