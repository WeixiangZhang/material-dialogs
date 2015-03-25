package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class only works on Honeycomb (API 11) and above.
 *
 * @author Aidan Follestad (afollestad)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MaterialMultiSelectListPreference extends MultiSelectListPreference {

    private Context context;
    private MaterialDialog mDialog;

    public MaterialMultiSelectListPreference(Context context) {
        this(context, null);
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        if (mDialog != null)
            mDialog.setItems(entries);
    }

    private void init(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            setWidgetLayoutResource(0);
    }

    @Override
    protected void showDialog(Bundle state) {
        List<Integer> indices = new ArrayList<>();
        for (String s : getValues()) {
            int index = findIndexOfValue(s);
            if (index >= 0)
                indices.add(findIndexOfValue(s));
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .content(getDialogMessage())
                .icon(getDialogIcon())
                .negativeText(getNegativeButtonText())
                .positiveText(getPositiveButtonText())
                .items(getEntries())
                .itemsCallbackMultiChoice(indices.toArray(new Integer[indices.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                        final Set<String> values = new HashSet<>();
                        for (CharSequence s : text)
                            values.add((String) s);
                        if (callChangeListener(values))
                            setValues(values);
                        return true;
                    }
                })
                .dismissListener(this);

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        mDialog = builder.show();
    }
}