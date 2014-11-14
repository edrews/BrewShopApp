package com.brew.brewshop.storage.recipes;

public enum HopUsage {
    FIRST_WORT("First Wort"),
    BOIL("Boil"),
    WHIRLPOOL("Whirlpool"),
    DRY_HOP("Dry Hop");

    private String mText;

    HopUsage(String value) {
        mText = value;
    }

    public String getText() {
        return mText;
    }

    public static HopUsage fromString(String text) {
        if (text != null) {
            for (HopUsage b : HopUsage.values()) {
                if (text.equalsIgnoreCase(b.getText())) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}