package com.languageapp.entities;

public class Flashcard extends Question<String> {
    public Flashcard(long id, String text, String correctAnswer) {
        super(id, text, correctAnswer);
    }

    public Flashcard(String text, String correctAnswer) {
        super(text, correctAnswer);
    }

    @Override
    public String toString() {
        return "Flashcard: " + super.getText() + " - " + super.getCorrectAnswer();
    }
}
