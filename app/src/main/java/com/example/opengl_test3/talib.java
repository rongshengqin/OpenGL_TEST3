package com.example.opengl_test3;

public class talib {
    // n日均线MA,
    public static void   getMA(float[] arr_in,float[] arr_out, int n,int color) {
        for (int i = 0; i < args.limit; i++) {
            if (i < n - 1) {
                arr_out[i*3]=i;
                arr_out[i*3+1]=arr_in[i];
                arr_out[i*3+2]=color;
                continue;
            }
            float sum = 0;
            for (int j = 0; j < n; j++) {
                sum += arr_in[i-j];
            }
            arr_out[i*3]=i;
            arr_out[i*3+1]= sum / n;
            arr_out[i*3+2]=color;
        }
    }


    /**
     * EMA算法
     * EMA(N) = 2/(N+1)*C + (N-1)/(N+1)*EMA', EMA'为前一天的ema; 通常N取12和26
     *
     * @param
     * @param n
     * @return
     */
    public static void getEMA(float[] arr_in,float[] arr_out, int n,int color) {
        //List<Entry> result = new ArrayList<>();
        float lastEma = arr_in[0];// 第一个EMA为第一个数据的价格
        arr_out[0*3]=0;
        arr_out[0*3+1]= lastEma;
        arr_out[0*3+2]=color;
        float[] emaFactor = getEMAFactor(n);
        for (int i = 1; i < args.limit; i++) {
            float ema = emaFactor[0] * arr_in[i] + emaFactor[1] * lastEma;
            arr_out[i*3]=i;
            arr_out[i*3+1]= ema;
            arr_out[i*3+2]=color;
            lastEma = ema;
        }
    }

    /**
     * 获取EMA计算时的相关系数 (后续多个地方需要这个系数 抽取出来用）
     * @param n
     * @return
     */
    private static float[] getEMAFactor(int n) {
        return new float[]{2f / (n + 1), (n - 1) * 1.0f / (n + 1)};
    }

    /**
     * kdj 9,3,3
     * N:=9; P1:=3; P2:=3;
     * RSV:=(CLOSE-L(LOW,N))/(H(HIGH,N)-L(LOW,N))*100;
     * K:SMA(RSV,P1,1);
     * D:SMA(K,P2,1);
     * J:3*K-2*D;
     * @param  数据集合
     * @param n 指标周期 9
     * @param m 权重 1
     * @param P1 参数值为3
     * @param P2 参数值为3
     * @return
     */
    public static void getKDJ(float[] open,float[] close,float[] high,float[] low,float[] K,float[] D,float[] J, int n, int P1, int P2, int m) {
//        List<Entry> kValue = new ArrayList();
//        List<Entry> dValue = new ArrayList();
//        List<Entry> jValue = new ArrayList();

        float[] maxs = getPeriodHighest(high, n);
        float[] mins = getPeriodLowest(low, n);
        float rsv = 0;
        float lastK = 50;
        float lastD = 50;

        for (int i = n - 1; i < args.limit; i++) {
            float div = maxs[i] - mins[i];
            if (div == 0) {
                //使用上一次的
            } else {
                rsv = ((close[i] - mins[i]) / (div)) * 100;
            }
            float k = countSMA(rsv, P1, m, lastK);
            float d = countSMA(k, P2, m, lastD);
            float j = 3 * k - 2 * d;
            lastK = k;
            lastD = d;
            K[i*3]=i;
            K[i*3+1]=k;
            K[i*3+2]=0;
            D[i*3]=i;
            D[i*3+1]=d;
            D[i*3+2]=1;
            J[i*3]=i;
            J[i*3+1]=j;
            J[i*3+2]=2;
        }
    }

    /**
     * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     * C=今天收盘价－昨天收盘价    N＝就是周期比如 6或者12或者24， M＝权重，一般取1
     *
     * @param c   今天收盘价－昨天收盘价
     * @param n   周期
     * @param m   1
     * @param sma 上一个周期的sma
     * @return
     */
    private static float countSMA(float c, float n, float m, float sma) {
        return (m * c + (n - m) * sma) / n;
    }

    /**
     * n周期内最低值集合
     * @param
     * @param n
     * @return
     */
    private static float[] getPeriodLowest(float[] low, int n) {
        float[] result = new float[args.limit];
        float minValue = 0;
        for (int i = n - 1; i < args.limit; i++) {
            for (int j = i - n + 1; j <= i; j++) {
                if (j == i - n + 1) {
                    minValue = low[j];
                } else {
                    minValue = Math.min(minValue, low[j]);
                }
            }
            result[i] =minValue;
        }
        return result;
    }

    /**
     *  N周期内最高值集合
     * @param
     * @param n
     * @return
     */
    private static float[] getPeriodHighest(float[] high, int n) {
        float[] result = new float[args.limit];
        float maxValue = high[0];
        for (int i = n - 1; i < args.limit; i++) {
            for (int j = i - n + 1; j <= i; j++) {
                if (j == i - n + 1) {
                    maxValue = high[j];
                } else {
                    maxValue = Math.max(maxValue, high[j]);
                }
            }
            result[i]= maxValue;
        }
        return result;
    }

    /**
     * 布林带BOLL（n， k） 一般n默认取20，k取2, mb为计算好的中轨线
     * 中轨线MB: n日移动平均线 MA(n)
     * 上轨线：MB + 2*MD
     * 下轨线：MB - 2*MD
     * MD：n日方差
     *
     * @param
     * @param n
     * @param
     * @return
     */
    public static void getBoll(float[] close, float[] MB,float[] UP,float[] DN, int n) {
        for (int i = 0, len = args.limit; i < len; i++) {
            if (i < n - 1) {
                continue;
            }
            float sumMB = 0;
            float sumMD = 0;
            for (int j = n - 1; j >= 0; j--) {
                float thisClose = close[i - j];
                sumMB += thisClose;
            }
            float mb = sumMB / n;
            MB[i*3]=i;
            MB[i*3+1]=mb;
            MB[i*3+2]=5;
            for (int j = n - 1; j >= 0; j--) {
                float thisClose =  close[i - j];;
                float cma = thisClose - mb; // C-MB
                sumMD += cma * cma;
            }
            float md = (float) Math.pow(sumMD / (n - 1), 1.0 / 2); //MD=前n日C-MB的平方和来开根
            UP[i*3]=i;
            UP[i*3+1]=mb + 2 * md; // UP=MB+2*MD
            UP[i*3+2]=5;
            DN[i*3]=i;
            DN[i*3+1]= mb - 2 * md; // DN=MB+2*MD
            DN[i*3+2]=5;
        }
    }
}
