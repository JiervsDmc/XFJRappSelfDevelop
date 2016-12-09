package com.huaxia.finance.consumer.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by lipiao on 2016/7/6.
 */
public abstract class  BaseFragment extends Fragment implements View.OnClickListener{
    private FragmentActivity fragmentActivity;
    protected View view;
    TextView title;
    LinearLayout btnLeft;
    protected Button btnRight;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(getLayout(), container, false);
        findViews(view);
        setup();
        return view;
    }
    /**
     * 设置页面布局
     * @return
     */
    protected abstract int getLayout();

    /**
     * 设置title标题
     * @return
     */
    protected abstract String getTitleText();

    protected void findViews(View view){
        title = (TextView)view.findViewById(R.id.title);
        btnLeft = (LinearLayout)view.findViewById(R.id.back);
        btnRight = (Button)view.findViewById(R.id.btn_right);
    }
    protected String getBtnRightText() {
        return null;
    }
    protected boolean showBtnLeft() {
        return true;
    }
    protected void setup(){
        title.setText(getTitleText());
        btnLeft.setVisibility(showBtnLeft() ? View.VISIBLE : View.INVISIBLE);

        String btnRightText = getBtnRightText();
        btnRight.setText(btnRightText);

    }
    protected void hideBtnRight() {
        btnRight.setVisibility(View.INVISIBLE);
    }

    protected void showBtnRight() {
        btnRight.setVisibility(View.VISIBLE);
    }

    protected void setBtnRightText(String txt) {
        btnRight.setText(txt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finishFragment();
                break;
            case R.id.btn_right:
                onBtnRightClick(view);
                break;
            default:
                break;
        }
    }

    protected void onBtnRightClick(View view) {
    }
    public void startFragment(int layoutId,BaseFragment fragment) {
        if (IsNullUtils.isNull(fragmentActivity)) {
            fragmentActivity = getActivity();
        }
        fragmentActivity.getSupportFragmentManager().beginTransaction().replace(layoutId, fragment)
                .addToBackStack(null).commit();
    }

    public void finishFragment() {
        if (IsNullUtils.isNull(fragmentActivity)) {
            fragmentActivity = getActivity();
        }
        fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(getActivity());
    }
}
