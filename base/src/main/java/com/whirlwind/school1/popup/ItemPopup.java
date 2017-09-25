package com.whirlwind.school1.popup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.R;
import com.whirlwind.school1.activity.ConfigItemActivity;
import com.whirlwind.school1.base.DialogPopup;
import com.whirlwind.school1.helper.DateHelper;
import com.whirlwind.school1.models.Item;

public class ItemPopup extends DialogPopup {

    private final Item item;

    public ItemPopup(Item item) {
        this.item = item;
        contentType = ContentType.custom;
    }

    @SuppressLint("InflateParams")
    @Override
    public void build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.text_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        // TODO: Enable completing
        /*if ((event.flags & Codes.typeMask) == Codes.task)
            builder.setNeutralButton((event.flags & Codes.completed) != 0 ? R.string.text_task_incomplete_button :
                    R.string.text_task_completed_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                    dataInterface.invertTaskCompleted(event);
                }
            });*/

        StringBuilder text = new StringBuilder();
        text.append(item.description)
                .append(context.getString((item.flags & Item.TYPE_MASK) == Item.TASK ? R.string.message_task_popup : R.string.message_appointment_popup))
                .append(DateHelper.getString(item.date));

        //if (dataInterface.isModifiable(event)) {
        customTitle = LayoutInflater.from(context).inflate(R.layout.popup_toolbar, null, false);
        Toolbar toolbar = (Toolbar) customTitle;
        toolbar.inflateMenu(R.menu.item_dialog);
        toolbar.setTitle(item.subject);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_edit) {
                    dismiss();

                    context.startActivity(new Intent(context, ConfigItemActivity.class).putExtra("isNew", false)
                            .putExtra("uid", item.getKey()).putExtra("subject", item.subject)
                            .putExtra("description", item.description).putExtra("date", item.date)
                            .putExtra("flags", item.flags));
                    return true;
                } else if (menuItem.getItemId() == R.id.action_delete) {
                    new ConfirmationPopup(
                            R.string.warning_title, R.string.warning_delete_entry, new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("items")
                                    .child(item.getKey())
                                    .removeValue();
                        }
                    }).show();
                    return true;
                } else return false;
            }
        });
        builder.setCustomTitle(customTitle);

        customView = LayoutInflater.from(context).inflate(R.layout.popup_text_view, null, false);
        ((TextView) customView).setText(text);
        builder.setView(customView);
        //} else
        //builder.setTitle(event.subject).setMessage(text);
        dialog = builder.create();
    }
}