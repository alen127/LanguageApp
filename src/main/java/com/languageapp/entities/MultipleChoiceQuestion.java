package com.languageapp.entities;

import java.util.List;

public final class MultipleChoiceQuestion extends Question<String> implements Answerable<String> {
    private List<String> possibleAnswers;
    private Integer correctAnswerPosition;

    public MultipleChoiceQuestion(long id, String text, List<String> possibleAnswers, Integer correctAnswerPosition) {
        super(id, text);
        this.possibleAnswers = possibleAnswers;
        this.correctAnswerPosition = correctAnswerPosition;
        this.setCorrectAnswer(possibleAnswers.get(correctAnswerPosition - 1));
    }


    public MultipleChoiceQuestion(String text, List<String> possibleAnswers, Integer correctAnswerPosition) {
        super(text);
        this.possibleAnswers = possibleAnswers;
        this.correctAnswerPosition = correctAnswerPosition;
        this.setCorrectAnswer(possibleAnswers.get(correctAnswerPosition - 1));
    }

    @Override
    public String toString() {
        return "Multiple Choice Question: " + super.getText() + " - " + super.getCorrectAnswer()
                + ", " + possibleAnswers + ", " + correctAnswerPosition;
    }

    public Integer getCorrectAnswerPosition() {
        return correctAnswerPosition;
    }

    public void setCorrectAnswerPosition(Integer correctAnswerPosition) {
        this.correctAnswerPosition = correctAnswerPosition;
    }

    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    @Override
    public boolean isCorrectAnswer(String givenAnswer) {
        return getCorrectAnswer().equals(givenAnswer);
    }
}
