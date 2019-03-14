package shu.eyespy;

class AccomplishmentsOutbox {
    int mScore = 0;
    int mGamesPlayed = 0;

    boolean isEmpty() {
        return mScore == 0 && mGamesPlayed == 0;
    }
}