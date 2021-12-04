package Fann.fannmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView songView;
    private List<Song> songList;
    private FloatingActionButton play_pause;
    private FloatingActionButton last;
    private FloatingActionButton next;
    private EditText searchText;
    private TextView songPlaying;
    private SeekBar seekBar;
    private TextView songLength;
    private TextView currentProgress;
    private boolean Paused=true;
    private MediaPlayer mediaPlayer;
    private SongDB dbHelper=new SongDB(this,"Song.db",null,1);
    String currentPlaying="";//当前正在播放的歌的地址
    int currentIndex=-1;//当前正在播放的歌在列表里的下标
    private final Handler handler = new Handler();

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSongList();
        initView();
        initButton();
        initSeekBar();
        initMediaPlayer();

        }

    public void initMediaPlayer()
    {
        mediaPlayer=new MediaPlayer();
        //重写音乐播放类的播放完毕函数 设置为播放完毕自动播放下一首
        mediaPlayer.setOnCompletionListener(v->{
            if(currentIndex==-1)
                return;
            next();
        });
    }

    //初始化显示控件
    public void initView()
    {
        songPlaying=findViewById(R.id.song_info);
        currentProgress=findViewById(R.id.currentProgress);
        songLength=findViewById(R.id.maxProgress);
        songPlaying.setTextSize(15);
        searchText=findViewById(R.id.searchText);
        searchText.setFocusable(false);
        searchText.setOnClickListener(v->{
            Intent intent =new Intent(MainActivity.this,SearchSong.class);
            startActivity(intent);
        });
    }

    //初始化按钮控件
    public void initButton()
    {
        play_pause=findViewById(R.id.play_pause);
        last=findViewById(R.id.last);
        next=findViewById(R.id.next);
        play_pause.setEnabled(false);
        last.setEnabled(false);
        next.setEnabled(false);

        play_pause.setOnClickListener(v->{
            play_pause();
        });
        last.setOnClickListener(v->{
            last();
        });
        next.setOnClickListener(v->{
            next();
        });
    }


    //初始化进度条
    public void initSeekBar()
    {
        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            //停止拖动时调节播放
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //获得当前进度
                int progress = seekBar.getProgress();
                //得到歌曲的最长秒数
                int musicMax = mediaPlayer.getDuration();
                //System.out.println("musicMax:"+musicMax);
                //seekBar最大值
                int seekBarMax = seekBar.getMax();
                //计算相对播放时间
                float msec = progress / (seekBarMax * 1.0F) * musicMax;
                // 跳到该曲该秒
                mediaPlayer.seekTo((int) msec);
            }
        });
        handler.post(updateUI);//自动更新进度条和播放时间
    }

   // 创建Runnable线程自动更新进度条和播放时间
    private final Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());//更新进度条
            currentProgress.setText(format.format(mediaPlayer.getCurrentPosition()));//更新当前播放时间
            handler.postDelayed(updateUI, 1000);//反复启动线程更新ui 延迟1秒 以实现进度条自动更新
        }
    };

    //播放音乐
    public void playMusic(String dataSource)
    {

        Paused=false;//播放状态改为正在播放
        mediaPlayer.reset();//重置mediaPlayer状态
        try {
            mediaPlayer.setDataSource(dataSource);//设置播放路径
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();//开始播放
        play_pause.setImageResource(R.drawable.pause_icon);
        seekBar.setMax(mediaPlayer.getDuration());//不能在reset后调用
    }

    //暂停、继续播放的函数
    public void play_pause()
    {
        if(Paused) {
            mediaPlayer.start();//当暂停时 点击按钮将会继续播放 播放状态改为正在播放
            play_pause.setImageResource(R.drawable.pause_icon);
            Paused=false;
        }
        else {
            mediaPlayer.pause();//当播放时，点击按钮将会暂停播放 播放状态改为暂停播放
            play_pause.setImageResource(R.drawable.play_icon);
            Paused = true;
        }
    }

    //播放上一首歌的函数
    public void last()
    {
        //取得上一首歌在列表里的的下标 如果没有上一首则直接循环跳到最后一首
        int last=currentIndex-1;
        if(last<0)
            last+=songList.size();
        //通过列表的歌曲对象构造播放路径
        Song song= songList.get(last);
        setSongInfo(song);
        String internalPath=String.valueOf(getFilesDir().getAbsoluteFile());
        currentIndex=last;
        currentPlaying=internalPath+"/"+song.getName()+"-"+song.getAuthor()+".mp3";//将歌曲设为正在播放
        playMusic(currentPlaying);
        songPlaying.setText(song.getName()+"-"+song.getAuthor());
    }

    //播放下一首歌的函数
    public void next()
    {
        //取得下一首歌在列表里的下标
        int next=(currentIndex+1)%songList.size();
        Song song= songList.get(next);
        setSongInfo(song);
        String internalPath=String.valueOf(getFilesDir().getAbsoluteFile());
        currentIndex=next;
        currentPlaying=internalPath+"/"+song.getName()+"-"+song.getAuthor()+".mp3";//将歌曲设为正在播放
        System.out.println(currentPlaying);
        playMusic(currentPlaying);
        songPlaying.setText(song.getName()+"-"+song.getAuthor());
    }

    //显示歌曲列表的函数
    public void setSongList()
    {
        songList=new ArrayList<>();

        if(!songList.isEmpty())
            songList.clear();
        //从数据库获取歌曲信息
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query("Song",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String id=cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name=cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String author=cursor.getString(cursor.getColumnIndex("author"));
                @SuppressLint("Range") String duration=cursor.getString(cursor.getColumnIndex("duration"));
                songList.add(new Song(name,author,id,duration));
            }while(cursor.moveToNext());
        }
        cursor.close();
        SongAdapter adapter=new SongAdapter(MainActivity.this,R.layout.song_item,songList);
        songView=findViewById(R.id.songView);
        songView.setOnItemClickListener((parent, view, position, id)->{
            play_pause.setEnabled(true);
            next.setEnabled(true);
            last.setEnabled(true);

            Song song=(Song)adapter.getItem(position);

            setSongInfo(song);

            String internalPath=String.valueOf(getFilesDir().getAbsoluteFile());
            if(currentPlaying.equals(internalPath+"/"+song.getName()+"-"+song.getAuthor()+".mp3"))//如果再次点击正在播放的歌曲 则不作操作
                return;
            currentIndex=songList.indexOf(song);
            currentPlaying=internalPath+"/"+song.getName()+"-"+song.getAuthor()+".mp3";//将所点击歌曲设为正在播放
            //开始播放歌曲
            playMusic(currentPlaying);

        });
        songView.setAdapter(adapter);
    }

    //设置正在播放的歌曲的信息：歌曲长度和歌曲名
    public void setSongInfo(Song song)
    {
        songPlaying.setText(song.getName()+"-"+song.getAuthor());
        String musicLength = getTime(Integer.parseInt(song.getDuration()));
        songLength.setText(musicLength);
    }

    //将歌曲时间转化为00:00的格式
    @SuppressLint("DefaultLocale")
    public String getTime(int progress){
        int sec = progress / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%02d:%02d", min, sec);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        setSongList();
    }

}


