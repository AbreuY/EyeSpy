package shu.eyespy;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item implements Parcelable {


    private int id;
    private String name;
    private ItemDifficulty difficulty;
    private ArrayList<String> synonyms;

    public Item (Parcel in) {
        readFromParcel(in);
    }

    public Item(int id) {
        this.id = id;
    }

    public Item(int id, String name, ItemDifficulty difficulty) {
        this.synonyms = new ArrayList<>();

        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
    }

    public Item(String name, ItemDifficulty difficulty, ArrayList synonyms) {
        this.name = name;
        this.difficulty = difficulty;
        this.synonyms = synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ItemDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(difficulty.ordinal());
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        difficulty = ItemDifficulty.values()[in.readInt()];
    }

    public enum ItemDifficulty {
        EASY,
        MEDIUM,
        HARD
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", difficulty=" + difficulty +
                ", synonyms=" + synonyms +
                '}';
    }
}
