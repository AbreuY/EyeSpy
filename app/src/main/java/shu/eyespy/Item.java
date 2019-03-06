package shu.eyespy;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    private String id;
    private String name;
    private ItemDifficulty difficulty;

    public Item (Parcel in) {
        readFromParcel(in);
    }

    public Item(String name, ItemDifficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
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

    public String getId() {
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(difficulty.ordinal());
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
        difficulty = ItemDifficulty.values()[in.readInt()];
    }

    public enum ItemDifficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
