package com.example.incredibly.smarttodo.view.model.core;

public abstract class Figure {
    public static final int NO_VALUE = -1;

    protected int pointsCount = NO_VALUE;

    protected float[][] controlPoints = null;

    protected Figure(float[][] controlPoints) {
        this.controlPoints = controlPoints;
        this.pointsCount = (controlPoints.length + 2) / 3;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public float[][] getControlPoints() {
        return controlPoints;
    }
}
