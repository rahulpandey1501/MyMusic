package com.rahul.mymusic.Adapter;

import android.app.DownloadManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rahul.mymusic.BollywoodDownloadActivity;
import com.rahul.mymusic.Helper.Constants;
import com.rahul.mymusic.Helper.BollywoodDetailInformation;
import com.rahul.mymusic.Helper.Information;
import com.rahul.mymusic.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rahul on 10 Apr 2016.
 */
public class BollywoodRecylerAdapter extends RecyclerView.Adapter<BollywoodRecylerAdapter.CustomViewHolder>{
    private List<Information> list;
    private Context context;
    private LayoutInflater inflater;
    ProgressDialog progressDialog;
    static BollywoodDetailInformation detail;
    private long lastDownload=-1L;
    boolean fromActivity;

    public BollywoodRecylerAdapter(Context context, List<Information> list, boolean fromActivity){
        progressDialog = new ProgressDialog(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.fromActivity = fromActivity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (fromActivity)
            view = inflater.inflate(R.layout.bollywood_recycler_list, parent, false);
        else
            view = inflater.inflate(R.layout.album_recycler_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Information information = list.get(position);
        holder.title.setText(information.title);
        if (fromActivity) {
            holder.artist.setText(information.artist);
        }
        if (information.image != null && information.image.contains("logo.png"))
            information.image  = null;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromActivity) {
                    new FetchDetailAsyncTask().execute(information.link, information.title);
                }else{
                    if (information.link.toLowerCase().contains(".mp3")){
                        showDialogOption(information.link, information.title);
                    }else {
                        new ExtractLinkAsyncTask().execute(information.link, information.title);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView title, artist;
        ImageView image;
        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image_view);
            artist = (TextView) itemView.findViewById(R.id.duration);
        }
    }

    public class FetchDetailAsyncTask extends AsyncTask<String, Void, String> {

        String title = "Movie";
        boolean exception = false;
        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            title = params[1];
            try {
                detail = new BollywoodDetailInformation();
                detail.albumDetail = new HashMap<>();
                detail.artist = new HashMap<>();
                detail.downloadList = new ArrayList<>();
                Document doc  = Jsoup.connect(params[0])
                        .timeout(0)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                        .get();

                Elements ele = doc.getElementsByClass("sngs-dtls-inr");
                detail.image = ele.first().select("div.mvi_img").select("img").attr("src");
                ele = ele.first().getElementsByClass("movi_dtls_txt").select("li");

                for (Element e : ele){
                    if (notInIgnoreList(e.select("label").text().toLowerCase())) {
                        String temp = e.select("label").text();
                        if (temp.toLowerCase().contains("movie name"))
                            temp = "Movie :";
                        else temp = temp.replace("Movie ", "").replace("Name", "");
                        detail.albumDetail.put(temp, e.select("span").text());
                    }
                }

                ele = doc.getElementsByClass("download-trget");
                for (Element e : ele.first().select("article")){
                    Information dlk = new Information();
                    dlk.link = e.select("a").attr("href");
                    dlk.title = e.select("a").text();
                    dlk.artist = e.select("span.artist_name").text();
                    for (Element artist :  e.select("span.artist_name").select("a")){
                        detail.artist.put(artist.text(), artist.attr("href"));
                    }
                    detail.downloadList.add(dlk);
                }

            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }

        private boolean notInIgnoreList(String label) {
            for (String il : Constants.IGNORE_LIST){
                if (label.contains(il))
                    return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(String responseLink) {
            Intent intent = new Intent(context, BollywoodDownloadActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("details", detail);
            intent.putExtra("title", title);
            if(!exception)
                context.startActivity(intent);
            else Toast.makeText(context, "Content not found", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
        }
    }

    public class ExtractLinkAsyncTask extends AsyncTask<String, Void, String>{

        String fileName = "";
        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                fileName = params[1];
                Document doc= Jsoup.connect(params[0])
                        .timeout(0)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                        .get();
                return doc.getElementsByClass("download-trget").select("a").attr("href");

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String link) {
            Log.d("link",link);
            progressDialog.hide();
            showDialogOption(link, fileName);
            super.onPostExecute(link);
        }
    }

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
        final List<String> tempList = new ArrayList<>();
        tempList.add("Play");
        tempList.add("External download");
        tempList.add("Internal download (Exp)");
        Log.d("artist dialog", detail.artist.size() + "");
        tempList.add("<<---- Something more for you ---->>");
        for (String key : detail.artist.keySet()){
            Log.d("artist dialog", key+detail.artist.get(key));
            tempList.add(key);
        }
        final CharSequence options[] = new CharSequence[tempList.size()];
        tempList.toArray(options);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        playMusic(params[0]);
                        break;
                    case 1:
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setData(Uri.parse(params[0]));
                        context.startActivity(i);
                        break;
                    case 2:
                        startDownloadManager(params);
                        break;
                    case 3:
                        break;
                    default:
                        new FetchDetailAsyncTask().execute(detail.artist.get(tempList.get(which)), options[which].toString());
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startDownloadManager(String... params) {
        Log.d("file name download",params[1]+ params[0]);
        Toast.makeText(context, "Start downloading ...", Toast.LENGTH_SHORT).show();
        DownloadManager mgr = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
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
}
