package com.rahul.mymusic.Adapter;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.NotificationCompat.Builder;

import com.rahul.mymusic.Helper.Constants;
import com.rahul.mymusic.Helper.Information;
import com.rahul.mymusic.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Rahul on 10 Apr 2016.
 */
public class YoutubeRecyclerAdapter extends RecyclerView.Adapter<YoutubeRecyclerAdapter.CustomViewHolder>{
    private List<Information> list;
    private Context context;
    private LayoutInflater inflater;
    private static boolean longPressed = false, validServer_1 = false, playMusic = false;
    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    int id = 1;
    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    ProgressDialog progressDialog;

    public YoutubeRecyclerAdapter(Context context, List<Information> list){
        progressDialog = new ProgressDialog(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Information information = list.get(position);
        holder.title.setText(information.title);
        holder.duration.setText(information.duration);
//        Picasso.with(context).load(information.image).placeholder(R.drawable.placeholder).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longPressed = false;
                playMusic = false;
                showDialogOption(Constants.VIDEO_TO_MP3_SERVER_2 + information.link
                        .substring(information.link.indexOf("=") + 1), information.link, information.title, Constants.VIDEO_TO_MP3_SERVER_1 + information.link);
//                new ResponseAsyncTaskForServer2().execute(Constants.VIDEO_TO_MP3_SERVER_2 + information.link
//                        .substring(information.link.indexOf("=") + 1), information.link, information.title);
            }
        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                longPressed = true;
//                showDialogOption(Constants.VIDEO_TO_MP3_SERVER_2 + information.link
//                        .substring(information.link.indexOf("=") + 1), information.link, information.title, Constants.VIDEO_TO_MP3_SERVER_1 + information.link);
//                return true;
//            }
//        });
//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playMusic = true;
//                new ResponseAsyncTaskForServer2().execute(Constants.VIDEO_TO_MP3_SERVER_2 + information.link
//                        .substring(information.link.indexOf("=") + 1), information.link);
//            }
//        });
    }

    private void checkForServer_1(String... params) {
        new ValidityOfServer_1().execute(params);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView title, duration;
        ImageView image;
        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);
            image = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }

    public class ResponseAsyncTaskForServer2 extends AsyncTask<String, Void, String> {

        String youtubeLink = "";
        String fileName = "";

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("enter response", longPressed + params.toString());
            youtubeLink = params[1];
            if (longPressed)
                fileName = params[2];
            try {
                Document doc  = Jsoup.connect(params[0])
                    .timeout(0)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                    .get();
                return doc.body().ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String responseLink) {
            progressDialog.hide();
            Log.d("enter response post", longPressed + responseLink);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (URLUtil.isValidUrl(responseLink)) {
                if(playMusic)
                    playMusic(responseLink);
                else if (longPressed)
                    startDownloadManager(responseLink, fileName);
                else {
                    i.setData(Uri.parse(responseLink));
                    context.startActivity(i);
                }

//                    mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    mBuilder = new NotificationCompat.Builder(context);
//                    mBuilder.setContentTitle(fileName)
//                            .setContentText("Download in progress")
//                            .setSmallIcon(android.R.drawable.stat_sys_download);
//                    new Downloader().execute(Constants.VIDEO_TO_MP3_SERVER_1 + youtubeLink, fileName);

            }
            // TRY WITH SERVER 1
            else {
                i.setData(Uri.parse(Constants.VIDEO_TO_MP3_SERVER_1 + youtubeLink));
                if (playMusic)
                    playMusic(Constants.VIDEO_TO_MP3_SERVER_1 + youtubeLink);
                else context.startActivity(i);
            }
        }
    }

    public class ValidityOfServer_1 extends AsyncTask<String, Void, String[]>{

        String fileName = "";
        @Override
        protected String[] doInBackground(String... params) {
            Log.d("enter validity 1", params[2]);
            fileName = params[2];
            try {
                URL url = new URL(params[3]);
                String cookie = CookieManager.getInstance().getCookie(params[3]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Cookie", cookie);
                con.setRequestMethod("HEAD");
                con.setInstanceFollowRedirects(true);
                con.connect();

                validServer_1 = con.getContentLength() >= 100000;

                Log.d("getContentLength", con.getContentLength()+" "+validServer_1+"");
            }catch (Exception e){
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(String... params) {
            Log.d("enter validity post", validServer_1+"");
            progressDialog.hide();
            if (validServer_1) {
                if (playMusic)
                    playMusic(params[3]);
                else
                    startDownloadManager(params[3], fileName);
            }
            else {
                new ResponseAsyncTaskForServer2().execute(params);
            }
        }
    }

    private void startDownloadManager(String... params) {
        Log.d("file name download",params[1]+ params[0]);
        Toast.makeText(context, "Start downloading ...", Toast.LENGTH_SHORT).show();
        mgr = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri download_uri = Uri.parse(params[0]);
        lastDownload =
                mgr.enqueue(new DownloadManager.Request(download_uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setDescription("Song")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, params[1]+".mp3"));

        context.registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        context.registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
    }

    private void copyToClipBoard(String responseLink) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        clipboard.setText(responseLink);
        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private class Downloader extends AsyncTask<String, Float, Integer> {

        int fileLength = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Displays the progress bar for the first time.
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            // Update progress
            if (fileLength == -1) {
                mBuilder.setProgress(100, 100, true);
                mBuilder.setContentText("Download in progress : " + String.format("%.2f\n", values[0]) + "MB");
            }
            else {
                mBuilder.setProgress(100, values[0].intValue(), false);
                mBuilder.setContentText("Download in progress : " + values[0].intValue() + "%");
            }
            mNotifyManager.notify(id, mBuilder.build());
            super.onProgressUpdate(values[0]);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Log.d("link", params[0]);
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(0);
                connection.connect();
                fileLength = connection.getContentLength();
                File root = new File(Environment.getExternalStorageDirectory(), "MyMusic");
                if (!root.exists())
                    root.mkdirs();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream( root +"/" +params[1]+".mp3");

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                int prevC = 0;
                float prevCUnknownFileLength = 0f;
                while ((count = input.read(data)) != -1) {
                    total += count ;
                    if(fileLength != -1) {
                        if (prevC != (int) (total * 100 / fileLength)) {
                            publishProgress((total * 100f / fileLength));
                            prevC = (int) (total * 100 / fileLength);
                        }
                    }else {
                        float temp = (total)/(1024f*1024);
                        if(prevCUnknownFileLength +0.01 < temp)
                            publishProgress(temp);
                        prevCUnknownFileLength = temp;
                    }
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mBuilder.setContentText("Download complete");
            mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dm);
        }
    };

    public void showProgressDialog(){
        if (progressDialog.isShowing())
            progressDialog.hide();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait ...");
        progressDialog.setMessage("Connecting to server ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void playMusic(String url){
        Uri myUri = Uri.parse(url);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(myUri, "audio/*");
        context.startActivity(intent);
    }

    public void showDialogOption(final String... params){
        CharSequence options[] = new CharSequence[] {"✏  Play", "✏  External downloader", "✏  Inbuilt downloader"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        playMusic = true;
                        longPressed = true;
                        showProgressDialog();
                        checkForServer_1(params);
//                        new ResponseAsyncTaskForServer2().execute(params);
                        break;
                    case 1:
                        playMusic = false;
                        longPressed = false;
                        new ResponseAsyncTaskForServer2().execute(params);
                        break;
                    case 2:
                        playMusic = false;
                        longPressed = true;
                        showProgressDialog();
                        checkForServer_1(params);
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
