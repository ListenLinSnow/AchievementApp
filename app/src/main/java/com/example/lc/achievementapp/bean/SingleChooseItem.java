package com.example.lc.achievementapp.bean;

public class SingleChooseItem<T> {

    boolean isSelected;
    T t;

    public SingleChooseItem(boolean isSelected, T t) {
        this.isSelected = isSelected;
        this.t = t;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
