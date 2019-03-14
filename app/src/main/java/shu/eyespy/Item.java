package shu.eyespy;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Item implements Parcelable {

    private int id;
    private HashMap<String, String> name;
    private ItemDifficulty difficulty;
    private ArrayList<String> synonyms;

    private Item(Parcel in) {
        readFromParcel(in);
    }

    Item(int id, HashMap<String, String> name, ArrayList<String> synonyms, ItemDifficulty difficulty) {
        this.id = id;
        this.name = name;
        this.synonyms = synonyms;
        this.difficulty = difficulty;
    }

    ArrayList<String> getSynonyms() {
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

    public String getName(String iso2Code) {
        return Optional.ofNullable(name.get(iso2Code)).orElse(getNameEn());
    }

    public String getNameEn() {
        return name.get("EN");
    }

    public int getId() {
        return id;
    }

    ItemDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(name.size());
        for(Map.Entry<String,String> e : name.entrySet()){
            dest.writeString(e.getKey());
            dest.writeString(e.getValue());
        }
        dest.writeInt(difficulty.ordinal());
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();

        int size = in.readInt();
        name = new HashMap<>(size);
        for(int i = 0; i < size; i++){
            name.put(in.readString(), in.readString());
        }

        difficulty = ItemDifficulty.values()[in.readInt()];
    }

    public enum ItemDifficulty {
        EASY,
        MEDIUM,
        HARD
    }

    @NonNull
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
