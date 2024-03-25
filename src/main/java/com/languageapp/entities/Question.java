package com.languageapp.entities;

public abstract class Question<T> extends Entity {
    private String text;
    private T correctAnswer;

    public Question(String text, T correctAnswer) {
        this.text = text;
        this.correctAnswer = correctAnswer;
    }

    public Question(long id, String text) {
        super(id);
        this.text = text;
    }

    public Question(long id, String text, T correctAnswer) {
        super(id);
        this.text = text;
        this.correctAnswer = correctAnswer;
    }

    public Question(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", correctAnswer=" + correctAnswer +
                '}';
    }

    public T getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(T correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
