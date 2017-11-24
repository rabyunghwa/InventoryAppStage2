package com.example.samsung.inventoryapp.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.inventoryapp.R;
import com.example.samsung.inventoryapp.data.InventoryContract;
import com.example.samsung.inventoryapp.data.InventoryDbHelper;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the inventory database.
     */
    private void displayDatabaseInfo() {
        Cursor cursor = readProductData();

        TextView displayView = findViewById(R.id.text_view_inventory);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The inventory table contains <number of rows in Cursor> products.
            // _id - name - price - quantity - weight - image path - supplier name - supplier email
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The inventory table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(InventoryContract.InventoryEntry._ID + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE_PATH + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER + " - " +
                    InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID );
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int weightColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT);
            int imagePathColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE_PATH);
            int nameSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int numberSupplierNumberIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER);
            int emailSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);
                String currentImagePath = cursor.getString(imagePathColumnIndex);
                String currentNameSupplier = cursor.getString(nameSupplierColumnIndex);
                String currentNumberSupplier = cursor.getString(numberSupplierNumberIndex);
                String currentEmailSupplier = cursor.getString(emailSupplierColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentWeight + " - " +
                        currentImagePath + " - " +
                        currentNameSupplier + " - " +
                        currentNumberSupplier + " - " +
                        currentEmailSupplier));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to read product data from the database and return a Cursor object.
     *
     * For debugging purposes only.
     */
    private Cursor readProductData() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE_PATH,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL};

        // Perform a query on the inventory table
        return db.query(
                InventoryContract.InventoryEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Lego Creative Toolbox's product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, "Lego Boost Creative Toolbox");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE_PATH, "blahblahblah");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, "lego@gmail.com");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER, "15348973456");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Lego");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, 159);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT, 5);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, 1000);

        // Insert a new row for Lego Boost Creative Toolbox into the database, returning the ID of that new row.
        // The first argument for db.insert() is the inventory table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the Lego toy.
        long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Data successfully inserted.", Toast.LENGTH_SHORT).show();

            // refresh the UI
            displayDatabaseInfo();
        }
    }

    // corresponding onClick handler for the onClick method definition in XML
    public void addProduct(View view) {
        insertProduct();
    }
}
