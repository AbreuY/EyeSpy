package shu.eyespy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

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

    Item getRandomItem(Item.ItemDifficulty difficulty) {
        @SuppressLint("DefaultLocale") final String itemIndexSQL = String.format("SELECT _id, name FROM Item WHERE difficulty = %d ORDER BY RANDOM() LIMIT 1", difficulty.ordinal());
        Item selectedItem;

        Cursor itemSelectCursor = mDatabase.rawQuery(itemIndexSQL, null);
        if (itemSelectCursor != null) {
            itemSelectCursor.moveToNext();
            selectedItem = new Item(itemSelectCursor.getInt(0), itemSelectCursor.getString(1), difficulty);
            itemSelectCursor.close();
        } else {
            throw new SQLException("Did not find an item.");
        }

        @SuppressLint("DefaultLocale") final String itemSelectSQL = String.format(
                "SELECT name FROM Synonyms WHERE item_id = %d",
                selectedItem.getId()
        );
        Cursor itemInformationCursor = mDatabase.rawQuery(itemSelectSQL, null);
        if (itemInformationCursor != null) {
            ArrayList<String> synonyms = new ArrayList<>();
            while (itemSelectCursor.moveToNext()) {
                synonyms.add(itemSelectCursor.getString(0));
            }
            itemInformationCursor.close();
            selectedItem.setSynonyms(synonyms);
        } else {
            throw new SQLException("Did not find an synonyms.");
        }

        return selectedItem;
    }
}
