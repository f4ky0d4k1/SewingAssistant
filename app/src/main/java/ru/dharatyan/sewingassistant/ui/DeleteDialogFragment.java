package ru.dharatyan.sewingassistant.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.dharatyan.sewingassistant.R;

public class DeleteDialogFragment extends DialogFragment {

    private final DialogInterface.OnClickListener onPositiveClick;
    private final DialogInterface.OnClickListener onNegativeClick;

    public DeleteDialogFragment(DialogInterface.OnClickListener onPositiveClick, DialogInterface.OnClickListener onNegativeClick) {
        this.onPositiveClick = onPositiveClick;
        this.onNegativeClick = onNegativeClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getText(R.string.dialog_delete_title).toString();
        String buttonPositiveLabel = getText(R.string.dialog_delete_positive).toString();
        String buttonNegativeLabel = getText(R.string.dialog_delete_negative).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setPositiveButton(buttonPositiveLabel, onPositiveClick);
        builder.setNegativeButton(buttonNegativeLabel, onNegativeClick);
        builder.setCancelable(true);

        return builder.create();
    }
}
