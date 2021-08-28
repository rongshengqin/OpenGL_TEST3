package com.example.opengl_test3;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class args {
    static int FINGER_INDEX=999;
    static String format="%.3f";
    static float fps=0;
    static float balance=0;
    static float withdrawAvailable=0;
    static int LEVER=1;//杠杠倍数
    static float postion=0;// 仓位信息
    static float entryPrice=1;
    static float markPrice=0;
    static float unRealizedProfit=0;
    static float leverage=1;
    static float PERCEMT=0;
    //Fibonacci回撤分析
    static float begin_price=0;
    static float end_price=0;
    static String  kdj_info="kdj";
    static String COIN="ALICEUSDT";
    static String COIN_TEMP="ALICEUSDT";
    static String KLINE_INTERVAL="15m";
    static String KLINE_INTERVAL_TEMP="15m";
    static String ORDER_INFO_STRING="NO Order Messages";
    static float Y_STEP=50f;
    static int limit=1000; //显示k线的数量
    static  int GRID_POINTS=0;
    static List OrderList =new ArrayList();
    static List OrderStringList =new ArrayList();
    static List BuySellPosList =new ArrayList();
    static float open_close[]=new float[limit*2*3]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float high_low[]=new float[limit*2*3]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float volume[]=new float[limit*2*3]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_open_close[]=new float[limit*2*3]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_high_low[]=new float[limit*2*3]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_volume[]=new float[limit*2*3]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static long  btc_current_kline_id=0; //k线的时间戳标识符
    static long  current_kline_id=0; //k线的时间戳标识符
    static long date_stamp[]=new long[limit];
    static float data_open[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float data_close[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float data_high[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float data_low[]=new float[limit]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float data_volume[]=new float[limit]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_data_open[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_data_close[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_data_high[]=new float[limit]; // k线数据 格式为 open close 2个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_data_low[]=new float[limit]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float btc_data_volume[]=new float[limit]; // k线数据 格式为  high low 四个坐标依次排列，坐标格式 x,y,c（c是颜色）
    static float ma5[]=new float[limit*3];
    static float ma10[]=new float[limit*3];

//    static float volume_ma5[]=new float[limit*3];
//    static float volume_ma10[]=new float[limit*3];

    static float btc_ma5[]=new float[limit*3];
    static float btc_ma10[]=new float[limit*3];
    static float K[]=new float[limit*3];
    static float D[]=new float[limit*3];
    static float J[]=new float[limit*3];
    static float MB[]=new float[limit*3];
    static float UP[]=new float[limit*3];
    static float DN[]=new float[limit*3];
    static float BMB[]=new float[limit*3];
    static float BUP[]=new float[limit*3];
    static float BDN[]=new float[limit*3];
    static float BK[]=new float[limit*3];
    static float BD[]=new float[limit*3];
    static float BJ[]=new float[limit*3];

    static float top_price=100f;
    static float bottom_price=0;
    static float figer_price=0;
    static float MAX_AND_MIN[]=new float[8];
    static ConcurrentLinkedQueue<float[]> queue = new ConcurrentLinkedQueue<float[]>();
    static LinkedBlockingQueue<String>com_queue=new LinkedBlockingQueue<String>(10); //存储按键发出的命令
    static LinkedBlockingQueue<String>msg_queue=new LinkedBlockingQueue<String>(10); //存储按键发出的命令
    static volatile boolean speaker_is_busy=false;
    static long  count_time[]=new long[3];
    static InetAddress device_addr;;
    static int params[]=new int[5];

    public static String stampToTime(long lt){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
//将时间戳转换为时间
        Date date = new Date(lt);
//将时间调整为yyyy-MM-dd HH:mm:ss时间样式
        res = simpleDateFormat.format(date);
        return res;
    }
}
