package com.epam.training;

public class Main {
    private static final String HASH_TO_CRACK = "4fd0101ea3d0f5abbe296ef97f47afec";

    public static void main(String[] args) {
        HashCracker hashCracker = new HashCracker();
        System.out.println(hashCracker.crackPassword(HASH_TO_CRACK, 7));
    }


}

