/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.samsung.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the  app.
 */
public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single product.
     */
    public static final class InventoryEntry implements BaseColumns {

        /** Name of database table for products */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * path of the product image.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_IMAGE_PATH ="image";

        /**
         * name of the supplier.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME ="name_supplier";

        /**
         * supplier's email address.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL ="email_supplier";

        /**
         * supplier's contact information.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NUMBER ="phone_supplier";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Gender of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Weight of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_WEIGHT = "weight";

    }

}

