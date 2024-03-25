package com.languageapp.entities;

import java.util.List;

public class Quiz<T extends Question<?>> extends TriviaGame<T> {
    private int score;

    public Quiz(List<T> questions) {
        super(questions);
    }

    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public float getScorePercent() {
        return ((float) this.score / getQuestions().size()) * 100;
    }
}
