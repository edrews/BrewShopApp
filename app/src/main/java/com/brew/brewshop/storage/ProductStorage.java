package com.brew.brewshop.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.brew.brewshop.R;
import com.brew.brewshop.storage.models.IProductRetrievedHandler;
import com.brew.brewshop.storage.models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ProductStorage extends FindCallback<ParseObject> implements ITimeoutHandler {
    private static final String TAG = ProductStorage.class.getName();
    private static final String PARSE_CLASS = "Products";

    private static final String TYPE_KEY = "type";
    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String MANUFACTURER_KEY = "manufacturer";
    private static final String PRICE_KEY = "price";
    private static final String UNIT_KEY = "unit";
    private static final String ICON_KEY = "icon";

    private static final int MAX_RESULTS = 100;
    private static final int TIMEOUT = 5000;

    private Context mContext;
    private ParseQuery<ParseObject> mQuery;
    private IProductRetrievedHandler mHandler;
    private TimeoutTask mTimeoutTask;

    public ProductStorage(Context context) {
        mContext = context;
    }

    public void retrieveProducts(IProductRetrievedHandler handler, ProductType productType) {
        mHandler = handler;
        String type = productType.toString().toLowerCase();
        mQuery = ParseQuery.getQuery(PARSE_CLASS)
                .whereEqualTo(TYPE_KEY, type)
                .orderByAscending(NAME_KEY)
                .setLimit(MAX_RESULTS);
        mQuery.findInBackground(this);
        mTimeoutTask = new TimeoutTask(this);
        mTimeoutTask.execute(TIMEOUT);
    }

    @Override
    public void done(List<ParseObject> objects, ParseException e) {

        List<Product> products = null;
        if (e == null) {
            products = parseObjects(objects);
        } else {
            Log.d(TAG, "Error retrieving product list");
        }
        mHandler.productsRetrieved(products);
    }

    private List<Product> parseObjects(List<ParseObject> objects) {
        mTimeoutTask.cancel(true);
        List<Product> productList = new ArrayList<Product>();
        for (ParseObject object : objects) {
            Product product = new Product();
            product.setName(object.getString(NAME_KEY));
            product.setDescription(object.getString(DESCRIPTION_KEY));
            product.setManufacturer(object.getString(MANUFACTURER_KEY));
            product.setPrice(object.getNumber(PRICE_KEY).doubleValue());
            product.setPriceUnit(object.getString(UNIT_KEY));
            productList.add(product);

            try {
                byte[] iconData = object.getParseFile(ICON_KEY).getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                product.setIcon(bitmap);
            } catch (ParseException e) {
                Log.d(TAG, "Error retrieving product icon");
            }

        }
        return productList;
    }

    @Override
    public void onTimeout() {
        mQuery.cancel();
        mQuery = null;
    }

    @Override
    public void onTimeoutMainThread() {
        mHandler.productsRetrieved(null);
    }
}
