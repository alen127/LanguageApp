package com.languageapp.entities;

public sealed interface Answerable<T> permits MultipleChoiceQuestion {

    boolean isCorrectAnswer(T givenAnswer);
}
