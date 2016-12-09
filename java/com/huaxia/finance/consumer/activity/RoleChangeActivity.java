package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.huaxia.finance.consumer.util.LogUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class RoleChangeActivity extends BaseActivity {

    @ViewInject(R.id.role_charge)
    private GridView roleCharge;
    private int[] image = {R.drawable.worker_icon, R.drawable.student_icon, R.drawable.boss_icon,
            R.drawable.freelance_icon};
    private String[] text = {"我是上班族", "我是学生党", "我是企业主", "我是自由职业者"};

    @Override
    protected int getLayout() {
        return R.layout.activity_role_change;
    }

    @Override
    protected String getTitleText() {
        return "我的身份";
    }

    @Override
    protected void setup() {
        super.setup();

        List<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 4; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", image[i]);
            map.put("text", text[i]);
            imagelist.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, imagelist,
                R.layout.layout_role_charge_item, new String[]{"image", "text"}, new int[]{
                R.id.iv_role, R.id.tv_role_name});
        roleCharge.setAdapter(simpleAdapter);
        roleCharge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        updateRole("02");
                        break;
                    case 1:
                        updateRole("03");
                        break;
                    case 2:
                        updateRole("01");
                        break;
                    case 3:
                        updateRole("04");
                        break;
                }
            }
        });
    }

    /**
     * 用户身份切换
     * @param role
     */
    public void updateRole(final String role) {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("identityTag", role);
        ApiCaller.call(this, Constant.URL, "0032", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("角色更换失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    mgr.putString(UniqueKey.ROLE,role);
                    Intent intent = new Intent(RoleChangeActivity.this, UserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("role", role);
                    startActivity(intent);
                    finish();
                }else{
                    toast(head.getResponseMsg());
                }
            }
        });
    }
}
