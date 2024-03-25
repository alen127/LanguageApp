package com.languageapp.entities;

import java.io.Serializable;

public record User(Long id, String username, String password, UserType type,
                   QuizTotalStats multipleChoiceQuizStats) implements Serializable {
    @Override
    public String toString() {
        return "User: " + id + ", " + username + ", " + password + ", " + type + ", " + multipleChoiceQuizStats;
    }

    public static final class Builder {
        Long id;
        String username;
        String password;
        UserType type;


        QuizTotalStats multipleChoiceQuizStats;

        public Builder(Long id, String username, String password, UserType type) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.type = type;
        }

        public Builder multipleChoiceQuizStats(QuizTotalStats stats) {
            this.multipleChoiceQuizStats = stats;
            return this;
        }


        public User build() {
            return new User(id, username, password, type, multipleChoiceQuizStats);
        }
    }
}