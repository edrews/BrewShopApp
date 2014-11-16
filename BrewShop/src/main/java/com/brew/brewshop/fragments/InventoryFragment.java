package com.brew.brewshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.brew.brewshop.FragmentHandler;
import com.brew.brewshop.R;
import com.brew.brewshop.storage.BrewStorage;
import com.brew.brewshop.storage.inventory.InventoryAdapter;
import com.brew.brewshop.storage.inventory.InventoryItem;
import com.brew.brewshop.storage.inventory.InventoryList;
import com.brew.brewshop.storage.recipes.Hop;
import com.brew.brewshop.storage.recipes.Malt;
import com.brew.brewshop.storage.recipes.Yeast;

public class InventoryFragment extends Fragment implements DialogInterface.OnClickListener,
        TabHost.OnTabChangeListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        ActionMode.Callback {

    @SuppressWarnings("unused")
    private static final String TAG = InventoryFragment.class.getName();
    private static final String ACTION_MODE = "ActionMode";
    private static final String TAB_TAG = "TabTag";

    private static final String MALT_TAG = "Malt";
    private static final String HOPS_TAG = "Hops";
    private static final String YEAST_TAG = "Yeast";

    private BrewStorage mStorage;
    private FragmentHandler mFragmentHandler;
    private ActionMode mActionMode;
    private ListView mMaltList;
    private ListView mHopsList;
    private ListView mYeastList;
    private InventoryList mInventoryList;
    private TabHost mTabHost;
    private TextView mMessageView;

    public InventoryFragment() {
        setArguments(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);
        mMaltList = (ListView) root.findViewById(R.id.malt_list);
        mHopsList = (ListView) root.findViewById(R.id.hops_list);
        mYeastList = (ListView) root.findViewById(R.id.yeast_list);
        mMessageView = (TextView) root.findViewById(R.id.inventory_message_view);

        mStorage = new BrewStorage(getActivity());
        mInventoryList = mStorage.retrieveInventory();

        InventoryAdapter maltAdapter = new InventoryAdapter(getActivity(), Malt.class);
        mMaltList.setAdapter(maltAdapter);
        mMaltList.setOnItemClickListener(this);
        mMaltList.setOnItemLongClickListener(this);

        InventoryAdapter hopsAdapter = new InventoryAdapter(getActivity(), Hop.class);
        mHopsList.setAdapter(hopsAdapter);
        mHopsList.setOnItemClickListener(this);
        mHopsList.setOnItemLongClickListener(this);

        InventoryAdapter yeastAdapter = new InventoryAdapter(getActivity(), Yeast.class);
        mYeastList.setAdapter(yeastAdapter);
        mYeastList.setOnItemClickListener(this);
        mYeastList.setOnItemLongClickListener(this);

        mTabHost = (TabHost) root.findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(this);

        TabHost.TabSpec maltSpec = mTabHost.newTabSpec(MALT_TAG);
        maltSpec.setContent(R.id.malt_tab);
        maltSpec.setIndicator("MALT");
        mTabHost.addTab(maltSpec);

        TabHost.TabSpec hopsSpec = mTabHost.newTabSpec(HOPS_TAG);
        hopsSpec.setIndicator("HOPS");
        hopsSpec.setContent(R.id.hops_tab);
        mTabHost.addTab(hopsSpec);

        TabHost.TabSpec yeastSpec = mTabHost.newTabSpec(YEAST_TAG);
        yeastSpec.setIndicator("YEAST");
        yeastSpec.setContent(R.id.yeast_tab);
        mTabHost.addTab(yeastSpec);

        setHasOptionsMenu(true);
        mFragmentHandler.setTitle(findString(R.string.inventory));
        onRestoreInstanceState(state);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTabHost != null) {
            getArguments().putString(TAB_TAG, mTabHost.getCurrentTabTag());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStorage.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActionBar();
        checkShowMessage();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (state == null) {
            state = new Bundle();
        }
        state.putBoolean(ACTION_MODE, mActionMode != null);
        if (mTabHost != null) {
            state.putString(TAB_TAG, mTabHost.getCurrentTabTag());
        }
    }

    private void onRestoreInstanceState(Bundle bundle) {
        attemptRestoreTab(getArguments());
        if (bundle != null) {
            attemptRestoreTab(bundle);
            if (bundle.getBoolean(ACTION_MODE)) {
                startActionMode();
            }
        }
        checkShowMessage();
    }

    private void attemptRestoreTab(Bundle bundle) {
        String tag = bundle.getString(TAB_TAG);
        if (tag != null) {
            mTabHost.setCurrentTabByTag(tag);
        }
    }

    private void checkShowMessage() {
        if (getCurrentList().getAdapter().getCount() > 0) {
            mMessageView.setVisibility(View.GONE);
        } else {
            mMessageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inventory_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_new_item) {
            addNewItem();
            return true;
        }
        return false;
    }

    private void addNewItem() {
        if (!canAddItem()) {
            int maxInventory = getActivity().getResources().getInteger(R.integer.max_inventory);
            String message = String.format(findString(R.string.max_inventory_reached), maxInventory);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            showNewItem();
        }
    }

    public boolean canAddItem() {
        return mInventoryList.size() < getResources().getInteger(R.integer.max_inventory);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentHandler = (FragmentHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + FragmentHandler.class.getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView list = getCurrentList();
        if (adapterView.getId() == list.getId()) {
            onInventoryItemClicked(list, position);
        }
    }

    private void showNewItem() {
        String tag = mTabHost.getCurrentTabTag();
        if (tag.equals(MALT_TAG)) {
            InventoryItem item = new InventoryItem(new Malt());
            mStorage.createInventoryItem(item);
            mFragmentHandler.showMaltEditor(item);
        } else if (tag.equals(HOPS_TAG)) {
            InventoryItem item = new InventoryItem(new Hop());
            mStorage.createInventoryItem(item);
            mFragmentHandler.showHopsEditor(item);
        } else if (tag.equals(YEAST_TAG)) {
            InventoryItem item = new InventoryItem(new Yeast());
            mStorage.createInventoryItem(item);
            mFragmentHandler.showYeastEditor(item);
        }
    }

    private void onInventoryItemClicked(ListView view, int position) {
        if (mActionMode != null) {
            if (getCheckedItemCount(view) == 0) {
                stopActionMode();
            }
            updateActionBar();
        } else {
            InventoryItem ingredient = (InventoryItem) view.getItemAtPosition(position);
            editIngredient(ingredient);
        }
    }

    private int getCheckedItemCount(ListView view) {
        int checked = 0;
        for (int i = 0; i < view.getCount(); i++) {
            if (view.isItemChecked(i)) {
                checked++;
            }
        }
        return checked;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.clear();
        ListView list = getCurrentList();
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        int checkedCount = getCheckedItemCount(list);

        mActionMode.setTitle(getResources().getString(R.string.select_items));
        mActionMode.setSubtitle(checkedCount + " " + getResources().getString(R.string.selected));

        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

        boolean itemsChecked = (checkedCount > 0);
        mActionMode.getMenu().findItem(R.id.action_delete).setVisible(itemsChecked);
        mActionMode.getMenu().findItem(R.id.action_select_all).setVisible(!areAllSelected());
        return true;
    }

    private boolean areAllSelected() {
        ListView list = getCurrentList();
        return getCheckedItemCount(list) == list.getCount();
    }

    private void setAllSelected(boolean selected) {
        setAllSelected(getCurrentList(), selected);
    }

    private void setAllSelected(ListView view, boolean selected) {
        for (int i = 0; i < view.getCount(); i++) {
            view.setItemChecked(i, selected);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_select_all:
                setAllSelected(true);
                updateActionBar();
                return true;
            case R.id.action_delete:
                int count = getCheckedItemCount(getCurrentList());
                String message;
                if (count > 1) {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_items), count);
                } else {
                    message = String.format(getActivity().getResources().getString(R.string.delete_selected_item), count);
                }
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(R.string.yes, this)
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        setAllSelected(false);
        mActionMode = null;
        getCurrentList().setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int deleted = deleteSelected();
        stopActionMode();
        toastDeleted(deleted);
    }

    private ListView getCurrentList() {
        String tag = mTabHost.getCurrentTabTag();
        if (tag.equals(MALT_TAG)) {
            return mMaltList;
        } else if (tag.equals(HOPS_TAG)) {
            return mHopsList;
        } else if (tag.equals(YEAST_TAG)) {
            return mYeastList;
        } else {
            throw new RuntimeException("Invalid tab tag");
        }
    }

    private void toastDeleted(int deleted) {
        Context context = getActivity();
        String message;
        if (deleted > 1) {
            message = String.format(context.getResources().getString(R.string.deleted_items), deleted);
        } else {
            message = context.getResources().getString(R.string.deleted_item);
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private int deleteSelected() {
        int deleted = 0;
        SparseBooleanArray checkedItems = getCurrentList().getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i)) {
                InventoryItem item = mInventoryList.get(checkedItems.keyAt(i));
                mStorage.deleteInventoryItem(item);
                deleted++;
            }
        }
        mInventoryList = mStorage.retrieveInventory();
        return deleted;
    }

    private void updateActionBar() {
        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    private void startActionMode() {
        ((ActionBarActivity) getActivity()).startSupportActionMode(this);
    }

    private void editIngredient(InventoryItem item) {
        if (item.getIngredient() instanceof Malt) {
            mFragmentHandler.showMaltEditor(item);
        } else if (item.getIngredient() instanceof Hop) {
            mFragmentHandler.showHopsEditor(item);
        } else if (item.getIngredient() instanceof Yeast) {
            mFragmentHandler.showYeastEditor(item);
        }
    }

    private String findString(int id) {
        return getActivity().getResources().getString(id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mActionMode != null) {
            updateActionBar();
            return false;
        } else {
            ListView list = getCurrentList();
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            list.setItemChecked(i, true);
            startActionMode();
        }
        return true;
    }

    @Override
    public void onTabChanged(String s) {
        setAllSelected(mMaltList, false);
        setAllSelected(mHopsList, false);
        setAllSelected(mYeastList, false);
        stopActionMode();
    }

    private void stopActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        checkShowMessage();
    }
}
