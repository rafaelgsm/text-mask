package br.com.aldeiadev.textmask;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Rafael on 12/11/2016.
 * <p>
 * Utility class to apply/remove characters to a String or TextView/EditText, based on a mask.
 * <p>
 */
public final class TextMask implements TextWatcher {

    /**
     * @param mask ###-###
     * @param editText EditText View.
     * @return Instance of a TextWatcher.
     */
    public static TextMask getWatcher(final String mask, final EditText editText) {
        return new TextMask(mask, editText);
    }

    /**
     * @param mask ###-###
     * @param editText EditText View.
     * @param hasLimit Whether or not the mask should limit the characters inserted.
     * @return Instance of a TextWatcher.
     */
    public static TextMask getWatcher(final String mask, final EditText editText, boolean hasLimit) {
        return new TextMask(mask, editText, hasLimit);
    }

    int totalHashes;// = this.getTotalHashes(mask);
    int totalNonHashes;
    int[] hashPositions;// = this.getHashPositions(mask);
    boolean isUpdating = false;
    String oldText;

    EditText editText;
    String mask;

    private boolean hasLimit;

    private boolean isBackspace;

    public TextMask(String mask, EditText editText) {
        this.editText = editText;
        this.mask = mask;

        totalHashes = getTotalHashes(mask);
        hashPositions = getHashPositions(mask);

        editText.setText(getMaskedText(mask, editText.getText().toString()));

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {

                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        //on backspace
                        isBackspace = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        isBackspace = false;
                    }
                }
                return false;
            }
        });
    }

    public TextMask(String mask, EditText editText, boolean hasLimit) {
        this(mask, editText);
        this.hasLimit = hasLimit;
    }

    public void setHasLimit(boolean hasLimit) {
        this.hasLimit = hasLimit;
    }

    public boolean hasLimit() {
        return hasLimit;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        this.oldText = charSequence.toString();
    }

    public void onTextChanged(CharSequence charSequence, int indexOfInsertion, int i1, int totalCharsInserted) {

        //isBackspace guarantees that the user can freely hold the button and erase continuously
        if (!isBackspace) {
            if (!this.isUpdating) {
                this.isUpdating = true;

                editText.setText(this.getMaskedTextForWatcher(charSequence.toString()));

                editText.setSelection(editText.getText().toString().length());
                this.isUpdating = false;
            }
        }

        isBackspace = false;
    }

    public void afterTextChanged(Editable finalText) {

        //If set to have a limit, then deletePet extra chars:
        if (hasLimit) {
            if (finalText.length() > mask.length()) {
                isUpdating = true;
                finalText.delete(finalText.length() - 1, finalText.length());
                isUpdating = false;
            }
        }
    }

    private String getMaskedTextForWatcher(String finalText) {

        finalText = finalText.replace("/", "")
                .replace(".", "")
                .replace(",", "")
                .replace("\\", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace("+", "")
                .replace("=", "")
                .replace(" ", "");

        StringBuilder sb = new StringBuilder(finalText);

        for (int i = 0; i < mask.length() && i < sb.length(); ++i) {
            if (!this.isHashPosition(i) && sb.charAt(i) != mask.charAt(i)) {
                for (int j = i; j < mask.length() && sb.charAt(j) != mask.charAt(j) && mask.charAt(j) != 35; ++j) {
                    sb.insert(j, "" + mask.charAt(j));
                }

                ++i;
            }
        }

        return sb.toString();
    }

    private int getTotalHashes(String maskText) {
        int counter = 0;

        for (int i = 0; i < maskText.length(); ++i) {
            if (maskText.charAt(i) == 35) {
                ++counter;
            } else {
                ++this.totalNonHashes;
            }
        }

        return counter;
    }

    private int[] getHashPositions(String maskText) {
        this.hashPositions = new int[this.totalHashes];
        int counter = 0;

        for (int i = 0; i < maskText.length(); ++i) {
            if (maskText.charAt(i) == 35) {
                this.hashPositions[counter] = i;
                ++counter;
            }
        }

        return this.hashPositions;
    }

    private boolean isHashPosition(int index) {
        for (int i = 0; i < this.hashPositions.length; ++i) {
            if (this.hashPositions[i] == index) {
                return true;
            }
        }

        return false;
    }

    /**
     * Utils:
     */
    public static String getMaskedText(String mask, String cleanText) {
        if (cleanText == null) {
            return "";
        }

        StringBuilder text = new StringBuilder(cleanText);

        for (int i = 0; i < text.length(); ++i) {
            if (i < mask.length() && mask.charAt(i) != 35) {
                text.insert(i, "" + mask.charAt(i));
            }
        }

        return text.toString();
    }

    public static String getUnmaskedText(String mask, String maskedText) {
        StringBuilder text = new StringBuilder(maskedText);
        StringBuilder sbMask = new StringBuilder(mask);

        for (int i = 0; i < text.length(); ++i) {
            if (i < sbMask.length() && sbMask.charAt(i) != 35) {
                text.deleteCharAt(i);
                sbMask.deleteCharAt(i);
                --i;
            }
        }

        return text.toString();
    }
}
