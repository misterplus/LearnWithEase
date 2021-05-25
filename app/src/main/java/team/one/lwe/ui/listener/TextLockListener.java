package team.one.lwe.ui.listener;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TextLockListener implements View.OnClickListener {

    private final EditText editText;

    public TextLockListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onClick(View view) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        editText.setTextColor(Color.GRAY);
        InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}
