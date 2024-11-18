package com.e104.realtime.application;

public enum Talker {
    AI(false), CHILD(true);

    private final boolean flag;

    Talker(boolean flag) {
        this.flag = flag;
    }

    public boolean getValue() {
        return flag;
    }
}
