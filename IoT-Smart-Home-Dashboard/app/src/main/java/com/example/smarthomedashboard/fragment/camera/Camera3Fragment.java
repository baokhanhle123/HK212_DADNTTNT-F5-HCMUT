package com.example.smarthomedashboard.fragment.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthomedashboard.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera3Fragment extends Fragment {

    // Declare
    private boolean camAvailable = false;
    private TextView textView;

    private String camUrl = "";
    private String camName = "";
    private String camInfo = "";

    private WebView cam;
    private ProgressBar progressBar;
    private TextView txtNoti;

    private ImageButton captureButton;
    private ImageButton infoButton;
    private ImageButton refreshButton;
    private ImageButton shareButton;
    private ImageButton settingButton;

    private Dialog myDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera3, container, false);

        cam = (WebView) view.findViewById(R.id.cam);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        txtNoti = (TextView) view.findViewById(R.id.txtNoti);

        cam.setWebViewClient(new WebViewClient());
        cam.loadUrl(camUrl);
        //enable JavaScript
        WebSettings webSettings = cam.getSettings();
        webSettings.setJavaScriptEnabled(true);

        cam.setWebViewClient(new WebViewClient() {
            //Method control page start + page finish functionality..
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //set progressBar when page loading is start...
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                getActivity().setTitle(view.getTitle());
                super.onPageFinished(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(getActivity(), "Your Internet Connection may not be active Or " + error.getDescription(), Toast.LENGTH_LONG).show();
                super.onReceivedError(view, request, error);
            }
        });

        captureButton = view.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSave();
            }
        });

        infoButton = view.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSharePopup(view);
            }
        });

        refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cam.loadUrl(camUrl);
                connectionStatus();
            }
        });

        myDialog = new Dialog(getContext());
        shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSharePopup(view);
            }
        });

        settingButton = view.findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPopup();
            }
        });

        setState();

        return view;
    }

    //Capture image support function:
    public static Bitmap viewToBitMap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void startSave() {
        FileOutputStream fileOutputStream = null;
        File file = getDisc();

        if (!file.exists() && !file.mkdirs()) {
            Toast.makeText(getContext(), "Can't create directory to save Image", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img" + date + ".jpg";
        String file_name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);
        try {
            fileOutputStream = new FileOutputStream(new_file);
            Bitmap bitmap = viewToBitMap(cam, cam.getWidth(), cam.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(getContext(), "Save image success", Toast.LENGTH_LONG).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        //sendBroadcast(intent);
    }

    public File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Image Demo");
    }

    //Refresh support function
    public void connectionStatus() {
        boolean check = checkConnection();

        if (check == true) {
            Toast.makeText(getActivity(), "Internet is Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Failed to connect to internet.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkConnection() {
        ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();

        final boolean connected = networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected();

        if (!connected) {
            return false;
        }
        return true;
    }

    //Share support function:
    public void showSharePopup(View v) {
        ImageView popup_qrCode;
        TextView txtclose;
        TextView txtCamName;
        TextView txtCamInfo;
        TextView txtCamUrl;
        Button btnExternalShare;

        myDialog.setContentView(R.layout.fragment_camera_share_popup);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        txtCamName = (TextView) myDialog.findViewById(R.id.popup_camName);
        txtCamName.setText(camName);
        txtCamInfo = (TextView) myDialog.findViewById(R.id.popup_camInfo);
        txtCamInfo.setText(camInfo);
        txtCamUrl = (TextView) myDialog.findViewById(R.id.popup_camUrl);
        txtCamUrl.setText(camUrl);

        popup_qrCode = myDialog.findViewById(R.id.popup_qrCode);
        generateQrCode(popup_qrCode, camUrl);

        //btnExternalShare = (Button) myDialog.findViewById(R.id.btnExternalShare);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    //Setting support function:
    public void showSettingPopup() {
        final int gravity = Gravity.CENTER;
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_camera_setting_popup);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //dialog location
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        EditText editCamName = dialog.findViewById(R.id.edit_camName);
        EditText editCamInfo = dialog.findViewById(R.id.edit_camInfo);
        EditText editCamUrl = dialog.findViewById(R.id.edit_camUrl);
        ImageButton btnQrScanner = dialog.findViewById(R.id.btn_qrScanner);
        Button btnGoBack = dialog.findViewById(R.id.btn_go_back);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);

        editCamName.setText(camName);
        editCamInfo.setText(camInfo);
        editCamUrl.setText(camUrl);

        btnQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize intent integerator
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                //set prompt text
                intentIntegrator.setPrompt("For flash use volume up key");
                //Set beep
                intentIntegrator.setBeepEnabled(true);
                //Locked oriented
                intentIntegrator.setOrientationLocked(true);
                //Set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);
                //Initiate scan
                intentIntegrator.forSupportFragment(Camera3Fragment.this).initiateScan();
                cam.loadUrl(camUrl);
                setState();
                dialog.dismiss();
            }
        });
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setTitle("Confirm change")
                        .setMessage("Are you sure you want to change this setting? You cannot revert the change!")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Commit to change setting
                                camName = editCamName.getText().toString();
                                camInfo = editCamInfo.getText().toString();
                                camUrl = editCamUrl.getText().toString();
                                setState();
                                Toast.makeText(getActivity(), "Confirm change", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setTitle("Confirm change")
                        .setMessage("Are you sure you want to delete this camera? You cannot revert the change!")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Commit to change setting
                                camName = "";
                                camInfo = "";
                                camUrl = "";
                                editCamName.setText(camName);
                                editCamInfo.setText(camInfo);
                                editCamUrl.setText(camUrl);
                                setState();
                                Toast.makeText(getActivity(), "Confirm change", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        dialog.show();
    }

    //QrCode Generator support function
    public void generateQrCode(ImageView barcode, String data) {
        String data_in_code = data;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data_in_code, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //QrCode Scanner support function
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //Check condition
        if (intentResult.getContents() != null) {
            camUrl = intentResult.getContents();
            Toast.makeText(getContext(), intentResult.getContents(), Toast.LENGTH_LONG);
        } else {
            //When result content is null
            //Display toast
            Toast.makeText(getContext(), "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    //Set visibility
    public void setState() {
        if (camName.isEmpty() && camInfo.isEmpty() && camUrl.isEmpty()) {
            camAvailable = false;
            cam.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            txtNoti.setVisibility(View.VISIBLE);
        } else {
            camAvailable = true;
            cam.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            txtNoti.setVisibility(View.GONE);
        }
    }
}