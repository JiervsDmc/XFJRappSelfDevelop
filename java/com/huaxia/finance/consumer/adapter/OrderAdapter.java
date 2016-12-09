package com.huaxia.finance.consumer.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.freelance.FreelanceAttachmentInfoActivity;
import com.huaxia.finance.consumer.activity.freelance.FreelanceAttachmentUploadActivity;
import com.huaxia.finance.consumer.activity.order.OrderDetailActivity;
import com.huaxia.finance.consumer.activity.protocol.AddLoanProtocolActivity;
import com.huaxia.finance.consumer.activity.student.StudentAttachmentUploadActivity;
import com.huaxia.finance.consumer.activity.worker.WorkerAttachmentUploadActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class OrderAdapter extends BaseAdapter {
    private Activity mActivity;
    private ViewHolder viewHolder;
    private  Intent intent;
    private Dialog dialog;
    private List list;
    private Map map1;
    private AccountMgr mgr;
    private List orderList;
    private  List firstPayList;
    public OrderAdapter( Activity activity,List list){
        this.mActivity=activity;
        this.list=list;
        mgr = new AccountMgr(mActivity);
        orderList=new ArrayList();
        firstPayList=new ArrayList();

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
            if(convertView==null) {
               convertView= LayoutInflater.from(mActivity).inflate(R.layout.layout_order_item,null);
                viewHolder = new ViewHolder();
                viewHolder.tvOrderNumber= (TextView) convertView.findViewById(R.id.tv_order_number);
                viewHolder.tvRepaymentCategory= (TextView) convertView.findViewById(R.id.tv_repayment_category);
                viewHolder.tvmerchantName= (TextView) convertView.findViewById(R.id.tv_merchant_name);
                viewHolder.tvCommodityName= (TextView) convertView.findViewById(R.id.tv_commodity_name);
                viewHolder.tvCommodityPrice= (TextView) convertView.findViewById(R.id.tv_commodity_price);
                viewHolder.tvDistributionPrice= (TextView) convertView.findViewById(R.id.tv_distribution_price);
                viewHolder.tvSaleName= (TextView) convertView.findViewById(R.id.tv_sale_name);
                viewHolder.tvSalePhone= (TextView) convertView.findViewById(R.id.tv_sale_phone);
                viewHolder.tvApplyTime= (TextView) convertView.findViewById(R.id.tv_apply_time);
                viewHolder.llRepaymentDetail= (LinearLayout) convertView.findViewById(R.id.ll_repayment_detail);
                viewHolder.llSupplement= (LinearLayout) convertView.findViewById(R.id.ll_supplement);
                viewHolder.addAttach= (TextView) convertView.findViewById(R.id.add_attach);
                viewHolder.tvWithdraw= (TextView) convertView.findViewById(R.id.tv_withdraw);
                viewHolder.tv_original_price= (TextView) convertView.findViewById(R.id.tv_original_price);
                //降额的现审批金额
                viewHolder.tv_reduce_price= (TextView) convertView.findViewById(R.id.tv_reduce_price);
                viewHolder.ll_new_price= (LinearLayout) convertView.findViewById(R.id.ll_new_price);
                viewHolder.view= convertView.findViewById(R.id.view);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
        for (int i=position;i<list.size();i++){
            LogUtil.getLogutil().d("list.size()"+list.size());
            map1=(Map)list.get(i);
            if(map1.get("orderStatus").toString().contains("ST02")) {
                viewHolder.llRepaymentDetail.setVisibility(View.VISIBLE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.VISIBLE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("还款中");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                //orderList.add(map1.get("orderNo"));
                //orderList.add(position,map1.get("orderNo"));
                viewHolder.llRepaymentDetail.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        toJump(position);

                    }
                });

                break;
            }
            else if(map1.get("orderStatus").toString().contains("ST04")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.VISIBLE);
                viewHolder.view.setVisibility(View.VISIBLE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("补充影像");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.button_theme_yuanjiao);
               // orderList.add(map1.get("orderNo"));
                //orderList.add(position,map1.get("orderNo"));
                firstPayList.add(map1.get("firstPayment").toString());
                viewHolder.addAttach.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toIdentity(position);
                    }
                });
                viewHolder.tvWithdraw.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(position);
                        return;
                    }
                });

               break;
            }
           else if(map1.get("orderStatus").toString().contains("ST03")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("未通过");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.corners_red_bg);
                break;
            }
            else if(map1.get("orderStatus").toString().contains("ST01")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("审核中");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.corners_green_bg);
                break;
            }
            else if(map1.get("orderStatus").toString().contains("ST05")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("已取消");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.corners_red_bg);
                break;
            }
            else if(map1.get("orderStatus").toString().contains("ST06")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("逾期");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.corners_green_bg);
                break;
            }
            else if(map1.get("orderStatus").toString().contains("ST07")) {
                viewHolder.llRepaymentDetail.setVisibility(View.VISIBLE);
                viewHolder.llSupplement.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.VISIBLE);
                setInform(position);
              // orderList.add(map1.get("orderNo"));
                LogUtil.getLogutil().d("position值"+position);
                LogUtil.getLogutil().d("orderNo值"+map1.get("orderNo"));
               //orderList.add(position,map1.get("orderNo").toString());
                viewHolder.tvRepaymentCategory.setText("已结清");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                viewHolder.llRepaymentDetail.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toJump(position);
                    }
                });
               break;
            } else if(map1.get("orderStatus").toString().contains("ST08")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.VISIBLE);
                viewHolder.view.setVisibility(View.VISIBLE);
                setInform(position);
                viewHolder.tvRepaymentCategory.setText("补充影像");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                //orderList.add(map1.get("orderNo"));
                //orderList.add(position,map1.get("orderNo"));
                firstPayList.add(map1.get("firstPayment").toString());
                viewHolder.addAttach.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toIdentity(position);
                    }
                });
                viewHolder.tvWithdraw.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // toCancel(position);
                        showDialog(position);
                        return;
                    }
                });
                break;
            }else if(map1.get("orderStatus").toString().contains("ST09")) {
                viewHolder.llRepaymentDetail.setVisibility(View.GONE);
                viewHolder.llSupplement.setVisibility(View.VISIBLE);
                viewHolder.view.setVisibility(View.VISIBLE);
                setInform(position);
                //经过降低额度操作的现审批金额(UI做新增一条字段)
                viewHolder.tv_original_price.setText("原审批金额");
                viewHolder.ll_new_price.setVisibility(View.VISIBLE);
                viewHolder.tvDistributionPrice.setText(new BigDecimal((String) map1.get("stagesMoney")).setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"元");
                viewHolder.tv_reduce_price.setText(new BigDecimal((String) map1.get("approvalAmount")).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "元");
                viewHolder.tvRepaymentCategory.setText("降低额度");
                viewHolder.tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                //orderList.add(map1.get("orderNo"));
                //orderList.add(position,map1.get("orderNo"));
                firstPayList.add(map1.get("firstPayment").toString());
                viewHolder.addAttach.setText("补充协议");
                viewHolder.addAttach.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳H5页面补充协议点击同意
                        toAddH5(position);
                    }
                });
                viewHolder.tvWithdraw.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // toCancel(position);
                        showDialog(position);
                        return;
                    }
                });
                break;
            }
            //orderList.add(i,map1.get("orderNo"));

        }
        //orderList.add(map1.get("orderNo"));
        LogUtil.getLogutil().d("orderList的值"+orderList);
        return convertView;
    }

    private void toAddH5(int position) {
        //跳H5页面补充协议点击同意
        Intent intent = new Intent(mActivity, AddLoanProtocolActivity.class);
        Map map2=(Map)list.get(position);
        intent.putExtra("orderNo",(String)map2.get("orderNo"));
        intent.putExtra("type","2");
        mActivity.startActivity(intent);
    }

    private void toJump(int position) {
        LogUtil.getLogutil().d("position的值"+position);
        intent=new Intent(mActivity,OrderDetailActivity.class);
        //intent.putExtra("orderNo",orderList.get(position).toString());
        Map map2=(Map)list.get(position);
        intent.putExtra("orderNo",map2.get("orderNo").toString());
        intent.putExtra("approvalAmount", map2.get("approvalAmount").toString());
        intent.putExtra("stagesMoney", map2.get("stagesMoney").toString());
        mActivity.startActivity(intent);
        LogUtil.getLogutil().d("orderNo的值1"+orderList.get(position).toString());
    }

    private void setInform(int position) {
       viewHolder.tvOrderNumber.setText(map1.get("orderNo").toString());
        // viewHolder.tvOrderNumber.setText("12315644561458963258c");
        viewHolder.tvmerchantName.setText(map1.get("companyName").toString());
        viewHolder.tvCommodityName.setText(map1.get("productName").toString());
        viewHolder.tvCommodityPrice.setText(new BigDecimal((String) map1.get("productPrice")).setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"元");
        viewHolder.tvDistributionPrice.setText(new BigDecimal((String) map1.get("approvalAmount")).setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"元");
        viewHolder.ll_new_price.setVisibility(View.GONE);
        viewHolder.tv_original_price.setText("分期金额");
        viewHolder.tvSaleName.setText(map1.get("salesmanName").toString());
        viewHolder.tvSalePhone.setText(map1.get("salesmanCellphone").toString());
        viewHolder.tvApplyTime.setText(map1.get("applyTime").toString().replace(".","-"));
        //orderList.add(map1.get("orderNo"));
        orderList.add(position,map1.get("orderNo"));
    }

    static class ViewHolder {
        TextView tvOrderNumber;
        TextView tvRepaymentCategory;
        TextView tvmerchantName;
        TextView tvCommodityName;
        TextView tvCommodityPrice;
        TextView tvDistributionPrice;
        TextView tvSaleName;
        TextView tvSalePhone;
        TextView tvApplyTime;
        TextView tv_original_price;
        TextView tv_reduce_price;
        LinearLayout ll_new_price;
        LinearLayout llRepaymentDetail;
        LinearLayout llSupplement;
        TextView addAttach;
        TextView tvWithdraw;
        View view;
    }

    private void showDialog(final int position) {
        LogUtil.getLogutil().d("position"+position);
        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.ok_dialog, null);
        view.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toCancel(position);
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setContentView(view);
        dialog.show();

        WindowManager manager = (mActivity).getWindowManager();
        Display display = manager.getDefaultDisplay();
        int width = 600;
        int height = display.getHeight();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(width,height/4);
    }
    //取消申请
    private void toCancel(int position) {
        Map<String, Object> mapCancel = new HashMap<>();
        mapCancel.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        mapCancel.put("orderNo",orderList.get(position));
        LogUtil.getLogutil().d("orderList值"+orderList);
        LogUtil.getLogutil().d("传入的orderNo"+orderList.get(position));
        ApiCaller.call(mActivity, Constant.URL, "0005", "appService", mapCancel, new ApiCaller.MyStringCallback(mActivity, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("取消申请结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                   ToastUtils.showSafeToast(mActivity,head.getResponseMsg().toString());
                    Intent intent = new Intent();
                    intent.setAction("com.huaxia.finance.broadcast.loadcomplete");
                    mActivity.sendBroadcast(intent);
                    dialog.dismiss();
                    return;
                }else {
                    ToastUtils.showSafeToast(mActivity,head.getResponseMsg());
                    dialog.dismiss();
                }
            }
        });
    }
    //判断身份
    private void toIdentity(final int number) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(mActivity, Constant.URL, "0026", "appService", map, new ApiCaller.MyStringCallback(mActivity, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }
            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("判断身份结果是"+response);
                if(head.getResponseCode().contains("0000")&&!body.isEmpty()) {
//                    Map map=(Map)body.get("result");
                    BigDecimal bigDecimal;
//                    if(map.get("identityTag").toString().contains("01")||map.get("identityTag").toString().contains("02")) {
////                        intent=new Intent(mActivity,WorkerAttachmentUploadActivity.class);
//                        intent=new Intent(mActivity,FreelanceAttachmentInfoActivity.class);
//                        intent.putExtra("number","2");
//                        intent.putExtra("orderNo",orderList.get(number).toString());
//                        LogUtil.getLogutil().d("工作族orderNo"+orderList.get(number).toString());
//                        //判断是否有首付 有则传入true
//                        bigDecimal = new BigDecimal(firstPayList.get(number).toString());
//                        // 首付大于0
//                        if (bigDecimal.compareTo(new BigDecimal(0)) > 0) {
//                            intent.putExtra("havePayment",true);
//                            // 首付为0
//                        } else {
//                            intent.putExtra("havePayment",false);
//                        }
//                        mActivity.startActivity(intent);
//                        return;
//                    }else if(map.get("identityTag").toString().contains("03")) {
//                        intent=new Intent(mActivity,StudentAttachmentUploadActivity.class);
//                        intent.putExtra("number","2");
//                        intent.putExtra("orderNo",orderList.get(number).toString());
//                        LogUtil.getLogutil().d("学生族orderNo"+orderList.get(number).toString());
//                        bigDecimal = new BigDecimal(firstPayList.get(number).toString());
//                        if (bigDecimal.compareTo(new BigDecimal(0)) > 0) {
//                            intent.putExtra("havePayment",true);
//                        } else {
//                            intent.putExtra("havePayment",false);
//                        }
//                        mActivity.startActivity(intent);
//                        return;
//                    }else if(map.get("identityTag").toString().contains("04")) {
//                        intent=new Intent(mActivity,FreelanceAttachmentUploadActivity.class);
//                        intent.putExtra("number","2");
//                        intent.putExtra("orderNo",orderList.get(number).toString());
//                        LogUtil.getLogutil().d("自由职业者orderNo"+orderList.get(number).toString());
//                        bigDecimal = new BigDecimal(firstPayList.get(number).toString());
//                        if (bigDecimal.compareTo(new BigDecimal(0)) > 0) {
//                            intent.putExtra("havePayment",true);
//                        } else {
//                            intent.putExtra("havePayment",false);
//                        }
//                        mActivity.startActivity(intent);
//                        return;
//                    }

                    intent=new Intent(mActivity,FreelanceAttachmentInfoActivity.class);
                    intent.putExtra("number","2");
                    intent.putExtra("orderNo",orderList.get(number).toString());
                    LogUtil.getLogutil().d("orderNo"+orderList.get(number).toString());
                    //判断是否有首付 有则传入true
                    bigDecimal = new BigDecimal(firstPayList.get(number).toString());
                    // 首付大于0
                    if (bigDecimal.compareTo(new BigDecimal(0)) > 0) {
                        intent.putExtra("havePayment",true);
                        // 首付为0
                    } else {
                        intent.putExtra("havePayment",false);
                    }
                    mActivity.startActivity(intent);
                    return;
                }else {
                    ToastUtils.showSafeToast(mActivity,head.getResponseMsg());
                }
            }

        });
    }
}
