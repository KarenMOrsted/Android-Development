package Fann.fannmusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class SearchSong extends AppCompatActivity {
    private ImageButton searchButton;
    private EditText searchText;
    private TextView songView;
    private ListView searchResult;
    private ProgressBar progressBar;
    private TextView songDownloading;
    public static final int LOGIN=0;
    public static final int SONG_INFO=1;
    public static final int PROGRESS=2;
    private List<View> viewList=new ArrayList<>();
    private List<Integer> posList=new ArrayList<>();
    private SongDB dbHelper=new SongDB(this,"Song.db",null,1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);
        initButton();
        initSongView();
        initSearchText();
        login();

    }


    //初始化搜索框
    public void initSearchText()
    {
        searchText=findViewById(R.id.searchText);
        searchText.setEnabled(false);
        //searchText.setFocusable(false);
    }
    //初始化按钮
    public void initButton()
    {
        searchButton=findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v->{
            //保证输入不为空
            if(searchText.getText().toString().isEmpty())
                return;
            //点击搜索按钮后获取歌曲信息
            getInfo(searchText.getText().toString());
        });

    }

    //初始化歌曲展示界面
    public void initSongView()
    {
        songView=findViewById(R.id.songView);
        songView.setText("正在连接....");
    }

    //用于网络服务的函数
    public void HttpService(String Url,int what)
    {
        new Thread(() -> {
            HttpURLConnection connection=null;
            try{
                URL url=new URL(Url);//从制定url下载数据
                connection=(HttpURLConnection)url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                InputStream in=connection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                StringBuilder response=new StringBuilder();
                String line;
                while((line=reader.readLine())!=null)
                    response.append(line);

                Message message=new Message();
                message.what=what;//向主线程发送当前线程的信息
                message.obj=response.toString();
                handler.sendMessage(message);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //登陆函数
    public void login()
    {
        songView.setText("正在连接...");
        HttpService("https://netease-cloud-music-api-beige-nine.vercel.app/login/cellphone?phone=15879638062&password=gxm751216",LOGIN);
    }

    //通过歌名、歌手搜索函数
    public void getInfo(String name)
    {
        HttpService("https://netease-cloud-music-api-beige-nine.vercel.app/search?keywords="+name+"&limit=10",SONG_INFO);
    }

    //获取线程发送的信息对UI进行更新以及进行对应操作
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch ((msg.what)){
                case LOGIN:
                    songView.setText("已连接");
                    searchText.setEnabled(true);
                    //只有在登陆成功后才会去调用别的功能
                    break;
                case SONG_INFO://查询任务
                    String response=(String)msg.obj;
                    try {
                        setResult(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PROGRESS://显示下载进度
                    progressBar=findViewById(R.id.progress);
                    progressBar.setVisibility(View.VISIBLE);
                    songDownloading=findViewById(R.id.songDownloading);
                    songDownloading.setVisibility(View.VISIBLE);
                    //获得从下载服务发送的下载进度数据和下载歌曲信息
                    Bundle bundle=msg.getData();
                    songDownloading.setText(bundle.getString("SONGINFO"));
                    progressBar.setProgress(bundle.getInt("PROGRESS"));
                    //System.out.println(bundle.getInt("PROGRESS"));

                    if(bundle.getInt("PROGRESS")==100)
                    {
                        progressBar.setVisibility(View.GONE);
                        songDownloading.setVisibility(View.GONE);
                    }
                    break;
                default:break;

            }
        }
    };


    //显示搜索结果
    public void setResult(String result) throws JSONException {
        List<Song> songResult=new ArrayList<>();
        songView.setVisibility(View.GONE);

        //解析获得的歌曲JSON数据
        JSONObject jsonObject=new JSONObject(result);
        JSONArray songs=jsonObject.getJSONObject("result").getJSONArray("songs");//搜索所得的歌
        for(int i=0;i<songs.length();i++)
        {
            JSONObject songInfo =songs.getJSONObject(i);
            String id=songInfo.getString("id");//获取歌曲的id
            String name=songInfo.getString("name");//获取歌曲名
            JSONArray artists=songInfo.getJSONArray("artists");//获取作者数组
            String author="";
            //遍历作者数组 获得作者信息
            for(int j=0;j<artists.length();j++)
            {
                JSONObject artistInfo=artists.getJSONObject(j);
                if(j==0)
                    author+=artistInfo.getString("name");
                else
                    author+=","+artistInfo.getString("name");
            }
            String duration=songInfo.getString("duration");//获取歌曲时长
            songResult.add(new Song(name,author,id,duration));//加入搜索结果列表
        }

        //将搜索歌曲结果显示
        searchResult=findViewById(R.id.searchResult);
        searchResult.setVisibility(View.VISIBLE);
        SongAdapter adapter=new SongAdapter(SearchSong.this,R.layout.song_item,songResult);
        searchResult.setAdapter(adapter);

        //点击歌曲后开始下载
        searchResult.setOnItemClickListener((parent, view, position, id)->{
            Song song=(Song)adapter.getItem(position);
            startDownload(song);
           // view.setVisibility(View.GONE);

            save2DB(song);//将下载的歌曲信息保存到数据库
        });



    }

    //启动下载的函数
    public void startDownload(Song song)
    {
        Intent intent=new Intent(this,DownloadService.class);//下载服务的intent
        String path=getFilesDir().getAbsoluteFile().toString()+"/"+song.getName()+"-" +song.getAuthor()+".mp3";
        String url="http://music.163.com/song/media/outer/url?id="+song.getId()+".mp3";//使用外链链接下载 获得id即可下载

        //将下载URL、存储路径、歌曲信息、信使发送给下载服务
        intent.putExtra("savePath",path);
        intent.putExtra("downloadUrl",url);
        intent.putExtra("songInfo",song.getName()+"-" +song.getAuthor());
        intent.putExtra("ProgressMessenger",new Messenger(handler));//用于传递下载进度的信使
        startService(intent);//启动下载服务
    }

    //将下载的歌曲信息保存到数据库
    public void save2DB(Song song)
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("id",song.getId());
        values.put("name",song.getName());
        values.put("author",song.getAuthor());
        values.put("duration",song.getDuration());
        db.insert("Song",null,values);
        values.clear();
    }

}