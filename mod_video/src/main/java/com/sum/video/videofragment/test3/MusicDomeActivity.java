package com.sum.video.videofragment.test3;

import static com.sum.common.constant.ARouterPathKt.VIDEO_TEST3_MUSICDEMOACTIVITY;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.sum.common.util.Loge;
import com.sum.video.R;
import com.sum.video.videofragment.MediaPlayerManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Route(path = VIDEO_TEST3_MUSICDEMOACTIVITY)
public class MusicDomeActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;

    TextView music_name;//音乐名称
    SeekBar seekBar; //进度条
    TextView seekBarHint1; //音乐时长提示
    TextView seekBarHint2; //音乐时长提示end

    StyledPlayerView styledPlayerView;//视频显示
    Button btn_1, btn_2, btn_3, btn_4; //播放按钮
    ExoPlayer player; //播放器
    private Timer timer; //定时器
    private boolean prepared; //播放器是否准备好
    List<String> music_list = new ArrayList<>();
    int idx = 0;//正在播放的music的id
    int count = 0;//第几首播放的

    public void initView() {
        styledPlayerView = findViewById(R.id.music_StyledPlayerView);

        prepared = false; //初始值
        //ListView 编程
        listView = findViewById(R.id.lv_music);
        music_list = getMusic();
        adapter = new ArrayAdapter<String>(
                MusicDomeActivity.this,
                android.R.layout.simple_list_item_1,
                music_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        player = new ExoPlayer.Builder(MusicDomeActivity.this).build();//创建播放器
        //开关初始化
        btn_1 = findViewById(R.id.button1);
        btn_2 = findViewById(R.id.button2);
        btn_3 = findViewById(R.id.button3);
        btn_4 = findViewById(R.id.button4);
        //进度条显示初始化
        music_name = findViewById(R.id.musictext);
        seekBar = findViewById(R.id.seekBar);
        seekBarHint1 = findViewById(R.id.seekBarHint1);
        seekBarHint2 = findViewById(R.id.seekBarHint2);
        //5个监听
        player.addListener(listener1);
        btn_1.setOnClickListener(listener_btn_1);
        btn_2.setOnClickListener(listener_btn_2);
        btn_3.setOnClickListener(listener_btn_3);
        btn_4.setOnClickListener(listener_btn_4);
        seekBar.setOnSeekBarChangeListener(listener3);


    }

    public List<String> getMusic() {
        List<String> pList = new ArrayList<>();
        try {
            String[] fNames = getAssets().list("music"); //获取assets/music目录下所有文件名
            for (String fn : fNames) {
                Loge.e(fn);
                pList.add(fn);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pList;
    }

    // ListView监听器
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lv = (ListView) parent;
            lv.setSelector(com.sum.common.R.color.colorPrimary); //设置高亮背景色
            String pName = parent.getItemAtPosition(position).toString(); //获得选中项名称

            if ((pName.equals(music_list.get(idx))) && (count != 0)) {//如果相同就音乐进度不变
                Log.d("flag", "选中相同的音乐" + pName + ",进度不变");
            } else {//如果是不同的音乐那么改变
                for (int i = 0; i < music_list.size(); i++) {
                    if (pName.equals(music_list.get(i))) {
                        Log.d("flag", "选中" + pName);
                        idx = i;
                    }
                }
                initExoPlayer();
            }
        }
    };

    public void initExoPlayer() {
        //使用与file Uri使用URL几乎相同的方式来使用该方案。
        // 语法是file:///android_asset/...（请注意：三个斜杠），
        // 其中省略号是文件assets/夹中文件的路径。
        //  Uri uri = Uri.parse("file:///android_asset/music/" + music_list.get(idx));
        String url = "";
        try {
            InputStream inputStream = getAssets().open("music/" + music_list.get(idx));
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            url = new String(bytes);
            Loge.e(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri uri = Uri.parse(url);
        String urlCache = MediaPlayerManager.Companion.getHttpProxyCacheServer().getProxyUrl(url);


        MediaItem mediaItem = MediaItem.fromUri(urlCache);
        count++;
        Log.d("flag", "播放的第" + count + "首音乐：" + music_list.get(idx));

        music_name.setText(music_list.get(idx));

        player.setMediaItem(mediaItem); //加载媒体资源

        styledPlayerView.setPlayer(player);

        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE); //单曲循环
        player.setPlayWhenReady(true);
        player.prepare(); //会触发下面的监听器↓


    }


    Player.Listener listener1 = new Player.Listener() {//player
        @Override
        //播放器状态监听器
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == ExoPlayer.STATE_READY) { //播放器准备好了
                prepared = true;
                long realDurationMillis = player.getDuration();//获取媒体文件的时长（毫秒）
                seekBar.setMax((int) realDurationMillis);//设置SeekBar最大值
                seekBarHint2.setText(format(realDurationMillis));
            }
        }
    };

    View.OnClickListener listener_btn_1 = new View.OnClickListener() {//btn_1
        @Override
        public void onClick(View v) {
            if (!prepared) //播放器没有准备好
                return;
            if (idx == 0) {
                idx = music_list.size() - 1;
            } else {
                idx = idx - 1;
            }
            initExoPlayer();
        }
    };
    View.OnClickListener listener_btn_2 = new View.OnClickListener() {//btn_2
        @Override
        public void onClick(View v) {
            if (!prepared) //播放器没有准备好
                return;
            if (player.isPlaying()) {
                player.pause();
                if (timer != null) timer.cancel(); //停止定时器
                timer = new Timer(); //新建定时器
            } else {
                player.play();
                timer = new Timer();
                timer.schedule(new ProgressUpdate(), 300, 500);
            }
        }
    };
    View.OnClickListener listener_btn_3 = new View.OnClickListener() {//btn_3
        @Override
        public void onClick(View v) {
            if (!prepared) //播放器没有准备好
                return;
            player.pause();
        }
    };
    View.OnClickListener listener_btn_4 = new View.OnClickListener() {//btn_4
        @Override
        public void onClick(View v) {
            if (!prepared) //播放器没有准备好
                return;
            if (idx == music_list.size() - 1) {
                idx = 0;
            } else {
                idx = idx + 1;
            }
            Log.d("flag", " " + idx);
            initExoPlayer();
        }
    };

    SeekBar.OnSeekBarChangeListener listener3 =
            new SeekBar.OnSeekBarChangeListener() {//seekBar
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    if (prepared && fromUser) {
                        player.seekTo(progress);//从指定位置开始播放
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekBarHint1.setText(format(seekBar.getProgress()));
                }
            };

    private class ProgressUpdate extends TimerTask {//定时任务类：定时刷新SeekBar进度条

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long position = player.getContentPosition();//获取媒体播放的当前位置（毫秒）
//                    Log.d("flag", "pos=" + position);
                    seekBar.setProgress((int) position);//SeekBar设置进度
                    seekBarHint1.setText(format(position));//显示当前音乐时间
                }
            });
        }
    }

    public String format(long position) {//"分:秒"格式
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String timeStr = sdf.format(position); //会自动将时长(毫秒数)转换为分秒格式
        return timeStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_dome);
        setSupportActionBar(findViewById(R.id.music_toolbar));
        // ActionBar actionBar = getSupportActionBar(); //获取ActionBar
        getSupportActionBar().setTitle("Music"); //设置标题

        initView();
    }

    /*重写Activity的onDestroy()方法，用于在当前Activity销毁时，
    停止正在播放的音频，并释放mediaplayer所占用的资源，否则你每打开一次就会播放一次，
    并且上次播放的不会停止 你可以试试的，我解释不清楚*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}
