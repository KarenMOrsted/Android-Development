package Fann.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//长按显示菜单可以删除√ 点击可回到主活动显示
public class CityManager extends AppCompatActivity {
    private List<City> cityList=new ArrayList<City>();//关注城市的列表
    private CityDB dbHelper=new CityDB(this,"City.db",null,1);
    private ListView cityListView;
    private CityAdapter adapter;
    public static final int CITY_INFO=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);

        /*try {
            getCity();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        FloatingActionButton addButton=findViewById(R.id.addButton);
        cityListView=findViewById(R.id.cities);
        registerForContextMenu(cityListView);
        setCities();

        addButton.setOnClickListener(v->{
            Intent intent=new Intent(CityManager.this,addCity.class);
            startActivity(intent);
        });
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            City city=(City) adapter.getItem(position);
            Intent intent=new Intent();
            intent.putExtra("cityId",city.getId());
            setResult(RESULT_OK,intent);
            finish();
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, Menu.FIRST+1,1,"取消关注");
        menu.add(0,Menu.FIRST+2,2,"设为常驻城市");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        int position=info.position;

        City city=(City)adapter.getItem(position);

        if(item.getItemId()==2)
            unfollow(city.getId());
        else
            setHome(city.getId());


        return super.onContextItemSelected(item);
    }
    void unfollow(String id)
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
       // Toast.makeText(CityManager.this,id,Toast.LENGTH_SHORT).show();
        db.delete("City","id=?",new String[]{id});
        SharedPreferences prefs=getSharedPreferences("Home",MODE_PRIVATE);

        if(prefs.getString("Id","0").equals(id))
        {
            SharedPreferences.Editor editor=getSharedPreferences("Home",MODE_PRIVATE).edit();
            if(editor.clear().commit())
                Toast.makeText(CityManager.this,"常驻城市已删除",Toast.LENGTH_SHORT).show();
        }
        setCities();

    }
    public void setHome(String id)
    {

        //Toast.makeText(CityManager.this,prefs.getString("Id","0"),Toast.LENGTH_SHORT).show();
        //Toast.makeText(CityManager.this,"!",Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor=getSharedPreferences("Home",MODE_PRIVATE).edit();
        editor.putString("Id",id);
        if(editor.commit())
            Toast.makeText(CityManager.this,"更改常驻城市成功",Toast.LENGTH_SHORT).show();

    }

    void setCities()
    {

        if(!cityList.isEmpty())
            cityList.clear();
         adapter=new CityAdapter(CityManager.this,R.layout.city_item,cityList);

        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query("City",null,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{

                @SuppressLint("Range")
                String name=cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range")
                String province=cursor.getString(cursor.getColumnIndex("province"));
                @SuppressLint("Range")
                String id=cursor.getString(cursor.getColumnIndex("id"));
                City city=new City();
                city.setName(name);
                city.setProvince(province);
                city.setId(id);
                cityList.add(city);

            }while(cursor.moveToNext());

        }
        cursor.close();
        cityListView.setAdapter(adapter);
       


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCities();
    }
   /* public void getCity() throws FileNotFoundException {
        //   省              市       区县
       // Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("CityJson.txt")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        //Map<String, List<Map<String, List<String>>>> allCity = gson.fromJson(String.valueOf(stringBuilder), new TypeToken<Map<String, List<Map<String, List<String>>>>>() {
        //}.getType());
        //System.out.println("---------"+stringBuilder.length()+"----------");
        //Province provinces=gson.fromJson(String.valueOf(stringBuilder),Province.class);
        //JSONObject jsonObject = null;
        List<String> provinceList=new ArrayList<>();

        //   省         市     区
        Map<String,List<String>> p2c=new HashMap<>();//province : city
        Map<String,List<String>>  c2a=new HashMap<>();//city : area


        JSONArray jsonArray= null;//将转化为string的json初始化为一个Json数组
        try {
            jsonArray = new JSONArray(String.valueOf(stringBuilder));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<jsonArray.length();i++) {
           // System.out.println(i+"-------------------");
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);//取得对象数组其中某个json对象即某个省
                String provinceName=jsonObject.getString("name");//获得省对象的名字
                provinceList.add(provinceName);//将省名加入省列表
                System.out.println("Province:"+provinceName);

                JSONArray city=jsonObject.getJSONArray("city");//取得当前省份的所有城市的数组
                List<String> cityList=new ArrayList<>();
                for(int j=0;j<city.length();j++)
                {

                    JSONObject jsonObject1=city.getJSONObject(j);//获得当前省份的城市数组中的某个城市
                    String cityName=jsonObject1.getString("name");//获得当前城市的名字
                    cityList.add(cityName);//将市名加入市列表
                    System.out.println("City:"+cityName);
                    //每个城市的所有地区是一个字符串 将该字符串中的地区识别名存入字符串数组
                    String[] area = jsonObject1.getString("area").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                    List<String> areaList=new ArrayList<>();
                    for(String sa:area) {
                        areaList.add(sa);//将所有地区名加入地区列表
                        System.out.println("Area:"+sa);
                    }
                    c2a.put(cityName,areaList);
                }
                p2c.put(provinceName,cityList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        System.out.println(p2c);
        System.out.println(c2a);



    }*/
}