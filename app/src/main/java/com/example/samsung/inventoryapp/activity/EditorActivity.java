package com.example.samsung.inventoryapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.samsung.inventoryapp.R;
import com.example.samsung.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mNameSupplierEditText;

    /**
     * EditText field to enter the supplier's number
     */
    private EditText mNumberSupplierEditText;

    /**
     * EditText field to enter the supplier's email
     */
    private EditText mEmailSupplierEditText;

    /**
     * TextView field showing product's quantity
     */
    private TextView mQuantityProductTextView;

    /**
     * EditText field to enter the amount to modify the quantity by
     */
    private EditText mModifyQuantityByEditText;

    private Button mContactButton;

    // initial values
    // all strings should be initialized to empty strings instead of the default values (null)
    private int mInitialQuantity;
    private String mInitialProductName = "";
    private int mInitialProductWeight;
    private String mInitialSupplierName = "";
    private String mInitialSupplierNumber = "";
    private String mInitialSuppllierEmail = "";
    private int mInitialProductPrice;

    // new quantity value
    private int mNewQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_product_name);

        mPriceEditText = findViewById(R.id.edit_product_price);

        mWeightEditText = findViewById(R.id.edit_product_weight);

        mNameSupplierEditText = findViewById(R.id.edit_supplier_name);

        mNumberSupplierEditText = findViewById(R.id.edit_supplier_number);

        mEmailSupplierEditText = findViewById(R.id.edit_supplier_email);

        mQuantityProductTextView = findViewById(R.id.tv_quantity);

        mModifyQuantityByEditText = findViewById(R.id.edit_quantity);

        mContactButton = findViewById(R.id.btn_contact);

        if (mCurrentProductUri == null) {
            // if it is the Add a Product screen, then hide the Contact Button
            mContactButton.setVisibility(View.GONE);
        }

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(EditorActivity.this)
                        .title("ORDER MORE")
                        .items(R.array.order_more)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0) {
                                    // call
                                    if (mNumberSupplierEditText.getText().toString().length() > 0) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + mNumberSupplierEditText.getText().toString()));
                                        startActivity(callIntent);
                                    }
                                } else if (which == 1) {
                                    // email
                                    if (mEmailSupplierEditText.getText().toString().length() > 0) {
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                "mailto",mEmailSupplierEditText.getText().toString(), null));
                                        startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                                    }

                                }
                            }
                        })
                        .show();
            }
        });

        mModifyQuantityByEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // modify by value cannot be less than 0
                if (s.length() > 0 && Integer.valueOf(s.toString()) < 1) {
                    mModifyQuantityByEditText.setText("1");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private boolean seeIfProductHasChanged() {
        // get the current values
        String productName = mNameEditText.getText().toString().trim();

        int productWeight = 0;
        if (mWeightEditText.getText().toString().trim().length() > 0) {
            productWeight = Integer.parseInt(mWeightEditText.getText().toString().trim());
        }

        int productPrice = 0;
        if (mPriceEditText.getText().toString().trim().length() > 0) {
            productPrice = Integer.parseInt(mPriceEditText.getText().toString().trim());
        }

        String supplierName = mNameSupplierEditText.getText().toString().trim();
        String supplierNumber = mNumberSupplierEditText.getText().toString().trim();
        String supplierEmail = mEmailSupplierEditText.getText().toString().trim();
        int productQuantity = Integer.parseInt(mQuantityProductTextView.getText().toString().trim());
        return (mInitialProductWeight != productWeight || !productName.equals(mInitialProductName) || mInitialProductPrice != productPrice || mInitialQuantity != productQuantity || !supplierName.equals(mInitialSupplierName) || !supplierEmail.equals(mInitialSuppllierEmail) || !supplierNumber.equals(mInitialSupplierNumber));
    }

    private boolean seeIfAnyFieldIsEmpty() {
        // get the current values
        String productName = mNameEditText.getText().toString().trim();
        String productWeight = mWeightEditText.getText().toString().trim();
        String productPrice = mPriceEditText.getText().toString().trim();
        String supplierName = mNameSupplierEditText.getText().toString().trim();
        String supplierNumber = mNumberSupplierEditText.getText().toString().trim();
        String supplierEmail = mEmailSupplierEditText.getText().toString().trim();
        String productQuantity = mQuantityProductTextView.getText().toString().trim();
        return (productName.length() == 0 || productWeight.length() == 0 || productPrice.length() == 0 || productQuantity.length() == 0 || supplierName.length() == 0 || supplierEmail.length() == 0 || supplierNumber.length() == 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!seeIfProductHasChanged()) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!seeIfProductHasChanged()) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        // get the current values
        String productName = mNameEditText.getText().toString().trim();
        String productWeight = mWeightEditText.getText().toString().trim();
        String productPrice = mPriceEditText.getText().toString().trim();
        String supplierName = mNameSupplierEditText.getText().toString().trim();
        String supplierNumber = mNumberSupplierEditText.getText().toString().trim();
        String supplierEmail = mEmailSupplierEditText.getText().toString().trim();
        String productQuantity = mQuantityProductTextView.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (seeIfAnyFieldIsEmpty()) {
            // if any of the fields is empty, display a Toast message stating that
            // this is not allowed
            Toast.makeText(this, "There cannot be empty fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT, Integer.parseInt(productWeight));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(productPrice));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(productQuantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER, supplierNumber);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);

        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE_PATH};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int weightColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_WEIGHT);
            int nameSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int numberSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER);
            int emailSupplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String nameSupplier = cursor.getString(nameSupplierColumnIndex);
            String numberSupplier = cursor.getString(numberSupplierColumnIndex);
            String emailSupplier = cursor.getString(emailSupplierColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mWeightEditText.setText(String.valueOf(weight));
            mNameSupplierEditText.setText(nameSupplier);
            mNumberSupplierEditText.setText(numberSupplier);
            mEmailSupplierEditText.setText(emailSupplier);
            mQuantityProductTextView.setText(String.valueOf(quantity));

            // get the initial values
            mInitialQuantity = Integer.parseInt(mQuantityProductTextView.getText().toString());
            mInitialProductName = mNameEditText.getText().toString();
            mInitialProductPrice = Integer.parseInt(mPriceEditText.getText().toString());
            mInitialProductWeight = Integer.parseInt(mWeightEditText.getText().toString());
            mInitialSupplierName = mNameSupplierEditText.getText().toString();
            mInitialSupplierNumber = mNumberSupplierEditText.getText().toString();
            mInitialSuppllierEmail = mEmailSupplierEditText.getText().toString();

            Log.i("EditorActi", "Initial quantity value: " + mInitialQuantity);

            // assign the initial value to the new value, which might be changed later
            mNewQuantity = mInitialQuantity;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mWeightEditText.setText("");
        mNameSupplierEditText.setText("");
        mNumberSupplierEditText.setText("");
        mEmailSupplierEditText.setText("");
        mQuantityProductTextView.setText("");
    }

    // modify quantity by increasing the amount of EditText value
    public void increaseQuantity(View view) {
        // first get the EditText value
        String editTextValue = mModifyQuantityByEditText.getText().toString();
        if (editTextValue.length() > 0) {
            int quantity = Integer.valueOf(editTextValue);

            // add the value
            mNewQuantity += quantity;
            mQuantityProductTextView.setText(String.valueOf(mNewQuantity));
        }

    }

    // modify quantity by decreasing the amount of EditText value
    public void decreaseQuantity(View view) {
        // first get the EditText value
        String editTextValue = mModifyQuantityByEditText.getText().toString();
        if (editTextValue.length() > 0) {
            int quantity = Integer.valueOf(editTextValue);

            if (quantity > mNewQuantity) {
                // display a Toast message saying that it is not allowed to decrease the quantity
                // by a value bigger than the current value
                Toast.makeText(EditorActivity.this, "the value you want to decrease the quantity by is bigger than the current quantity value.", Toast.LENGTH_SHORT).show();
            } else {
                // subtract the value
                mNewQuantity -= quantity;
                mQuantityProductTextView.setText(String.valueOf(mNewQuantity));
            }
        }

    }
}
