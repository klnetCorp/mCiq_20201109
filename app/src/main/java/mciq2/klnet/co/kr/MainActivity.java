package mciq2.klnet.co.kr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    ObservableWebView WebView01;
    RelativeLayout rel_intro;
    RelativeLayout rel_main;

    private final Handler handler = new Handler();

    private Toast toast;
    private RequestQueue queue;
    //Security Check
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";
    public static final String ROOTING_PATH_5 = "/system/app/Superuser.apk";
    // HttpURLConnection 참조 변수.
    HttpURLConnection urlConn = null;
    // URL 뒤에 붙여서 보낼 파라미터.
    StringBuffer sbParams = new StringBuffer();

    public String[] RootFilesPath = new String[]{
            ROOT_PATH + ROOTING_PATH_1 ,
            ROOT_PATH + ROOTING_PATH_2 ,
            ROOT_PATH + ROOTING_PATH_3 ,
            ROOT_PATH + ROOTING_PATH_4 ,
            ROOT_PATH + ROOTING_PATH_5
    };

    long LoginBackKeyClickTme;
    long MainBackKeyClickTme;
    String sHash = "";
    String DstprtCode = "";
    String DstprtName = "";
    SharedPreferences prefsDstPrt;

    boolean isLoginPage = false;
    boolean isMianPage = false;

    boolean isKeyboard = false;

    String deviceId ="";

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        Log.i("CHECK", "push_id : " + intent.getStringExtra("push_id"));
        //앱이 실행된 상태에서 푸시를 보는 경우
        if (intent != null) {
            if(DataSet.getInstance().islogin.equals("true")) {
                if (intent.getStringExtra("push_id") != null) {
                    //앱이 실행된 상태에서 푸시 클릭한 경우 처리 부분

                    DataSet.getInstance().push_id = intent.getStringExtra("push_id");
                    DataSet.getInstance().msg = intent.getStringExtra("msg");

                    if (BuildConfig.DEBUG) {
                        Log.d("CHECK", "push value1, push_id:" + DataSet.getInstance().push_id);
                        Log.d("CHECK", "push value1, msg:" + DataSet.getInstance().msg);
                    }

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "mciq2.klnet.co.kr");
                    badgeIntent.putExtra("badge_count_class_name", "mciq2.klnet.co.kr.MainActivity");
                    sendBroadcast(badgeIntent);

                    /**
                     * 푸시 처리 관련 앱 기능 추가
                     * */

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);

                    //
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("알림")
                            .setMessage( DataSet.getInstance().msg)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(DataSet.getInstance().userid.equals( DataSet.getInstance().recv_id)) {
                                                WebView01.loadUrl("javascript:goPush('"+DataSet.getInstance().push_id+"')");
                                            }
                                            DataSet.getInstance().push_id = "";
                                            DataSet.getInstance().msg = "";
                                            DataSet.getInstance().isrunapppush = false;
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataSet.getInstance().push_id = "";
                                            DataSet.getInstance().isrunapppush = false;
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            } else {
                if (intent.getStringExtra("push_id") != null) {
                    //앱이 종료된 상태에서 푸시 클릭한 경우 처리 부분
                    DataSet.getInstance().push_id = getIntent().getStringExtra("push_id");
                    DataSet.getInstance().obj_id = getIntent().getStringExtra("obj_id");
                    DataSet.getInstance().type = getIntent().getStringExtra("type");
                    DataSet.getInstance().msg = getIntent().getStringExtra("msg");

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "mciq2.klnet.co.kr");
                    badgeIntent.putExtra("badge_count_class_name", "mciq2.klnet.co.kr.MainActivity");
                    sendBroadcast(badgeIntent);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d("CHECK", "push_id :" + DataSet.getInstance().push_id);
            Log.d("CHECK", "isrunapppush :" + DataSet.getInstance().isrunapppush);
            Log.d("CHECK", "userid :" + DataSet.getInstance().userid);
            Log.d("CHECK", "recv_id :" + DataSet.getInstance().recv_id);
        }
        super.onResume();
        if( DataSet.getInstance().push_id != null && ! DataSet.getInstance().push_id.equals("") && DataSet.getInstance().userid.equals( DataSet.getInstance().recv_id)) {
            if (DataSet.getInstance().isrunapppush) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("알림")
                        .setMessage(DataSet.getInstance().msg)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        WebView01.loadUrl("javascript:goPush('"+DataSet.getInstance().push_id+"')");
                                        DataSet.getInstance().push_id = "";
                                        DataSet.getInstance().msg = "";
                                        DataSet.getInstance().isrunapppush = false;
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataSet.getInstance().push_id = "";
                                        DataSet.getInstance().isrunapppush = false;
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AlertDialog.Builder alertDialogBuilderExit = new AlertDialog.Builder(this);
        //Log.e("###","BuildConfig.DEBUG:"+BuildConfig.DEBUG);
        if(!BuildConfig.DEBUG ) {
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, DataSet.connect_url + "/newmobile/selectMobileHashKey.do?app_id=MCIQ&app_os=android&app_version=1", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        sHash = response.getString("hashCode");
                        Log.e("###",response.getString("hashCode"));
                        if (!sHash.trim().equals(getHashKey().trim())) {
                            alertDialogBuilderExit.setMessage("프로그램 무결성에 위배됩니다. \nPlayStore 내에서 \n 설치하시기 바랍니다.").setCancelable(false)
                                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            AlertDialog dialog = alertDialogBuilderExit.create();
                            dialog.show();

                        }


                    } catch (JSONException e) {
                        Log.e("########",e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(jsonObjectRequest);
            //Rooting Check
            boolean isRootingFlag = false;
            try {
                Runtime.getRuntime().exec("su");
                isRootingFlag = true;
            } catch (Exception e) {
                // Exception 나면 루팅 false;
                isRootingFlag = false;
            }

            if (!isRootingFlag) {
                isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
            }

            if (BuildConfig.DEBUG) Log.d("test", "isRootingFlag = " + isRootingFlag);

            alertDialogBuilderExit.setTitle("프로그램 종료");


            if (isRootingFlag == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


            if (kernelBuildTagTest() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 2").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }
            if (shellComendExecuteCheck() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 3").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


        }
        //Rooting Check End

        rel_intro = (RelativeLayout) findViewById(R.id.rel_intro);
        rel_main = (RelativeLayout) findViewById(R.id.rel_main);

        final Context myApp = this;

        deviceId = DataSet.getDeviceID(this);
        if (BuildConfig.DEBUG) Log.d("CHECK", "deviceId :" + deviceId);

        WebView01 = (ObservableWebView) findViewById(R.id.webView);
        WebSettings webSettings = WebView01.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        WebView01.addJavascriptInterface(new AndroidBridge(), "AndroidInterface");
        WebView01.clearHistory();
        WebView01.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        WebView01.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        WebView01.setScrollbarFadingEnabled(false);
        WebView01.clearCache(true);
        WebView01.clearView();

        WebView01.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                WebView01.getWindowVisibleDisplayFrame(r);
                int screenHeight = WebView01.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    if(isKeyboard == false) {
                        Log.i("CHECK", "keyboard show!!"+ keypadHeight);
                        //WebView01.loadUrl("javascript:setKeyboard("+keypadHeight+",'Y')");
                        isKeyboard = true;
                    }
                }
                else {
                    if(isKeyboard == true) {
                        Log.i("CHECK", "keboard hide!!");
                        //WebView01.loadUrl("javascript:setKeyboard(0, 'Y')");
                        isKeyboard = false;
                    }
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //앱 실행 아이콘 개수 조절
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", 0);
        badgeIntent.putExtra("badge_count_package_name", getComponentName().getPackageName());
        badgeIntent.putExtra("badge_count_class_name", getComponentName().getClassName());
        sendBroadcast(badgeIntent);

        //구글 STORE에서 버전읽는부분 막힘. 2022.5월 ?
        //forceUpdate();
//        String storeVersion = getMarketVersion(getPackageName());
//        String deviceVersion = "";
//        try {
//            deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            deviceVersion = "0";
//        }
//        Log.i("CHECK", "deviceVersion :" + deviceVersion);
//        Log.i("CHECK", "storeVersion :" + storeVersion);
//
//        if (storeVersion.compareTo(deviceVersion) > 0) {
//            // 업데이트 필요
//            AlertDialog.Builder alertDialogBuilder =
//                    new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
//            alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
//                    .setMessage("새로운버전("+storeVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
//                    .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//
//                            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
//                            startActivity(intent);
//                        }
//                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setCanceledOnTouchOutside(true);
//            alertDialog.show();
//        }

        Log.i("CHECK", "url :" + DataSet.connect_url + "/newmobile/login.jsp");
       WebView01.loadUrl(DataSet.connect_url + "/newmobile/login.jsp");

        WebView01.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "");
                    fileName = fileName.replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }

                }
            }
        });
//        WebView01.setDownloadListener(new DownloadListener() {
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//
//        });

        WebView01.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (BuildConfig.DEBUG) Log.d("CHECK", url);
                if (view == null || url == null) {
                    return false;
                }


                if (url.contains("play.google.com")) {
                    // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
                    String[] params = url.split("details");
                    if (params.length > 1) {
                        url = "market://details" + params[1];
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }
                }


                if (url.contains(".text") || url.contains(".txt")) {
                    DataSet.getInstance().istext = "true";
                }

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    // HTTP/HTTPS 요청은 내부에서 처리한다.
                    view.loadUrl(url);
                } else {
                    Intent intent;

                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        // 처리하지 못함
                        return false;
                    }

                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                        if (url.startsWith("intent:") && intent.getPackage() != null) {
                            url = "market://details?id=" + intent.getPackage();
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else {
                            // 처리하지 못함
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("/newmobile/login.jsp")) {
                    isLoginPage = true;
                    isMianPage = false;
                } else if (url.contains("/newmobile/main.jsp")) {
                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    isLoginPage = false;
                    isMianPage = true;
                } else {
                    isLoginPage = false;
                    isMianPage = false;
                }
            }


            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {
                switch(errorCode) {
                    case ERROR_AUTHENTICATION:// 서버에서 사용자 인증 실패
                    case ERROR_BAD_URL: // 잘못된 URL
                    case ERROR_CONNECT: // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                    case ERROR_FILE: // 일반 파일 오류
                    case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                    case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                    case ERROR_TIMEOUT: // 연결 시간 초과
                    case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_UNKNOWN: // 일반 오류
                    case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                    case ERROR_UNSUPPORTED_SCHEME: // URI가 지원되지 않는 방식

                    WebView01.setVisibility(View.GONE);
                    new AlertDialog.Builder(myApp)
                            .setTitle("데이터 네트워크 연결상태 확인")
                            .setMessage("인터넷 연결 상태를 확인 후 다시 실행해주시기 바랍니다.")
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataSet.getInstance().isrunning = "false";
                                            DataSet.getInstance().islogin = "false";
                                            DataSet.getInstance().userid = "";
                                            finish();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();

                    break;
                }

                super.onReceivedError(view, errorCode, description, failingUrl);
            }



            @Override
            public void onPageFinished(WebView view, String url) {

                //WebView01.loadUrl("javascript:(function() { " + "document.getElementById('footer').style.display='none';})()");

                if (url.contains("/newmobile/login.jsp")) {
                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    String isAutoLogin = prefs.getString("isAutoLogin", "");
                    String vId = prefs.getString("vId", "");

                    WebView01.loadUrl("javascript:setIsAutoLogin('" + isAutoLogin + "','" + deviceId + "','" + vId + "')");
                    if (isAutoLogin.equals("Y") && !vId.equals("") && !deviceId.equals("")) {
                        WebView01.loadUrl("javascript:appAutoLogin('" + vId + "','" + deviceId + "')");
                    } else {
                        rel_intro.setVisibility(View.GONE);
                        rel_main.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                    }

                } else {
                    SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
                    String sRegId = prefs2.getString("prefGCMRegsterID", null);

                    if (BuildConfig.DEBUG) {
                        Log.d("CHECK", "deviceId : " + deviceId);
                        Log.d("CHECK", "sRegId : " + sRegId);
                        Log.d("CHECK", "userid : " + DataSet.getInstance().userid);
                        Log.d("CHECK", "pushurl : " + DataSet.push_url);
                        Log.d("CHECK", "ModelName : " + Build.MODEL);
                        Log.d("CHECK", "OsVersion : " + Build.VERSION.RELEASE);
                    }



                    WebView01.loadUrl("javascript:setJPPMobileAppId('MCIQ2')");
                    WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
                    WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                    WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                    WebView01.loadUrl("javascript:setJPPUserId('" + DataSet.getInstance().userid + "')");
                    WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                    WebView01.loadUrl("javascript:setJPPModelName('" +Build.MODEL + "')");
                    WebView01.loadUrl("javascript:setJPPDeviceOsVersion('" + Build.VERSION.RELEASE + "')");



                }

                if (url.contains("/newmobile/main.jsp")) {
                    prefsDstPrt = getSharedPreferences("Dstprt", Activity.MODE_PRIVATE);
                    DstprtCode = prefsDstPrt.getString("DstprtCode", "");
                    DstprtName = prefsDstPrt.getString("DstprtName", "");

                    if( DataSet.getInstance().push_id != null && ! DataSet.getInstance().push_id.equals("") && DataSet.getInstance().userid.equals( DataSet.getInstance().recv_id)) {
                        if (DataSet.getInstance().isrunapppush) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("알림")
                                    .setMessage(DataSet.getInstance().msg)
                                    .setPositiveButton(android.R.string.ok,
                                            new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    WebView01.loadUrl("javascript:goPush('"+DataSet.getInstance().push_id+"')");
                                                    DataSet.getInstance().push_id = "";
                                                    DataSet.getInstance().msg = "";
                                                    DataSet.getInstance().isrunapppush = false;
                                                }
                                            })
                                    .setNegativeButton(android.R.string.cancel,
                                            new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DataSet.getInstance().push_id = "";
                                                    DataSet.getInstance().isrunapppush = false;
                                                }
                                            })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }


                checkPermissionF();
                //최초 실행 여부 판단
                SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                boolean first = pref.getBoolean("isFirst", false);
                if(first==false){
                    if (BuildConfig.DEBUG) Log.d("first","THE FIRST TIME");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst",true);
                    editor.commit();
                    DialogHtmlView();
                }
            }
        });


        WebView01.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(myApp)
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                // TODO Auto-generated method stub
                //return super.onJsConfirm(view, url, message, result);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("확인")
                        .setMessage(message)
                        .setPositiveButton("확인",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("취소",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
    }

    public static String getMarketVersion(String packageName) {

        if (BuildConfig.DEBUG) Log.d("CHECK1", packageName);

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).timeout(5000).get();
            //Elements Version = document.select(".content");
            Elements Version = document.select(".htlgb").eq(3);

            for (Element mElement : Version) {
                if (BuildConfig.DEBUG) Log.d("###", mElement.text().trim());
                return mElement.text().trim();
            }
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) Log.d("###",ex.toString());
            return "1.0";
        }
        return null;
    }


    private class AndroidBridge {

        @JavascriptInterface
        public void SendVersion(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "sendVersion(" + arg + ")");

                    String versionName = "";
                    try {
                        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionName = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (BuildConfig.DEBUG) Log.d("CHECK", "versionName(" + versionName + ")");

                    if (arg != null && !arg.equals(versionName)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("확인")
                                .setMessage("새로운버전(" + arg + ")이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mciq2.klnet.co.kr")));
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                }
            });
        }

        //로그인 후 저장
        @JavascriptInterface
        public void SendAppAutoRegister(final String vId, final String vDeviceKey, final String isAutoLogin) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppAutoRegister(" + vId + ","+vDeviceKey+","+isAutoLogin+")");
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("isAutoLogin", isAutoLogin);
                    editor.putString("vId", vId);
                    //editor.putString("vDeviceKey", vDeviceKey);
                    editor.remove("vPassword");

                    editor.commit();
                    DataSet.getInstance().userid = vId;
                    DataSet.getInstance().islogin = "true";


                    SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
                    String sRegId = prefs2.getString("prefGCMRegsterID", null);


                    WebView01.loadUrl("javascript:setJPPMobileAppId('MCIQ2')");
                    WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
                    WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                    WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                    WebView01.loadUrl("javascript:setJPPUserId('" + DataSet.getInstance().userid + "')");
                    WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                    WebView01.loadUrl("javascript:setJPPModelName('" +Build.MODEL + "')");
                    WebView01.loadUrl("javascript:setJPPDeviceOsVersion('" + Build.VERSION.RELEASE + "')");

                    WebView01.loadUrl("javascript:setPush('Y')");

                    //WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.jsp");

                }
            });
        }

        //기기값 전달
        @JavascriptInterface
        public void SendDeviceId() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    WebView01.loadUrl("javascript:fn_setDeviceId('" + deviceId + "')");
                }
            });
        }

        //자동로그인 처리
        @JavascriptInterface
        public void SendAppAutoLoginResult(final String arg, final String arg1) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppAutoLoginResult(" + arg + "," + arg1 + ")");
                    if("success".equals(arg)) {
                        //rel_intro.setVisibility(View.GONE);
                        //rel_main.setVisibility(View.VISIBLE);
                        DataSet.getInstance().userid = arg1;
                        DataSet.getInstance().islogin = "true";

                        SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
                        String sRegId = prefs2.getString("prefGCMRegsterID", null);

                        WebView01.loadUrl("javascript:setJPPMobileAppId('MCIQ2')");
                        WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
                        WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                        WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                        WebView01.loadUrl("javascript:setJPPUserId('" + DataSet.getInstance().userid + "')");
                        WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                        WebView01.loadUrl("javascript:setJPPModelName('" +Build.MODEL + "')");
                        WebView01.loadUrl("javascript:setJPPDeviceOsVersion('" + Build.VERSION.RELEASE + "')");

                        WebView01.loadUrl("javascript:setPush('Y')");

                        WebView01.loadUrl(DataSet.connect_url + "/newmobile/main.jsp");

                    } else {
                        rel_intro.setVisibility(View.GONE);
                        rel_main.setVisibility(View.VISIBLE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                    }

                }
            });
        }


        //환경설정 자동로그인 세팅
        @JavascriptInterface
        public void SendAppConfigSetAutoLogin(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppConfigSetAutoLogin(" + arg + ")");

                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("isAutoLogin", arg);

                    editor.commit();
                }
            });
        }

        //환경설정 대표코드 설정
        @JavascriptInterface
        public void SendAppDstPrtCode(final String arg, final String arg1) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppDstPrtCode(" + arg + "," + arg1 + ")");

                    SharedPreferences.Editor editor = prefsDstPrt.edit();
                    editor.putString("DstprtCode", arg);
                    editor.putString("DstprtName", arg1);

                    editor.commit();

                    DstprtCode = prefsDstPrt.getString("DstprtCode", "");
                    DstprtName = prefsDstPrt.getString("DstprtName", "");
                    WebView01.loadUrl("javascript:setDstprtCode('"+DstprtCode+"', '"+DstprtName+"');");
                }
            });
        }

        //환경설정 대표코드 세팅
        @JavascriptInterface
        public void SendAppDstPrtCodeInit() {
            handler.post(new Runnable() {
                public void run() {
                    WebView01.loadUrl("javascript:fn_pop_setDstprtCode_init('"+DstprtCode+"', '"+DstprtName+"');");
                }
            });
        }

        //웹페이지 이동
        @JavascriptInterface
        public void SendAppGoWebUrl(final String url) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppGoWebUrl("+url+")");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DataSet.connect_url + url));
                    startActivity(intent);
                }
            });
        }


        // 앱링크
        @JavascriptInterface
        public void SendAppLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "sendAppLink(" + arg + ")");
                    if (arg != null) {
                        PackageInfo pi;
                        PackageManager pm = getPackageManager();
                        try {
                            String strAppPackage = arg;
                            pi = pm.getPackageInfo(strAppPackage,  PackageManager.GET_ACTIVITIES);
                            Intent intent = getPackageManager().getLaunchIntentForPackage(strAppPackage);
                            startActivity(intent);
                        }
                        catch (PackageManager.NameNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+arg)));
                        }
                    }
                }
            });
        }


        //로그아웃 이후 처리
        @JavascriptInterface
        public void SendAppLogout(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppLogout(" + arg + ")");
//                    SharedPreferences settings =getSharedPreferences("AutoLogin", Context.MODE_PRIVATE);
//                    settings.edit().clear().commit();
//                    prefsDstPrt.edit().clear().commit();
//                    DstprtCode = "";
//                    DstprtName = "";
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("vDeviceKey", "");
//                    editor.commit();
                    WebView01.loadUrl(DataSet.connect_url + arg);
                }
            });
        }

        //운영,개발 사이트 연결 수정
        @JavascriptInterface
        public void setChangeMode(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "setChangeMode()");
                    if ("D".equals(DataSet.isMode)) {
                        //REAL 사이트
                        DataSet.connect_url = DataSet.connect_real_url;
                        DataSet.push_url = DataSet.push_real_url;
                        DataSet.isMode = "P";
                    } else {
                        //TEST 사이트
                        DataSet.connect_url = DataSet.connect_test_url;
                        DataSet.push_url = DataSet.push_test_url;
                        DataSet.isMode = "D";
                    }
                    WebView01.loadUrl(DataSet.connect_url + arg);
                }
            });
        }

        //세션끊겼을때 자동로그인 처리
        @JavascriptInterface
        public void SendAppAutoReLogin(final String sVersion) {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) ("CHECK", "SendAppAutoReLogin("+sVersion+")");

                    float fVersion=Float.parseFloat(sVersion);
                    PackageManager packageManager = getPackageManager();
                    PackageInfo packageInfo = null;
                    if (BuildConfig.DEBUG) Log.d("###","forceUpdate");
                    try {
                        packageInfo =packageManager.getPackageInfo(getPackageName(),0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String currentVersion = packageInfo.versionName;
                    if (BuildConfig.DEBUG) Log.d("###","currentVersion:"+currentVersion);
                    float fCurrentVersion=Float.parseFloat(currentVersion);
                    if(fVersion > fCurrentVersion) {
                        AlertDialog.Builder alertDialogBuilder =
                                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_DeviceDefault_Light));
                        alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
                                .setMessage("새로운버전("+fVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);

                                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(true);
                        alertDialog.show();
                    } else {
                        SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                        String isAutoLogin = prefs.getString("isAutoLogin", "Y");
                        String vId = prefs.getString("vId", DataSet.getInstance().userid);

                        if (BuildConfig.DEBUG) {
                            Log.d("CHECK", "vId : " + vId + " isAutoLogin : " + isAutoLogin);
                            Log.d("CHECK", "deviceId " + deviceId + " isAutoLogin : " + isAutoLogin);
                            Log.d("CHECK", "DataSet.getInstance().userid : " + DataSet.getInstance().userid);
                            Log.d("CHECK", "DataSet.getInstance().islogin : " + DataSet.getInstance().islogin);
                        }
                        WebView01.loadUrl("javascript:setIsAutoLogin('"+isAutoLogin+"','" + deviceId + "','" + vId + "')");
                        if (DataSet.getInstance().islogin.equals("true") && isAutoLogin.equals("Y") && !vId.equals("") ) {
                            WebView01.loadUrl("javascript:appAutoLogin('" + vId + "','" + deviceId + "')");
                        }
                    }
                }
            });
        }

        //환경설정세팅
        @JavascriptInterface
        public void SendAppInitConfig() {
            handler.post(new Runnable() {
                public void run() {
                    if (BuildConfig.DEBUG) Log.d("CHECK", "SendAppInitConfig()");
                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    String isAutoLogin = prefs.getString("isAutoLogin", "");
                    if ("Y".equals(isAutoLogin)) {
                        WebView01.loadUrl("javascript:setConfigIsAutoLogin('Y');");
                    }

                    //대표코드
                    if(!DstprtCode.equals("")) {
                        WebView01.loadUrl("javascript:setDstprtCode('"+DstprtCode+"', '"+DstprtName+"');");
                    }

                    SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
                    String sRegId = prefs2.getString("prefGCMRegsterID", null);

                    WebView01.loadUrl("javascript:setJPPMobileAppId('MCIQ2')");
                    WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
                    WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                    WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                    WebView01.loadUrl("javascript:setJPPUserId('" + DataSet.getInstance().userid + "')");
                    WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                    WebView01.loadUrl("javascript:setJPPModelName('" +Build.MODEL + "')");
                    WebView01.loadUrl("javascript:setJPPDeviceOsVersion('" + Build.VERSION.RELEASE + "')");

                    WebView01.loadUrl("javascript:onLoadInit();");
                }
            });
        }

//        //환경설정세팅
//        @JavascriptInterface
//        public void SendAppInitConfig() {
//            handler.post(new Runnable() {
//                public void run() {
//                    Log.d("CHECK", "SendAppInitConfig()");
//                    SharedPreferences prefs = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
//                    String isAutoLogin = prefs.getString("isAutoLogin", "");
//                    if ("Y".equals(isAutoLogin)) {
//                        WebView01.loadUrl("javascript:setConfigIsAutoLogin('Y');");
//                    }
//
//                    //대표코드
//                    if(!DstprtCode.equals("")) {
//                        WebView01.loadUrl("javascript:setDstprtCode('"+DstprtCode+"', '"+DstprtName+"');");
//                    }
//
//                    SharedPreferences prefs2 = getSharedPreferences("JPP_GCM_Property", Activity.MODE_PRIVATE);
//                    String sRegId = prefs2.getString("prefGCMRegsterID", null);
//
//                    WebView01.loadUrl("javascript:setJPPMobileAppId('MCIQ2')");
//                    WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
//                    WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
//                    WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
//                    WebView01.loadUrl("javascript:setJPPUserId('" + DataSet.getInstance().userid + "')");
//                    WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
//                    WebView01.loadUrl("javascript:setJPPModelName('" +Build.MODEL + "')");
//                    WebView01.loadUrl("javascript:setJPPDeviceOsVersion('" + Build.VERSION.RELEASE + "')");
//
//                    WebView01.loadUrl("javascript:onLoadInit();");
//                }
//            });
//        }
    }


    private void checkPermissionF() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            // only for LOLLIPOP and newer versions
            if (BuildConfig.DEBUG) Log.d("CHECK","Hello Marshmallow (마시멜로우)");
            int permissionResult = getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                //요청한 권한( WRITE_EXTERNAL_STORAGE )이 없을 때..거부일때...
                /* 사용자가 WRITE_EXTERNAL_STORAGE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                 * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                 */

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("단말기의 파일쓰기 권한이 필요합니다.\n 취소하실 경우 앱이 종료됩니다. \n계속하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= M) {

                                        Log.i("CHECK","감사합니다. 권한을 허락했네요 (마시멜로우)");
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();

                    //최초로 권한을 요청할 때.
                } else {
                    Log.i("CHECK","최초로 권한을 요청할 때. (마시멜로우)");
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }else{
                //권한이 있을 때.
            }

        } else {
            if (BuildConfig.DEBUG) Log.d("CHECK","(마시멜로우 이하 버전입니다.)");
            //   getThumbInfo();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
/*            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/

            for(int i = 0 ; i < permissions.length ; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult WRITE_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }


                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult READ_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }
                }


            }

        } else {
            Log.i("CHECK","onRequestPermissionsResult ( 권한 거부) ");
            Toast.makeText(getApplicationContext(), "요청 권한 거부", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if (isLoginPage) {

                if (System.currentTimeMillis() > LoginBackKeyClickTme + 2000) {

                    LoginBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;

                }

                if (System.currentTimeMillis() <= LoginBackKeyClickTme + 2000) {

                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    this.finish();
                    return true;

                }

            } else if (isMianPage) {

                if (System.currentTimeMillis() > MainBackKeyClickTme + 2000) {

                    MainBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;

                }

                if (System.currentTimeMillis() <= MainBackKeyClickTme + 2000) {

                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    finish();
                    return true;

                }

            } else if(DataSet.getInstance().istext.equals("true")) {

                DataSet.getInstance().istext = "false";
                WebView01.goBack();
                return true;

            }else {

                WebView01.loadUrl("javascript:appUrlBack();");
                return true;
            }

        }

        return super.onKeyDown(keyCode, event);
    }


    public void finishGuide() {
        toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 루팅파일 의심 Path를 가진 파일들을 생성 한다.
     */
    private File[] createFiles(String[] sfiles){
        File[] rootingFiles = new File[sfiles.length];
        for(int i=0 ; i < sfiles.length; i++){
            rootingFiles[i] = new File(sfiles[i]);
        }
        return rootingFiles;
    }

    /**
     * 루팅파일 여부를 확인 한다.
     */
    private boolean checkRootingFiles(File... file){
        boolean result = false;
        for(File f : file){
            if(f != null && f.exists() && f.isFile()){
                result = true;
                break;
            }else{
                result = false;
            }
        }
        return result;
    }

    /**
     * 최초 실행 알림창
     */
    private void DialogHtmlView(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("[필수적 접근 권한] \n" +
                "*인터넷 : 인터넷을 이용한 PLISM 입출항 서비스 접근 \n" +
                "*저장공간 : 기기 사진, 미디어, 파일 액세스 권한으로 다운로드 파일 보관\n" +
                "[선택적 접근 권한] \n" +
                "*푸시알림 : PUSH 알림 서비스");
        ab.setPositiveButton("확인", null);
        AlertDialog title = ab.create();
        title.setTitle("앱 권한 이용 안내");
        title.show();
    }

    public String getHashKey(){
        String hashKey = "";
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("####",e.toString());
            e.printStackTrace();
        }
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);

            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest.");
                return null;
            }
        }
        return hashKey;
    }


    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,this).execute();
    }
    /* 커널 빌드 태그 검사 */
    public boolean kernelBuildTagTest() {

        String buildTags = Build.TAGS;

        if(buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }else {
            return false;
        }
    }
    /* Shell 명령어 실행 가능 여부 */
    public boolean shellComendExecuteCheck() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        private Context context;
        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName()+ "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e("latestversion","---"+latestVersion);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null){
                if(!currentVersion.equalsIgnoreCase(latestVersion)){
                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(new ContextThemeWrapper(this.context, android.R.style.Theme_DeviceDefault_Light));
                    alertDialogBuilder.setTitle("업데이트");alertDialogBuilder
                            .setMessage("새로운버전("+latestVersion+")이 나왔습니다. 업데이트 하시겠습니까?")
                            .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);

                                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    startActivity(intent);
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
}
