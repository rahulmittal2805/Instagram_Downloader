package com.example.instagramdownloader.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.instagramdownloader.Activity.ImageShowActivity;
import com.example.instagramdownloader.Activity.ImageShowSliderActivity;
import com.example.instagramdownloader.Activity.InstaWebviewActivity;
import com.example.instagramdownloader.R;
import com.example.instagramdownloader.model.ResponseModel;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.CLIPBOARD_SERVICE;


public class HomeFragments extends Fragment {
    private View rootView;
    LinearLayout llPasteLink, llDownloadButton;
    EditText etPasteLink;
    String url = "";
    String newurl;
    String key = null;
    SharedPreferences preferences ;

    DonutProgress donut_progress;

    CardView CardViewData;
    AlertDialog.Builder alertdialog;

    private Context mContext;

    static ProgressDialog mprogressDialog;
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;


    ImageView ivproductImage;
    TextView tvUsername, tvCaption;
    CircleImageView ivProfilePic;

    String imgPath;
    String videoPath;
    String profilePath;
    String contentType;
    JSONObject jobj;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            initialiseView();
            return rootView;
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            return rootView;
        }
    }

    public void initialiseView() {
        CardViewData = rootView.findViewById(R.id.CardViewData);
        llPasteLink = rootView.findViewById(R.id.llPasteLink);
        llDownloadButton = rootView.findViewById(R.id.llDownloadButton);
        donut_progress = rootView.findViewById(R.id.donut_progress);

        etPasteLink = rootView.findViewById(R.id.etPasteLink);

        ivproductImage = rootView.findViewById(R.id.main_image);
        ivProfilePic = rootView.findViewById(R.id.ivProfilePic);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvCaption = rootView.findViewById(R.id.comment);
        preferences=getActivity().getSharedPreferences(getActivity().getPackageName(),Context.MODE_PRIVATE);


        llPasteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String clipURL = item.getText().toString();
                etPasteLink.setText(clipURL);

            }
        });


        llDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            checkUrl();
                        }


                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                }).check();


            }
        });
    }


    public void checkUrl() {
        url = etPasteLink.getText().toString();
        System.out.println(url);
        if (url.equals("")) {

        } else if (url.startsWith("https://www.instagram.com/p")) {

            if (url.substring(url.indexOf("p/") + 2, url.indexOf("/?")).length() == 11) {
                //Link is public

                Toast.makeText(getActivity(), "This is public link", Toast.LENGTH_LONG).show();
                newurl = url.substring(0, 39);
                newurl = newurl + "/?__a=1";
                Log.d("myurl", newurl);
                mRequestForGetData(newurl);
            } else {
                System.out.println(preferences.getBoolean("private_session",false));
                Toast.makeText(getActivity(), "This is Private link", Toast.LENGTH_LONG).show();
                System.out.println("Your link in private please login to instagram");
                etPasteLink.setText(null);

                if (preferences.getBoolean("private_session",false)){
                    newurl = url.substring(0, 67);
                    newurl = newurl + "/?__a=1";
                    mRequestForPrivatGetData(newurl);
                }else{ displayAlertBox();}

            }
        } else {
            Toast.makeText(getActivity(), "This is Invalid Instagram Link", Toast.LENGTH_LONG).show();
        }
    }
    public void mRequestForGetData(String newurl1) {


        mprogressDialog = ProgressDialog.show(getActivity(), null, "Loading...");
        mprogressDialog.setCanceledOnTouchOutside(false);
        mprogressDialog.setCancelable(true);

        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, newurl1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response, ResponseModel.class);
                    String aa = responseModel.getGraphql().getShortcode_media().get__typename();
                    Log.d("typename", aa);
                    getDataonCardView(responseModel);
                    mprogressDialog.dismiss();
                    etPasteLink.setText(null);
                    new DownloadFileFromURL().execute(responseModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Message", error.toString());
                mprogressDialog.dismiss();
                etPasteLink.setText(null);
            }
        });
        mRequestQueue.add(mStringRequest);


    }

    public void mRequestForPrivatGetData(String newurl1) {


        mprogressDialog = ProgressDialog.show(getActivity(), null, "Loading...");
        mprogressDialog.setCanceledOnTouchOutside(false);
        mprogressDialog.setCancelable(true);

        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, newurl1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response, ResponseModel.class);
                    String aa = responseModel.getGraphql().getShortcode_media().get__typename();
                    Log.d("typename", aa);
                    getDataonCardView(responseModel);
                    mprogressDialog.dismiss();
                    etPasteLink.setText(null);
                    new DownloadFileFromURL().execute(responseModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Message", error.toString());
                mprogressDialog.dismiss();
                etPasteLink.setText(null);
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<String, String>();
                System.out.println("Session id========"+  preferences.getString("sessionid",""));
                params.put("Cookie",preferences.getString("sessionid",""));
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);


    }

    //Logic for show data on card view
    public void getDataonCardView(final ResponseModel data) {

        CardViewData.setVisibility(View.VISIBLE);
        tvUsername.setText(data.getGraphql().getShortcode_media().getOwner().getUsername());


        tvCaption.setText(data.getGraphql().getShortcode_media().getEdge_media_to_caption().getEdges().get(0).getNode().getText());

        imgPath = data.getGraphql().getShortcode_media().getDisplay_url();
        System.out.println(imgPath);
        Picasso.get().load(imgPath).into(ivproductImage);
        videoPath = data.getGraphql().getShortcode_media().getVideo_url();

        System.out.println(videoPath);


        contentType = data.getGraphql().getShortcode_media().get__typename();
        System.out.println(contentType);


        profilePath = data.getGraphql().getShortcode_media().getOwner().getProfile_pic_url();
        System.out.println(profilePath);
        Picasso.get().load(profilePath).into(ivProfilePic);
        CardViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentType.equals("GraphImage")) {
                    Intent intent = new Intent(getActivity(), ImageShowActivity.class);
                    intent.putExtra("image_url", imgPath);
                    intent.putExtra("Content_Type", contentType);
                    getActivity().startActivity(intent);
                } else if (contentType.equals("GraphSidecar")) {


                    Intent intent1 = new Intent(mContext, ImageShowSliderActivity.class);
                    intent1.putExtra("image_slider_list", data);

                    mContext.startActivity(intent1);

                } else if (contentType.equals("GraphVideo")) {
                    Intent intent = new Intent(getActivity(), ImageShowActivity.class);
                    intent.putExtra("image_url", imgPath);
                    intent.putExtra("viedo_url", videoPath);
                    intent.putExtra("Content_Type", contentType);

                    getActivity().startActivity(intent);
                }
            }
        });


    }


    //Asynk task for downloading image or video

    class DownloadFileFromURL extends AsyncTask<ResponseModel, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            donut_progress.setVisibility(View.VISIBLE);
            donut_progress.setText("0%");
            donut_progress.setDonut_progress("0");
            donut_progress.setMax(100);

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(ResponseModel... data) {

            //File destinationFile = new File(Environment.getDownloadCacheDirectory() + "/InstaGram Downloader");
            File destinationFile = new File("/storage/emulated/0/Instagram App");
            System.out.println(destinationFile.toString());
            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            int count;
            ResponseModel data1 = data[0];

            String contentType = data1.getGraphql().getShortcode_media().get__typename();

            String imagePath = data1.getGraphql().getShortcode_media().getDisplay_url();

            String videoPath = data1.getGraphql().getShortcode_media().getVideo_url();
//Image Download Logic
            if (contentType.equals("GraphImage")) {

                try {
                    URL url = new URL(imagePath);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                    // Output stream to write file


                    OutputStream output = new FileOutputStream(destinationFile + File.separator + "Image_" + formatter.format(new Date()) + ".jpg");

                    byte data2[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data2)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data2, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            } else if (contentType.equals("GraphVideo")) {
                try {
                    URL url = new URL(videoPath);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                    // Output stream to write file


                    OutputStream output = new FileOutputStream(destinationFile + File.separator + "video_" + formatter.format(new Date()) + ".mp4");

                    byte data3[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data3)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data3, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            } else if (contentType.equals("GraphSidecar")) {

               
                int length = data1.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().size();
                System.out.println("Length is =============" + length);

                for (int i = 0; i < length - 1; i++) {

                    String content_type = data1.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(i).getNode().get__typename();
                    String image_path = data1.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(i).getNode().getDisplay_url();
                    String video_path = data1.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(i).getNode().getVideo_url();
                    if (content_type.equals("GraphImage")) {
                        try {
                            URL url = new URL(image_path);
                            URLConnection conection = url.openConnection();
                            conection.connect();
                            // getting file length
                            int lenghtOfFile = conection.getContentLength();

                            // input stream to read file - with 8k buffer
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);
                            Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                            // Output stream to write file


                            OutputStream output = new FileOutputStream(destinationFile + File.separator + "Image_" + formatter.format(new Date()) + ".jpg");

                            byte data3[] = new byte[1024];

                            long total = 0;

                            while ((count = input.read(data3)) != -1) {
                                total += count;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                                // writing data to file
                                output.write(data3, 0, count);
                            }

                            // flushing output
                            output.flush();

                            // closing streams
                            output.close();
                            input.close();

                        } catch (Exception e) {
                            Log.e("Error: ", e.getMessage());
                        }
                    } else if (content_type.equals("GraphVideo")) {
                        try {
                            URL url = new URL(video_path);
                            URLConnection conection = url.openConnection();
                            conection.connect();
                            // getting file length
                            int lenghtOfFile = conection.getContentLength();

                            // input stream to read file - with 8k buffer
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);
                            Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
                            // Output stream to write file


                            OutputStream output = new FileOutputStream(destinationFile + File.separator + "Video_" + formatter.format(new Date()) + ".mp4");

                            byte data3[] = new byte[1024];

                            long total = 0;

                            while ((count = input.read(data3)) != -1) {
                                total += count;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                                // writing data to file
                                output.write(data3, 0, count);
                            }
                            // flushing output
                            output.flush();

                            // closing streams
                            output.close();
                            input.close();

                        } catch (Exception e) {
                            Log.e("Error: ", e.getMessage());
                        }
                    }
                }
            }

            return null;
        }


        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            donut_progress.setText(progress[0] + "%");
            donut_progress.setDonut_progress(progress[0]);

        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {

            donut_progress.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Post Saved", Toast.LENGTH_LONG).show();
        }
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void displayAlertBox() {
        alertdialog = new AlertDialog.Builder(getActivity());
        alertdialog.setMessage("This app require you to login in your instagram account in order to download video and your data will be safe.")
                .setCancelable(false).setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getActivity(), "you choose yes action for alertbox",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), InstaWebviewActivity.class);
                startActivityForResult(i, 1);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertdialog.create();
        //Setting the title manually
        alert.setTitle("Private Link Detected");
        alert.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            newurl = url.substring(0, 67);
            newurl=newurl + "/?__a=1";
            System.out.println(newurl);

            mRequestForPrivatGetData(newurl);
        }
    }
}
