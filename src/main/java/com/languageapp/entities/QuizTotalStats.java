package com.languageapp.entities;

import java.io.Serializable;

public class QuizTotalStats implements Serializable {
    private int attempts;
    private float totalScorePercent;

    public QuizTotalStats(int attempts, float totalScorePercent) {
        this.attempts = attempts;
        this.totalScorePercent = totalScorePercent;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public float getTotalScorePercent() {
        return totalScorePercent;
    }

    public void setTotalScorePercent(float totalScorePercent) {
        this.totalScorePercent = totalScorePercent;
    }

    public void init() {
        if (this.attempts < 0 && this.totalScorePercent < 0) {
            this.attempts = 0;
            this.totalScorePercent = 0;
        }
    }

    public void addToTotalScorePercent(float percent) {
        totalScorePercent += percent;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public float getAverageScorePercent() {
        return this.totalScorePercent / this.attempts;
    }

    @Override
    public String toString() {
        return "[" + attempts + ", " + totalScorePercent + "]";
    }
}
