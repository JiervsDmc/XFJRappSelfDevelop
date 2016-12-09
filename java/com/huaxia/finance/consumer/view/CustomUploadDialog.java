package com.huaxia.finance.consumer.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.huaxia.finance.consumer.R;

import java.io.File;

/**
 *
 */
public class CustomUploadDialog extends Dialog implements OnClickListener {

	private Activity mActivity;
	private int number=0;
	private Dialog dialog;
	//保存图片本地路径
	public static final String ACCOUNT_DIR = Environment.getExternalStorageDirectory().getPath()
			+ "/account/";
	public static final String ACCOUNT_MAINTRANCE_ICON_CACHE = "icon_cache/";
	private static final String IMGPATH = ACCOUNT_DIR + ACCOUNT_MAINTRANCE_ICON_CACHE;

	private static final String IMAGE_FILE_NAME = "faceImage.jpeg";
	private static final String TMP_IMAGE_FILE_NAME = "tmp_faceImage.jpeg";

	//常量定义
	public static final int TAKE_A_PICTURE = 10;
	public static final int SELECT_A_PICTURE = 20;
	public static final int SET_PICTURE = 30;
	public static final int SET_ALBUM_PICTURE_KITKAT = 40;
	public static final int SELECET_A_PICTURE_AFTER_KIKAT = 50;
	public CustomUploadDialog(Activity activity) {
		super(activity);
		this.mActivity = activity;
		initView(mActivity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.photo_choose_dialog, null);
		view.findViewById(R.id.take_phone_btn).setOnClickListener(this);
		view.findViewById(R.id.native_btn).setOnClickListener(this);
		view.findViewById(R.id.close_btn).setOnClickListener(this);
		dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围消失
		dialog.setCanceledOnTouchOutside(true);
		// dialog.show();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.take_phone_btn:
				nativeCar();
				break;
			case R.id.native_btn:
				takePhone();
				break;
			case R.id.close_btn:
				dialog.dismiss();
				break;
			default:
				break;
		}
	}

	//版本比较：是否是4.4及以上版本
	final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	private void takePhone() {

		if (mIsKitKat) {
			selectImageUriAfterKikat();
		} else {
			cropImageUri();
		}
		dialog.dismiss();
	}

	private void nativeCar() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMGPATH, IMAGE_FILE_NAME)));
		mActivity.startActivityForResult(intent, TAKE_A_PICTURE);
		dialog.dismiss();
	}

	public void show() {
		if (dialog != null) {
			dialog.show();

		}
	}

	public boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}

	public void hides() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.hide();
			}
		}
	}

	public void dismiss() {

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
	/**
	 *  <br>功能简述:4.4以上裁剪图片方法实现---------------------- 相册
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void selectImageUriAfterKikat() {
		/*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image");
		mActivity.startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT);*/
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		mActivity.startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT
		);
	}

	/** <br>功能简述:裁剪图片方法实现---------------------- 相册
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void cropImageUri() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image*//*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		mActivity.startActivityForResult(intent, SELECT_A_PICTURE);

	}
}
