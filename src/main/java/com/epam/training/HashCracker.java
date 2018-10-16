package com.epam.training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HashCracker {
    private static final int ALPHABET_LENGTH = 26;
    private static final HashCalculator HASH_CALCULATOR = new HashCalculator();
    private static final char ALPHABET_FIRST_CHAR = 'a';
    private static final int MIN_WORD_LENGTH_FOR_POOL_THREAD_CREATION = 5;
    private static final int THREAD_COUNT = 10;

    private ExecutorService threadPool;

    public String crackPassword(String hashToCrack, int maxWordLength) {
        Optional<String> result = Optional.empty();
        for (int i = 1; i <= maxWordLength && !result.isPresent(); i++) {
            if (i == MIN_WORD_LENGTH_FOR_POOL_THREAD_CREATION) {
                threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
            }

            char[] chars = new char[i];
            Arrays.fill(chars, ALPHABET_FIRST_CHAR);

            if (i >= MIN_WORD_LENGTH_FOR_POOL_THREAD_CREATION) {
                result = executeCrackInThreads(chars, hashToCrack, threadPool);
            } else {
                result = bruteForceCrack(chars, chars.length - 1, hashToCrack);
            }
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
        return result.orElse(null);
    }

    private Optional<String> bruteForceCrack(char[] chars, int position, String hashToCrack) {
        Optional<String> result = Optional.empty();
        if (position != 0) {
            for (int i = 0; i < ALPHABET_LENGTH && !result.isPresent(); i++) {
                result = bruteForceCrack(chars, position - 1, hashToCrack);
                if (!result.isPresent()) {
                    chars[position] += 1;
                    for (int j = position - 1; j >= 0; j--) {
                        chars[j] = ALPHABET_FIRST_CHAR;
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        } else {
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (HASH_CALCULATOR.hash(String.valueOf(chars)).equals(hashToCrack)) {
                    result = Optional.of(String.valueOf(chars));
                }
                chars[0] += 1;
            }
        }
        return result;
    }

    @SuppressWarnings(value = "unchecked")
    private Optional<String> executeCrackInThreads(char[] chars, String hashToCrack, ExecutorService executor) {
        List<Callable<Optional<String>>> tasks = new ArrayList<>();
        Optional<String> result = Optional.empty();
        for (int j = 0; j < ALPHABET_LENGTH; j++) {
            chars[chars.length - 1] = (char) (ALPHABET_FIRST_CHAR + j);
            char[] array = Arrays.copyOf(chars, chars.length);
            tasks.add(() -> bruteForceCrack(array, array.length - 2, hashToCrack));
        }

        try {
            List<Future<Optional<String>>> futures = executor.invokeAll(tasks);
            for (Future future : futures) {
                result = (Optional<String>) future.get();
                if (result.isPresent()) {
                    executor.shutdownNow();
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error during execution");
        }
        return result;
    }

}
