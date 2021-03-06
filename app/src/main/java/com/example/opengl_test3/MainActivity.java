package com.example.opengl_test3;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.opengl.GLES31;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;

import java.lang.Math;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener {
    private GLSurfaceView gLView;
    private CheckedTextView txt_current_positon;
    private AudioTrackManager audio;
    private TextToSpeech mTextToSpeech;
    private ToggleButton btn_order_type,btn_loss_and_profit,btn_Fibonacci;
    private Button btn_open_order,btn_close_order,btn_turn_order,btn_cancel_order,btn_change_coin,btn_choise_coin,btn_lever,btn_condition_order,btn_sell_order,btn_order_side;
    private Switch Switch1;
    private TextInputEditText txt_input_txt;
    private SeekBar SeekBar_position;

    private Socket socket=null;
    private int sample_index=0;
    short clock_div[]={256,128,64,32,16,12,10,8,4,2,1};
    float sample_cirlces[]={160.5f,79.5f,39.5f,19.5f,12.5f,7.5f,3.5f,1.5f};
    private NUM N =new NUM();
    private void fill_data(int i,int j,JsonArray kline){
        float open=kline.get(j*12+1).getAsFloat(); //open
        float close=kline.get(j*12+4).getAsFloat(); //close
        float high=kline.get(j*12+2).getAsFloat(); //high
        float low=kline.get(j*12+3).getAsFloat(); //low
        float vol=kline.get(j*12+5).getAsFloat(); //?????????
        args.date_stamp[i]=kline.get(j*12).getAsLong();
        args.data_open[i]=open;
        args.data_close[i]=close;
        args.data_high[i]=high;
        args.data_low[i]=low;
        args.data_volume[i]=vol;
        args.MAX_AND_MIN[0]=Math.min( args.MAX_AND_MIN[0],low);//?????????
        args.MAX_AND_MIN[1]=Math.max( args.MAX_AND_MIN[1],high);//?????????
        args.MAX_AND_MIN[2]=Math.max( args.MAX_AND_MIN[2],vol); //???????????????
        float c=0.0f;
        if(close>open){
            c=1.0f;  // ????????????
        }
        args.open_close[i*6]=i;
        args.open_close[i*6+1]=open; //open
        args.open_close[i*6+2]=c;
        args.open_close[i*6+3]=i;
        args.open_close[i*6+4]=close; //close
        args.open_close[i*6+5]=c;

        args.high_low[i*6]=i;
        args.high_low[i*6+1]=high; //high
        args.high_low[i*6+2]=c;
        args.high_low[i*6+3]=i;
        args.high_low[i*6+4]=low; //open
        args.high_low[i*6+5]=c;

        args.volume[i*6]=i;
        args.volume[i*6+1]=0; //high
        args.volume[i*6+2]=c;
        args.volume[i*6+3]=i;
        args.volume[i*6+4]=vol; //open
        args.volume[i*6+5]=c;
    }
    private void btc_fill_data(int i,int j,JsonArray kline){
        float open=kline.get(j*12+1).getAsFloat(); //open
        float close=kline.get(j*12+4).getAsFloat(); //close
        float high=kline.get(j*12+2).getAsFloat(); //high
        float low=kline.get(j*12+3).getAsFloat(); //low
        float vol=kline.get(j*12+5).getAsFloat(); //?????????
        args.btc_data_open[i]=open;
        args.btc_data_close[i]=close;
        args.btc_data_high[i]=high;
        args.btc_data_low[i]=low;
        args.btc_data_volume[i]=vol;
        args.MAX_AND_MIN[3]=Math.min( args.MAX_AND_MIN[3],low);//?????????
        args.MAX_AND_MIN[4]=Math.max( args.MAX_AND_MIN[4],high);//?????????
        args.MAX_AND_MIN[5]=Math.max( args.MAX_AND_MIN[5],vol); //???????????????
        float c=0.0f;
        if(close>open){
            c=1.0f;  // ????????????
        }
        args.btc_open_close[i*6]=i;
        args.btc_open_close[i*6+1]=open; //open
        args.btc_open_close[i*6+2]=c;
        args.btc_open_close[i*6+3]=i;
        args.btc_open_close[i*6+4]=close; //close
        args.btc_open_close[i*6+5]=c;

        args.btc_high_low[i*6]=i;
        args.btc_high_low[i*6+1]=high; //high
        args.btc_high_low[i*6+2]=c;
        args.btc_high_low[i*6+3]=i;
        args.btc_high_low[i*6+4]=low; //open
        args.btc_high_low[i*6+5]=c;

        args.btc_volume[i*6]=i;
        args.btc_volume[i*6+1]=0; //high
        args.btc_volume[i*6+2]=c;
        args.btc_volume[i*6+3]=i;
        args.btc_volume[i*6+4]=vol; //open
        args.btc_volume[i*6+5]=c;
    }

    private String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private void speaker(){
        new Thread((new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        String msg = args.msg_queue.poll(10, TimeUnit.SECONDS);
                       // System.out.println("tts msg "+msg);
                        args.speaker_is_busy = true;
                        mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                        if (!mTextToSpeech.isSpeaking()) {
                            initTextToSpeech();
                            System.out.println("tts restarted");
                        }
                        try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }//??????
                        args.speaker_is_busy = false;
                    }catch (InterruptedException e){
                    }
                }
            }
        })).start();
    }
    private boolean INITIAILIZED=false;
    //InetAddress device_addr;
    private void startListen() {
        new Thread(new Runnable() {
            public void run() {
                InputStream is=null;
                InputStreamReader reader=null;
                BufferedReader bufReader=null;
                OutputStream os=null;
                System.out.println("??????socket");

                while(true) {
                    try {
                        //1.????????????????????????????????????????????????????????????????????????
                        socket = new Socket("167.71.210.63", 7000);
                        socket.setSoTimeout(5000);
                        //2.??????????????????socket??????????????????????????????????????????
                        os = socket.getOutputStream();
                        //??????socket?????????????????????????????????????????????????????????
                        is = socket.getInputStream();
                        //??????????????????????????????
                        reader = new InputStreamReader(is);
                        bufReader = new BufferedReader(reader);
                        String s = null;
                        JsonObject object = null; //???json????????????
                        String ack=String.format( "{'type':'RESET','info':'next','symbol':'%s','INTERVAL':'%s'}",args.COIN,args.KLINE_INTERVAL);
                        if(!INITIAILIZED){
                            try {
                                args.com_queue.put(ack);
                            }catch (InterruptedException e){
                                System.out.println("##########????????????######");
                                break;
                            }
                        }
                        System.out.println("##########???????????????######");
                        //String s = null;
                        // ?????????????????????K???????????????????????????
                        args.count_time[0]=0;
                        while (true) {
                            String cmd="test";
                            ack=String.format( "{'type':'kline','info':'next','symbol':'%s','INTERVAL':'%s'}",args.COIN,args.KLINE_INTERVAL);
                            try {
                                try {
                                    cmd=args.com_queue.poll(10, TimeUnit.MILLISECONDS);
                                }catch ( InterruptedException e){
                                    System.out.println("cmd InterruptedException");
                                }
                                if(cmd!=null){
                                    object = JsonParser.parseString(cmd).getAsJsonObject(); //???json????????????
                                    String msg_type = object.get("type").getAsString();
                                    ack=cmd;
                                    System.out.println("cmd "+cmd);
                                }
                                //????????????????????????????????????
                                os.write(ack.getBytes());
                                os.flush();
                                //is = socket.getInputStream();
                                //??????????????????????????????
                                //reader = new InputStreamReader(is);
                                //bufReader = new BufferedReader(reader);
                                s = bufReader.readLine();
//                                if (!(s = bufReader.readLine()).equals("\n")) {
//                                };
                                //System.out.println(s);
                                try{
                                    if( s.length()<10){
                                        throw new IOException("something wrong wiht readline, will reconnect");
                                    }
                                }catch (NullPointerException e){
                                    throw new IOException("something wrong wiht readline, will reconnect");
                                }
                                try{
                                    object = JsonParser.parseString(s).getAsJsonObject(); //???json??????
                                }catch (JsonSyntaxException e){
                                    throw new IOException("something wrong wiht Json, will reconnect");
                                }

                                String msg_type = object.get("type").getAsString();

                                //String msg_info = object.get("info").getAsString();
                                // ???????????????
                                if(msg_type.equals("RESET")){
                                    INITIAILIZED=false;
                                    System.out.println("#########################???????????????##########################");
                                    //System.out.println(s);
                                    JsonArray kline = object.getAsJsonArray("kline");
                                    assert (kline.size()==args.limit*12);
                                    args.MAX_AND_MIN[0]=1000000;
                                    args.MAX_AND_MIN[1]=-1;
                                    args.MAX_AND_MIN[2]=0;
                                    // ???????????????k???
                                    for (int i = 0; i < args.limit; i++) {
                                        fill_data( i ,i,kline);
                                    }
                                    args.current_kline_id=kline.get((args.limit-1)*12).getAsLong();
                                    System.out.println(args.current_kline_id);
                                    JsonArray btckline = object.getAsJsonArray("btckline");
                                    assert ( btckline.size()==args.limit*12);

                                    args.MAX_AND_MIN[3]=1000000;
                                    args.MAX_AND_MIN[4]=-1;
                                    args.MAX_AND_MIN[5]=0;
                                    // ???????????????k???
                                    for (int i = 0; i < args.limit; i++) {
                                        btc_fill_data( i ,i, btckline);
                                    }
                                    args. btc_current_kline_id= btckline.get((args.limit-1)*12).getAsLong();
                                    System.out.println(args. btc_current_kline_id);
                                    //args.USDT=object.get("USDT").getAsFloat();
                                    ack=String.format( "{'type':'RESET_INDICATORS','info':'ma5','symbol':'%s','INTERVAL':'%s'}",args.COIN,args.KLINE_INTERVAL);
//                                    try {
//                                        args.com_queue.put(ack);
//                                    }catch (InterruptedException e){
//                                        System.out.println("##########????????????######");
//                                        break;
//                                    }
                                    JsonArray open_orders = object.getAsJsonArray("open_orders");
                                    for (int i=0;i<open_orders.size();i++){
                                        JsonObject info = open_orders.get(i).getAsJsonObject();
                                        float price=info.get("price").getAsFloat();
                                        float avgprice=info.get("avgPrice").getAsFloat();
                                        float stopPrice =info.get("stopPrice").getAsFloat();
                                        price=Math.max(price,avgprice);
                                        price=Math.max(price,stopPrice);
                                        args.ORDER_INFO_STRING= String.format("%s Oid:%s %s %s Pri:%s Qty:%s Sta:%s",
                                                info.get("symbol").getAsString().replace("USDT",""),
                                                info.get("orderId").getAsString(),
                                                info.get("side").getAsString(),
                                                info.get("type").getAsString(),""+price,
                                                info.get("origQty").getAsString(),info.get("status").getAsString());
                                        if (info.get("status").getAsString().equals("NEW")){
                                            boolean is_new=true;
                                            String orderid=info.get("orderId").getAsString();
                                            for(int j=0;j<args.OrderList.size();j++){
                                                String oid=(String) args.OrderList.get(j);
                                                if(oid.equals(orderid)){
                                                    is_new=false;
                                                    break;
                                                }
                                            }
                                            if(!is_new) continue;
                                            args.OrderList.add(info.get("orderId").getAsString());
                                            args.OrderStringList.add(args.ORDER_INFO_STRING);
                                            if(info.get("type").getAsString().equals("TAKE_PROFIT_MARKET")||info.get("type").getAsString().equals("STOP_MARKET")||info.get("type").getAsString().equals("LIMIT")){
                                                Map dict = new HashMap();
                                                dict.put("symbol",info.get("symbol").getAsString());
                                                dict.put("orderId",info.get("orderId").getAsString());
                                                if(info.get("side").getAsString().equals("BUY")){
                                                    dict.put("side","BUY");
                                                    dict.put("color",1);
                                                }else {
                                                    dict.put("side","SELL");
                                                    dict.put("color",0);
                                                }
                                                if(info.get("type").getAsString().equals("LIMIT")){
                                                    dict.put("price",info.get("price").getAsFloat());
                                                }else{
                                                    dict.put("price",info.get("stopPrice").getAsFloat());
                                                }
                                                args.BuySellPosList.add(dict);
                                            }
                                        }
//

                                    }
                                    INITIAILIZED=true;
                                    gLView.requestRender();
                                    System.out.println("#########################k??????????????????##########################");
                                }
//                                else if(msg_type.equals("RESET_INDICATORS")){
//                                    JsonArray ma5 = object.getAsJsonArray("ma5");
//                                    JsonArray ma10 = object.getAsJsonArray("ma10");
//                                    JsonArray K = object.getAsJsonArray("K");
//                                    JsonArray D = object.getAsJsonArray("D");
//                                    JsonArray J = object.getAsJsonArray("J");
//                                    for(int i=0;i<ma5.size();i++){
//                                        args.ma5[i*3] =i;
//                                        args.ma5[i*3+1] =ma5.get(i).getAsFloat();
//                                        args.ma5[i*3+2] =1;
//                                        args.ma10[i*3] =i;
//                                        args.ma10[i*3+1] =ma10.get(i).getAsFloat();
//                                        args.ma10[i*3+2] =0;
//
//                                        args.K[i*3] =i;
//                                        args.K[i*3+1] =K.get(i).getAsFloat();
//                                        args.K[i*3+2] =0;
//
//                                        args.D[i*3] =i;
//                                        args.D[i*3+1] =D.get(i).getAsFloat();
//                                        args.D[i*3+2] =1;
//
//                                        args.J[i*3] =i;
//                                        args.J[i*3+1] =J.get(i).getAsFloat();
//                                        args.J[i*3+2] =2;
//                                    }
//                                }
                                else if(msg_type.equals("kline"))
                                {
                                    // ????????????
                                    JsonObject position = object.getAsJsonObject("position");
                                    args.postion=position.get("positionAmt").getAsFloat();
                                    args.entryPrice=position.get("entryPrice").getAsFloat();
                                    args.markPrice=position.get("markPrice").getAsFloat();
                                    args.unRealizedProfit=position.get("unRealizedProfit").getAsFloat();
                                    args.leverage=position.get("leverage").getAsFloat();
                                    args.balance=object.get("balance").getAsFloat();
                                    args.withdrawAvailable=object.get("withdrawAvailable").getAsFloat();
                                    JsonArray kline = object.getAsJsonArray("kline");
                                    JsonArray btckline = object.getAsJsonArray("btckline");
                                    //System.out.println(kline);
                                    if(kline.isJsonNull()){
                                        System.out.println("kline is null");
                                        continue;
                                    }
                                    assert (kline.size()==12);
                                    long new_kline_id=kline.get(0).getAsLong();
                                    long btc_new_kline_id=btckline.get(0).getAsLong();
                                    if(new_kline_id==args.current_kline_id){
                                        fill_data( args.limit-1 ,0,kline); //??????????????????k???
//                                        JsonArray indicators = object.getAsJsonArray("indicators");
//                                        args.ma5[(args.limit-1)*3+1]=indicators.get(0).getAsFloat();
//                                        args.ma10[(args.limit-1)*3+1]=indicators.get(1).getAsFloat();
//                                        args.K[(args.limit-1)*3+1]=indicators.get(2).getAsFloat();
//                                        args.D[(args.limit-1)*3+1]=indicators.get(3).getAsFloat();
//                                        args.J[(args.limit-1)*3+1]=indicators.get(4).getAsFloat();
                                    }
                                    if(btc_new_kline_id==args.btc_current_kline_id){
                                        btc_fill_data( args.limit-1 ,0,btckline); //??????????????????k???
                                    }
                                    if(new_kline_id>args.current_kline_id){  //k????????????????????????????????????????????????k???
                                        System.arraycopy(args.open_close,6,args.open_close,0,(args.limit-1)*6);
                                        System.arraycopy(args.high_low,6,args.high_low,0,(args.limit-1)*6);
                                        System.arraycopy(args.volume,6,args.volume,0,(args.limit-1)*6);
                                        System.arraycopy(args.data_open,1,args.data_open,0,(args.limit-1)*1);
                                        System.arraycopy(args.data_close,1,args.data_close,0,(args.limit-1)*1);
                                        System.arraycopy(args.data_high,1,args.data_high,0,(args.limit-1)*1);
                                        System.arraycopy(args.data_low,1,args.data_low,0,(args.limit-1)*1);
                                        System.arraycopy(args.data_volume,1,args.data_volume,0,(args.limit-1)*1);
                                        System.arraycopy(args.date_stamp,1,args.date_stamp,0,(args.limit-1)*1);
//                                        System.arraycopy(args.ma5,3,args.ma5,0,(args.limit-1)*3);
//                                        System.arraycopy(args.ma10,3,args.ma10,0,(args.limit-1)*3);
//                                        System.arraycopy(args.K,3,args.K,0,(args.limit-1)*3);
//                                        System.arraycopy(args.D,3,args.D,0,(args.limit-1)*3);
//                                        System.arraycopy(args.J,3,args.J,0,(args.limit-1)*3);

                                        for (int i=0;i<args.limit-1;i++){
                                            args.open_close[i*6]=i;
                                            args.open_close[i*6+3]=i;
                                            args.high_low[i*6]=i;
                                            args.high_low[i*6+3]=i;
                                            args.volume[i*6]=i;
                                            args.volume[i*6+3]=i;
//                                            args.ma5[i*3]=i;
//                                            args.ma10[i*3]=i;
//                                            args.K[i*3]=i;
//                                            args.D[i*3]=i;
//                                            args.J[i*3]=i;
                                        }
                                        fill_data(args.limit-1,0,kline);
                                        //                                       JsonArray indicators = object.getAsJsonArray("indicators");
//                                        args.ma5[(args.limit-1)*3+1]=indicators.get(0).getAsFloat();
//                                        args.ma10[(args.limit-1)*3+1]=indicators.get(1).getAsFloat();
//                                        args.K[(args.limit-1)*3+1]=indicators.get(2).getAsFloat();
//                                        args.D[(args.limit-1)*3+1]=indicators.get(3).getAsFloat();
//                                        args.J[(args.limit-1)*3+1]=indicators.get(4).getAsFloat();
//                                        args.ma5[(args.limit-1)*3+2]=1;
//                                        args.ma10[(args.limit-1)*3+2]=0;
//                                        args.K[(args.limit-1)*3+2]=0;
//                                        args.D[(args.limit-1)*3+2]=1;
//                                        args.J[(args.limit-1)*3+2]=2;
                                        args.current_kline_id=new_kline_id;
                                    }
                                    if(btc_new_kline_id>args.btc_current_kline_id){  //k????????????????????????????????????????????????k???
                                        System.arraycopy(args.btc_open_close,6,args.btc_open_close,0,(args.limit-1)*6);
                                        System.arraycopy(args.btc_high_low,6,args.btc_high_low,0,(args.limit-1)*6);
                                        System.arraycopy(args.btc_volume,6,args.btc_volume,0,(args.limit-1)*6);
                                        System.arraycopy(args.btc_data_open,1,args.btc_data_open,0,(args.limit-1)*1);
                                        System.arraycopy(args.btc_data_close,1,args.btc_data_close,0,(args.limit-1)*1);
                                        System.arraycopy(args.btc_data_high,1,args.btc_data_high,0,(args.limit-1)*1);
                                        System.arraycopy(args.btc_data_low,1,args.btc_data_low,0,(args.limit-1)*1);
                                        System.arraycopy(args.btc_data_volume,1,args.btc_data_volume,0,(args.limit-1)*1);
                                        for (int i=0;i<args.limit-1;i++){
                                            args.btc_open_close[i*6]=i;
                                            args.btc_open_close[i*6+3]=i;
                                            args.btc_high_low[i*6]=i;
                                            args.btc_high_low[i*6+3]=i;
                                            args.btc_volume[i*6]=i;
                                            args.btc_volume[i*6+3]=i;
                                        }
                                        btc_fill_data(args.limit-1,0,btckline);
                                        args.btc_current_kline_id=btc_new_kline_id;
                                    }
//                                    args.count_time[0]=args.count_time[0]+1;
//                                    //var fps=0
//                                    if(args.count_time[0]==10L){
//                                        float dt=(SystemClock.uptimeMillis()-args.count_time[1])/1000f;
//                                        args.count_time[2]= (long)(10f/dt);
//                                        args.fps=10f/dt;
//                                        args.count_time[0]=0;
//                                        args.count_time[1]= SystemClock.uptimeMillis();
//                                    }
                                    gLView.requestRender();
                                }
                                else if(msg_type.equals("order")){
                                    JsonObject info = object.getAsJsonObject("info");
                                    //args.USDT=object.get("USDT").getAsFloat();
                                    float price=info.get("price").getAsFloat();
                                    float avgprice=info.get("avgPrice").getAsFloat();
                                    float stopPrice =info.get("stopPrice").getAsFloat();
                                    price=Math.max(price,avgprice);
                                    price=Math.max(price,stopPrice);
                                    args.ORDER_INFO_STRING= String.format("%s Oid:%s %s %s Pri:%s Qty:%s Sta:%s",
                                            info.get("symbol").getAsString().replace("USDT",""),
                                            info.get("orderId").getAsString(),
                                            info.get("side").getAsString(),
                                            info.get("type").getAsString(),
                                            ""+price,
                                            info.get("origQty").getAsString(),
                                            info.get("status").getAsString());
//
//                                    if(info.get("type").getAsString().equals("TAKE_PROFIT_MARKET")||info.get("type").getAsString().equals("STOP_MARKET"))
//                                        args.ORDER_INFO_STRING= String.format("Oid:%s %s %s Sta:%s Pri:%s Qty:0",info.get("orderId").getAsString(),
//                                                info.get("side").getAsString(),
//                                            info.get("type").getAsString(),info.get("status").getAsString(),info.get("stopPrice").getAsString());
//                                    else if(info.get("type").getAsString().equals("LIMIT"))
//                                        args.ORDER_INFO_STRING= String.format("Oid:%s %s %s Sta:%s Pri:%s Qty:%s",info.get("orderId").getAsString(),
//                                                info.get("side").getAsString(),
//                                                info.get("type").getAsString(),info.get("status").getAsString(),info.get("price").getAsString(),
//                                                info.get("origQty").getAsString());
                                    System.out.println(s);
                                    if (info.get("status").getAsString().equals("NEW")){
                                        args.OrderList.add(info.get("orderId").getAsString());
                                        args.OrderStringList.add(args.ORDER_INFO_STRING);
                                        if(info.get("type").getAsString().equals("TAKE_PROFIT_MARKET")||info.get("type").getAsString().equals("STOP_MARKET")||info.get("type").getAsString().equals("LIMIT")){
                                            Map dict = new HashMap();
                                            dict.put("symbol",info.get("symbol").getAsString());
                                            dict.put("orderId",info.get("orderId").getAsString());
                                            if(info.get("side").getAsString().equals("BUY")){
                                                dict.put("side","BUY");
                                                dict.put("color",1);
                                            }else {
                                                dict.put("side","SELL");
                                                dict.put("color",0);
                                            }
                                            if(info.get("type").getAsString().equals("LIMIT")){
                                                dict.put("price",info.get("price").getAsFloat());
                                            }else{
                                                dict.put("price",info.get("stopPrice").getAsFloat());
                                            }
                                            args.BuySellPosList.add(dict);
                                        }
                                    }else if(info.get("status").getAsString().equals("CANCELED")||info.get("status").getAsString().equals("FILLED"))
                                    {
                                        for (int i=0;i<args.OrderList.size();i++){
                                            if (info.get("orderId").getAsString().equals(args.OrderList.get(i))){
                                                args.OrderList.remove(i);
                                                args.OrderStringList.remove(i);
                                                args.BuySellPosList.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                                else if(msg_type.equals("fail_info")){
                                    args.ORDER_INFO_STRING= object.get("info").getAsString();
                                    System.out.println(s);
                                }
                                //txt_overview.setText(s);
                                //audio.playBeep(1,500);
                                //mTextToSpeech.speak("ETH cross", TextToSpeech.QUEUE_FLUSH, null);
                                //if (!mTextToSpeech.isSpeaking()) {
                                //  initTextToSpeech();
                                //System.out.println("tts restarted");
                                //}
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                            // try {Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }//??????
                            //if(!INITIAILIZED) throw new IOException("something wrong wiht INITIAILIZED, will reconnect");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //3?????????IO??????????????????????????????????????????finally??????
                        try {
                            bufReader.close();
                            reader.close();
                            is.close();
                            os.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        args.count_time[0]=0;
                    }
                }

            }
        }).start();
    }

    private void startListen_market() {
        new Thread(new Runnable() {
            public void run() {
                InputStream is=null;
                InputStreamReader reader=null;
                BufferedReader bufReader=null;
                OutputStream os=null;
                System.out.println("??????socket");

                while(true) {
                    try {
                        //1.????????????????????????????????????????????????????????????????????????
                        socket = new Socket("128.199.138.233", 7000);
                        socket.setSoTimeout(5000);
                        //2.??????????????????socket??????????????????????????????????????????
                        os = socket.getOutputStream();
                        //??????socket?????????????????????????????????????????????????????????
                        is = socket.getInputStream();
                        //??????????????????????????????
                        reader = new InputStreamReader(is);
                        bufReader = new BufferedReader(reader);
                        String s = null;
                        JsonObject object = null; //???json????????????
                        String ack="{'type':'ack','info':'ack'}";
                        System.out.println("##########???????????????######");
                        //String s = null;
                        // ?????????????????????K???????????????????????????
                        args.count_time[0]=0;
                        while (true) {
                            try {
                                //????????????????????????????????????
                                os.write(ack.getBytes());
                                os.flush();
                                //is = socket.getInputStream();
                                //??????????????????????????????
                                //reader = new InputStreamReader(is);
                                //bufReader = new BufferedReader(reader);
                                s = bufReader.readLine();
//                                if (!(s = bufReader.readLine()).equals("\n")) {
//                                };
                                //System.out.println(s);
                                try{
                                    if( s.length()<10){
                                        throw new IOException("something wrong wiht readline, will reconnect");
                                    }
                                }catch (NullPointerException e){
                                    throw new IOException("something wrong wiht readline, will reconnect");
                                }
                                try{
                                    object = JsonParser.parseString(s).getAsJsonObject(); //???json??????
                                }catch (JsonSyntaxException e){
                                    throw new IOException("something wrong wiht Json, will reconnect");
                                }
                                String msg_type = object.get("type").getAsString();
                                //String msg_info = object.get("info").getAsString();
                                // ???????????????
//                                if(msg_type.equals("ack")) {
//                                    String info = object.get("info").getAsString();
//                                    //System.out.println(info);
//                                }else
                                if(msg_type.equals("kdj")){
                                    args.kdj_info=object.get("symbol").getAsString()+" "+object.get("info").getAsString();
                                    args.kdj_info=args.kdj_info.replace("USDT","");
                                    mTextToSpeech.speak(args.kdj_info, TextToSpeech.QUEUE_FLUSH, null);
                                    if (!mTextToSpeech.isSpeaking()) {
                                        initTextToSpeech();
                                        System.out.println("tts restarted");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                            try {Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }//??????
                            //if(!INITIAILIZED) throw new IOException("something wrong wiht INITIAILIZED, will reconnect");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //3?????????IO??????????????????????????????????????????finally??????
                        try {
                            //bufReader.close();
                            //reader.close();
                            //is.close();
                            //os.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        args.count_time[0]=0;
                    }
                }

            }
        }).start();
    }

    /**
     * ???????????????TextToSpeech??????
     *
     * @param status SUCCESS???ERROR???2??????
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            /*
                ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            ????????????????????????????????????????????????????????????????????????????????????????????????????????????TTS????????????
            ???????????????????????????Pico TTs???????????????????????????3.0?????????????????????3.0?????????Pico TTS?????????
            ??????????????????????????????????????????????????????????????????????????????3.0??????????????????

                ??????????????????????????????????????????????????????????????????
            ???????????????????????????Module??????????????????????????????????????????3.0.apk??????????????????????????????????????????
            ??????????????????????????????????????????TTS??????????????????????????????????????????????????????3.0???????????????????????????
            Demo???????????????????????????????????????
             */
            // setLanguage????????????
            int result = mTextToSpeech.setLanguage(Locale.ENGLISH);
            // TextToSpeech.LANG_MISSING_DATA??????????????????????????????
            // TextToSpeech.LANG_NOT_SUPPORTED????????????
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                System.out.println("????????????????????????");
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        // ????????????????????????TTS????????????
        mTextToSpeech.stop();
        // ?????????????????????
        mTextToSpeech.shutdown();
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
        super.onDestroy();
    }
    private void initTextToSpeech() {
        // ??????Context,TextToSpeech.OnInitListener
        mTextToSpeech = new TextToSpeech(this,this);
        // ???????????????????????????????????????????????????????????????????????????,1.0?????????
        mTextToSpeech.setPitch(1.0f);
        // ????????????
        mTextToSpeech.setSpeechRate(0.6f);
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Toast.makeText(MainActivity.this, "???????????????" + progress, Toast.LENGTH_SHORT).show();
            txt_current_positon.setText(String.format("?????? %d%% %.2fUSDT",progress,args.withdrawAvailable));
            args.PERCEMT=progress/100f;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(MainActivity.this, "?????????" + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(MainActivity.this, "?????????" + seekBar.getProgress(), Toast.LENGTH_SHORT).show();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????
        args.MAX_AND_MIN[0]=0;
        args.MAX_AND_MIN[1]=100;
        args.MAX_AND_MIN[3]=0;
        args.MAX_AND_MIN[4]=100;
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        setContentView(R.layout.activity_main);
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.layout1);
        //layout1.setOnTouchListener(this);
        audio=new AudioTrackManager();
        gLView = new MyGLSurfaceView(this);
        gLView.setRenderMode(gLView.RENDERMODE_WHEN_DIRTY);
        //gLView.setRenderMode(gLView.RENDERMODE_CONTINUOUSLY);
        layout1.addView(gLView);
        ViewGroup.LayoutParams p=layout1.getLayoutParams();
//        WindowManager wm = getWindowManager();
//        Display d =  this.getDisplay() ;
//        p.width=(int)(d.getWidth()*6.0/7);
        txt_current_positon=(CheckedTextView)findViewById(R.id.current_positon);
        btn_order_type=(ToggleButton)findViewById(R.id.order_type);
        btn_order_side=(Button) findViewById(R.id.order_side);
        btn_loss_and_profit=(ToggleButton) findViewById(R.id.loss_and_profit);
        btn_Fibonacci=(ToggleButton) findViewById(R.id.Fibonacci);
        btn_cancel_order=(Button)findViewById(R.id.cancel_order);
        btn_open_order=(Button) findViewById(R.id.open_order);
        btn_sell_order=(Button) findViewById(R.id.sell_order);
        btn_close_order=(Button) findViewById(R.id.close_order);
        btn_turn_order=(Button)  findViewById(R.id.turn_order);
        btn_choise_coin=(Button) findViewById(R.id.choise_coin);
        btn_lever=(Button)  findViewById(R.id.lever);
        btn_condition_order=(Button)  findViewById(R.id.condition_order);
        Switch1=(Switch) findViewById(R.id.Switch1);
        btn_change_coin=(Button) findViewById(R.id.change_coin);
        SeekBar_position=(SeekBar)findViewById(R.id.position);
        initTextToSpeech();
        SeekBar_position.setProgress(0);
        SeekBar_position.setOnSeekBarChangeListener(onSeekBarChangeListener);
        btn_order_type.setChecked(true);
        btn_order_type.setTextColor(Color.GREEN);
        btn_loss_and_profit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ????????????????????????????????????????????????
                if (btn_loss_and_profit.isChecked()) {
                    btn_loss_and_profit.setTextColor(Color.GREEN);
                }
                // ?????????????????????????????????????????????
                else {
                    btn_loss_and_profit.setTextColor(Color.RED);
                }
            }
        });

        btn_order_type.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ????????????????????????????????????????????????
                if (btn_order_type.isChecked()) {
                    //btn_order_type.setBackgroundColor(Color.RED);
                    btn_order_type.setTextColor(Color.GREEN);
                }
                // ?????????????????????????????????????????????
                else {
                    //btn_order_type.setBackgroundColor(Color.GREEN);
                    btn_order_type.setTextColor(Color.GREEN);
                }
            }
        });

        btn_Fibonacci.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ????????????????????????????????????????????????
                if (btn_Fibonacci.isChecked()) {
                    args.begin_price=args.figer_price;
                }
                // ?????????????????????????????????????????????
                else {
                    args.end_price=args.figer_price;
                }
            }
        });

        btn_lever.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //audio.playBeep(1,500);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("?????????????????????");
                final String[] sex = {"1","3","5", "7","10", "15", "20"};
                //    ?????????????????????????????????
                /**
                 * ???????????????????????????????????????????????????????????????????????????
                 * ???????????????????????????????????????????????????????????????????????????1????????????????????? ???????????????
                 * ?????????????????????????????????????????????????????????
                 */
                builder.setSingleChoiceItems(sex, 1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        args.LEVER= Integer.parseInt(sex[which]);
                        //args.com_queue.put("{:"change""});
                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        btn_lever.setText(""+args.LEVER);
                        btn_lever.setTextColor(Color.RED);
                        JSONObject JS=new JSONObject();
                        try{
                            JS.put("type","SET_LEVER");
                            JS.put("LEVER",args.LEVER);
                            try{
                                args.com_queue.put(JS.toString());
                            }catch (InterruptedException e){
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(MainActivity.this, "???????????????" , Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            Toast.makeText(MainActivity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });

        btn_change_coin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("?????????k?????????");
                final String[] sex = {"1m", "3m","5m", "15m","30m", "1h", "4h","1d"};
                //    ?????????????????????????????????
                /**
                 * ???????????????????????????????????????????????????????????????????????????
                 * ???????????????????????????????????????????????????????????????????????????1????????????????????? ???????????????
                 * ?????????????????????????????????????????????????????????
                 */
                builder.setSingleChoiceItems(sex, 1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        args.KLINE_INTERVAL_TEMP=sex[which];
                        //args.com_queue.put("{:"change""});
                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.KLINE_INTERVAL_TEMP.equals(args.KLINE_INTERVAL)==false) {
                            args.KLINE_INTERVAL=args.KLINE_INTERVAL_TEMP;
                            btn_change_coin.setText(args.KLINE_INTERVAL);
                            btn_change_coin.setTextColor(Color.GREEN);
                            JSONObject JS=new JSONObject();
                            try{
                                JS.put("type","RESET");
                                JS.put("symbol",args.COIN);
                                JS.put("INTERVAL",args.KLINE_INTERVAL);
                                try{
                                    INITIAILIZED=false;
                                    args.com_queue.put(JS.toString());
                                }catch (InterruptedException e){
                                    Toast.makeText(MainActivity.this, "??????KLINE_INTERVAL?????????" , Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(MainActivity.this, "???????????????" , Toast.LENGTH_SHORT).show();
                            }catch (JSONException e){
                                Toast.makeText(MainActivity.this, "????????????KLINE_INTERVAL?????????" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });
        btn_choise_coin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("??????????????????");
                final String[] sex ={"BTCUSDT", "ETHUSDT", "BCHUSDT", "XRPUSDT", "EOSUSDT", "LTCUSDT", "TRXUSDT", "ETCUSDT", "LINKUSDT", "XLMUSDT", "ADAUSDT",
                        "XMRUSDT", "DASHUSDT", "ZECUSDT", "XTZUSDT", "BNBUSDT", "ATOMUSDT", "ONTUSDT", "IOTAUSDT", "BATUSDT", "VETUSDT", "NEOUSDT", "QTUMUSDT",
                        "IOSTUSDT", "THETAUSDT", "ALGOUSDT", "ZILUSDT", "KNCUSDT", "ZRXUSDT", "COMPUSDT", "OMGUSDT", "DOGEUSDT", "SXPUSDT", "KAVAUSDT", "BANDUSDT", "RLCUSDT",
                        "WAVESUSDT", "MKRUSDT", "SNXUSDT", "DOTUSDT", "DEFIUSDT", "YFIUSDT", "BALUSDT", "CRVUSDT", "TRBUSDT", "YFIIUSDT", "RUNEUSDT", "SUSHIUSDT",
                        "SRMUSDT", "BZRXUSDT", "EGLDUSDT", "SOLUSDT", "ICXUSDT", "STORJUSDT", "BLZUSDT", "UNIUSDT", "AVAXUSDT", "FTMUSDT", "HNTUSDT", "ENJUSDT",
                        "FLMUSDT", "TOMOUSDT", "RENUSDT", "KSMUSDT", "NEARUSDT", "AAVEUSDT", "FILUSDT", "RSRUSDT", "LRCUSDT", "MATICUSDT", "OCEANUSDT", "CVCUSDT",
                        "BELUSDT", "CTKUSDT", "AXSUSDT", "ALPHAUSDT", "ZENUSDT", "SKLUSDT", "GRTUSDT", "1INCHUSDT", "AKROUSDT", "CHZUSDT", "SANDUSDT", "ANKRUSDT",
                        "LUNAUSDT", "BTSUSDT", "LITUSDT", "UNFIUSDT", "DODOUSDT", "REEFUSDT", "RVNUSDT", "SFPUSDT", "XEMUSDT", "COTIUSDT", "CHRUSDT", "MANAUSDT",
                        "ALICEUSDT", "HBARUSDT", "ONEUSDT", "LINAUSDT", "STMXUSDT", "DENTUSDT", "CELRUSDT", "HOTUSDT", "MTLUSDT", "OGNUSDT", "BTTUSDT", "NKNUSDT",
                        "SCUSDT", "DGBUSDT", "1000SHIBUSDT", "ICPUSDT", "BAKEUSDT", "GTCUSDT", "BTCDOMUSDT", "KEEPUSDT", "TLMUSDT", "IOTXUSDT"};
                //    ?????????????????????????????????
                /**
                 * ???????????????????????????????????????????????????????????????????????????
                 * ???????????????????????????????????????????????????????????????????????????1????????????????????? ???????????????
                 * ?????????????????????????????????????????????????????????
                 */
                builder.setSingleChoiceItems(sex, 1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        args.COIN_TEMP=sex[which];
                        //args.com_queue.put("{:"change""});
                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.COIN_TEMP.equals(args.COIN)==false) {
                            args.COIN=args.COIN_TEMP;
                            btn_choise_coin.setText(args.COIN.replace("USDT",""));
                            btn_choise_coin.setTextColor(Color.GREEN);
                            JSONObject JS=new JSONObject();
                            try{
                                JS.put("type","RESET");
                                JS.put("symbol",args.COIN);
                                JS.put("INTERVAL",args.KLINE_INTERVAL);
                                try{
                                    INITIAILIZED=false;
                                    args.com_queue.put(JS.toString());
                                    int index=N.indexof(args.COIN);
                                    args.format=String.format("%%.%df",N.pricePrecision[index]);
                                }catch (InterruptedException e){
                                    Toast.makeText(MainActivity.this, "????????????????????????" , Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(MainActivity.this, "???????????????" , Toast.LENGTH_SHORT).show();
                            }catch (JSONException e){
                                Toast.makeText(MainActivity.this, "????????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });

        btn_cancel_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("????????????");
                //    ??????Content?????????????????????
                String msg="?????????????????????????";
                builder.setMessage(msg);
                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.BuySellPosList.size()>0){
                            int index=(int)((1000-(args.figer_price-args.bottom_price)/(args.top_price-args.bottom_price)*1080)/50);
                            if (index<0 || index>args.BuySellPosList.size()-1){
                                index=args.BuySellPosList.size()-1;
                            }
                            Map dict= (Map) args.BuySellPosList.get(index);
                            String orderId=(String)dict.get("orderId");
                            String symbol=(String)dict.get("symbol");
                            String ORDER=String.format( "{'type':'order','side':'CANCEL','order_type':'MARKET','orderId':'%s','symbol':'%s'}",orderId,symbol);
                            System.out.printf(" ORDERxy= "+ORDER);
                            try {
                                args.com_queue.put(ORDER);
                                Toast.makeText(MainActivity.this, "????????????" , Toast.LENGTH_SHORT).show();
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "???????????????????????????" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });

        btn_open_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("????????????");
                //    ??????Content?????????????????????
                float price=args.figer_price;
                if(btn_order_type.getText().equals("??????"))
                    price=args.data_close[args.limit-1];
                String msg=btn_order_type.getText().toString()+String.format(" ????????? %2.3f ?????????%d",price,SeekBar_position.getProgress());
                builder.setMessage(msg);

                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String side="BUY";
                        float price=args.data_close[args.limit-1];
                        String type="MARKET";
                        if (btn_order_type.getText().equals("??????"))
                        {
                            type="LIMIT";
                        }
                        if(btn_order_type.getText().equals("??????") && args.figer_price>price && !Switch1.isChecked()){
                            Toast.makeText(MainActivity.this, "????????????????????????!" , Toast.LENGTH_SHORT).show();
                        }else
                        {
                            // ??????????????????
                            price=Math.min(args.figer_price,price);
                            if(Switch1.isChecked()){
                                if(btn_loss_and_profit.getText().equals("??????")){
                                    //Toast.makeText(MainActivity.this, "Switch1 ??????!!!" , Toast.LENGTH_SHORT).show();
                                    type="STOP_MARKET";
                                }
                                else
                                    type="TAKE_PROFIT_MARKET";
                                price=args.figer_price;
                            }
                            String qty=N.QTY(args.COIN,args.withdrawAvailable*args.LEVER/price*0.99f*args.PERCEMT);;
                            String pri=N.PRICE(args.COIN,price);
                            String ORDER=String.format("{'type':'order','symbol':'%s','side':'%s','order_type':'%s','price':%s,'amount':%s}",args.COIN,side,type,pri,qty);
                            try {
                                args.com_queue.put(ORDER);
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(MainActivity.this, "?????????"+ORDER , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });
        btn_sell_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("????????????");
                //    ??????Content?????????????????????
                float price=args.figer_price;
                if(btn_order_type.getText().equals("??????"))
                    price=args.data_close[args.limit-1];
                String msg=btn_order_type.getText().toString()+String.format(" ????????? %2.3f ?????????%d",price,SeekBar_position.getProgress());
                builder.setMessage(msg);

                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String side="SELL";
                        float price=args.data_close[args.limit-1];
                        String type="MARKET";
                        if (btn_order_type.getText().equals("??????"))
                        {
                            type="LIMIT";
                        }
                        if(btn_order_type.getText().equals("??????") && args.figer_price<price && !Switch1.isChecked()){
                            Toast.makeText(MainActivity.this, "?????????????????????!" , Toast.LENGTH_SHORT).show();
                        }else{
                            // ??????????????????
                            price=Math.max(args.figer_price,price);
                            if(Switch1.isChecked()){
                                if(btn_loss_and_profit.getText().equals("??????")){
                                    //Toast.makeText(MainActivity.this, "Switch1 ??????!!!" , Toast.LENGTH_SHORT).show();
                                    type="STOP_MARKET";
                                }
                                else
                                    type="TAKE_PROFIT_MARKET";
                                price=args.figer_price;
                            }
                            String qty=N.QTY(args.COIN,args.withdrawAvailable*args.LEVER/price*0.99f*args.PERCEMT);;
                            String pri=N.PRICE(args.COIN,price);
                            String ORDER=String.format("{'type':'order','symbol':'%s','side':'%s','order_type':'%s','price':%s,'amount':%s}",args.COIN,side,type,pri,qty);
                            //System.out.printf(ORDER);
                            try {
                                args.com_queue.put(ORDER);
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(MainActivity.this, "?????????"+ORDER , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });

        btn_condition_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("???????????????");
                //    ??????Content?????????????????????
                float price=args.figer_price;
                if(btn_order_type.getText().equals("??????"))
                    price=args.data_close[args.limit-1];
                String msg=btn_order_type.getText().toString()+btn_order_side.getText().toString()+String.format(" ????????? %2.3f ?????????%d",price,SeekBar_position.getProgress());
                builder.setMessage(msg);

                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String side="BUY";
                        if (btn_order_side.getText().equals("??????"))
                            side="SELL";
                        float price=args.data_close[args.limit-1];
                        String type="MARKET";
                        if (btn_order_type.getText().equals("??????"))
                        {
                            type="LIMIT";
                            // ??????????????????
                            if(btn_order_side.getText().equals("??????")){
                                price=Math.min(args.figer_price,price);
                            }
                            if(btn_order_side.getText().equals("??????")){
                                price=Math.max(args.figer_price,price);
                            }
                        }

                        String qty=N.QTY(args.COIN,args.withdrawAvailable*args.LEVER/price*0.99f*args.PERCEMT);;
                        String pri=N.PRICE(args.COIN,price);
                        String orderid=getRandomString(10);
                        String ORDER=String.format("{'type':'condition_order','side':'%s','order_type':'%s','orderid':'%s','price':%s,'amount':%s,'condition':'%%s<%%s'}",side,type,orderid,pri,qty);
                        try {
                            args.com_queue.put(ORDER);
                        }catch (InterruptedException e)
                        {
                            Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MainActivity.this, "?????????"+ORDER , Toast.LENGTH_SHORT).show();
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        btn_order_side.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("??????????????????");
                //    ??????Content?????????????????????
                builder.setMessage("??????????????????");
                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.postion!=0.0f){
                            String type="MARKET";
                            float price=args.data_close[args.limit-1];
                            String pri=N.PRICE(args.COIN,price);
                            String qty=N.QTY(args.COIN,Math.abs(args.postion));

                            String side="SELL";
                            if(args.postion<0) side="BUY";
                            String ORDER=String.format("{'type':'order','symbol':'%s','side':'%s','order_type':'%s','price':%s,'amount':%s}",args.COIN,side,type,pri,qty);
                            try {
                                args.com_queue.put(ORDER);
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "?????????0", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "????????????: " , Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });
        btn_close_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("????????????");
                //    ??????Content?????????????????????
                builder.setMessage("??????????????????");
                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.postion!=0.0f){
                            String type="MARKET";
                            float price=args.data_close[args.limit-1];
                            if (btn_order_type.getText().equals("??????"))
                            {
                                type="LIMIT";
                                // ??????????????????
                                if(args.postion<0){
                                    price=Math.min(args.figer_price,price);
                                }
                                if(args.postion>0){
                                    price=Math.max(args.figer_price,price);
                                }
                            }
                            String pri=N.PRICE(args.COIN,price);
                            String qty=N.QTY(args.COIN,Math.abs(args.postion));

                            String side="SELL";
                            if(args.postion<0) side="BUY";
                            String ORDER=String.format("{'type':'order','symbol':'%s','side':'%s','order_type':'%s','price':%s,'amount':%s}",args.COIN,side,type,pri,qty);
                            try {
                                args.com_queue.put(ORDER);
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "?????????0", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "????????????: " , Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });
        btn_turn_order.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //    ??????AlertDialog.Builder????????????????????????????????????AlertDialog?????????
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //    ??????Title?????????
                //builder.setIcon(R.drawable.ic_launcher);
                //    ??????Title?????????
                builder.setTitle("????????????");
                //    ??????Content?????????????????????
                builder.setMessage("??????????????????");
                //    ????????????PositiveButton
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(args.postion!=0){
                            float price=args.data_close[args.limit-1]; //?????????????????????
                            String type="MARKET";
                            if (btn_order_type.getText().equals("??????"))
                            {
                                type="LIMIT";
                                // ??????????????????
                                if(args.postion<0){
                                    price=Math.min(args.figer_price,price);
                                }
                                if(args.postion>0){
                                    price=Math.max(args.figer_price,price);
                                }
                            }
                            String pri=N.PRICE(args.COIN,price);
                            String qty=N.QTY(args.COIN,Math.abs(2*args.postion));//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            String ORDER=String.format("{'type':'order','symbol':'%s','side':'SELL','order_type':'%s','price':%s,'amount':%s}",args.COIN,type,pri,qty);
                            if(args.postion<0)
                                ORDER=String.format("{'type':'order','symbol':'%s','side':'BUY','order_type':'%s','price':%s,'amount':%s}",args.COIN,type,pri,qty);
                            try {
                                args.com_queue.put(ORDER);
                            }catch (InterruptedException e)
                            {
                                Toast.makeText(MainActivity.this, "?????????????????????" , Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(MainActivity.this, "?????????"+ORDER , Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "?????????0", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //    ????????????NegativeButton
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "????????????: " , Toast.LENGTH_SHORT).show();
                    }
                });
                //    ?????????????????????
                AlertDialog dialog= builder.create();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y=400;
                dialog.show();
                //Toast.makeText(MainActivity.this, "test!!!" , Toast.LENGTH_SHORT).show();
            }
        });

        // start_cmd_Listen();
        speaker();
        startListen();
        startListen_market();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;
    private ScaleGestureDetector mScaleDetector;
    private float mLastTouchX,mLastTouchY,mPosX,mPosY ;
    private int mActivePointerId;
    public MyGLSurfaceView(Context context){
        super(context);
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = 0;//MotionEventCompat.getActionIndex(ev);
                final float x =ev.getX();// MotionEventCompat.getX(ev, pointerIndex);
                final float y =ev.getY();// MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                renderer.finger_pos_y = (float) y;
                renderer.finger_pos_x = (float) x;
                requestRender();
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =0;//MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = ev.getX();// MotionEventCompat.getX(ev, pointerIndex);
                final float y = ev.getY();// MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
                if(true) {
                    //renderer.mTriggerVoltage = (float) y;
                    renderer.finger_pos_y = (float) y;
                    renderer.finger_pos_x = (float) x;
                    requestRender();
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = 0;
                final float x = ev.getX();// MotionEventCompat.getX(ev, pointerIndex);
                final float y = ev.getY();// MotionEventCompat.getY(ev, pointerIndex);
                if(true) {
                    renderer.finger_pos_y = (float) y;
                    renderer.finger_pos_x = (float) x;
                    requestRender();
                }

               // System.out.printf("xy= %f %f\n",x ,renderer.finger_pos_y);
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactorx=renderer.getScalex();
            float x=detector.getCurrentSpanX();
            float y=detector.getCurrentSpanY();
            if(x>y) {
                mScaleFactorx *= detector.getScaleFactor();
                // Don't let the object get too small or too large.
                mScaleFactorx = Math.min(20.0f, Math.max(mScaleFactorx, 1.0f));
                renderer.setScalex(mScaleFactorx);
            }
            requestRender();
            return true;
        }
    }
}

