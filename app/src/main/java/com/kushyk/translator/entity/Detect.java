package com.kushyk.translator.entity;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class Detect {
    private String language;
    private boolean isReliable;
    private int confidence;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isReliable() {
        return isReliable;
    }

    public void setReliable(boolean reliable) {
        isReliable = reliable;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
