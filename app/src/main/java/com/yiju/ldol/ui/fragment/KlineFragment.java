package com.yiju.ldol.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.bean.response.KLineResp;
import com.yiju.idol.ui.activity.CoinDetailsActivity;
import com.yiju.idol.ui.view.mychart.CoupleChartGestureListener;
import com.yiju.idol.ui.view.mychart.DataParse;
import com.yiju.idol.ui.view.mychart.KLineBean;
import com.yiju.idol.ui.view.mychart.KMAEntity;
import com.yiju.idol.ui.view.mychart.MyBottomMarkerView;
import com.yiju.idol.ui.view.mychart.MyCombinedChart;
import com.yiju.idol.ui.view.mychart.MyHMarkerView;
import com.yiju.idol.ui.view.mychart.MyLeftMarkerView;
import com.yiju.idol.ui.view.mychart.MyUtils;
import com.yiju.idol.ui.view.mychart.VMAEntity;
import com.yiju.idol.ui.view.mychart.VolFormatter;

import java.util.ArrayList;
import java.util.List;

public class KlineFragment extends BaseFragment {


    protected MyCombinedChart mChartKline;
    protected MyCombinedChart mChartVolume;
    protected MyCombinedChart mChartCharts;

    protected TextView mTvEntity;


    //K线图数据
    private ArrayList<KLineBean> kLineDatas;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mChartKline.setAutoScaleMinMaxEnabled(true);
            mChartVolume.setAutoScaleMinMaxEnabled(true);
            mChartCharts.setAutoScaleMinMaxEnabled(true);

            mChartKline.notifyDataSetChanged();
            mChartVolume.notifyDataSetChanged();
            mChartCharts.notifyDataSetChanged();

            mChartKline.invalidate();
            mChartVolume.invalidate();
            mChartCharts.invalidate();
        }
    };

    private Handler handlerAdd = new Handler();
    private Runnable runnable;

    protected int chartType = 1;

    boolean isRefresh = true;
    private int personCurrencyId;
    private int time;

    boolean isVisible = false;

    private String[] yValues;

    //解析数据
    private DataParse mData;

    boolean isFirst = true;

    public static KlineFragment newInstance(int personCurrencyId, int time) {
        KlineFragment fragment = new KlineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CoinDetailsActivity.PERSON_CURRENCY_ID, personCurrencyId);
        bundle.putInt(CoinDetailsActivity.TIME, time);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_kline;
    }

    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        personCurrencyId = arguments.getInt(CoinDetailsActivity.PERSON_CURRENCY_ID, 5);
        time = arguments.getInt(CoinDetailsActivity.TIME, -1);
        if (personCurrencyId == -1) {
            return;
        }
        mData = new DataParse();
        getKlineData(personCurrencyId, time);
        if (time == 1) {//默认第一个进来刷新
            handlerAdd.postDelayed(runnable, 2000);
        }
    }

    @Override
    public void initView(View view) {
        mChartKline = view.findViewById(R.id.kline_chart_k);
        mChartVolume = view.findViewById(R.id.kline_chart_volume);
        mChartCharts = view.findViewById(R.id.kline_chart_charts);
        mTvEntity = view.findViewById(R.id.kline_tv_entity);

        initChartKline();
        initChartVolume();
        initChartCharts();
        setChartListener();
        view.findViewById(R.id.kline_iv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartType++;
                if (chartType > 7) {
                    chartType = 1;
                }
                String entity = getString(R.string.entity_macd);
                switch (chartType) {
                    case 1:
                        entity = getString(R.string.entity_macd);
                        setMACDByChart(mChartCharts);
                        break;
                    case 2:
                        entity = getString(R.string.entity_kdj);
                        setKDJByChart(mChartCharts);
                        break;
                    case 3:
                        entity = getString(R.string.entity_wr);
                        setWRByChart(mChartCharts);
                        break;
                    case 4:
                        entity = getString(R.string.entity_rsi);
                        setRSIByChart(mChartCharts);
                        break;
                    case 5:
                        entity = getString(R.string.entity_boll);
                        setBOLLByChart(mChartCharts);
                        break;
                    case 6:
                        entity = getString(R.string.entity_expma);
                        setEXPMAByChart(mChartCharts);
                        break;
                    case 7:
                        entity = getString(R.string.entity_dmi);
                        setDMIByChart(mChartCharts);
                        break;
                }

                mChartCharts.invalidate();
                mTvEntity.setText(entity);

            }
        });


/****************************************************************************************
 此处解决方法来源于CombinedChartDemo，k线图y轴显示问题，图表滑动后才能对齐的bug
 ****************************************************************************************/

        handler.sendEmptyMessageDelayed(0, 100);

        runnable = new Runnable() {
            @Override
            public void run() {
//                refreshData();
//                addKlineData();
//                addVolumeData();
                getKlineData(personCurrencyId, time);
                switch (chartType) {
                    case 1:
                        setMACDByChart(mChartCharts);
                        break;
                    case 2:
                        setKDJByChart(mChartCharts);
                        break;
                    case 3:
                        setWRByChart(mChartCharts);
                        break;
                    case 4:
                        setRSIByChart(mChartCharts);
                        break;
                    case 5:
                        setBOLLByChart(mChartCharts);
                        break;
                    case 6:
                        setEXPMAByChart(mChartCharts);
                        break;
                    case 7:
                        setDMIByChart(mChartCharts);
                        break;
                }

                mChartKline.setAutoScaleMinMaxEnabled(false);
                mChartKline.notifyDataSetChanged();
                mChartKline.invalidate();
                mChartVolume.setAutoScaleMinMaxEnabled(true);
                mChartVolume.notifyDataSetChanged();
                mChartVolume.invalidate();
                mChartCharts.setAutoScaleMinMaxEnabled(true);
                mChartCharts.notifyDataSetChanged();
                mChartCharts.moveViewToX(0);
                mChartCharts.invalidate();
                if (isVisible) {
                    handlerAdd.postDelayed(this, 2000);
                }
            }
        };

    }

//    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible) {
            handlerAdd.postDelayed(runnable, 2000);
        } else {
            handlerAdd.removeCallbacks(runnable);
        }
    }

    /**
     * 每秒刷新数据
     */
    private void refreshData() {
//        APIHelper.getInstance().getKLineData(personCurrencyId, time, new CallBack<KLineResp>() {
//            @Override
//            public void onSuccess(KLineResp data) {
//                List<Object> objects = data.klineData.get(data.klineData.size() - 1);
//                KLineBean kLineData = new KLineBean();
//                kLineData.date = format.format(new BigDecimal((Double) objects.get(0)).longValue());
//                kLineData.open = Float.parseFloat(String.valueOf(objects.get(1)));
//                kLineData.high = Float.parseFloat(String.valueOf(objects.get(2)));
//                kLineData.low = Float.parseFloat(String.valueOf(objects.get(3)));
//                kLineData.close = Float.parseFloat(String.valueOf(objects.get(4)));
//                kLineData.vol = Float.parseFloat(String.valueOf(objects.get(5)));
//                kLineDatas.add(kLineData);
//                mData.getXVals().add(kLineData.date);
//            }
//
//            @Override
//            public void onFailure(BaseReslut data) {
//            }
//        });
    }

    /**
     * 初始化上面的chart公共属性
     */
    private void initChartKline() {
        mChartKline.setScaleEnabled(true);//启用图表缩放事件
        mChartKline.setDrawBorders(false);//是否绘制边线
        mChartKline.setBorderWidth(1);//边线宽度，单位dp
        mChartKline.setDragEnabled(true);//启用图表拖拽事件
        mChartKline.setScaleYEnabled(false);//启用Y轴上的缩放
        mChartKline.setBorderColor(getResources().getColor(R.color.border_color));//边线颜色
        mChartKline.setDescription("");//右下角对图表的描述信息
        mChartKline.setMinOffset(0f);
        mChartKline.setExtraOffsets(0f, 0f, 0f, 3f);

        Legend lineChartLegend = mChartKline.getLegend();
        lineChartLegend.setEnabled(false);//是否绘制 Legend 图例
        lineChartLegend.setForm(Legend.LegendForm.CIRCLE);
        //bar x y轴
        XAxis xAxisKline = mChartKline.getXAxis();
        xAxisKline.setEnabled(false);
        xAxisKline.setDrawLabels(true); //是否显示X坐标轴上的刻度，默认是true
        xAxisKline.setDrawGridLines(false);//是否显示X坐标轴上的刻度竖线，默认是true
        xAxisKline.setDrawAxisLine(false); //是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxisKline.enableGridDashedLine(10f, 10f, 0f);//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
        xAxisKline.setTextColor(getResources().getColor(R.color.text_color_common));//设置字的颜色
        xAxisKline.setPosition(XAxis.XAxisPosition.BOTTOM);//设置值显示在什么位置
        xAxisKline.setAvoidFirstLastClipping(true);//设置首尾的值是否自动调整，避免被遮挡

        YAxis axisLeftKline = mChartKline.getAxisLeft();
        axisLeftKline.setEnabled(false);

        YAxis axisRightKline = mChartKline.getAxisRight();
        axisRightKline.setDrawLabels(true);
        axisRightKline.setDrawGridLines(false);
        axisRightKline.setDrawAxisLine(false);
//        axisRightKline.setDrawZeroLine(true);
        axisRightKline.setLabelCount(4, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
//        axisRightKline.setValueFormatter(new DefaultYAxisValueFormatter(6) {
//            @Override
//            public String getFormattedValue(float value, YAxis yAxis) {
//                return String.valueOf(value);
//            }
//        });
        mChartKline.setDragDecelerationEnabled(true);
        mChartKline.setDragDecelerationFrictionCoef(0.2f);

        mChartKline.animateXY(2000, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 初始化下面的chart公共属性
     */
    private void initChartVolume() {
        mChartVolume.setDrawBorders(false);  //边框是否显示
        mChartVolume.setBorderWidth(1);//边框的宽度，float类型，dp单位
        mChartVolume.setBorderColor(getResources().getColor(R.color.border_color));//边框颜色
        mChartVolume.setDescription(""); //图表默认右下方的描述，参数是String对象
        mChartVolume.setDragEnabled(true);// 是否可以拖拽
        mChartVolume.setScaleYEnabled(false); //是否可以缩放 仅y轴
        mChartVolume.setMinOffset(3f);
        mChartVolume.setExtraOffsets(0f, 0f, 0f, 5f);

        Legend combinedchartLegend = mChartVolume.getLegend(); // 设置比例图标示，就是那个一组y的value的
        combinedchartLegend.setEnabled(false);//是否绘制比例图

        //bar x y轴
        XAxis xAxisVolume = mChartVolume.getXAxis();
        xAxisVolume.setEnabled(false);
//        xAxisVolume.setDrawLabels(false); //是否显示X坐标轴上的刻度，默认是true
//        xAxisVolume.setDrawGridLines(false);//是否显示X坐标轴上的刻度竖线，默认是true
//        xAxisVolume.setDrawAxisLine(false); //是否绘制坐标轴的线，即含有坐标的那条线，默认是true
//        xAxisVolume.enableGridDashedLine(10f, 10f, 0f);//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
//        xAxisVolume.setTextColor(getResources().getColor(R.color.text_color_common));//设置字的颜色
//        xAxisVolume.setPosition(XAxis.XAxisPosition.BOTTOM);//设置值显示在什么位置
//        xAxisVolume.setAvoidFirstLastClipping(true);//设置首尾的值是否自动调整，避免被遮挡

        YAxis axisLeftVolume = mChartVolume.getAxisLeft();
        axisLeftVolume.setEnabled(false);
        axisLeftVolume.setAxisMinValue(0);//设置Y轴坐标最小为多少
//        axisLeftVolume.setShowOnlyMinMax(true);//设置Y轴坐标最小为多少
        axisLeftVolume.setDrawGridLines(true);
        axisLeftVolume.setDrawAxisLine(false);
//        axisLeftVolume.setShowOnlyMinMax(true);
        axisLeftVolume.setDrawLabels(true);
        axisLeftVolume.enableGridDashedLine(10f, 10f, 0f);
        axisLeftVolume.setTextColor(getResources().getColor(R.color.text_color_common));
//        axisLeftVolume.setGridColor(getResources().getColor(R.color.minute_grayLine));
        axisLeftVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftVolume.setLabelCount(1, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        axisLeftVolume.setSpaceTop(0);//距离顶部留白
//        axisLeftVolume.setSpaceBottom(0);//距离顶部留白

        YAxis axisRightVolume = mChartVolume.getAxisRight();
        axisRightVolume.setDrawLabels(false);
        axisRightVolume.setDrawGridLines(false);
        axisRightVolume.setDrawAxisLine(false);

        mChartVolume.setDragDecelerationEnabled(true);
        mChartVolume.setDragDecelerationFrictionCoef(0.2f);

        mChartVolume.animateXY(2000, 2000);
    }

    /**
     * 初始化下面的chart公共属性
     */
    private void initChartCharts() {
        mChartCharts.setScaleEnabled(true);//启用图表缩放事件
        mChartCharts.setDrawBorders(false);//是否绘制边线
        mChartCharts.setBorderWidth(1);//边线宽度，单位dp
        mChartCharts.setDragEnabled(true);//启用图表拖拽事件
        mChartCharts.setScaleYEnabled(false);//启用Y轴上的缩放
        mChartCharts.setBorderColor(getResources().getColor(R.color.border_color));//边线颜色
        mChartCharts.setDescription("");//右下角对图表的描述信息
        mChartCharts.setMinOffset(0f);
        mChartCharts.setExtraOffsets(0f, 0f, 0f, 3f);

        Legend lineChartLegend = mChartCharts.getLegend();
        lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

        //bar x y轴
        XAxis xAxisCharts = mChartCharts.getXAxis();
        xAxisCharts.setEnabled(false);

        YAxis axisLeftCharts = mChartCharts.getAxisLeft();
        axisLeftCharts.setEnabled(false);
        axisLeftCharts.setDrawGridLines(true);
        axisLeftCharts.setDrawAxisLine(false);
        axisLeftCharts.setDrawLabels(true);
        axisLeftCharts.enableGridDashedLine(10f, 10f, 0f);
        axisLeftCharts.setTextColor(getResources().getColor(R.color.text_color_common));
        axisLeftCharts.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftCharts.setLabelCount(1, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布


        YAxis axisRightCharts = mChartCharts.getAxisRight();
        axisRightCharts.setDrawLabels(false);
        axisRightCharts.setDrawGridLines(false);
        axisRightCharts.setDrawAxisLine(false);

        mChartCharts.setDragDecelerationEnabled(true);
        mChartCharts.setDragDecelerationFrictionCoef(0.2f);

        mChartCharts.animateXY(2000, 2000);
    }

    private void setChartListener() {
        // 将K线控的滑动事件传递给交易量控件
        mChartKline.setOnChartGestureListener(new CoupleChartGestureListener(mChartKline, new Chart[]{mChartVolume, mChartCharts}));
        // 将交易量控件的滑动事件传递给K线控件
        mChartVolume.setOnChartGestureListener(new CoupleChartGestureListener(mChartVolume, new Chart[]{mChartKline, mChartCharts}));

        mChartCharts.setOnChartGestureListener(new CoupleChartGestureListener(mChartCharts, new Chart[]{mChartKline, mChartVolume}));

        mChartKline.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Highlight highlight = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY = h.getTouchY() - mChartKline.getHeight();
                Highlight h1 = mChartVolume.getHighlightByTouchPoint(h.getXIndex(), touchY);
                highlight.setTouchY(touchY);
                if (null == h1) {
                    highlight.setTouchYValue(0);
                } else {
                    highlight.setTouchYValue(h1.getTouchYValue());
                }
                mChartVolume.highlightValues(new Highlight[]{highlight});

                Highlight highlight2 = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY2 = h.getTouchY() - mChartKline.getHeight() - mChartVolume.getHeight();
                Highlight h2 = mChartCharts.getHighlightByTouchPoint(h.getXIndex(), touchY2);
                highlight2.setTouchY(touchY2);
                if (null == h2) {
                    highlight2.setTouchYValue(0);
                } else {
                    highlight2.setTouchYValue(h2.getTouchYValue());
                }
                mChartCharts.highlightValues(new Highlight[]{highlight2});

                updateText(e.getXIndex());
            }

            @Override
            public void onNothingSelected() {
                mChartVolume.highlightValue(null);
                mChartCharts.highlightValue(null);
            }
        });

        mChartVolume.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Highlight highlight = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY = h.getTouchY() + mChartKline.getHeight();
                Highlight h1 = mChartKline.getHighlightByTouchPoint(h.getXIndex(), touchY);
                highlight.setTouchY(touchY);
                if (null == h1) {
                    highlight.setTouchYValue(0);
                } else {
                    highlight.setTouchYValue(h1.getTouchYValue());
                }
                mChartKline.highlightValues(new Highlight[]{highlight});

                Highlight highlight2 = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY2 = h.getTouchY() - mChartVolume.getHeight();
                Highlight h2 = mChartCharts.getHighlightByTouchPoint(h.getXIndex(), touchY2);
                highlight2.setTouchY(touchY2);
                if (null == h2) {
                    highlight2.setTouchYValue(0);
                } else {
                    highlight2.setTouchYValue(h2.getTouchYValue());
                }
                mChartCharts.highlightValues(new Highlight[]{highlight2});

                updateText(e.getXIndex());
            }

            @Override
            public void onNothingSelected() {
                mChartKline.highlightValue(null);
                mChartCharts.highlightValue(null);
            }
        });

        mChartCharts.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Highlight highlight = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY = h.getTouchY() + mChartVolume.getHeight();
                Highlight h1 = mChartVolume.getHighlightByTouchPoint(h.getXIndex(), touchY);
                highlight.setTouchY(touchY);
                if (null == h1) {
                    highlight.setTouchYValue(0);
                } else {
                    highlight.setTouchYValue(h1.getTouchYValue());
                }
                mChartVolume.highlightValues(new Highlight[]{highlight});

                Highlight highlight2 = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());

                float touchY2 = h.getTouchY() + mChartVolume.getHeight() + mChartKline.getHeight();
                Highlight h2 = mChartKline.getHighlightByTouchPoint(h.getXIndex(), touchY2);
                highlight2.setTouchY(touchY2);
                if (null == h2) {
                    highlight2.setTouchYValue(0);
                } else {
                    highlight2.setTouchYValue(h2.getTouchYValue());
                }
                mChartKline.highlightValues(new Highlight[]{highlight2});

                updateText(e.getXIndex());
            }

            @Override
            public void onNothingSelected() {
                mChartKline.highlightValue(null);
                mChartVolume.highlightValue(null);
            }
        });
    }

    /**
     * 1524896700000, 时间
     * 0.12416,       开盘
     * 0.15881,       最高
     * 0.00222,       最低
     * 0.061728,      收盘
     * 4416          成交量
     */
    private void getKlineData(int personCurrencyId, int time) {
        APIHelper.getInstance().getKLineData(personCurrencyId, time, new CallBack<KLineResp>() {

            @Override
            public void onSuccess(KLineResp data) {
                if (mContext == null || mContext.isFinishing()) {
                    return;
                }
                mData.test2(data);
                YAxis axisRight = mChartKline.getAxisRight();
                axisRight.resetAxisMaxValue();
                axisRight.setAxisMaxValue(mData.getYmax());
                axisRight.resetAxisMinValue();
                axisRight.setAxisMinValue(mData.getYmin());
                if (isFirst) {
                    setKLineDatas();
                } else {
                    kLineDatas = mData.getKLineDatas();
                    mData.initLineDatas(kLineDatas);
                    setKLineByChart(mChartKline);
//                    setVolumeByChart(mChartVolume);
//                    setMACDByChart(mChartCharts);
                    isRefresh = false;
                    mChartKline.moveViewToX(kLineDatas.size() - 1);
                    if (isFirst) {
//                        isFirst = false;
//                        mChartVolume.moveViewToX(kLineDatas.size() - 1);
//                        mChartCharts.moveViewToX(kLineDatas.size() - 1);
                    }
//                    setMarkerViewButtom(mData, mChartKline);
//                    setMarkerView(mData, mChartVolume);
//                    setMarkerView(mData, mChartCharts);
                }
            }

            @Override
            public void onFailure(BaseReslut data) {
            }
        });
    }

    private void setKLineDatas() {
        kLineDatas = mData.getKLineDatas();
        mData.initLineDatas(kLineDatas);
        setKLineByChart(mChartKline);
        setVolumeByChart(mChartVolume);
        setMACDByChart(mChartCharts);
        isRefresh = false;
        mChartKline.moveViewToX(kLineDatas.size() - 1);
        if (isFirst) {
            isFirst = false;
            mChartVolume.moveViewToX(kLineDatas.size() - 1);
            mChartCharts.moveViewToX(kLineDatas.size() - 1);
        }
        setMarkerViewButtom(mData, mChartKline);
        setMarkerView(mData, mChartVolume);
        setMarkerView(mData, mChartCharts);
    }

    private void setMarkerViewButtom(DataParse mData, MyCombinedChart combinedChart) {
        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(mContext, R.layout.mymarkerview);
        MyHMarkerView hMarkerView = new MyHMarkerView(mContext, R.layout.mymarkerview_line);
        MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(mContext, R.layout.mymarkerview);
        combinedChart.setMarker(leftMarkerView, bottomMarkerView, hMarkerView, mData);
    }

    private void setMarkerView(DataParse mData, MyCombinedChart combinedChart) {
        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(mContext, R.layout.mymarkerview);
        MyHMarkerView hMarkerView = new MyHMarkerView(mContext, R.layout.mymarkerview_line);
        combinedChart.setMarker(leftMarkerView, hMarkerView, mData);
    }

    private void setKLineByChart(MyCombinedChart combinedChart) {
        CandleDataSet set = new CandleDataSet(mData.getCandleEntries(), "");
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightEnabled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(1f);
        set.setValueTextSize(0f);
        set.setDecreasingColor(getResources().getColor(R.color.decreasing_color));//设置开盘价高于收盘价的颜色
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(getResources().getColor(R.color.increasing_color));//设置开盘价地狱收盘价的颜色
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(getResources().getColor(R.color.decreasing_color));//设置开盘价等于收盘价的颜色
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(1f);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
//        set.setDrawValues(true);
//        set.setValueTextColor(getResources().getColor(R.color.marker_text_bg));
        CandleData candleData = new CandleData(mData.getXVals(), set);

        mData.initKLineMA(kLineDatas);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataL()));
        sets.add(setMaLine(10, mData.getXVals(), mData.getMa10DataL()));
        sets.add(setMaLine(20, mData.getXVals(), mData.getMa20DataL()));
        sets.add(setMaLine(30, mData.getXVals(), mData.getMa30DataL()));

        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(lineData);
        combinedData.setData(candleData);
        combinedChart.setData(combinedData);

        setHandler(combinedChart);
    }

    private void setVolumeByChart(MyCombinedChart combinedChart) {
        String unit = MyUtils.getVolUnit(mData.getVolmax());
        String wan = getString(R.string.wan_unit);
        String yi = getString(R.string.yi_unit);
        int u = 1;
        if (wan.equals(unit)) {
            u = 4;
        } else if (yi.equals(unit)) {
            u = 8;
        }
        combinedChart.getAxisLeft().setValueFormatter(new VolFormatter((int) Math.pow(10, u)));


        BarDataSet set = new BarDataSet(mData.getBarEntries(), "成交量");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);

        List<Integer> list = new ArrayList<>();
        list.add(getResources().getColor(R.color.increasing_color));
        list.add(getResources().getColor(R.color.decreasing_color));
        set.setColors(list);
        BarData barData = new BarData(mData.getXVals(), set);

        mData.initVlumeMA(kLineDatas);
        ArrayList<ILineDataSet> sets = new ArrayList<>();

        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        sets.add(setMaLine(5, mData.getXVals(), mData.getMa5DataV()));
        sets.add(setMaLine(10, mData.getXVals(), mData.getMa10DataV()));
        sets.add(setMaLine(20, mData.getXVals(), mData.getMa20DataV()));
        sets.add(setMaLine(30, mData.getXVals(), mData.getMa30DataV()));

        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        setHandler(combinedChart);
    }

    private void setMACDByChart(MyCombinedChart combinedChart) {
        mData.initMACD(kLineDatas);

        BarDataSet set = new BarDataSet(mData.getMacdData(), "BarDataSet");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        List<Integer> list = new ArrayList<>();
        list.add(getResources().getColor(R.color.increasing_color));
        list.add(getResources().getColor(R.color.decreasing_color));
        set.setColors(list);

        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setMACDMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getDeaData()));
        sets.add(setMACDMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getDifData()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setKDJByChart(MyCombinedChart combinedChart) {
        mData.initKDJ(kLineDatas);

        BarDataSet set = new BarDataSet(mData.getBarDatasKDJ(), "BarDataSet");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.transparent));

        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getkData()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getdData()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getjData()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setWRByChart(MyCombinedChart combinedChart) {
        mData.initWR(kLineDatas);

        BarDataSet set = new BarDataSet(mData.getBarDatasWR(), "BarDataSet");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(mContext.getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.transparent));

        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getWrData13()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getWrData34()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getWrData89()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setRSIByChart(MyCombinedChart combinedChart) {
        mData.initRSI(kLineDatas);

        BarDataSet set = new BarDataSet(mData.getBarDatasRSI(), "BarDataSet");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.transparent));

        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getRsiData6()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getRsiData12()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getRsiData24()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setBOLLByChart(MyCombinedChart combinedChart) {
        mData.initBOLL(kLineDatas);

//        BarDataSet set = new BarDataSet(mData.getBarDatasBOLL(), "Sinus Function");
//        set.setBarSpacePercent(20); //bar空隙
//        set.setHighlightEnabled(true);
//        set.setHighLightAlpha(255);
//        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
//        set.setDrawValues(false);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(getResources().getColor(R.color.transparent));
//
//        BarData barData = new BarData(mData.getXVals(), set);

        CandleDataSet set = new CandleDataSet(mData.getCandleEntries(), "");
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightEnabled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(1f);
        set.setValueTextSize(10f);
        set.setDecreasingColor(getResources().getColor(R.color.decreasing_color));
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(getResources().getColor(R.color.increasing_color));
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(getResources().getColor(R.color.decreasing_color));
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(1f);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(true);
        set.setValueTextColor(getResources().getColor(R.color.marker_text_bg));
        set.setShowCandleBar(false);
        CandleData candleData = new CandleData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getBollDataUP()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getBollDataMB()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getBollDataDN()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setEXPMAByChart(MyCombinedChart combinedChart) {
        mData.initEXPMA(kLineDatas);

        int size = kLineDatas.size();   //点的个数
        CandleDataSet set = new CandleDataSet(mData.getCandleEntries(), "");
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightEnabled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowWidth(1f);
        set.setValueTextSize(10f);
        set.setDecreasingColor(getResources().getColor(R.color.decreasing_color));
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(getResources().getColor(R.color.increasing_color));
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(getResources().getColor(R.color.decreasing_color));
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(1f);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(true);
        set.setValueTextColor(getResources().getColor(R.color.marker_text_bg));
//        set.setShowCandleBar(false);
        CandleData candleData = new CandleData(mData.getXVals(), set);

//        BarDataSet set = new BarDataSet(mData.getBarDatasEXPMA(), "Sinus Function");
//        set.setBarSpacePercent(20); //bar空隙
//        set.setHighlightEnabled(true);
//        set.setHighLightAlpha(255);
//        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
//        set.setDrawValues(false);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(getResources().getColor(R.color.transparent));
//
//        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getExpmaData5()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getExpmaData10()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getExpmaData20()));
        sets.add(setKDJMaLine(3, mData.getXVals(), (ArrayList<Entry>) mData.getExpmaData60()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setDMIByChart(MyCombinedChart combinedChart) {
        mData.initDMI(kLineDatas);

        BarDataSet set = new BarDataSet(mData.getBarDatasDMI(), "Sinus Function");
        set.setBarSpacePercent(20); //bar空隙
        set.setHighlightEnabled(true);
        set.setHighLightAlpha(255);
        set.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.transparent));

        BarData barData = new BarData(mData.getXVals(), set);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setKDJMaLine(0, mData.getXVals(), (ArrayList<Entry>) mData.getDmiDataDI1()));
        sets.add(setKDJMaLine(1, mData.getXVals(), (ArrayList<Entry>) mData.getDmiDataDI2()));
        sets.add(setKDJMaLine(2, mData.getXVals(), (ArrayList<Entry>) mData.getDmiDataADX()));
        sets.add(setKDJMaLine(3, mData.getXVals(), (ArrayList<Entry>) mData.getDmiDataADXR()));
        LineData lineData = new LineData(mData.getXVals(), sets);

        CombinedData combinedData = new CombinedData(mData.getXVals());
        combinedData.setData(barData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);

        if (isRefresh)
            setHandler(combinedChart);
    }

    private void setHandler(MyCombinedChart combinedChart) {
        final ViewPortHandler viewPortHandlerBar = combinedChart.getViewPortHandler();
        viewPortHandlerBar.setMaximumScaleX(culcMaxscale(mData.getXVals().size()));
        Matrix touchmatrix = viewPortHandlerBar.getMatrixTouch();
        final float xscale = 3;
        touchmatrix.postScale(xscale, 1f);
    }


    private void addKlineData() {
        CandleData combinedData = mChartKline.getCandleData();
        LineData lineData = mChartKline.getLineData();

        int count = 0;
        int i = kLineDatas.size() - 1;
        String xVals = mData.getXVals().get(mData.getXVals().size() - 1);
        if (combinedData != null) {
            int indexLast = getLastDataSetIndex(combinedData);
            CandleDataSet lastSet = (CandleDataSet) combinedData.getDataSetByIndex(indexLast);

            if (lastSet == null) {
                lastSet = createCandleDataSet();
                combinedData.addDataSet(lastSet);
            }
            count = lastSet.getEntryCount();

//            combinedData.addXValue(xVals);
            // 位最后一个DataSet添加entry
            combinedData.addEntry(new CandleEntry(count, kLineDatas.get(i).high, kLineDatas.get(i).low, kLineDatas.get(i).open, kLineDatas.get(i).close), indexLast);
        }

        if (lineData != null) {
            int index = getDataSetIndexCount(lineData);
            LineDataSet lineDataSet5 = (LineDataSet) lineData.getDataSetByIndex(0);//五日均线;
            LineDataSet lineDataSet10 = (LineDataSet) lineData.getDataSetByIndex(1);//十日均线;
            LineDataSet lineDataSet20 = (LineDataSet) lineData.getDataSetByIndex(2);//二十日均线;
            LineDataSet lineDataSet30 = (LineDataSet) lineData.getDataSetByIndex(3);//三十日均线;

            if (lineDataSet5 != null) {
                mData.getMa5DataL().add(new Entry(KMAEntity.getLastMA(kLineDatas, 5), count));
                lineData.addEntry(mData.getMa5DataL().get(mData.getMa5DataL().size() - 1), 0);
            }

            if (lineDataSet10 != null) {
                mData.getMa10DataL().add(new Entry(KMAEntity.getLastMA(kLineDatas, 10), count));
                lineData.addEntry(mData.getMa10DataL().get(mData.getMa10DataL().size() - 1), 1);
            }

            if (lineDataSet20 != null) {
                mData.getMa20DataL().add(new Entry(KMAEntity.getLastMA(kLineDatas, 20), count));
                lineData.addEntry(mData.getMa20DataL().get(mData.getMa20DataL().size() - 1), 2);
            }

            if (lineDataSet30 != null) {
                mData.getMa30DataL().add(new Entry(KMAEntity.getLastMA(kLineDatas, 30), count));
                lineData.addEntry(mData.getMa30DataL().get(mData.getMa30DataL().size() - 1), 3);
            }
        }
    }

    private void addVolumeData() {
        BarData barData = mChartVolume.getBarData();
        LineData lineData = mChartVolume.getLineData();

        int count = 0;

        int i = kLineDatas.size() - 1;
        String xVals = mData.getXVals().get(mData.getXVals().size() - 1);
        if (barData != null) {
            int indexLast = getLastDataSetIndex(barData);
            BarDataSet lastSet = (BarDataSet) barData.getDataSetByIndex(indexLast);

            if (lastSet == null) {
                lastSet = createBarDataSet();
                barData.addDataSet(lastSet);
            }
            count = lastSet.getEntryCount();

//            barData.addXValue(xVals);
            // 位最后一个DataSet添加entry
            barData.addEntry(new BarEntry(count, kLineDatas.get(i).high, kLineDatas.get(i).low, kLineDatas.get(i).open, kLineDatas.get(i).close, kLineDatas.get(i).vol), indexLast);
        }

        if (lineData != null) {
            int index = getDataSetIndexCount(lineData);
            LineDataSet lineDataSet5 = (LineDataSet) lineData.getDataSetByIndex(0);//五日均线;
            LineDataSet lineDataSet10 = (LineDataSet) lineData.getDataSetByIndex(1);//十日均线;
            LineDataSet lineDataSet20 = (LineDataSet) lineData.getDataSetByIndex(2);//二十日均线;
            LineDataSet lineDataSet30 = (LineDataSet) lineData.getDataSetByIndex(3);//三十日均线;

            if (lineDataSet5 != null) {
                mData.getMa5DataV().add(new Entry(VMAEntity.getLastMA(kLineDatas, 5), count));
                lineData.addEntry(mData.getMa5DataV().get(mData.getMa5DataV().size() - 1), 0);
            }

            if (lineDataSet10 != null) {
                mData.getMa10DataV().add(new Entry(VMAEntity.getLastMA(kLineDatas, 10), count));
                lineData.addEntry(mData.getMa10DataV().get(mData.getMa10DataV().size() - 1), 1);
            }

            if (lineDataSet20 != null) {
                mData.getMa20DataV().add(new Entry(VMAEntity.getLastMA(kLineDatas, 20), count));
                lineData.addEntry(mData.getMa20DataV().get(mData.getMa20DataV().size() - 1), 2);
            }

            if (lineDataSet30 != null) {
                mData.getMa30DataV().add(new Entry(VMAEntity.getLastMA(kLineDatas, 30), count));
                lineData.addEntry(mData.getMa30DataV().get(mData.getMa30DataV().size() - 1), 3);
            }
        }
    }

    /**
     * 获取最后一个CandleDataSet的索引
     */
    private int getLastDataSetIndex(CandleData candleData) {
        int dataSetCount = candleData.getDataSetCount();
        return dataSetCount > 0 ? (dataSetCount - 1) : 0;
    }

    /**
     * 获取最后一个LineDataSet的索引
     */
    private int getDataSetIndexCount(LineData lineData) {
        int dataSetCount = lineData.getDataSetCount();
        return dataSetCount;
    }

    /**
     * 获取最后一个CandleDataSet的索引
     */
    private int getLastDataSetIndex(BarData barData) {
        int dataSetCount = barData.getDataSetCount();
        return dataSetCount > 0 ? (dataSetCount - 1) : 0;
    }

    private CandleDataSet createCandleDataSet() {
        CandleDataSet dataSet = new CandleDataSet(null, "DataSet 1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setValueTextSize(12f);

        return dataSet;
    }

    private LineDataSet createLineDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "DataSet 1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setValueTextSize(12f);

        return dataSet;
    }

    private BarDataSet createBarDataSet() {
        BarDataSet dataSet = new BarDataSet(null, "DataSet 1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setValueTextSize(12f);

        return dataSet;
    }

    private float culcMaxscale(float count) {
        float max = 1;
        max = count / 127 * 5;
        return max;
    }

    @NonNull
    private LineDataSet setMaLine(int ma, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);
        if (ma == 5) {
            lineDataSetMa.setHighlightEnabled(true);
            lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
            lineDataSetMa.setHighLightColor(getResources().getColor(R.color.marker_line_bg));
        } else {/*此处必须得写*/
            lineDataSetMa.setHighlightEnabled(false);
        }
        lineDataSetMa.setDrawValues(false);
        if (ma == 5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
        } else if (ma == 10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
        } else if (ma == 20) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma30));
        }
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineDataSetMa.setHighlightEnabled(false);
        return lineDataSetMa;
    }

    @NonNull
    private LineDataSet setMACDMaLine(int type, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawValues(false);

        //DEA
        if (type == 0) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
        }

        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

        return lineDataSetMa;
    }

    @NonNull
    private LineDataSet setKDJMaLine(int type, ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawValues(false);

        //DEA
        if (type == 0) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
        } else if (type == 1) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
        } else if (type == 2) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma30));
        }

        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

        return lineDataSetMa;
    }

    private void updateText(int index) {
        int newIndex = index;
        if (null != mData.getMa5DataL() && mData.getMa5DataL().size() > 0) {
            if (newIndex >= 0 && newIndex < mData.getMa5DataL().size()) {
            }
//                mTvKMa5.setText(MyUtils.getDecimalFormatVol(mData.getMa5DataL().get(newIndex).getVal()));
        }
        if (null != mData.getMa10DataL() && mData.getMa10DataL().size() > 0) {
            if (newIndex >= 0 && newIndex < mData.getMa10DataL().size()) {
            }
//                mTvKMa10.setText(MyUtils.getDecimalFormatVol(mData.getMa10DataL().get(newIndex).getVal()));
        }
        if (null != mData.getMa20DataL() && mData.getMa20DataL().size() > 0) {
            if (newIndex >= 0 && newIndex < mData.getMa20DataL().size()) {
            }
//                mTvKMa20.setText(MyUtils.getDecimalFormatVol(mData.getMa20DataL().get(newIndex).getVal()));
        }
        if (null != mData.getMa30DataL() && mData.getMa30DataL().size() > 0) {
            if (newIndex >= 0 && newIndex < mData.getMa30DataL().size()) {
            }
//                mTvKMa30.setText(MyUtils.getDecimalFormatVol(mData.getMa30DataL().get(newIndex).getVal()));
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handlerAdd.removeCallbacks(runnable);
    }
}
