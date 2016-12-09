package com.huaxia.finance.consumer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.util.FileManager;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.view.zxing.camera.CameraManager;
import com.huaxia.finance.consumer.view.zxing.decoding.CaptureActivityHandler;
import com.huaxia.finance.consumer.view.zxing.decoding.InactivityTimer;
import com.huaxia.finance.consumer.view.zxing.decoding.RGBLuminanceSource;
import com.huaxia.finance.consumer.view.zxing.view.ViewfinderView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Initial the camera
 * 
 * @author zhangguoyu
 * 
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	// 判断是否开启闪光灯
	int ifOpenLight = 0;
	protected static final int CODE_PICK_IMG = 1;

	@Override
	protected int getLayout() {
		return R.layout.activity_capture;
	}

	@Override
	protected String getTitleText() {
		return "二维码扫描";
	}

	@Override
	protected boolean showBtnRight() {
		return true;
	}

	@Override
	protected String getBtnRightText() {
		return "相册";
	}

	@Override
	protected void setup() {
		super.setup();
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * 
	 * @param result
	 * @param barcode
	 *            获取结果
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		// FIXME
		try {
			JSONObject object=new JSONObject(resultString);
			if (resultString.equals("")) {
				Toast.makeText(CaptureActivity.this, "扫描失败!",
						Toast.LENGTH_SHORT).show();
			} else {
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("result", resultString);
				resultIntent.putExtras(bundle);
				CaptureActivity.this.setResult(RESULT_OK, resultIntent);
			}
			CaptureActivity.this.finish();
		} catch (JSONException e) {
			ToastUtils.showSafeToast(CaptureActivity.this,"二维码无效");
			e.printStackTrace();
		}
	}
	/**
	 * 获取带二维码的相片进行扫描
	 */
	@Override
	protected void onBtnRightClick(View view) {
		super.onBtnRightClick(view);
		// 打开手机中的相册
		FileManager.pickAPicture(this, null, CODE_PICK_IMG);
	}

	ProgressDialog mProgress;
	Bitmap scanBitmap;

	/**
	 *  对相册获取的结果进行分析
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (CODE_PICK_IMG == requestCode && RESULT_OK == resultCode) {
			Uri uri = data.getData();
			final String path = FileManager.getRealPathFromURI(this, uri);
			LogUtil.getLogutil().d("path值"+path);
					mProgress = new ProgressDialog(CaptureActivity.this);
					mProgress.setMessage("正在扫描...");
					mProgress.setCancelable(false);
					mProgress.show();

					new Thread(new Runnable() {
						@Override
						public void run() {
							Result result = scanningImage(path);
							LogUtil.getLogutil().d("result值"+result);
							if (result != null) {
								Message m = mHandler.obtainMessage();
								m.what = 1;
								m.obj = result.getText();
								mHandler.sendMessage(m);
							} else {
								Message m = mHandler.obtainMessage();
								m.what = 2;
								m.obj = "Scan failed!";
								mHandler.sendMessage(m);
							}
						}
					}).start();


		}
	}

	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mProgress.dismiss();
				String resultString = msg.obj.toString();
				try {
					JSONObject object=new JSONObject(resultString);
					if (resultString.equals("")) {
						Toast.makeText(CaptureActivity.this, "扫描失败!",
								Toast.LENGTH_SHORT).show();
					} else {
						Intent resultIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("result", resultString);
						resultIntent.putExtras(bundle);
						CaptureActivity.this.setResult(RESULT_OK, resultIntent);
					}
					CaptureActivity.this.finish();
				} catch (JSONException e) {
					ToastUtils.showSafeToast(CaptureActivity.this,"二维码无效");
					e.printStackTrace();
				}

				break;

			case 2:
				mProgress.dismiss();
				Toast.makeText(CaptureActivity.this, "图片格式有误！", Toast.LENGTH_LONG)
						.show();

				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	/**
	 * 扫描二维码图片的方法
	 *
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		LogUtil.getLogutil().d("scanBitmap值"+scanBitmap);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		LogUtil.getLogutil().d("bitmap1值"+bitmap1);
		QRCodeReader reader = new QRCodeReader();

		try {
			return reader.decode(bitmap1, hints);


		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 是否开启闪光灯
	public void IfOpenLight(View v) {
		ifOpenLight++;

		switch (ifOpenLight % 2) {
		case 0:
			// 关闭
			CameraManager.get().closeLight();
			break;

		case 1:
			// 打开
			CameraManager.get().openLight(); // 开闪光灯
			break;
		default:
			break;
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			// 获取系统的Vibrator服务(震动)
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			// 控制手机震动时间
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}