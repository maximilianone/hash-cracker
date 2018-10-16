package com.epam.training;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashCrackerTest {
    private static final String HASH_TO_CRACK = "098f6bcd4621d373cade4e832627b4f6";
    private static final String CRACKED_HASH = "test";
    private static final int MAX_WORD_LENGTH = 7;
    private static final int INVALID_WORD_LENGTH = 3;

    private HashCracker hashCracker = new HashCracker();

    @Test
    void shouldReturnCrackedWord() {
        String result = hashCracker.crackPassword(HASH_TO_CRACK, MAX_WORD_LENGTH);

        assertEquals(CRACKED_HASH, result);
    }

    @Test
    void shouldReturnNullWhenGreaterThanMaxLength() {
        String result = hashCracker.crackPassword(HASH_TO_CRACK, INVALID_WORD_LENGTH);

        assertNull(result);
    }
}