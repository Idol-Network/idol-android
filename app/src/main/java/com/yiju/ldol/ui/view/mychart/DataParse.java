package com.yiju.ldol.ui.view.mychart;

import android.util.SparseArray;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.yiju.idol.bean.response.KLineResp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loro on 2017/2/8.
 */
public class DataParse {
    private ArrayList<MinutesBean> datas = new ArrayList<>();
    private ArrayList<KLineBean> kDatas = new ArrayList<>();
    private ArrayList<String> xVals = new ArrayList<>();//X轴数据
    private ArrayList<BarEntry> barEntries = new ArrayList<>();//成交量数据
    private ArrayList<CandleEntry> candleEntries = new ArrayList<>();//K线数据

    private ArrayList<Entry> ma5DataL = new ArrayList<>();
    private ArrayList<Entry> ma10DataL = new ArrayList<>();
    private ArrayList<Entry> ma20DataL = new ArrayList<>();
    private ArrayList<Entry> ma30DataL = new ArrayList<>();

    private ArrayList<Entry> ma5DataV = new ArrayList<>();
    private ArrayList<Entry> ma10DataV = new ArrayList<>();
    private ArrayList<Entry> ma20DataV = new ArrayList<>();
    private ArrayList<Entry> ma30DataV = new ArrayList<>();

    private List<BarEntry> macdData = new ArrayList<>();
    private List<Entry> deaData = new ArrayList<>();
    private List<Entry> difData = new ArrayList<>();

    private List<BarEntry> barDatasKDJ = new ArrayList<>();
    private List<Entry> kData = new ArrayList<>();
    private List<Entry> dData = new ArrayList<>();
    private List<Entry> jData = new ArrayList<>();

    private List<BarEntry> barDatasWR = new ArrayList<>();
    private List<Entry> wrData13 = new ArrayList<>();
    private List<Entry> wrData34 = new ArrayList<>();
    private List<Entry> wrData89 = new ArrayList<>();

    private List<BarEntry> barDatasRSI = new ArrayList<>();
    private List<Entry> rsiData6 = new ArrayList<>();
    private List<Entry> rsiData12 = new ArrayList<>();
    private List<Entry> rsiData24 = new ArrayList<>();

    private List<BarEntry> barDatasBOLL = new ArrayList<>();
    private List<Entry> bollDataUP = new ArrayList<>();
    private List<Entry> bollDataMB = new ArrayList<>();
    private List<Entry> bollDataDN = new ArrayList<>();

    private List<BarEntry> barDatasEXPMA = new ArrayList<>();
    private List<Entry> expmaData5 = new ArrayList<>();
    private List<Entry> expmaData10 = new ArrayList<>();
    private List<Entry> expmaData20 = new ArrayList<>();
    private List<Entry> expmaData60 = new ArrayList<>();

    private List<BarEntry> barDatasDMI = new ArrayList<>();
    private List<Entry> dmiDataDI1 = new ArrayList<>();
    private List<Entry> dmiDataDI2 = new ArrayList<>();
    private List<Entry> dmiDataADX = new ArrayList<>();
    private List<Entry> dmiDataADXR = new ArrayList<>();

    private float baseValue;
    private float permaxmin;
    private float volmax;
    private String code = "sz002081";
    private SparseArray<String> xValuesLabel = new SparseArray<>();

    public void parseMinutes(JSONObject object) {
        JSONArray jsonArray = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optJSONArray("data");
        String date = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optString("date");
        if (date.length() == 0) {
            return;
        }
        /*数据解析依照自己需求来定，如果服务器直接返回百分比数据，则不需要客户端进行计算*/
        baseValue = (float) object.optJSONObject("data").optJSONObject(code).optJSONObject("qt").optJSONArray(code).optDouble(4);
        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            String[] t = jsonArray.optString(i).split(" ");/*  "0930 9.50 4707",*/
            MinutesBean minutesData = new MinutesBean();
            minutesData.time = t[0].substring(0, 2) + ":" + t[0].substring(2);
            minutesData.cjprice = Float.parseFloat(t[1]);
            if (i != 0) {
                String[] pre_t = jsonArray.optString(i - 1).split(" ");
                minutesData.cjnum = Integer.parseInt(t[2]) - Integer.parseInt(pre_t[2]);
                minutesData.total = minutesData.cjnum * minutesData.cjprice + datas.get(i - 1).total;
                minutesData.avprice = (minutesData.total) / Integer.parseInt(t[2]);
            } else {
                minutesData.cjnum = Integer.parseInt(t[2]);
                minutesData.avprice = minutesData.cjprice;
                minutesData.total = minutesData.cjnum * minutesData.cjprice;
            }
            minutesData.cha = minutesData.cjprice - baseValue;
            minutesData.per = (minutesData.cha / baseValue);
            double cha = minutesData.cjprice - baseValue;
            if (Math.abs(cha) > permaxmin) {
                permaxmin = (float) Math.abs(cha);
            }
            volmax = Math.max(minutesData.cjnum, volmax);
            datas.add(minutesData);
        }

        if (permaxmin == 0) {
            permaxmin = baseValue * 0.02f;
        }
    }

    /**
     * 将jsonobject转换为K线数据
     *
     * @param obj
     */
    public void parseKLine(JSONObject obj) {
        ArrayList<KLineBean> kLineBeans = new ArrayList<>();
        JSONObject data = obj.optJSONObject("data").optJSONObject(code);
        JSONArray list = data.optJSONArray("day");
        if (list != null) {
            int count = list.length();
            for (int i = 0; i < count; i++) {
                JSONArray dayData = list.optJSONArray(i);
                KLineBean kLineData = new KLineBean();
                kLineData.date = dayData.optString(0);
                kLineData.open = (float) dayData.optDouble(1);
                kLineData.close = (float) dayData.optDouble(2);
                kLineData.high = (float) dayData.optDouble(3);
                kLineData.low = (float) dayData.optDouble(4);
                kLineData.vol = (float) dayData.optDouble(5);

                kLineBeans.add(kLineData);

                volmax = Math.max(kLineData.vol, volmax);
                xValuesLabel.put(i, kLineData.date);
            }
        }
        kDatas.addAll(kLineBeans);
    }

    /**
     * 将jsonobject转换为K线数据
     * 2013/6/13
     * 2190.1
     * 2148.35
     * 2126.22
     * 2190.1"
     *
     * @param resp
     */
    public void test1(KLineData resp) {
        try {
            ArrayList<KLineBean> kLineBeans = new ArrayList<>();
            int count = resp.data.sz002081.day.size();
            for (int i = 0; i < count; i++) {
                List<String> strings = resp.data.sz002081.day.get(i);
                KLineBean kLineData = new KLineBean();
                kLineData.date = String.valueOf(new SimpleDateFormat("yyyy/MM/dd").parse(String.valueOf(strings.get(0))));
                kLineData.open = Float.parseFloat(strings.get(1));
                kLineData.close = Float.parseFloat(strings.get(2));
                kLineData.low = Float.parseFloat(strings.get(3));
                kLineData.high = Float.parseFloat(strings.get(4));
                kLineData.vol = Float.parseFloat(strings.get(5));
                kLineBeans.add(kLineData);
                volmax = Math.max(kLineData.vol, volmax);
                xValuesLabel.put(i, kLineData.date);
//            }
            }
            kDatas.addAll(kLineBeans);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1524896700000, 时间
     * 0.12416,       开盘
     * 0.15881,       最高
     * 0.00222,       最低
     * 0.061728,      收盘
     * 4416          成交量
     *
     * @param resp
     */
    public void test2(KLineResp resp) {
        kDatas.clear();
        xValuesLabel.clear();
        ArrayList<KLineBean> kLineBeans = new ArrayList<>();
        int count = resp.klineData.size();
        //设置最小默认值，防止实际最小值大于默认最小值。ps：最大值中的最小值应该是大于0的吧。。。
        mYmin = Float.parseFloat(String.valueOf(resp.klineData.get(0).get(3)));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < count; i++) {
            List<Object> objects = resp.klineData.get(i);
            KLineBean kLineData = new KLineBean();
            kLineData.date = format.format(new BigDecimal((Double) objects.get(0)).longValue());
            kLineData.open = Float.parseFloat(String.valueOf(objects.get(1)));
            kLineData.high = Float.parseFloat(String.valueOf(objects.get(2)));
            kLineData.low = Float.parseFloat(String.valueOf(objects.get(3)));
            kLineData.close = Float.parseFloat(String.valueOf(objects.get(4)));
            kLineData.vol = Float.parseFloat(String.valueOf(objects.get(5)));
            if (mYmax < kLineData.high) {
                mYmax = kLineData.high;
            }
            if (kLineData.low < mYmin) {
                mYmin = kLineData.low;
            }
            kLineBeans.add(kLineData);
            volmax = Math.max(kLineData.vol, volmax);
            xValuesLabel.put(i, kLineData.date);

        }
        kDatas.addAll(kLineBeans);
    }

    public float getYmax() {
        return Float.parseFloat(dcmFmt.format(mYmax));
    }

    public float getYmin() {
        return Float.parseFloat(dcmFmt.format(mYmin));
    }

    double mYmax = 0d;
    double mYmin = 0d;

    DecimalFormat dcmFmt = new DecimalFormat("0.000000");
    String[] str = new String[4];

    /**
     * an=a1+(n-1)*d。
     * d=an-a1/(n-1)
     *
     * @return
     */
    public String[] getYValues() {
        double v = (mYmax - mYmin) / 3;
        str[0] = dcmFmt.format(mYmin);
        str[1] = dcmFmt.format(mYmin + 1 * v);
        str[2] = dcmFmt.format(mYmin + 2 * v);
        str[3] = dcmFmt.format(mYmax);
        return str;
    }

    //得到成交量
    public void initLineDatas(ArrayList<KLineBean> datas) {
        if (null == datas) {
            return;
        }
        xVals.clear();
        barEntries.clear();//成交量数据
        candleEntries.clear();//K线数据
        for (int i = 0, j = 0; i < datas.size(); i++, j++) {
            xVals.add(datas.get(i).date + "");
            barEntries.add(new BarEntry(i, datas.get(i).high, datas.get(i).low, datas.get(i).open, datas.get(i).close, datas.get(i).vol));
            candleEntries.add(new CandleEntry(i, datas.get(i).high, datas.get(i).low, datas.get(i).open, datas.get(i).close));
        }
    }

    /**
     * 初始化K线图均线
     *
     * @param datas
     */
    public void initKLineMA(ArrayList<KLineBean> datas) {
        if (null == datas) {
            return;
        }
        ma5DataL = new ArrayList<>();
        ma10DataL = new ArrayList<>();
        ma20DataL = new ArrayList<>();
        ma30DataL = new ArrayList<>();

        KMAEntity kmaEntity5 = new KMAEntity(datas, 5);
        KMAEntity kmaEntity10 = new KMAEntity(datas, 10);
        KMAEntity kmaEntity20 = new KMAEntity(datas, 20);
        KMAEntity kmaEntity30 = new KMAEntity(datas, 30);
        for (int i = 0; i < kmaEntity5.getMAs().size(); i++) {
            if (i >= 5)
                ma5DataL.add(new Entry(kmaEntity5.getMAs().get(i), i));
            if (i >= 10)
                ma10DataL.add(new Entry(kmaEntity10.getMAs().get(i), i));
            if (i >= 20)
                ma20DataL.add(new Entry(kmaEntity20.getMAs().get(i), i));
            if (i >= 30)
                ma30DataL.add(new Entry(kmaEntity30.getMAs().get(i), i));
        }

    }

    /**
     * 初始化成交量均线
     *
     * @param datas
     */
    public void initVlumeMA(ArrayList<KLineBean> datas) {
        if (null == datas) {
            return;
        }
        ma5DataV = new ArrayList<>();
        ma10DataV = new ArrayList<>();
        ma20DataV = new ArrayList<>();
        ma30DataV = new ArrayList<>();

        VMAEntity vmaEntity5 = new VMAEntity(datas, 5);
        VMAEntity vmaEntity10 = new VMAEntity(datas, 10);
        VMAEntity vmaEntity20 = new VMAEntity(datas, 20);
        VMAEntity vmaEntity30 = new VMAEntity(datas, 30);
        for (int i = 0; i < vmaEntity5.getMAs().size(); i++) {
            ma5DataV.add(new Entry(vmaEntity5.getMAs().get(i), i));
            ma10DataV.add(new Entry(vmaEntity10.getMAs().get(i), i));
            ma20DataV.add(new Entry(vmaEntity20.getMAs().get(i), i));
            ma30DataV.add(new Entry(vmaEntity30.getMAs().get(i), i));
        }

    }

    /**
     * 初始化MACD
     *
     * @param datas
     */
    public void initMACD(ArrayList<KLineBean> datas) {
        MACDEntity macdEntity = new MACDEntity(datas);

        macdData.clear();
        deaData.clear();
        difData.clear();
        for (int i = 0; i < macdEntity.getMACD().size(); i++) {
            macdData.add(new BarEntry(macdEntity.getMACD().get(i), i));
            deaData.add(new Entry(macdEntity.getDEA().get(i), i));
            difData.add(new Entry(macdEntity.getDIF().get(i), i));
        }
    }

    /**
     * 初始化KDJ
     *
     * @param datas
     */
    public void initKDJ(ArrayList<KLineBean> datas) {
        KDJEntity kdjEntity = new KDJEntity(datas, 9);

        barDatasKDJ.clear();
        kData.clear();
        dData.clear();
        jData.clear();
        for (int i = 0; i < kdjEntity.getD().size(); i++) {
            barDatasKDJ.add(new BarEntry(0, i));
            kData.add(new Entry(kdjEntity.getK().get(i), i));
            dData.add(new Entry(kdjEntity.getD().get(i), i));
            jData.add(new Entry(kdjEntity.getJ().get(i), i));
        }
    }

    /**
     * 初始化WR
     *
     * @param datas
     */
    public void initWR(ArrayList<KLineBean> datas) {
        WREntity wrEntity13 = new WREntity(datas, 13);
        WREntity wrEntity34 = new WREntity(datas, 34);
        WREntity wrEntity89 = new WREntity(datas, 89);

        barDatasWR.clear();
        wrData13.clear();
        wrData34.clear();
        wrData89.clear();
        for (int i = 0; i < wrEntity13.getWRs().size(); i++) {
            barDatasWR.add(new BarEntry(0, i));
            wrData13.add(new Entry(wrEntity13.getWRs().get(i), i));
            wrData34.add(new Entry(wrEntity34.getWRs().get(i), i));
            wrData89.add(new Entry(wrEntity89.getWRs().get(i), i));
        }
    }

    /**
     * 初始化RSI
     *
     * @param datas
     */
    public void initRSI(ArrayList<KLineBean> datas) {
        RSIEntity rsiEntity6 = new RSIEntity(datas, 6);
        RSIEntity rsiEntity12 = new RSIEntity(datas, 12);
        RSIEntity rsiEntity24 = new RSIEntity(datas, 24);

        barDatasRSI.clear();
        rsiData6.clear();
        rsiData12.clear();
        rsiData24.clear();
        for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
            barDatasRSI.add(new BarEntry(0, i));
            rsiData6.add(new Entry(rsiEntity6.getRSIs().get(i), i));
            rsiData12.add(new Entry(rsiEntity12.getRSIs().get(i), i));
            rsiData24.add(new Entry(rsiEntity24.getRSIs().get(i), i));
        }
    }

    /**
     * 初始化BOLL
     *
     * @param datas
     */
    public void initBOLL(ArrayList<KLineBean> datas) {
        BOLLEntity bollEntity = new BOLLEntity(datas, 20);

        barDatasBOLL.clear();
        bollDataUP.clear();
        bollDataMB.clear();
        bollDataDN.clear();
        for (int i = 0; i < bollEntity.getUPs().size(); i++) {
            barDatasBOLL.add(new BarEntry(0, i));
            bollDataUP.add(new Entry(bollEntity.getUPs().get(i), i));
            bollDataMB.add(new Entry(bollEntity.getMBs().get(i), i));
            bollDataDN.add(new Entry(bollEntity.getDNs().get(i), i));
        }
    }

    /**
     * 初始化BOLL
     *
     * @param datas
     */
    public void initEXPMA(ArrayList<KLineBean> datas) {
        EXPMAEntity expmaEntity5 = new EXPMAEntity(datas, 5);
        EXPMAEntity expmaEntity10 = new EXPMAEntity(datas, 10);
        EXPMAEntity expmaEntity20 = new EXPMAEntity(datas, 20);
        EXPMAEntity expmaEntity60 = new EXPMAEntity(datas, 60);

        barDatasEXPMA.clear();
        expmaData5.clear();
        expmaData10.clear();
        expmaData20.clear();
        expmaData60.clear();
        for (int i = 0; i < expmaEntity5.getEXPMAs().size(); i++) {
            barDatasEXPMA.add(new BarEntry(0, i));
            expmaData5.add(new Entry(expmaEntity5.getEXPMAs().get(i), i));
            expmaData10.add(new Entry(expmaEntity10.getEXPMAs().get(i), i));
            expmaData20.add(new Entry(expmaEntity20.getEXPMAs().get(i), i));
            expmaData60.add(new Entry(expmaEntity60.getEXPMAs().get(i), i));
        }
    }

    /**
     * 初始化DMI
     *
     * @param datas
     */
    public void initDMI(ArrayList<KLineBean> datas) {
        DMIEntity dmiEntity = new DMIEntity(datas, 12, 7, 6, true);

        barDatasDMI.clear();
        dmiDataDI1.clear();
        dmiDataDI2.clear();
        dmiDataADX.clear();
        dmiDataADXR.clear();
        for (int i = 0; i < dmiEntity.getDI1s().size(); i++) {
            barDatasDMI.add(new BarEntry(0, i));
            dmiDataDI1.add(new Entry(dmiEntity.getDI1s().get(i), i));
            dmiDataDI2.add(new Entry(dmiEntity.getDI2s().get(i), i));
            dmiDataADX.add(new Entry(dmiEntity.getADXs().get(i), i));
            dmiDataADXR.add(new Entry(dmiEntity.getADXRs().get(i), i));
        }
    }

    /**
     * 得到Y轴最小值
     *
     * @return
     */
    public float getMin() {
        return baseValue - permaxmin;
    }

    /**
     * 得到Y轴最大值
     *
     * @return
     */
    public float getMax() {
        return baseValue + permaxmin;
    }

    /**
     * 得到百分百最大值
     *
     * @return
     */
    public float getPercentMax() {
        return permaxmin / baseValue;
    }

    /**
     * 得到百分比最小值
     *
     * @return
     */
    public float getPercentMin() {
        return -getPercentMax();
    }

    /**
     * 得到成交量最大值
     *
     * @return
     */
    public float getVolmax() {
        return volmax;
    }


    /**
     * 得到分时图数据
     *
     * @return
     */
    public ArrayList<MinutesBean> getDatas() {
        return datas;
    }

    /**
     * 得到K线图数据
     *
     * @return
     */
    public ArrayList<KLineBean> getKLineDatas() {
        return kDatas;
    }

    /**
     * 得到X轴数据
     *
     * @return
     */
    public ArrayList<String> getXVals() {
        return xVals;
    }

    /**
     * 得到K线数据
     *
     * @return
     */
    public ArrayList<CandleEntry> getCandleEntries() {
        return candleEntries;
    }

    /**
     * 得到成交量数据
     *
     * @return
     */
    public ArrayList<BarEntry> getBarEntries() {
        return barEntries;
    }


    /**
     * 得到K线图5日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa5DataL() {
        return ma5DataL;
    }


    /**
     * 得到K线图10日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa10DataL() {
        return ma10DataL;
    }

    /**
     * 得到K线图20日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa20DataL() {
        return ma20DataL;
    }

    /**
     * 得到K线图30日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa30DataL() {
        return ma30DataL;
    }

    /**
     * 得到成交量5日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa5DataV() {
        return ma5DataV;
    }

    /**
     * 得到成交量10日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa10DataV() {
        return ma10DataV;
    }

    /**
     * 得到成交量20日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa20DataV() {
        return ma20DataV;
    }

    /**
     * 得到K线图30日均线
     *
     * @return
     */
    public ArrayList<Entry> getMa30DataV() {
        return ma30DataV;
    }

    /**
     * 得到MACD bar
     *
     * @return
     */
    public List<BarEntry> getMacdData() {
        return macdData;
    }

    /**
     * 得到MACD dea
     *
     * @return
     */
    public List<Entry> getDeaData() {
        return deaData;
    }

    /**
     * 得到MACD dif
     *
     * @return
     */
    public List<Entry> getDifData() {
        return difData;
    }

    /**
     * 得到KDJ bar
     *
     * @return
     */
    public List<BarEntry> getBarDatasKDJ() {
        return barDatasKDJ;
    }

    /**
     * 得到DKJ k
     *
     * @return
     */
    public List<Entry> getkData() {
        return kData;
    }

    /**
     * 得到KDJ d
     *
     * @return
     */
    public List<Entry> getdData() {
        return dData;
    }

    /**
     * 得到KDJ j
     *
     * @return
     */
    public List<Entry> getjData() {
        return jData;
    }

    /**
     * 得到WR bar
     *
     * @return
     */
    public List<BarEntry> getBarDatasWR() {
        return barDatasWR;
    }

    /**
     * 得到WR 13
     *
     * @return
     */
    public List<Entry> getWrData13() {
        return wrData13;
    }

    /**
     * 得到WR 34
     *
     * @return
     */
    public List<Entry> getWrData34() {
        return wrData34;
    }

    /**
     * 得到WR 89
     *
     * @return
     */
    public List<Entry> getWrData89() {
        return wrData89;
    }

    /**
     * 得到RSI bar
     *
     * @return
     */
    public List<BarEntry> getBarDatasRSI() {
        return barDatasRSI;
    }

    /**
     * 得到RSI 6
     *
     * @return
     */
    public List<Entry> getRsiData6() {
        return rsiData6;
    }

    /**
     * 得到RSI 12
     *
     * @return
     */
    public List<Entry> getRsiData12() {
        return rsiData12;
    }

    /**
     * 得到RSI 24
     *
     * @return
     */
    public List<Entry> getRsiData24() {
        return rsiData24;
    }

    public List<BarEntry> getBarDatasBOLL() {
        return barDatasBOLL;
    }

    public List<Entry> getBollDataUP() {
        return bollDataUP;
    }

    public List<Entry> getBollDataMB() {
        return bollDataMB;
    }

    public List<Entry> getBollDataDN() {
        return bollDataDN;
    }

    public List<BarEntry> getBarDatasEXPMA() {
        return barDatasEXPMA;
    }

    public List<Entry> getExpmaData5() {
        return expmaData5;
    }

    public List<Entry> getExpmaData10() {
        return expmaData10;
    }

    public List<Entry> getExpmaData20() {
        return expmaData20;
    }

    public List<Entry> getExpmaData60() {
        return expmaData60;
    }

    public List<BarEntry> getBarDatasDMI() {
        return barDatasDMI;
    }

    public List<Entry> getDmiDataDI1() {
        return dmiDataDI1;
    }

    public List<Entry> getDmiDataDI2() {
        return dmiDataDI2;
    }

    public List<Entry> getDmiDataADX() {
        return dmiDataADX;
    }

    public List<Entry> getDmiDataADXR() {
        return dmiDataADXR;
    }
}
