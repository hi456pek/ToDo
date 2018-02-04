package sekcja23.todo.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import sekcja23.todo.R;

public class LocationDialog {
    protected static final int ADD_REMAINDER_DIALOG_LAYOUT = R.layout.location_dialog;

    protected static final int LOCATION_DIALOG_TITLE = R.string.location_dialog_title;
    protected static final int ADD_BUTTON_TEXT = R.string.add_button;
    protected static final int CANCEL_BUTTON_TEXT = R.string.cancel_button;

    protected LayoutInflater layoutInflater;
    protected Context context;

    protected AlertDialog dialog;

    public LocationDialog(Context context, LayoutInflater layoutInflater, String journalId) {
        this.layoutInflater = layoutInflater;
        this.context = context;

        View view = this.layoutInflater.inflate(ADD_REMAINDER_DIALOG_LAYOUT, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        this.initControls();
        builder.setTitle(LOCATION_DIALOG_TITLE);
        builder.setView(view)
                .setPositiveButton(ADD_BUTTON_TEXT, (DialogInterface dialog, int id) -> {
                    dialog.dismiss();
                })
                .setNegativeButton(CANCEL_BUTTON_TEXT, (DialogInterface dialog, int id) -> dialog.cancel());
        this.dialog = builder.create();
    }


    protected void initControls() {

        this.initControlsOnClick();
    }

    protected void initControlsOnClick() {

    }

    public void show() {
        this.dialog.show();
    }

    public AlertDialog getDialog() {
        return this.dialog;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.dialog.setOnDismissListener(onDismissListener);
    }
}
