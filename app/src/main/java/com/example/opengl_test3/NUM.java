package com.example.opengl_test3;

public class NUM {
//    {"BTCUSDT", "ETHUSDT", "LINKUSDT","BNBUSDT", "BCHUSDT", "ALICEUSDT","MATICUSDT",
//            "LTCUSDT", "AXSUSDT","ETCUSDT", "ADAUSDT", "DASHUSDT","DOGEUSDT", "SUSHIUSDT", "SANDUSDT","DOTUSDT",
//            "CHRUSDT","ALPHAUSDT","LUNAUSDT","TLMUSDT","ICPUSDT","COMPUSDT","AAVEUSDT","BAKEUSDT","KSMUSDT","XRPUSDT",
//            "NKNUSDT","IOTXUSDT","DENTUSDT","LINAUSDT","WAVESUSDT","BATUSDT"};
    private String[] allsymbols={"BTCUSDT", "ETHUSDT", "BCHUSDT", "XRPUSDT", "EOSUSDT", "LTCUSDT", "TRXUSDT", "ETCUSDT", "LINKUSDT", "XLMUSDT", "ADAUSDT", "XMRUSDT", "DASHUSDT", "ZECUSDT", "XTZUSDT", "BNBUSDT", "ATOMUSDT", "ONTUSDT", "IOTAUSDT", "BATUSDT", "VETUSDT", "NEOUSDT", "QTUMUSDT", "IOSTUSDT", "THETAUSDT", "ALGOUSDT", "ZILUSDT", "KNCUSDT", "ZRXUSDT", "COMPUSDT", "OMGUSDT", "DOGEUSDT", "SXPUSDT", "KAVAUSDT", "BANDUSDT", "RLCUSDT", "WAVESUSDT", "MKRUSDT", "SNXUSDT", "DOTUSDT", "DEFIUSDT", "YFIUSDT", "BALUSDT", "CRVUSDT", "TRBUSDT", "YFIIUSDT", "RUNEUSDT", "SUSHIUSDT", "SRMUSDT", "BZRXUSDT", "EGLDUSDT", "SOLUSDT", "ICXUSDT", "STORJUSDT", "BLZUSDT", "UNIUSDT", "AVAXUSDT", "FTMUSDT", "HNTUSDT", "ENJUSDT", "FLMUSDT", "TOMOUSDT", "RENUSDT", "KSMUSDT", "NEARUSDT", "AAVEUSDT", "FILUSDT", "RSRUSDT", "LRCUSDT", "MATICUSDT", "OCEANUSDT", "CVCUSDT", "BELUSDT", "CTKUSDT", "AXSUSDT", "ALPHAUSDT", "ZENUSDT", "SKLUSDT", "GRTUSDT", "1INCHUSDT", "AKROUSDT", "CHZUSDT", "SANDUSDT", "ANKRUSDT", "LUNAUSDT", "BTSUSDT", "LITUSDT", "UNFIUSDT", "DODOUSDT", "REEFUSDT", "RVNUSDT", "SFPUSDT", "XEMUSDT", "COTIUSDT", "CHRUSDT", "MANAUSDT", "ALICEUSDT", "HBARUSDT", "ONEUSDT", "LINAUSDT", "STMXUSDT", "DENTUSDT", "CELRUSDT", "HOTUSDT", "MTLUSDT", "OGNUSDT", "BTTUSDT", "NKNUSDT", "SCUSDT", "DGBUSDT", "1000SHIBUSDT", "ICPUSDT", "BAKEUSDT", "GTCUSDT", "BTCDOMUSDT", "KEEPUSDT", "TLMUSDT", "IOTXUSDT"};
    public int[] pricePrecision={2, 2, 2, 4, 3, 2, 5, 3, 3, 5, 4, 2, 2, 2, 3, 2, 3, 4, 4, 4, 5, 3, 3, 6, 3, 4, 5, 3, 4, 2, 4, 5, 4, 4, 4, 4, 3, 1, 3, 3, 1, 0, 3, 3, 2, 1, 3, 3, 3, 4, 2, 3, 4, 4, 5, 3, 3, 5, 3, 4, 4, 4, 5, 2, 4, 2, 3, 6, 5, 4, 5, 5, 4, 3, 2, 4, 3, 5, 5, 4, 5, 5, 5, 5, 3, 5, 3, 3, 3, 6, 5, 4, 4, 5, 4, 4, 3, 5, 5, 5, 5, 6, 5, 6, 4, 4, 6, 5, 6, 5, 6, 2, 4, 3, 1, 4, 4, 5};
    private int[] quantityPrecision={3, 3, 3, 1, 1, 3, 0, 2, 2, 0, 0, 3, 3, 3, 1, 2, 2, 1, 1, 1, 0, 2, 1, 0, 1, 1, 0, 0, 1, 3, 1, 0, 1, 1, 1, 1, 1, 3, 1, 1, 3, 3, 1, 1, 1, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 3, 0, 0, 0};
    public int indexof(String C){
        int index=0;
        for(int i=0;i<allsymbols.length;i++){
            if (C.equals(allsymbols[i]))
            {
                index= i;
                break;
            }
        }
        return index;
    }
    // 将价格转换到标准精度
    public String PRICE(String symbol,float qty){
        int index=indexof(symbol);
        String format=String.format("%%.%df",pricePrecision[index]);
        String price=String.format(format,qty);
        return price;
    }
    // 将数量转换到标准精度
    public String QTY(String symbol,float qty){
        int index=indexof(symbol);
        String format=String.format("%%.%df",quantityPrecision[index]);
        String Qty=String.format(format,qty);
        return Qty;
    }
}
