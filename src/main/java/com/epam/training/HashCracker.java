package com.epam.training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HashCracker {
    private static final int ALPHABET_LENGTH = 26;
    private static final HashCalculator HASH_CALCULATOR = new HashCalculator();
    private static final char ALPHABET_FIRST_CHAR = 'a';
    private static final int MIN_WORD_LENGTH_POOL_THREAD_CREATION = 5;

    private ExecutorService threadPool;

    public String crackPassword(String hashToCrack, int maxWordLength) {
        for (int i = 1; i <= maxWordLength; i++) {
            System.out.println(i);
            if (i == MIN_WORD_LENGTH_POOL_THREAD_CREATION) {
                threadPool = Executors.newFixedThreadPool(ALPHABET_LENGTH);
            }
            try {
                char[] chars = new char[i];
                Arrays.fill(chars, ALPHABET_FIRST_CHAR);
                return bruteForceCrack(chars, chars.length - 1, hashToCrack);
            } catch (RuntimeException ignore) {
            }
        }
        throw new RuntimeException("Cannot be cracked");
    }

    private String bruteForceCrack(char[] chars, int position, String hashToCrack) {
        if (position == chars.length - 1 && chars.length >= MIN_WORD_LENGTH_POOL_THREAD_CREATION) {
            List<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                chars[chars.length - 1] += 1;
                tasks.add(() -> bruteForceCrack(chars, position - 1, hashToCrack));
            }
            try {
                List<Future<String>> futures = threadPool.invokeAll(tasks);
                String result;
                for (Future future : futures) {
                    if (future.isDone()) {
                        result = future.get().toString();
                        threadPool.shutdown();
                        return result;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(123);
            }

        } else if (position != 0) {
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                try {
                    return bruteForceCrack(chars, position - 1, hashToCrack);
                } catch (RuntimeException e) {
                    chars[position] += 1;
                    for (int j = position - 1; j >= 0; j--) {
                        chars[j] = ALPHABET_FIRST_CHAR;
                    }
                }
            }
        } else {
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (HASH_CALCULATOR.hash(String.valueOf(chars)).equals(hashToCrack)) {
                    return String.valueOf(chars);
                }
                chars[0] += 1;
            }
        }
        throw new RuntimeException();
    }

}
