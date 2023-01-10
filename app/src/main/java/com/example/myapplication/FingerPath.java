package com.example.myapplication;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public boolean emboss;
    public boolean blur;
    public boolean figure;
    public int strokeWidth;
    public Path path;

    public FingerPath(int color,boolean figure, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}