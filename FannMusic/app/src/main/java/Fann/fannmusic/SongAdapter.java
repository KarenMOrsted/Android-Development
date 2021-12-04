package Fann.fannmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SongAdapter extends ArrayAdapter {
    private int resourceId;
    private TextView singer;
    private Context context;
    public TextView name;
    //DiaryAdapter的构造函数
    public SongAdapter(Context context, int textViewResourceId, List<Song> objects)
    {
        super(context,textViewResourceId,objects);
        this.resourceId=textViewResourceId;
        this.context=context;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song=(Song)getItem(position);
        View view;

        //旧布局为空则用LayoutInflater加载布局
        if(convertView==null)
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            //否则直接对旧布局重用
        else
            view=convertView;


        name=view.findViewById(R.id.music_name);
        singer=view.findViewById(R.id.singer);
        name.setText(song.getName());
        singer.setText(song.getAuthor());
        return view;
    }



}
