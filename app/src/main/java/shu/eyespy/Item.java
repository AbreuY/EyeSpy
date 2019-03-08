package shu.eyespy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item {

    private String id;
    private String name;
    private ItemDifficulty difficulty;
    private List<String> synonyms;

    public Item(String name, ItemDifficulty difficulty) {
        this.synonyms = new ArrayList<>();

        this.name = name;
        this.difficulty = difficulty;
    }

    public Item(String name, ItemDifficulty difficulty, String[] synonyms) {
        this.name = name;
        this.difficulty = difficulty;
        this.synonyms = Arrays.asList(synonyms);
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
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
