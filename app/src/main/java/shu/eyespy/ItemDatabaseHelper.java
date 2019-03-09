package shu.eyespy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ItemDatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = ItemDatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "Items.db";

    private SQLiteDatabase mDatabase;
    private final String mDatabasePath;
    private final Context mContext;

    ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.mDatabasePath = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void createDatabase() throws IOException
    {
        if(!checkDatabase())
        {
            this.getReadableDatabase();
            this.close();
            //Copy the database from assests

            copyDatabase();
            Log.d(TAG, "createDatabase database created");
        }
    }



    private boolean checkDatabase() {
        Log.d(TAG, String.format("checkDatbase: Checking whether %s exists", DATABASE_NAME));
        return (new File(mDatabasePath.concat(DATABASE_NAME))).exists();
    }

    private void copyDatabase() throws IOException
    {
        Log.d(TAG, String.format("copyDatabase: Attempting to copy %s.", DATABASE_NAME));

        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        OutputStream mOutput = new FileOutputStream(mDatabasePath.concat(DATABASE_NAME));

        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    void openDatabase() {
        mDatabase = SQLiteDatabase
                .openDatabase(mDatabasePath.concat(DATABASE_NAME),
                        null,
                        SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (Objects.nonNull(mDatabase)) {
            mDatabase.close();
        }

        super.close();
    }
}
