package com.epam.training;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final String HASH_TO_CRACK = "4fd0101ea3d0f5abbe296ef97f47afec";
    private static final int MAX_WORD_LENGTH = 7;

    public static void main(String[] args) {
        HashCracker hashCracker = new HashCracker();
        LocalDateTime start = LocalDateTime.now();
        System.out.println(hashCracker.crackPassword(HASH_TO_CRACK, MAX_WORD_LENGTH));
        System.out.println("Duration: " + Duration.between(start, LocalDateTime.now()).toMillis());
    }


}

