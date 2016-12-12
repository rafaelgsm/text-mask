package br.com.aldeiadev.textmask;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Rafael on 12/11/2016.
 * <p/>
 * Utility class to apply/remove characters to a String or TextView/EditText, based on a mask.
 * <p/>
 * The mask ignores any character inserted that is not at the end of the text...
 */
public abstract class TextMask {
    public static String getMaskedText(final String mask, final String cleanText) {
        StringBuilder text = new StringBuilder(cleanText);

        for (int i = 0; i < text.length(); i++) {
            if (i < mask.length() && mask.charAt(i) != '#') {
                text.insert(i, "" + mask.charAt(i));
            }
        }

        return text.toString();
    }

    public static String getUnmaskedText(final String mask, String maskedText) {
        StringBuilder text = new StringBuilder(maskedText);
        StringBuilder sbMask = new StringBuilder(mask);

        for (int i = 0; i < text.length(); i++) {
            if (i < sbMask.length() && sbMask.charAt(i) != '#') {
                text.deleteCharAt(i);
                sbMask.deleteCharAt(i);
                i--;
            }
        }

        return text.toString();
    }

    public static TextWatcher getWatcher(final String mask, final EditText editText) {
        editText.setText(getMaskedText(mask, editText.getText().toString()));
        return new TextWatcher() {

            int totalHashes = getTotalHashes(mask); //Total digits the user can type
            int totalNonHashes; //getTotalHashes(mask) is setting this value!

            int[] hashPositions = getHashPositions(mask); //Specific position of each key typed by the user

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

                if (indexOfInsertion + 1 < charSequence.length()) {
//                    Log.d("GenericMask", "Ignored");
                    editText.setText(oldText);
                } else {
                    editText.setText(getMaskedText(charSequence.toString()));
                }

                editText.setSelection(editText.getText().toString().length());

                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable finalText) {
            }

            /**
             * Applies the mask at a text. It will ignore the new digits that matches the mask...
             * Ex.:
             * mask = "#1#"
             *
             * user types => 12
             *
             * Result => 112
             *
             * Ex2.:
             * mask = "#1#"
             *
             * user types => 11
             *
             * Result => 11
             */
            private String getMaskedText(String finalText) {

                StringBuilder sb = new StringBuilder(finalText);
                for (int i = 0; i < mask.length() && i < sb.length(); i++) {
                    if (!isHashPosition(i)) {
                        if (sb.charAt(i) != mask.charAt(i)) {
                            for (int j = i; j < mask.length(); j++) {
                                if (sb.charAt(j) != mask.charAt(j) && mask.charAt(j) != '#') {
                                    sb.insert(j, "" + mask.charAt(j));
                                } else {
                                    break;
                                }
                            }
                            i++;
                        }
                    }
                }

                return sb.toString();
            }

            //Returns the number of keys that the user can type (number of hashes) AND also the "totalNonHashes"!!!
            private int getTotalHashes(String maskText) {

                int counter = 0;
                for (int i = 0; i < maskText.length(); i++) {
                    if (maskText.charAt(i) == '#') {
                        counter++;
                    } else {
                        totalNonHashes++;
                    }
                }
                return counter;
            }

            //Fills the array with each hash's position
            private int[] getHashPositions(String maskText) {
                hashPositions = new int[totalHashes];
                int counter = 0;
                for (int i = 0; i < maskText.length(); i++) {
                    if (maskText.charAt(i) == '#') {
                        hashPositions[counter] = i;
                        counter++;
                    }
                }
                return hashPositions;
            }

            //Tells wether an index is a hash position...
            private boolean isHashPosition(int index) {
                for (int i = 0; i < hashPositions.length; i++) {
                    if (hashPositions[i] == index) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

}
