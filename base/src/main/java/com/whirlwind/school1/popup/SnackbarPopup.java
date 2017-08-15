package com.whirlwind.school1.popup;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.whirlwind.school1.base.BaseActivity;
import com.whirlwind.school1.base.Popup;

public class SnackbarPopup extends Popup {

    private final int length;
    private final boolean isProgress;
    private ContentType contentType;
    private Snackbar snackbar;
    private String text;
    private int resId;
    private boolean actionSet = false;
    private int actionResId;
    private String actionText;
    private View.OnClickListener actionListener;

    public SnackbarPopup(String text, int length, boolean isProgress) {
        this.text = text;
        this.length = length;
        this.isProgress = isProgress;
        contentType = ContentType.string;
    }

    public SnackbarPopup(@StringRes int resId, int length, boolean isProgress) {
        this.resId = resId;
        this.length = length;
        this.isProgress = isProgress;
        contentType = ContentType.resId;
    }

    public SnackbarPopup(String text, boolean isProgress) {
        this(text, isProgress ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_SHORT, isProgress);
    }

    public SnackbarPopup(@StringRes int resId, boolean isProgress) {
        this(resId, isProgress ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_SHORT, isProgress);
    }

    @Override
    public void build() {
        text = contentType == ContentType.string || contentType == ContentType.stringResId ? text : context.getString(resId);
        BaseActivity activity = (BaseActivity) context;
        //View view = activity.findViewById(R.id.coordinator_layout);
        View view = null;
        snackbar = Snackbar.make(view != null ? view : activity.findViewById(android.R.id.content), text, length);

        if (actionSet) {
            actionText = contentType == ContentType.resIdString || contentType == ContentType.string ? actionText : context.getString(actionResId);
            snackbar.setAction(actionText, actionListener);
        }
        if (isProgress) {
            View textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            ProgressBar bar = new ProgressBar(context);
            //textView.setPadding(0, 0, Codes.convertDp(40), 0);
            //bar.setLayoutParams(new FrameLayout.LayoutParams(Codes.convertDp(32), Codes.convertDp(32), Gravity.END | Gravity.CENTER_VERTICAL));

            ((Snackbar.SnackbarLayout) snackbar.getView()).addView(bar);
            //if (actionSet)
                //snackbar.getView().findViewById(android.support.design.R.id.snackbar_action).setPadding(0, 0, Codes.convertDp(40), 0);
        }
    }

    public SnackbarPopup setAction(int actionResId, View.OnClickListener actionListener) {
        this.actionResId = actionResId;
        this.actionListener = actionListener;

        if (contentType == ContentType.string)
            contentType = ContentType.stringResId;
        actionSet = true;
        return this;
    }

    public SnackbarPopup setAction(String actionText, View.OnClickListener actionListener) {
        this.actionText = actionText;
        this.actionListener = actionListener;

        if (contentType == ContentType.resId)
            contentType = ContentType.resIdString;
        actionSet = true;
        return this;
    }

    @Override
    public Popup show(Context context) {
        if (context instanceof BaseActivity && ((BaseActivity) context).isValid())
            return super.show(context);
        else
            return super.show();
    }

    @Override
    public void showPopup() {
        if (snackbar != null)
            snackbar.show();
    }

    @Override
    public void dismissPopup() {
        if (snackbar != null && snackbar.isShownOrQueued())
            snackbar.dismiss();
    }
}