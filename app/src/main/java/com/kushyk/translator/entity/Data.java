package com.kushyk.translator.entity;

import java.util.List;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class Data {
    private List<Detect> detections;

    public List<Detect> getDetections() {
        return detections;
    }

    public void setDetections(List<Detect> detections) {
        this.detections = detections;
    }
}
