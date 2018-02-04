package sekcja23.todo.Utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import sekcja23.todo.Models.Remainder;
import sekcja23.todo.R;

public class FileDialog {
    protected static final int ADD_REMAINDER_DIALOG_LAYOUT = R.layout.remainder_dialog;

    // Dialog controls
    protected static final int DATE_PICKER_EDIT = R.id.dateText;
    protected static final int TIME_PICKER_EDIT = R.id.timeText;

    protected static final int REMAINDER_DIALOG_TITLE = R.string.remainder_dialog_title;
    protected static final int ADD_BUTTON_TEXT = R.string.add_button;
    protected static final int CANCEL_BUTTON_TEXT = R.string.cancel_button;

    protected LayoutInflater layoutInflater;
    protected Context context;

    protected AlertDialog dialog;

    protected EditText dateControl;
    protected EditText timeControl;

    protected Calendar today;
    protected Calendar remainderDate;
    protected Remainder remainder;

    protected DatePickerDialog datePickerDialog;
    protected TimePickerDialog timePickerDialog;


    public FileDialog(Context context, LayoutInflater layoutInflater, String journalId) {
        this.layoutInflater = layoutInflater;
        this.today = Calendar.getInstance();
        this.remainderDate = Calendar.getInstance();
        this.context = context;

        View view = this.layoutInflater.inflate(ADD_REMAINDER_DIALOG_LAYOUT, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        this.initControls(view.findViewById(DATE_PICKER_EDIT), view.findViewById(TIME_PICKER_EDIT));

        builder.setTitle(REMAINDER_DIALOG_TITLE);
        builder.setView(view)
                .setPositiveButton(ADD_BUTTON_TEXT, (DialogInterface dialog, int id) -> {
                    this.remainder = this.createReminder(journalId);

                    dialog.dismiss();
                })
                .setNegativeButton(CANCEL_BUTTON_TEXT, (DialogInterface dialog, int id) -> dialog.cancel());
        this.dialog = builder.create();
    }


    protected void initControls(EditText dateControl, EditText timeControl) {
        this.dateControl = dateControl;
        this.timeControl = timeControl;

        this.initControlsOnClick();
    }

    protected void initControlsOnClick() {
        this.dateControl.setOnClickListener((View view) -> {
            this.datePickerDialog = new DatePickerDialog(this.context, (DatePicker datePicker, int year, int month, int day) -> {
                this.remainderDate.set(year, month, day);
                String date = day + "-" + month + "-" + year;
                this.dateControl.setText(date);
                this.datePickerDialog.dismiss();
            }, this.today.get(Calendar.YEAR), this.today.get(Calendar.MONTH), this.today.get(Calendar.DAY_OF_MONTH));
            this.datePickerDialog.show();
        });

        this.timeControl.setOnClickListener((View view) -> {
            this.timePickerDialog = new TimePickerDialog(this.context, (TimePicker timePicker, int hours, int minutes) -> {
                this.remainderDate.set(Calendar.HOUR, hours);
                this.remainderDate.set(Calendar.MINUTE, minutes);
                String time = hours + ":" + minutes;
                this.timeControl.setText(time);
                this.timePickerDialog.dismiss();
            }, 0, 0, true);
            this.timePickerDialog.show();
        });
    }

    protected Remainder createReminder(String journalId) {
        return new Remainder(this.remainderDate, journalId);

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

    public Remainder getRemainder() {
        return this.remainder;
    }

}
