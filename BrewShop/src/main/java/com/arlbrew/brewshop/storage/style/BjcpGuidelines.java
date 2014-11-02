package com.arlbrew.brewshop.storage.style;

public class BjcpGuidelines {
    private String aroma;
    private String appearance;
    private String flavor;
    private String mouthfeel;
    private String overallImpression;
    private String comments;
    private String ingredients;
    private VitalStatistics vitalStatistics;

    public BjcpGuidelines() {
        aroma = "";
        appearance = "";
        flavor = "";
        mouthfeel = "";
        overallImpression = "";
        comments = "";
        ingredients = "";
    }

    public String getAroma() {
        return aroma;
    }

    public String getAppearance() {
        return appearance;
    }

    public String getFlavor() {
        return flavor;
    }

    public String getMouthfeel() {
        return mouthfeel;
    }

    public String getOverallImpression() {
        return overallImpression;
    }

    public String getComments() {
        return comments;
    }

    public String getIngredients() {
        return ingredients;
    }

    public VitalStatistics getVitalStatistics() {
        return vitalStatistics;
    }
}
