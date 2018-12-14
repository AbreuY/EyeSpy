package shu.apps.eyespy;

public class Item {
    private String name;
    private ItemDifficulty difficulty;

    public Item(String name, ItemDifficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }

    public String getName() {

        return name;
    }

    public ItemDifficulty getDifficulty() {
        return difficulty;
    }

    public enum ItemDifficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
