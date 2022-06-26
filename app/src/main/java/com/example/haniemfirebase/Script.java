package com.example.haniemfirebase;

// firebase에서 가져온 문장 객체

public class Script {
    private String english;
    private String korean;

    public Script() {
    }

    public Script(String english) {
        this.english = english;
    }

    public String getEnglish() {
        return english;
    }

    public String getKorean() {
        return korean;
    }
}
