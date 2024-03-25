package com.languageapp.entities;

import java.util.ArrayList;
import java.util.List;

public class FlashcardGame extends TriviaGame<Flashcard> {
    private List<Flashcard> virtualFlashcards;
    private int virtualFlashcardIndex;

    public FlashcardGame(List<Flashcard> questions) {
        super(questions);
        this.virtualFlashcards = generateVirtualFlashcards();
        this.virtualFlashcardIndex = 0;
    }

    public int getCurrentQuestionIndex() {
        return virtualFlashcardIndex;
    }

    public int getNumberOfVirtualFlashcards() {
        return virtualFlashcards.size();
    }

    public boolean nextFlashcardExists() {
        return virtualFlashcardIndex < virtualFlashcards.size() - 1;
    }

    public boolean previousFlashcardExists() {
        return virtualFlashcardIndex != 0;
    }

    public void decrementCurrentQuestionIndex() {
        if (virtualFlashcardIndex != 0)
            virtualFlashcardIndex--;
    }

    public void incrementCurrentQuestionIndex() {
        if (virtualFlashcardIndex != virtualFlashcards.size() - 1)
            virtualFlashcardIndex++;
    }

    public Flashcard getCurrentQuestion() {
        return virtualFlashcards.get(virtualFlashcardIndex);
    }

    public List<Flashcard> getVirtualFlashcards() {
        return virtualFlashcards;
    }

    private List<Flashcard> generateVirtualFlashcards() {
        List<Flashcard> flashcards = new ArrayList<>(getQuestions());
        for (var flashcard : getQuestions()) {
            flashcards.add(new Flashcard(flashcard.getCorrectAnswer(), flashcard.getText()));
        }
        return flashcards;
    }
}
