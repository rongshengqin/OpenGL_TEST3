package com.example.opengl_test3;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import java.util.Arrays;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mTriangle;
    private FFT fft=new FFT(1024,3200000);
    private NUM N =new NUM();
    float fftRealArray[]=new float[1024];
    float spectrum[]=new float[1024];
    float freq_resolution=1000000000f;
    float freq_sample=3200000;
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        // initialize a triangle
        mTriangle = new Triangle();
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //
    }
    public volatile float finger_pos_y=1000;
    public volatile float finger_pos_x=1000;
    public volatile float mScaleFactorx=10;
    public volatile float mScaleFactory=0.1f;
    private float bottom_price=0f;
    private float top_price=100f;
    private float finger_price=0;
    private float txt_color=0;
    public int mWidth=1;
    public int mHeight=1;

    public float getScalex() {
        return mScaleFactorx;
    }
    public void setScalex(float Scale) {
        mScaleFactorx = Scale;
    }
    public float getScaley() {
        return mScaleFactory;
    }
    public void setScaley(float Scale) {
        mScaleFactory = Scale;
    }
    private float[] rotationMatrix = new float[16];
    int div_x=21;
    int div_y=11;
    float div_v=(2*8192f/(div_y+1))/4096f*3300f;
    float div_t=1f;
    float grids[]=new float[div_x*div_y*6];
    float info_out[]=new float[64];
    static short clock_div[]={256,128,64,32,16,12,10,8,4,2,1};
    static float sample_cirlces[]={160.5f,79.5f,39.5f,19.5f,12.5f,7.5f,3.5f,1.5f};
    int spe_index[]=new int[513];
    private int[] Arraysort(float[] arr, boolean desc) {
        float temp;
        int index;
        int k = arr.length;
        int[] Index = new int[k];
        for (int i = 0; i < k; i++) {
            Index[i] = i;
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (desc) {
                    if (arr[j] < arr[j + 1]) {
                        temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;

                        index = Index[j];
                        Index[j] = Index[j + 1];
                        Index[j + 1] = index;
                    }
                } else {
                    if (arr[j] > arr[j + 1]) {
                        temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;

                        index = Index[j];
                        Index[j] = Index[j + 1];
                        Index[j + 1] = index;
                    }
                }
            }
        }
        return Index;
    }
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16]; //投影矩阵
    private final float[] projectionMatrix = new float[16];
    private final float[] projectionMatrix_for_btc = new float[16]; //绘制文字时的投影矩阵
    private final float[] projectionMatrix_for_txt = new float[16]; //绘制文字时的投影矩阵
    private final float[] projectionMatrix_for_volume = new float[16]; //绘制文字时的投影矩阵
    private final float[] projectionMatrix_for_KDJ = new float[16]; //绘制KDJ时的投影矩阵
    private final float[] projectionMatrix_for_BKDJ = new float[16];

    private final float[] viewMatrix = new float[16];
    private   float LINDE_WIDTH=1f;

    private void update_projectionMatrix(){
        float right=1200;
        float left=0;//(mScaleFactorx-1f)*110;
        int begin=(int)(1000-1000f/mScaleFactorx);
        float pl=args.data_low[begin];
        float ph=args.data_high[begin];
        float vh=args.data_volume[begin];
        float bpl=args.btc_data_low[begin];
        float bph=args.btc_data_high[begin];
        float bvh=args.btc_data_volume[begin];
        if(pl==ph) {pl=0;ph=100;}
        if(bpl==bph) {bpl=0;bph=100;}
        for (int i=begin;i<args.limit;i++){
            pl=Math.min(pl,args.data_low[i]);
            ph=Math.max(ph,args.data_high[i]);
            vh=Math.max(vh,args.data_volume[i]);
            bpl=Math.min(bpl,args.btc_data_low[i]);
            bph=Math.max(bph,args.btc_data_high[i]);
            bvh=Math.max(bvh,args.btc_data_volume[i]);
        }
        args.MAX_AND_MIN[0]=pl;
        args.MAX_AND_MIN[1]=ph;
        args.MAX_AND_MIN[2]=vh;
        args.MAX_AND_MIN[3]=bpl;
        args.MAX_AND_MIN[4]=bph;
        args.MAX_AND_MIN[5]=bvh;
        //LINDE_WIDTH=mWidth/(right-left);
        float low_price=args.MAX_AND_MIN[0];
        float high_price=args.MAX_AND_MIN[1];
        float price_range=high_price-low_price;
        if (price_range<0){price_range=100f;}
        bottom_price=low_price-price_range/4*2;
        top_price=high_price+price_range/4*6;
        args.top_price=top_price;
        args.bottom_price=bottom_price;
        int pi=0;
        float dy=(top_price-bottom_price)/12;
        for (int i=0;i<5;i++){
            grids[pi*6]=left;
            grids[pi*6+1]=low_price+i*dy;
            grids[pi*6+2]=2;
            grids[pi*6+3]=right;
            grids[pi*6+4]=low_price+i*dy;
            grids[pi*6+5]=2;
            pi+=1;
        }
        finger_price=bottom_price+(mHeight-finger_pos_y)/mHeight*(top_price-bottom_price);
        args.figer_price=finger_price;
        grids[pi*6]=left;
        grids[pi*6+1]=finger_price;
        grids[pi*6+2]=0;
        grids[pi*6+3]=right;
        grids[pi*6+4]=finger_price;
        grids[pi*6+5]=0;
        pi+=1;
        float cp=args.open_close[(args.limit-1)*6+4];
        float op=args.open_close[(args.limit-1)*6+1];
        txt_color=0;
        if(cp>op){ txt_color=1f;}
        //cp=(cp-bottom_price)/(top_price-bottom_price)*1080;
        grids[pi*6]=1000;
        grids[pi*6+1]=cp;
        grids[pi*6+2]=txt_color;
        grids[pi*6+3]=right;
        grids[pi*6+4]=cp;
        grids[pi*6+5]=txt_color;
        pi+=1;

        float tx=(finger_pos_x/mWidth)*1200f ;//1000-1000/mScaleFactorx+(finger_pos_x/mWidth)*1100/mScaleFactorx;///    (finger_pos_x/mWidth)*1100f*mScaleFactorx;
        args.FINGER_INDEX=Math.max(0, Math.min(999,Math.round(tx/mScaleFactorx+1000-1000f/mScaleFactorx)));
        grids[pi*6]=tx;
        grids[pi*6+1]=top_price;
        grids[pi*6+2]=2;
        grids[pi*6+3]=tx;
        grids[pi*6+4]=bottom_price;
        grids[pi*6+5]=2;
        pi+=1;
        try {
            for (int i = 0; i < args.OrderList.size(); i++) {
                Map dict = (Map) args.BuySellPosList.get(i);
                float pr = (float) dict.get("price");
                float py = (pr - bottom_price) / (top_price - bottom_price) * 1080;
                int c = (int) dict.get("color");
                grids[pi * 6] = 1000;
                grids[pi * 6 + 1] = pr;
                grids[pi * 6 + 2] = c;
                grids[pi * 6 + 3] = right;
                grids[pi * 6 + 4] = pr;
                grids[pi * 6 + 5] = c;
                pi += 1;
            }
            }  catch(IndexOutOfBoundsException e){
                System.out.printf("IndexOutOfBoundsException");
            }
        if(args.entryPrice!=0.0f){
            float c=1; if (args.postion<0.0f) c=0;
            float py = (args.entryPrice - bottom_price) / (top_price - bottom_price) * 1080;
            grids[pi * 6] = 1000;
            grids[pi * 6 + 1] = args.entryPrice;
            grids[pi * 6 + 2] = c;
            grids[pi * 6 + 3] = right;
            grids[pi * 6 + 4] = args.entryPrice;
            grids[pi * 6 + 5] = c;
            pi += 1;
        }
        //Fibonacci回撤分析
        grids[pi * 6] = 1000;
        grids[pi * 6 + 1] = args.begin_price;
        grids[pi * 6 + 2] = 5;
        grids[pi * 6 + 3] = right;
        grids[pi * 6 + 4] = args.begin_price;
        grids[pi * 6 + 5] = 5;
        pi += 1;
        grids[pi * 6] = 1000;
        grids[pi * 6 + 1] = args.begin_price+(args.end_price-args.begin_price)*0.382f;
        grids[pi * 6 + 2] = 5;
        grids[pi * 6 + 3] = right;
        grids[pi * 6 + 4] = args.begin_price+(args.end_price-args.begin_price)*0.382f;
        grids[pi * 6 + 5] = 5;
        pi += 1;
        grids[pi * 6] = 1000;
        grids[pi * 6 + 1] = args.begin_price+(args.end_price-args.begin_price)*0.5f;
        grids[pi * 6 + 2] = 5;
        grids[pi * 6 + 3] = right;
        grids[pi * 6 + 4] = args.begin_price+(args.end_price-args.begin_price)*0.5f;
        grids[pi * 6 + 5] =5;
        pi += 1;
        grids[pi * 6] = 1000;
        grids[pi * 6 + 1] = args.begin_price+(args.end_price-args.begin_price)*0.618f;
        grids[pi * 6 + 2] = 5;
        grids[pi * 6 + 3] = right;
        grids[pi * 6 + 4] = args.begin_price+(args.end_price-args.begin_price)*0.618f;
        grids[pi * 6 + 5] = 5;
        pi += 1;
        grids[pi * 6] = 1000;
        grids[pi * 6 + 1] = args.end_price;
        grids[pi * 6 + 2] = 5;
        grids[pi * 6 + 3] = right;
        grids[pi * 6 + 4] = args.end_price;
        grids[pi * 6 + 5] = 5;
        pi += 1;
        args.GRID_POINTS=pi;
        //System.out.println("pi= "+pi);
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom_price, top_price, 3, 7);
        Matrix.frustumM(projectionMatrix_for_volume, 0, left, right, 0, 10.0f, 3, 7);
        Matrix.frustumM(projectionMatrix_for_btc, 0, left, right, args.MAX_AND_MIN[3], args.MAX_AND_MIN[4]+2*(args.MAX_AND_MIN[4]-args.MAX_AND_MIN[3]), 3, 7);
        Matrix.frustumM(projectionMatrix_for_KDJ, 0, left, right, -10, 800.0f, 3, 7);
        Matrix.frustumM(projectionMatrix_for_BKDJ, 0, left, right, -450, 350.0f, 3, 7);
        //
        talib.getEMA(args.btc_data_close,args.btc_ma10,10,6);
        talib.getEMA(args.btc_data_close,args.btc_ma5,5,7);
        talib.getKDJ(args.btc_data_open,args.btc_data_close,args.btc_data_high,args.btc_data_low,args.BK,args.BD,args.BJ, 9, 3, 3, 1);
        talib.getEMA(args.data_close,args.ma10,10,6);
        talib.getEMA(args.data_close,args.ma5,5,7);
        talib.getKDJ(args.data_open,args.data_close,args.data_high,args.data_low,args.K,args.D,args.J, 9, 3, 3, 1);
        talib.getBoll(args.data_close, args.MB,args.UP,args.DN,20);
        talib.getBoll(args.btc_data_close, args.BMB,args.BUP,args.BDN,20);
//      talib.getEMA(args.data_volume,args.volume_ma5,5,1);
//      talib.getEMA(args.data_volume,args.volume_ma10,10,4);
        // 检测买入卖出信号
        String msg=null;
        if(args.K[(args.limit-1)*3+1]>args.D[(args.limit-1)*3+1] && args.K[(args.limit-2)*3+1]<args.D[(args.limit-2)*3+1]){
            // KDJ金叉
            msg="KDJ up";
        }
        if(args.K[(args.limit-1)*3+1]<args.D[(args.limit-1)*3+1] && args.K[(args.limit-2)*3+1]>args.D[(args.limit-2)*3+1]){
            // KDJ死叉
            msg="KDJ down";
        }
        if(args.ma5[(args.limit-1)*3+1]>args.ma10[(args.limit-1)*3+1] && args.ma5[(args.limit-2)*3+1]<args.ma10[(args.limit-2)*3+1]){
            // 均线金叉
            msg="MA up";
        }
        if(args.ma5[(args.limit-1)*3+1]<args.ma10[(args.limit-1)*3+1] && args.ma5[(args.limit-2)*3+1]>args.ma10[(args.limit-2)*3+1]){
            // 均线死叉
            msg="MA down";
        }
        //System.out.println("speaker_"+msg);
        if (!args.speaker_is_busy && msg!=null){
            //System.out.println("speaker_not_busy");
            try{
                args.msg_queue.put(msg);
            }catch ( InterruptedException e){
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        args.count_time[0]=args.count_time[0]+1;
        //var fps=0
        if(args.count_time[0]==10L){
            float dt=(SystemClock.uptimeMillis()-args.count_time[1])/1000f;
            args.count_time[2]= (long)(10f/dt);
            args.fps=10f/dt;
            args.count_time[0]=0;
            args.count_time[1]= SystemClock.uptimeMillis();
        }
        //float buf[]=args.queue.poll();
        // Redraw background color
        GLES20.glClearColor(1.0f,1.0f,1.0f,1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        update_projectionMatrix();
        //float[] scratch = new float[16];
        // Create a rotation transformation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        //Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, 1.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
        // Calculate the projection and view transformation

        // 画k线
        float temp_m[]=new float[16];
        temp_m=vPMatrix.clone();
        Matrix.translateM(temp_m,0,-1000f*mScaleFactorx+1000,0,0f);
        Matrix.scaleM(temp_m,0,mScaleFactorx,1f,1f);
        GLES20.glLineWidth(2);
        mTriangle.draw_grid(vPMatrix,grids,args.GRID_POINTS*2);
        GLES20.glLineWidth(mScaleFactorx);
        mTriangle.draw(temp_m,args.open_close);
        GLES20.glLineWidth(2);
        mTriangle.draw(temp_m,args.high_low);
        GLES20.glLineWidth(5);
        mTriangle.draw_indicator(temp_m,args.ma5,args.limit);
        mTriangle.draw_indicator(temp_m,args.ma10,args.limit);
        GLES20.glLineWidth(1);
        mTriangle.draw_indicator(temp_m,args.MB,args.limit);
        mTriangle.draw_indicator(temp_m,args.UP,args.limit);
        mTriangle.draw_indicator(temp_m,args.DN,args.limit);
        //画kdj指标
        Matrix.multiplyMM(temp_m, 0, projectionMatrix_for_KDJ, 0, viewMatrix, 0);
        Matrix.translateM(temp_m,0,-1000f*mScaleFactorx+1000,0,0f);
        Matrix.scaleM(temp_m,0,mScaleFactorx,1f,1f);
        GLES20.glLineWidth(2);
        mTriangle.draw_indicator(temp_m,args.K,args.limit);
        mTriangle.draw_indicator(temp_m,args.D,args.limit);
        mTriangle.draw_indicator(temp_m,args.J,args.limit);
        // 画btc k线
        //temp_m=vPMatrix.clone();
        //float temp[]=new float[16];
        Matrix.multiplyMM(temp_m, 0, projectionMatrix_for_btc, 0, viewMatrix, 0);
        //System.out.println("pi= "+args.btc_open_close[(args.limit-1)*6+3]);
        //Matrix.scaleM(temp_m,0,mScaleFactorx,3f/args.MAX_AND_MIN[4],1f);
        Matrix.translateM(temp_m,0,-1000f*mScaleFactorx+1000,2*(args.MAX_AND_MIN[4]-args.MAX_AND_MIN[3]),0f);
        Matrix.scaleM(temp_m,0,mScaleFactorx,1.0f,1f);
        GLES20.glLineWidth(mScaleFactorx);

        mTriangle.draw(temp_m,args.btc_open_close);
        GLES20.glLineWidth(1);
        mTriangle.draw(temp_m,args.btc_high_low);
        GLES20.glLineWidth(5);
        mTriangle.draw_indicator(temp_m,args.btc_ma10,args.limit);
        mTriangle.draw_indicator(temp_m,args.btc_ma5,args.limit);
        GLES20.glLineWidth(1);
        mTriangle.draw_indicator(temp_m,args.BMB,args.limit);
        mTriangle.draw_indicator(temp_m,args.BUP,args.limit);
        mTriangle.draw_indicator(temp_m,args.BDN,args.limit);
        //BTC KDJ指标
        Matrix.multiplyMM(temp_m, 0, projectionMatrix_for_BKDJ, 0, viewMatrix, 0);
        Matrix.translateM(temp_m,0,-1000f*mScaleFactorx+1000,0,0f);
        Matrix.scaleM(temp_m,0,mScaleFactorx,1.0f,1f);
        GLES20.glLineWidth(2);
        mTriangle.draw_indicator(temp_m,args.BK,args.limit);
        mTriangle.draw_indicator(temp_m,args.BD,args.limit);
        mTriangle.draw_indicator(temp_m,args.BJ,args.limit);

        GLES20.glLineWidth(mScaleFactorx);
        Matrix.multiplyMM(temp_m, 0, projectionMatrix_for_volume, 0, viewMatrix, 0);
        Matrix.translateM(temp_m,0,-1000f*mScaleFactorx+1000,5.52f,0f);
        Matrix.scaleM(temp_m,0,mScaleFactorx,0.4f/args.MAX_AND_MIN[5],1f);
        mTriangle.draw(temp_m,args.btc_volume);

        // 画成交量图
        //GLES20.glLineWidth(10);
        //float temp_m[]=new float[16];
        GLES20.glLineWidth(mScaleFactorx);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix_for_volume, 0, viewMatrix, 0);
        Matrix.translateM(vPMatrix,0,-1000f*mScaleFactorx+1000,0.05f,0f);
        Matrix.scaleM(vPMatrix,0,mScaleFactorx,0.4f/args.MAX_AND_MIN[2],1f);
        mTriangle.draw(vPMatrix,args.volume);
//        GLES20.glLineWidth(1);
//        mTriangle.draw_indicator(vPMatrix,args.volume_ma5,args.limit);
//        mTriangle.draw_indicator(vPMatrix,args.volume_ma10,args.limit);
        // 绘制文字，切换回专门的投影矩阵
        // 网格价格
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix_for_txt, 0, viewMatrix, 0);
        float low_price=args.MAX_AND_MIN[0];
        float high_price=args.MAX_AND_MIN[1];
        int n=5;
        float dprice=(high_price-low_price)/4;
        float dposy=(4f/12f*mHeight)/4;
        for(int i =0;i<n;i++){
            mTriangle.draw_text(String.format(args.format,low_price+i*dprice),10,2f/12*mHeight+dposy*i, vPMatrix,2f);
        }
        // kdj
        mTriangle.draw_text(String.format("K:%.1f",args.K[(args.limit-1)*3+1]),10,140, vPMatrix,2f);
        mTriangle.draw_text(String.format("D:%.1f",args.D[(args.limit-1)*3+1]),10,90, vPMatrix,2f);
        mTriangle.draw_text(String.format("J:%.1f",args.J[(args.limit-1)*3+1]),10,50, vPMatrix,2f);
        // btc网格价格
        low_price=args.MAX_AND_MIN[3];
        high_price=args.MAX_AND_MIN[4];
        n=3;
        dprice=(high_price-low_price)/2;
        dposy=(1.0f/3*mHeight)/2;
        for(int i =0;i<n;i++){
            mTriangle.draw_text(String.format("%.2f",low_price+i*dprice),10,2.0f/3*mHeight+dposy*i, vPMatrix,2f);
        }

        mTriangle.draw_text(String.format(args.format,finger_price),950,mHeight-finger_pos_y, vPMatrix,2f);
        float cp=args.open_close[(args.limit-1)*6+4];
        mTriangle.draw_text(String.format(args.format,cp),950,(cp-bottom_price)/(top_price-bottom_price)*mHeight, vPMatrix,txt_color);

        cp=args.btc_open_close[(args.limit-1)*6+4];
        float col=0;
        if(args.btc_open_close[(args.limit-1)*6+1]<cp) col=1;
        mTriangle.draw_text(String.format("%.2f",cp),950,2f/3f*mHeight+(cp-args.MAX_AND_MIN[3])/(args.MAX_AND_MIN[4]-args.MAX_AND_MIN[3])/3f*mHeight, vPMatrix,col);
        try {
            for(int i=0;i<args.OrderList.size();i++){
                mTriangle.draw_text((String)args.OrderStringList.get(i),0,mHeight-args.Y_STEP*i, vPMatrix,1);
                Map dict= (Map) args.BuySellPosList.get(i);
                float pr=(float)dict.get("price");
                float py=(pr-bottom_price)/(top_price-bottom_price)*mHeight;
                int c=(int)dict.get("color");
                if(c>0){
                    mTriangle.draw_text("B"+pr,950,py, vPMatrix,1);
                }else{
                    mTriangle.draw_text("S"+pr,950,py, vPMatrix,0);
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.printf("IndexOutOfBoundsException");
        }
        if(args.postion!=0.0f){
           // float c=1; if (args.postion<0.0f) c=0;
            float py = (args.entryPrice - bottom_price) / (top_price - bottom_price) * mHeight;
            if (args.postion<0.0f)
                mTriangle.draw_text("PS"+args.entryPrice,970,py, vPMatrix,0);
            else
                mTriangle.draw_text("PB"+args.entryPrice,970,py, vPMatrix,1);
        }

        mTriangle.draw_text(args.ORDER_INFO_STRING,10,810, vPMatrix,2);
        String qty=N.QTY(args.COIN,args.withdrawAvailable*args.LEVER/args.figer_price*0.99f);
        String cqty=N.QTY(args.COIN,args.withdrawAvailable*args.LEVER/args.figer_price*0.99f*args.PERCEMT);
        mTriangle.draw_text("N:"+cqty,950,120, vPMatrix,1);
        mTriangle.draw_text("M:"+qty,950,80, vPMatrix,1);
        //mTriangle.draw_text("I:"+args.FINGER_INDEX,950,200, vPMatrix,1);
        //打印仓位信息
        float c=0;if(args.unRealizedProfit>0) c=1;
        float c2=1;if(args.postion==0.0f) {c2=2.0f;c=c2;}
        mTriangle.draw_text("PO:"+args.postion,900,1020, vPMatrix,c2);
        mTriangle.draw_text("PR:"+args.entryPrice,900,980, vPMatrix,c2);
        mTriangle.draw_text("MP:"+args.markPrice,900,940, vPMatrix,c2);
        mTriangle.draw_text("PF:"+args.unRealizedProfit,900,900, vPMatrix,c);
        float profitRate=0;if(args.postion!=0) profitRate=(args.markPrice-args.entryPrice)/args.entryPrice*args.leverage*100*args.postion/Math.abs(args.postion);
        mTriangle.draw_text(String.format( "RT:%.3f%%",profitRate),900,860, vPMatrix,c);
        String fps=String.format("%.2f",args.fps);
        mTriangle.draw_text("F:" + fps+" "+args.count_time[0],950,40, vPMatrix,1);
        //Fibonacci回撤分析
        if(args.end_price>args.begin_price){
            float py=(args.end_price-args.bottom_price)/(args.top_price-args.bottom_price)*mHeight;
            mTriangle.draw_samll_text("0.0%",910,py, vPMatrix,2f);

            float pr=args.end_price-(args.end_price-args.begin_price)*0.382f;
            py=(pr-args.bottom_price)/(args.top_price-args.bottom_price)*mHeight;
            mTriangle.draw_samll_text("38.2%",910,py, vPMatrix,2f);

            pr=args.end_price-(args.end_price-args.begin_price)*0.5f;
            py=(pr-args.bottom_price)/(args.top_price-args.bottom_price)*mHeight;
            mTriangle.draw_samll_text("50.0%",910,py, vPMatrix,2f);

            pr=args.end_price-(args.end_price-args.begin_price)*0.618f;
            py=(pr-args.bottom_price)/(args.top_price-args.bottom_price)*mHeight;
            mTriangle.draw_samll_text("61.8%",910,py, vPMatrix,2f);


            py=(args.begin_price-args.bottom_price)/(args.top_price-args.bottom_price)*mHeight;
            mTriangle.draw_samll_text("100%",910,py, vPMatrix,2f);
        }
        // k线信息
        mTriangle.draw_samll_text(String.format("O:"+args.format,args.data_open[args.FINGER_INDEX]),950,220, vPMatrix,1f);
        mTriangle.draw_samll_text(String.format("C:"+args.format,args.data_close[args.FINGER_INDEX]),950,200, vPMatrix,1f);
        float rate=0;
        c=0;
        if(args.data_open[args.FINGER_INDEX]>0f)
            rate=(args.data_close[args.FINGER_INDEX]-args.data_open[args.FINGER_INDEX])/args.data_open[args.FINGER_INDEX]*100f;
        if(rate>0f)
            c=1;
        mTriangle.draw_samll_text(String.format("R:%.2f%%",rate),950,180, vPMatrix,c);
        mTriangle.draw_samll_text(args.stampToTime(args.date_stamp[args.FINGER_INDEX]),950,160, vPMatrix,1);
        //mTriangle.draw_text(args.kdj_info,400,500, vPMatrix,1);
//        String s=String.format("%%.%df",0);
        //String txt=N.QTY("SANDUSDT",4.4324234f);
       // System.out.println("SANDUSDT"+N.indexof(args.COIN));
        //mTriangle.draw_grid_price(800,mHeight, vPMatrix);
        //System.out.printf();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mWidth=width;
        mHeight=height;
        mScaleFactory=mHeight/4f/4096f;
        //mScaleFactorx=mWidth/2f/4096f;
        float ratio = (float) width / height;
        // init girds x direction
        float left=0;
        float right=1100;
        float low_price=args.MAX_AND_MIN[0];
        float high_price=args.MAX_AND_MIN[1];
        float price_range=high_price-low_price;
        if (price_range<0){price_range=100f;}
        float bottom=low_price-price_range/7*2;
        float top=high_price+price_range/7;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, 3, 7);
        Matrix.frustumM(projectionMatrix_for_txt, 0, 0, width, 0, height, 3, 7);
        Matrix.frustumM(projectionMatrix_for_volume, 0, left, right, 0, 10.0f, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES31.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES31.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
