package com.languageapp.entities;

import java.util.List;

public abstract class TriviaGame<T extends Question<?>> {
    private List<T> questions;
    private int currentQuestionIndex;

    public TriviaGame(List<T> questions) {
        this.questions = questions;
        this.currentQuestionIndex = 0;
    }

    public int getNumberOfQuestions() {
        return questions.size();
    }

    public List<T> getQuestions() {
        return questions;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public T getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    public void incrementCurrentQuestionIndex() {
        currentQuestionIndex++;
    }

    public void decrementCurrentQuestionIndex() {
        currentQuestionIndex--;
    }
}
