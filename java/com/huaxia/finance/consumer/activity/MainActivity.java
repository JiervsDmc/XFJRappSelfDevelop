package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.freelance.FreelanceAttachmentInfoActivity;
import com.huaxia.finance.consumer.activity.freelance.FreelanceAttachmentUploadActivity;
import com.huaxia.finance.consumer.fragment.ApplyFragment;
import com.huaxia.finance.consumer.fragment.MyInfoFragment;
import com.huaxia.finance.consumer.fragment.OrderFragment;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.huaxia.finance.consumer.util.LogUtil;


public class MainActivity extends FragmentActivity implements View.OnClickListener,ApplyFragment.OnButtonOrder {
    private ImageView orderIMG;
    private ImageView applyIMG;
    private ImageView userIMG;

    private LinearLayout orderLL;
    private LinearLayout userLL;
    private LinearLayout applyLL;

    private MyInfoFragment myInfo;
    private ApplyFragment apply;
    private OrderFragment order;
    public AccountMgr mgr;

    private Bundle bundle;
    private int lastFunc = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManagerUtil.getAppManager().addActivity(this);
        mgr = new AccountMgr(this);
        bundle = new Bundle();
        bundle.putString("role", mgr.getVal(UniqueKey.ROLE));
        myInfo = new MyInfoFragment();
        myInfo.setArguments(bundle);
        apply = new ApplyFragment();
        apply.setArguments(bundle);
        apply.setButton(this);
        order = new OrderFragment();
        order.setArguments(bundle);
        lastFunc = 1;

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, apply).commit();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bundle.putString("role", mgr.getVal(UniqueKey.ROLE));
    }

    private void initView() {
        orderIMG = (ImageView) findViewById(R.id.img_order);
        applyIMG = (ImageView) findViewById(R.id.img_apply);
        userIMG = (ImageView) findViewById(R.id.img_user);
        orderLL = (LinearLayout) findViewById(R.id.ll_order);
        userLL = (LinearLayout) findViewById(R.id.ll_my);
        applyLL = (LinearLayout) findViewById(R.id.ll_apply);
        orderLL.setOnClickListener(this);
        userLL.setOnClickListener(this);
        applyLL.setOnClickListener(this);
    }

    /**
     * fragment之间切换
     *
     * @param from
     * @param to
     * @param direction
     */
    public void switchFragment(Fragment from, Fragment to, int direction) {
        if (from == null || to == null)
            return;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(direction == 0 ? R.anim.enter_from_right : R.anim.enter_from_left,
                        direction == 0 ? R.anim.exit_to_left : R.anim.exit_to_right);
        if (!to.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            transaction.hide(from).add(R.id.main_frame_layout, to).commit();
        } else {
            // 隐藏当前的fragment，显示下一个
            transaction.hide(from).show(to).commit();
        }

    }

    private long exitTime = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_order:
                if (lastFunc == 1) {
                    switchFragment(apply, order, 1);
                } else if (lastFunc == 2) {
                    switchFragment(myInfo, order, 1);
                }
                orderIMG.setImageResource(R.drawable.nav1_current);
                userIMG.setImageResource(R.drawable.nav3_default);
                lastFunc = 0;
                break;

            case R.id.ll_apply:
                if (lastFunc == 0) {
                    switchFragment(order, apply, 0);
                } else if (lastFunc == 2) {
                    switchFragment(myInfo, apply, 1);
                }
                orderIMG.setImageResource(R.drawable.nav1_default);
                userIMG.setImageResource(R.drawable.nav3_default);
                lastFunc = 1;
                break;

            case R.id.ll_my:
                if (lastFunc == 0) {
                    switchFragment(order, myInfo, 0);
                } else if (lastFunc == 1) {
                    switchFragment(apply, myInfo, 0);
                }
                orderIMG.setImageResource(R.drawable.nav1_default);
                userIMG.setImageResource(R.drawable.nav3_current);
                lastFunc = 2;
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String type=getIntent().getStringExtra("type");
        if(type!="") {
            if (lastFunc == 1) {
                switchFragment(apply, order, 1);
            } else if (lastFunc == 2) {
                switchFragment(myInfo, order, 1);
            }
            orderIMG.setImageResource(R.drawable.nav1_current);
            userIMG.setImageResource(R.drawable.nav3_default);
            lastFunc = 0;
        }
    }

    /**
     * 双击退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return false;

            } else {
                //todo:双击退出程序下次进入从主页面
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
                System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonOrder() {
        if (lastFunc == 1) {
            switchFragment(apply, order, 1);
        } else if (lastFunc == 2) {
            switchFragment(myInfo, order, 1);
        }
        orderIMG.setImageResource(R.drawable.nav1_current);
        userIMG.setImageResource(R.drawable.nav3_default);
        lastFunc = 0;
    }
}
