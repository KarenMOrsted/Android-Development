    package Fann.fannmusic;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

    public class DownloadService extends IntentService {
        public static final int PROGRESS=2;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null)
        {
            String savePath=intent.getStringExtra("savePath");
            String downloadUrl=intent.getStringExtra("downloadUrl");
            String songName=intent.getStringExtra("songInfo");
          //  System.out.println(downloadUrl+"----"+savePath);
            int count;
            File file=new File(savePath);//传入的参数第二个为存储路径

            try{
                URL url=new URL(downloadUrl);//第一个为下载路径
                URLConnection connection=url.openConnection();
                connection.connect();
                int contentLength= connection.getContentLength();
                InputStream input=new BufferedInputStream(url.openStream());
                OutputStream output=new FileOutputStream(file);

                byte[] buffer=new byte[1024];
                long total=0;

                while ((count = input.read(buffer)) != -1) {

                    total += count;
                    int progress=(int) (100 * (total / (double) contentLength));

                    //获取从 Activity 传入的 Messenger信使
                    Messenger messenger = (Messenger) intent.getExtras().get("ProgressMessenger");

                    //新建消息，设置下载进度参数
                    Message msg = new Message();
                    msg.what=PROGRESS;
                    Bundle bundle = new Bundle();
                    bundle.putInt("PROGRESS", progress);
                    bundle.putString("SONGINFO",songName);
                    msg.setData(bundle);
                    try {
                        //通过信使回复消息 将下载进度和信息发送给活动的主线程用于更新进度条
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    output.write(buffer, 0, count);
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }



}