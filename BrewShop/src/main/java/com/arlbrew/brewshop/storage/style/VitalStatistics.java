package com.arlbrew.brewshop.storage.style;

public class VitalStatistics {
    private String og;
    private String fg;
    private String abv;
    private String ibu;
    private String srm;

    public double getIbuMin() {
        return parse(ibu, 0);
    }

    public double getIbuMax() {
        return parse(ibu, 1);
    }

    public double getSrmMin() {
        return parse(srm, 0);
    }

    public double getSrmMax() {
        return parse(srm, 1);
    }

    public double getAbvMin() {
        return parse(abv, 0);
    }

    public double getAbvMax() {
        return parse(abv, 1);
    }

    public double getOgMin() {
        return parse(og, 0);
    }

    public double getOgMax() {
        return parse(og, 1);
    }

    public double getFgMin() {
        return parse(fg, 0);
    }

    public double getFgMax() {
        return parse(fg, 1);
    }

    private double parse(String value, int position) {
        if (value.startsWith("V")) { //Varies
            return -1;
        }
        value = value.replaceAll("[^0-9.\\s+]", "");
        String[] strings = value.split("\\s+");
        return Double.parseDouble(strings[position]);
    }
}
