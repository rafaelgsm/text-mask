package br.com.aldeiadev.textmask;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Rafael on 12/11/2016.
 * <p>
 * Utility class to apply/remove characters to a String or TextView/EditText, based on a mask.
 * <p>
 */
public abstract class TextMask {
    public static String getMaskedText(final String mask, final String cleanText) {
        StringBuilder text = new StringBuilder(cleanText);

        for (int i = 0; i < text.length(); i++) {
            if (i < mask.length() && mask.charAt(i) != '#') {
                text.insert(i, "" + mask.charAt(i));
            }
        }

        if (text.length() > mask.length()) {
            text.delete(mask.length(), text.length());
        }

        return text.toString();
    }

    public static String getUnmaskedText(final String mask, String maskedText) {
        StringBuilder text = new StringBuilder(maskedText);
        StringBuilder sbMask = new StringBuilder(mask);

        for (int i = 0; i < text.length(); i++) {
            if (i < sbMask.length()) {

                if (sbMask.charAt(i) != '#') {

                    if (text.charAt(i) == sbMask.charAt(i)) {
                        text.deleteCharAt(i);
                        sbMask.deleteCharAt(i);
                        i--;
                    } else {
                        sbMask.deleteCharAt(i);
                    }

                }
            }
        }

        return text.toString();
    }

    public static TextWatcher getWatcher(final String mask, final EditText editText) {
        editText.setText(getMaskedText(mask, editText.getText().toString()));
        return new TextWatcher() {

            boolean isUpdating = false;
            String oldText; //Used only to reapply a previous text in case the user inserts a character before the last position...

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int indexOfInsertion, int i1, int totalCharsInserted) {
                if (isUpdating) return;
                isUpdating = true;

                //If a character is inserted in the middle of the text, the rest of the string will be erased (else)...
                if (indexOfInsertion + 1 > mask.length()) {
//                    Log.d("GenericMask", "Ignored");
                    editText.setText(oldText);
                } else {
//                    Log.d("GenericMask", "Mask...");

                    if (indexOfInsertion + 1 < charSequence.length()) {

                        if (indexOfInsertion < 1) {
                            editText.setText(getMaskedText(mask, getUnmaskedText(mask, charSequence.toString())));
                        } else {
                            StringBuilder sb = new StringBuilder(charSequence.toString());
                            sb.delete(indexOfInsertion + 1, sb.length());
                            editText.setText(getMaskedText(mask, getUnmaskedText(mask, sb.toString())));
                        }

                    } else {
                        editText.setText(getMaskedText(mask, getUnmaskedText(mask, charSequence.toString())));
                    }
                }

                editText.setSelection(editText.getText().toString().length());

                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable finalText) {
            }

            private String getMaskedText(final String mask, final String cleanText) {
                StringBuilder text = new StringBuilder(cleanText);

                for (int i = 0; i < text.length(); i++) {
                    if (i < mask.length() && mask.charAt(i) != '#') {
                        text.insert(i, "" + mask.charAt(i));
                    }
                }

                if (text.length() > mask.length()) {
                    text.delete(mask.length(), text.length());
                }

                return text.toString();
            }

            private String getUnmaskedText(final String mask, String maskedText) {
                StringBuilder text = new StringBuilder(maskedText);
                StringBuilder sbMask = new StringBuilder(mask);

                for (int i = 0; i < text.length(); i++) {
                    if (i < sbMask.length()) {

                        if (sbMask.charAt(i) != '#') {

                            if (text.charAt(i) == sbMask.charAt(i)) {
                                text.deleteCharAt(i);
                                sbMask.deleteCharAt(i);
                                i--;
                            } else {
                                sbMask.deleteCharAt(i);
                            }

                        }
                    }
                }

                return text.toString();
            }
        };
    }
}
