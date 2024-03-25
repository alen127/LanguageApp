package com.languageapp.entities;

public class DictionaryEntry extends Question<String> {
    public DictionaryEntry(String text, String correctAnswer) {
        super(text, correctAnswer);
    }

    public DictionaryEntry(long id, String text, String correctAnswer) {
        super(id, text, correctAnswer);
    }

    @Override
    public String toString() {
        return "Dictionary Entry: " + super.getText() + " - " + super.getCorrectAnswer();
    }
}
