package com.fsck.k9.util;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import androidx.core.content.FileProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
public class UpdateUtil {
	Context context;
	private static final String UPDATEURL = "http://edu.hwebook.cn/xys/mobile.mvc?api=getapps&type=13";
	private static final String TAG = "UpdateUtul";
	private String apkPath;
	private Uri uri;
	private int contentLength;
    private CustomDialog customDialog;


    public UpdateUtil(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}

    // 下载xml检查版本号
	public  class CheckApkTask extends AsyncTask<Void, Void, Void> {

		int curVersion;
		int resultCode = -1;
		String errorMessage;
		UpdataInfo record;

//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            customDialog.dismiss();
//            Log.i("tag","11111111111111111111111111111111111111111111112222222");
//        }

        protected void onPreExecute() {
			super.onPreExecute();
            curVersion = getVersionName();
		}

		@Override
		protected Void doInBackground(Void... voids) {

			try {

				URL url = new URL(UPDATEURL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5 * 1000);
				connection.setReadTimeout(5 * 1000);
				connection.setRequestProperty("Connection", "close");

				// 网络连接错误
				int responseCode = connection.getResponseCode();
				Log.i("tag","tttttttt"+responseCode);
				if (responseCode != 200) {
					return null;
					// return LoginUtils.LOGIN_NETWORK_ERROR;
				}

				org.w3c.dom.Document doc = null;

				InputStream inputStream = connection.getInputStream();

				if (inputStream == null) {
					return null;
				}

				try {
					// 解析xml文件
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = factory.newDocumentBuilder();
					doc = docBuilder.parse(inputStream);

					// 断开连接
					inputStream.close();

				} catch (IOException e) {
					return null;
				} catch (Exception e) {
					return null;
				}
				/**
				 * <Return Code="0" Message="成功"> <Apps> <App Order="01"> <Version>147</Version>
				 * <Description>当当阅读</Description>
				 * <Uri>HTTP://edu.hwebook.cn/soft/hvDDReader930UP.apk</Uri> </App> </Apps>
				 * </Return>
				 */
				// 获取根节点<Return>。
				Element root = doc.getDocumentElement();
				resultCode = Integer.parseInt(root.getAttribute("Code"));
				String message = root.getAttribute("Message");
				LogUtils.printLog(TAG, "message: " + message);
                Log.i("tag","tttttttt"+message);
				if (resultCode != 0) {
					errorMessage = message;
					return null;
				}
				NodeList appsList = root.getElementsByTagName("Apps");
				LogUtils.printLog(TAG, "appsList.getLength(): " + appsList.getLength());
				if (appsList.getLength() != 1) {
					return null;
				}
				Element appsRoot = (Element) appsList.item(0);
				NodeList appList = appsRoot.getElementsByTagName("App");
				int itemCount = appList.getLength();
				LogUtils.printLog(TAG, "itemCount: " + itemCount);
				record = new UpdataInfo();
				for (int i = 0; i < itemCount; i++) {
					Element recordNode = (Element) appList.item(i);
					LogUtils.printLog(TAG, "recordNode.getAttribute(Order): " + recordNode.getAttribute("Order"));
					if (recordNode.getAttribute("Order").equals("13")) {
						NodeList childList = recordNode.getChildNodes();
						LogUtils.printLog(TAG, "childList.getLength()=" + childList.getLength());
						for (int j = 0; j < childList.getLength(); j++) {
							LogUtils.printLog(TAG,
									"childList.item(j).getNodeName() j =" + j + childList.item(j).getNodeName());
							if ((childList.item(j)).getNodeName().equals("Version")) {
								record.setVersion(childList.item(j).getTextContent());
							     } else if ((childList.item(j)).getNodeName().equals("Description")) {
								record.setDescription(childList.item(j).getTextContent());
							} else if ((childList.item(j)).getNodeName().equals("Uri")) {
                                String textContent = childList.item(j).getTextContent();
                                Log.i("tag", "333333333:..... "+textContent);
								record.setUrl(childList.item(j).getTextContent());
							}
						}
					}

				} 

				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (curVersion != 0 && record != null) {
//				Log.i("tag", "1111111111: "+ Integer.parseInt(record.getVersion()));
//                Log.i("tag", "2222222:..... "+curVersion);
				if (record.getVersion() != null && Integer.parseInt(record.getVersion()) > curVersion) {
				    if (context!=null){
				        try{
                            customDialog = new CustomDialog(context);
                            customDialog.setYcorShow_Btn(View.VISIBLE);
                            customDialog.setYcorShow_bar(View.GONE);
                            customDialog.setTitle("更新提示");
                            customDialog.setMessage("有新版本，您是否更新?");
                            customDialog.setCancel("取消", new CustomDialog.IOnCancelListener() {
                                @Override
                                public void onCancel(CustomDialog dialog) {
//                                    SharedPreferences is_update = context.getSharedPreferences("is_update", Context.MODE_PRIVATE);
//                                    is_update.edit().putBoolean("key",false).commit();
                                    if (customDialog!=null){
                                        customDialog.dismiss();
                                        Log.i("tag","QQQQQQQQQQQQQ22");
                                    }
                                }
                            });
                            customDialog.setConfirm("更新", new CustomDialog.IOnConfirmListener(){
                                @Override
                                public void onConfirm(CustomDialog dialog) {
                                    if (customDialog!=null){
                                        customDialog.dismiss();
                                        Log.i("tag","QQQQQQQQQQQQQ22");
                                    }
                                    new DownNewApkTask(record.getUrl()).execute();
                                }
                            });
                            customDialog.show();
                        }catch (Exception e){}

                    }
				}
			}

			Log.i("tag","1111111111111111111111111111111111111111111111");
		}


	}
















    // 下载xml检查版本号
    public  class CheckApkTask1 extends AsyncTask<Void, Void, Void> {

        int curVersion;
        int resultCode = -1;
        String errorMessage;
        UpdataInfo record;
        protected void onPreExecute() {
            super.onPreExecute();
            curVersion = getVersionName();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                URL url = new URL(UPDATEURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5 * 1000);
                connection.setReadTimeout(5 * 1000);
                connection.setRequestProperty("Connection", "close");

                // 网络连接错误
                int responseCode = connection.getResponseCode();
                Log.i("tag","tttttttt"+responseCode);
                if (responseCode != 200) {
                    return null;
                    // return LoginUtils.LOGIN_NETWORK_ERROR;
                }

                org.w3c.dom.Document doc = null;

                InputStream inputStream = connection.getInputStream();

                if (inputStream == null) {
                    return null;
                }

                try {
                    // 解析xml文件
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    doc = docBuilder.parse(inputStream);

                    // 断开连接
                    inputStream.close();

                } catch (IOException e) {
                    return null;
                } catch (Exception e) {
                    return null;
                }
                /**
                 * <Return Code="0" Message="成功"> <Apps> <App Order="01"> <Version>147</Version>
                 * <Description>当当阅读</Description>
                 * <Uri>HTTP://edu.hwebook.cn/soft/hvDDReader930UP.apk</Uri> </App> </Apps>
                 * </Return>
                 */
                // 获取根节点<Return>。
                Element root = doc.getDocumentElement();
                resultCode = Integer.parseInt(root.getAttribute("Code"));
                String message = root.getAttribute("Message");
                LogUtils.printLog(TAG, "message: " + message);
                Log.i("tag","tttttttt"+message);
                if (resultCode != 0) {
                    errorMessage = message;
                    return null;
                }
                NodeList appsList = root.getElementsByTagName("Apps");
                LogUtils.printLog(TAG, "appsList.getLength(): " + appsList.getLength());
                if (appsList.getLength() != 1) {
                    return null;
                }
                Element appsRoot = (Element) appsList.item(0);
                NodeList appList = appsRoot.getElementsByTagName("App");
                int itemCount = appList.getLength();
                LogUtils.printLog(TAG, "itemCount: " + itemCount);
                record = new UpdataInfo();
                for (int i = 0; i < itemCount; i++) {
                    Element recordNode = (Element) appList.item(i);
                    LogUtils.printLog(TAG, "recordNode.getAttribute(Order): " + recordNode.getAttribute("Order"));
                    if (recordNode.getAttribute("Order").equals("13")) {
                        NodeList childList = recordNode.getChildNodes();
                        LogUtils.printLog(TAG, "childList.getLength()=" + childList.getLength());
                        for (int j = 0; j < childList.getLength(); j++) {
                            LogUtils.printLog(TAG,
                                    "childList.item(j).getNodeName() j =" + j + childList.item(j).getNodeName());
                            if ((childList.item(j)).getNodeName().equals("Version")) {
                                record.setVersion(childList.item(j).getTextContent());
                            } else if ((childList.item(j)).getNodeName().equals("Description")) {
                                record.setDescription(childList.item(j).getTextContent());
                            } else if ((childList.item(j)).getNodeName().equals("Uri")) {
                                String textContent = childList.item(j).getTextContent();
                                Log.i("tag", "333333333:..... "+textContent);
                                record.setUrl(childList.item(j).getTextContent());
                            }
                        }
                    }

                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if (curVersion != 0 && record != null) {
//				Log.i("tag", "1111111111: "+ Integer.parseInt(record.getVersion()));
//                Log.i("tag", "2222222:..... "+curVersion);
                if (record.getVersion() != null && Integer.parseInt(record.getVersion()) > curVersion) {
                    SharedPreferences update = context.getSharedPreferences("update", Context.MODE_PRIVATE);
                    update.edit().putBoolean("key",true).commit();
                }else{
                    SharedPreferences update = context.getSharedPreferences("update", Context.MODE_PRIVATE);
                    update.edit().putBoolean("key",false).commit();

                }
            }
        }
    }














    // 下载apk
	public class DownNewApkTask extends AsyncTask<Void, Integer, Integer> {

		String apkUrl;
		private CustomDialog dialog;
//		TextView progressTv;
		File installFile;

		public DownNewApkTask(String url) {
			apkUrl = url;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new CustomDialog(context);
			dialog.setYcorShow_Btn(View.GONE);
			dialog.setYcorShow_bar(View.VISIBLE);
			dialog.setTitle("更新提示");
			dialog.setMessage("正在更新,请稍等!");
			dialog.setCancelable(false);//设置进度条是否可以按退回键取消
			//设置点击进度对话框外的区域对话框不消失
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Integer doInBackground(Void... voids) {
			try {
				apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+ "Hmail.apk";
				uri = FileProvider.getUriForFile(context, "com.fsck.k9.ui.fileprovider", new File(apkPath));
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
                conn.setRequestProperty("Charset", "UTF-8");

                installFile = new File(apkPath);


				if (installFile == null) {
					return -1;
				}
				int total = 0;
				if (installFile.exists()) {
					installFile.delete();
	            }
				int code = conn.getResponseCode();
	            System.out.println("code = " + code);
                if (dialog!=null){
					contentLength = conn.getContentLength();
					dialog.Max(conn.getContentLength());
					dialog.setMax();
				}
				InputStream is = conn.getInputStream();
				LogUtils.printLog(TAG, "getContentLength=" + conn.getContentLength());
				FileOutputStream fos = new FileOutputStream(apkPath, true);
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] buffer = new byte[1024];
				int len;

				while ((len = bis.read(buffer)) != -1) {
					if (isCancelled()) {
						return -2;
					}
					fos.write(buffer, 0, len);
					total += len;
                    Log.i("xxxxxxx","mmmmmmm"+total+"nnnnnnnnn"+len);
					// 获取当前下载量
					//Thread.sleep(1000);
					publishProgress(total);
					LogUtils.printLog(TAG, "total: " + total);
				}
				fos.close();
				bis.close();
				is.close();
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// TODO total总长度，current下载长度
			int total = values[0];
			dialog.Progress(total);
			dialog.setProgress();
			double i = total / (double) contentLength * 100;
			int progress = (int) Math.ceil(i);
			dialog.setMessage("正在更新"+progress+"%");
			dialog.setMessagetext();
		}

		@Override
		protected void onPostExecute(Integer reslut) {
			super.onPostExecute(reslut);
//				installApk(uri,apkPath);
                dialog.dismiss();
            	hvApkUtils.sendStaticBroacast_hvLauncher(context, "hanvon.action.updateapk", "filepath", apkPath);
				return;
			}

	}

	/*
	 * 获取当前程序的版本号
	 */
	public  int getVersionName() {
//		PackageInfo packInfo = null;
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * 安装apk
	 *
	 * @param fileSavePath
	 * @param apkPath
	 */
	private void installApk(Uri fileSavePath, String apkPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
			intent.setDataAndType(fileSavePath, "application/vnd.android.package-archive");
		} else {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			data = Uri.fromFile(new File(apkPath));
			intent.setDataAndType(data, "application/vnd.android.package-archive");
		}
		context.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
