package com.tw0far.potiongames.models;

public class Kit {
    /** Kit that starts with coins and doubles the coins of a kill. */
    public static final String RICH_KID = "Rich Kid";

    private final String name;

    public Kit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
