package com.huaxia.finance.consumer.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mirui on 2016/12/13.
 */

public class StringFilterEdittext extends EditText{
    public StringFilterEdittext(Context context) {
        this(context,null);
    }

    public StringFilterEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StringFilterEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = getText().toString();
                String str = StringFilter(editable.toString());
                if(!editable.equals(str)){
                    setText(str);
                    setSelection(str.length()); //光标置后
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public Editable getText() {
        return super.getText();
    }

    public String StringFilter(String str){
        try {
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; //要过滤掉的字符
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            return str;
        }
    }
}
