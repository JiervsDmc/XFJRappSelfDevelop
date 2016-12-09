package com.huaxia.finance.consumer.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;


/**
 * Created by lipiao on 2016/7/1.
 */
public class DialogUtil {
    /**
     * 通用提示
     */
    public static void inform(Context ctx, String msg) {
        inform(ctx, msg, null);
    }

    /**
     * 通用提示
     */
    static Dialog dialog_inform;

    public static void inform(Context ctx, String msg, final DialogInterface.OnClickListener listener) {
        inform(ctx, msg, true, listener);
    }

    public static void inform(Context ctx, String msg, Boolean b,
                              DialogInterface.OnClickListener listener) {
        inform(ctx, msg, b, listener, null);
    }

    public static void inform(Context ctx, String msg,
                              final DialogInterface.OnClickListener listener, final DialogInterface.OnClickListener listener1) {
        inform(ctx, msg, true, listener, listener1);
    }


    /**
     * 有两个按钮的通用提示
     */
    public static void inform(Context ctx, String msg, boolean cancelable,
                              final DialogInterface.OnClickListener listener, final DialogInterface.OnClickListener listener1) {
        final Dialog dialog_inform = new Dialog(ctx, R.style.Dialog_Transparent);
        dialog_inform.setCanceledOnTouchOutside(cancelable);
        dialog_inform.setCancelable(cancelable);

        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_inform, null);
        TextView txt_msg = (TextView) v.findViewById(R.id.txt_msg);
        txt_msg.setText(msg);
        View left = v.findViewById(R.id.btn_left);
        View right = v.findViewById(R.id.btn_right);
        View line = v.findViewById(R.id.line);
        left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_inform.dismiss();
                        if (null != listener)
                            listener.onClick(dialog_inform, Dialog.BUTTON_POSITIVE);
                    }
                }
        );

        if (null != listener1) {
            right.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_inform.dismiss();
                            listener1.onClick(dialog_inform, Dialog.BUTTON_NEGATIVE);
                        }
                    }
            );
        } else {
            line.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
            ((TextView) left).setText("我知道了");
            ((TextView) left).setTextColor(ctx.getResources().getColor(R.color.app_blue_color));
        }

        dialog_inform.setContentView(v);
        try {
            if (dialog_inform != null)
                dialog_inform.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
