package com.arlbrew.brewshop.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.arlbrew.brewshop.R;
import com.arlbrew.brewshop.storage.ProductListAdapter;
import com.arlbrew.brewshop.storage.ProductStorage;
import com.arlbrew.brewshop.storage.ProductType;
import com.arlbrew.brewshop.storage.models.IProductRetrievedHandler;
import com.arlbrew.brewshop.storage.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment implements IProductRetrievedHandler {
    @SuppressWarnings("unused")
    private static final String TAG = ProductListFragment.class.getName();
    public static String PRODUCT_TYPE_KEY = "ProductType";
    public static String PRODUCTS_KEY = "Products";

    private ListView mProductList;
    private View mProgressView;
    private View mErrorView;
    private TextView mErrorMessage;
    private List<Product> mProducts;
    private ProductType mCurrentType = ProductType.NONE;
    private String mRetrieveError, mNoProducts;
    private Menu mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        super.onCreate(inState);
        setHasOptionsMenu(true);
        if (inState == null) {
            Bundle args = getArguments();
            if (args != null) {
                String typeStr = getArguments().getString(PRODUCT_TYPE_KEY);
                mCurrentType = ProductType.valueOf(typeStr);
            }
        } else {
            mProducts = inState.getParcelableArrayList(PRODUCTS_KEY);
            mCurrentType = ProductType.valueOf(inState.getString(PRODUCT_TYPE_KEY));
        }

        setTitle(mCurrentType);

        View root = inflater.inflate(R.layout.fragment_products, container, false);
        mProductList = (ListView) root.findViewById(R.id.product_list);
        mProgressView = root.findViewById(R.id.progress_layout);
        mErrorView = root.findViewById(R.id.error_layout);
        mErrorMessage = (TextView) root.findViewById(R.id.error_message);

        mRetrieveError = getResources().getString(R.string.retrieve_error);
        mNoProducts = getResources().getString(R.string.no_products);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProducts != null) {
            mErrorView.setVisibility(View.GONE);
            productsRetrieved(mProducts);
        } else {
            loadProducts(mCurrentType);
        }
    }

    public void loadProducts(ProductType type) {
        mCurrentType = type;
        mErrorView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
        setRefreshEnabled(false);
        new ProductStorage().retrieveProducts(this, type);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.products_menu, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mProducts != null) {
            outState.putParcelableArrayList(PRODUCTS_KEY, (ArrayList<Product>) mProducts);
        }
        outState.putString(PRODUCT_TYPE_KEY, mCurrentType.toString());
    }

    @Override
    public void productsRetrieved(List<Product> products) {
        setRefreshEnabled(true);
        mProducts = products;
        mProgressView.setVisibility(View.GONE);
        if (products == null) {
            mErrorMessage.setText(mRetrieveError);
            mErrorView.setVisibility(View.VISIBLE);
        } else if (products.isEmpty()) {
            mErrorMessage.setText(mNoProducts);
            mErrorView.setVisibility(View.VISIBLE);
        } else {
            Context context = getActivity();
            if (context != null) {
                ProductListAdapter adapter = new ProductListAdapter(context, products);
                mProductList.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_refresh) {
            loadProducts(mCurrentType);
            return true;
        }
        return false;
    }

    private void setRefreshEnabled(boolean enabled) {
        if (mMenu != null) {
            MenuItem refresh = mMenu.findItem(R.id.action_refresh);
            if (refresh != null) {
                refresh.setEnabled(enabled);
            } else {
                Log.d(TAG, "Cannot access refresh menu item");
            }
        } else {
            Log.d(TAG, "Cannot access menu");
        }
    }

    private void setTitle(ProductType type) {
        int id;
        switch (type) {
            case BEER:
                id = R.string.beer;
                break;
            case WINE:
                id = R.string.wine;
                break;
            case COFFEE:
                id = R.string.coffee;
                break;
            case HOMEBREW:
                id = R.string.homebrew_supplies;
                break;
            case NONE:
            default:
                //TODO change default
                id = R.string.homebrew_supplies;
                break;
        }
        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getActivity().getResources().getString(id));
        }
    }
}
