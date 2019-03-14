package shu.eyespy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class ItemAdapter {

    private static final String TAG = ItemAdapter.class.getSimpleName();

    private SQLiteDatabase mDatabase;
    private ItemDatabaseHelper itemDatabaseHelper;

    ItemAdapter(Context context) {
        this.itemDatabaseHelper = new ItemDatabaseHelper(context);

        createDatabase();
        open();
    }

    private void createDatabase() throws SQLException {
        try {
            itemDatabaseHelper.createDatabase();
        } catch (IOException exception) {
            Log.e(TAG, exception.toString() + ", unable to create database.");
            throw new Error("Unable to create database.");
        }
    }

    private void open() throws SQLException {
        itemDatabaseHelper.openDatabase();
        itemDatabaseHelper.close();
        mDatabase = itemDatabaseHelper.getReadableDatabase();
    }

    void close() {
        itemDatabaseHelper.close();
    }

    Item getRandomItem(Item.ItemDifficulty difficulty) throws Exception {
        final int index = getItemID(difficulty);
        final HashMap<String, String> names = getItemNames(index);
        final ArrayList<String> synonyms = getItemSynonyms(index);

        return new Item(index, names, synonyms, difficulty);
    }

    private int getItemID(Item.ItemDifficulty difficulty) throws Exception {
        @SuppressLint("DefaultLocale") final String itemIndexSQL = String.format("SELECT _id FROM ItemDifficulty WHERE difficulty = %d ORDER BY RANDOM() LIMIT 1", difficulty.ordinal());
        Cursor itemSelectCursor = mDatabase.rawQuery(itemIndexSQL, null);
        int itemIndex = 0;
        if (itemSelectCursor != null) {
            itemSelectCursor.moveToNext();
            itemIndex = itemSelectCursor.getInt(0);
            itemSelectCursor.close();

            if (itemIndex == -1) {
                throw new Exception("Item index could not be selected.");
            }
            return itemIndex;
        } else {
            throw new SQLException("Did not find an item.");
        }
    }

    private HashMap<String, String> getItemNames(int index) {
        @SuppressLint("DefaultLocale") final String itemNameSQL = String.format("SELECT isoCode, name FROM Item WHERE itemId = %d", index);
        Cursor itemSelectCursor = mDatabase.rawQuery(itemNameSQL, null);
        if (itemSelectCursor != null) {
            HashMap<String, String> names = new HashMap<>();
            while (itemSelectCursor.moveToNext()) {
                names.put(itemSelectCursor.getString(0), itemSelectCursor.getString(1));
            }
            itemSelectCursor.close();
            Log.d(TAG, names.toString());
            return names;
        } else {
            throw new SQLException("Did not find an item.");
        }
    }

    private ArrayList<String> getItemSynonyms(int id) {
        @SuppressLint("DefaultLocale") final String itemSelectSQL = String.format(
                "SELECT name FROM Synonyms WHERE itemId = %d",
                id
        );
        Cursor itemInformationCursor = mDatabase.rawQuery(itemSelectSQL, null);
        if (itemInformationCursor != null) {
            ArrayList<String> synonyms = new ArrayList<>();
            while (itemInformationCursor.moveToNext()) {
                synonyms.add(itemInformationCursor.getString(0));
            }
            itemInformationCursor.close();
            return synonyms;
        } else {
            throw new SQLException("Did not find an synonyms.");
        }
    }
}
