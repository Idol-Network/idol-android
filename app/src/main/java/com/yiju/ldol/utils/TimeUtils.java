package com.yiju.ldol.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.text.TextUtils;

import com.yiju.idol.R;
import com.yiju.idol.base.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm",
            Locale.getDefault());
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd",
            Locale.getDefault());

    /**
     * 根据时间戳返回MM-dd HH:mm 格式的时间 如果是当天的只返回HH:mm
     *
     * @param time
     * @return
     */
    public static String getTimeString(long time) {
        Date date = new Date(time);
        String str = hourFormat.format(date);
        String day = dayFormat.format(date);
        String today = dayFormat.format(new Date());

        // if (!today.equals(day))
        // {
        str = day + " " + str;
        // }

        return str;
    }

    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return sf.format(d);
    }

    static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String getDateString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    /**
     * 返回带 时 分 的时间字符串
     *
     * @param time
     * @return
     */
    public static String getDateToString2(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sf.format(d);
    }

    public static String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(date);
    }

    /**
     * 返回带 时 分 秒 的时间字符串
     *
     * @param time
     * @return
     */
    public static String getDateToString3(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sf.format(d);
    }


    /**
     * 2016-05-22
     *
     * @param time
     * @return
     */
    public static String timeFromat(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sf.format(d);
    }

    /**
     * 2018-06-28
     * 根据系统设置 格式化时间
     *
     * @param time    时间戳
     * @param pattern 格式化规则
     * @return
     */
    public static String formatTime(long time, String pattern) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sf.format(d);
    }

    public static String getBirthday(long time) {
        Date d = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        return format.format(d);
    }

    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 时 " + minutes + " 分 " + seconds + " 秒";
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static String getStringTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws
     */
    public static boolean isToday(String day) {
        if (TextUtils.isEmpty(day)) {
            return false;
        }
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = null;
            date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    return true;
                }
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();
        DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
        return DateLocal.get();
    }

    /**
     * 是本月返回本月  否则返回月  不是本年返回年月
     *
     * @param time
     * @return
     */
    public static String formatDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String param = sdf.format(date);// 参数时间
        Date nowData = new Date();
        String now = sdf.format(nowData);// 当前时间
        if (TextUtils.equals(param, now)) {//当年
            sdf = new SimpleDateFormat("MM");
            String s1 = sdf.format(date);
            String s2 = sdf.format(nowData);
            if (TextUtils.equals(s1, s2)) {
                return "本月";
            } else {
                return s1 + "月";
            }
        } else {//去年或以前
            sdf = new SimpleDateFormat("yyyy年MM月");
            return sdf.format(date);//返回年份
        }
    }


    public static final long MILLIS_PER_SECOND = 1000 * 60;

    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;


    public static void main(String args[]) {
        String s = formatFriendlyTimeSpanByNow(new Date().getTime());
        System.out.println(s);
    }

    /**
     * 刚刚
     * 1-59分钟前
     * 1-23小时前
     * 1天前
     * 2天前
     * 3天前显示日期 2017-3-18
     * <p>
     * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
     * <p>
     * copy from AndroidUtilCode
     *
     * @param timeStampMillis
     * @return
     */
    public static String formatFriendlyTimeSpanByNow(long timeStampMillis) {
        long now = System.currentTimeMillis();
        long span = now - timeStampMillis;
//        if (span < 0) {
//            // 'c' 日期和时间，被格式化为 "%ta %tb %td %tT %tZ %tY"，例如 "Sun Jul 20 16:17:00 EDT 1969"。
//            return String.format("%tc", timeStampMillis);
//        }
        Resources resources = App.getApp().getResources();
        if (span < MILLIS_PER_SECOND) {//分钟
            return resources.getString(R.string.just_now);
        } else if (span < MILLIS_PER_MINUTE) {
            return String.format(resources.getString(R.string.minutes_ago), span / MILLIS_PER_SECOND);
        } else if (span < MILLIS_PER_HOUR) {
            return String.format(resources.getString(R.string.hours_ago), span / MILLIS_PER_MINUTE);
        }
        // 获取当天00:00
        long wee = getMorningTime();
        if (timeStampMillis >= wee) {
            // 'R' 24 小时制的时间，被格式化为 "%tH:%tM"
            return String.format(resources.getString(R.string.today), timeStampMillis);
        } else if (timeStampMillis >= wee - MILLIS_PER_DAY) {
            return String.format(resources.getString(R.string.yesterday), timeStampMillis);
        } else {
            // 'F' ISO 8601 格式的完整日期，被格式化为 "%tY-%tm-%td"。
            return String.format("%tF", timeStampMillis);
        }
    }

    /**
     * @return
     * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
     * 1 返回yyyy-MM-dd 23:59:59日期
     */
    public static long getMorningTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

//        if (flag == 0) {
        return cal.getTimeInMillis();
//        } else if (flag == 1) {
//            //凌晨23:59:59
//            cal.setTimeInMillis(cal.getTimeInMillis() + 23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);
//        }
//        return cal.getTime();
    }

    private static SimpleDateFormat sFormat3 = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());

//    public static List<List<HisData>> get5Day(Context context) {
//        InputStream is = context.getResources().openRawResource(R.raw.fiveday);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String json = writer.toString();
//        final List<LinkedHashMap<String, List<LineModel>>> list = new Gson().fromJson(json, new TypeToken<List<LinkedHashMap<String, List<LineModel>>>>() {
//        }.getType());
//        List<List<HisData>> fivedays = new ArrayList<>(5);
//
//        for (int i = 0; i < list.size(); i++) {
//
//            List<HisData> hisData = new ArrayList<>(100);
//            List<LineModel> lineModels = list.get(i).values().iterator().next();
//            String time = list.get(i).keySet().iterator().next();
//
//            for (int j = 0; j < lineModels.size(); j++) {
//                LineModel m = lineModels.get(j);
//                HisData data = new HisData();
//                data.setClose(m.getPrice());
//                data.setVol(m.getVolume());
//                data.setOpen(j == 0 ? 0 : lineModels.get(j - 1).getPrice());
//                try {
//                    data.setDate(sFormat3.parse(time + m.getTime()).getTime());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                hisData.add(data);
//            }
//            fivedays.add(hisData);
//        }
//        return fivedays;
//    }
//    private static SimpleDateFormat sFormat1 = new SimpleDateFormat("HHmm", Locale.getDefault());
//
//    public static List<HisData> get1Day(Context context) {
//        InputStream is = context.getResources().openRawResource(R.raw.oneday);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String json = writer.toString();
//        final List<LineModel> list = new Gson().fromJson(json, new TypeToken<List<LineModel>>() {
//        }.getType());
//        List<HisData> hisData = new ArrayList<>(100);
//        for (int i = 0; i < list.size(); i++) {
//            LineModel m = list.get(i);
//            HisData data = new HisData();
//            data.setClose(m.getPrice());
//            data.setVol(m.getVolume());
//            data.setOpen(i == 0 ? 0 : list.get(i - 1).getPrice());
//            try {
//                data.setDate(sFormat1.parse(m.getTime()).getTime());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            hisData.add(data);
//        }
//        return hisData;
//    }
//
//    private static SimpleDateFormat sFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
//    public static List<HisData> getK(Context context, int day) {
//        int res = R.raw.day_k;
//        if (day == 7) {
//            res = R.raw.week_k;
//        } else if (day == 30) {
//            res = R.raw.month_k;
//        }
//        InputStream is = context.getResources().openRawResource(res);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String json = writer.toString();
//        final List<KModel> list = new Gson().fromJson(json, new TypeToken<List<KModel>>() {}.getType());
//        List<HisData> hisData = new ArrayList<>(100);
//        for (int i = 0; i < list.size(); i++) {
//            KModel m = list.get(i);
//            HisData data = new HisData();
//            data.setClose(m.getPrice_c());
//            data.setOpen(m.getPrice_o());
//            data.setHigh(m.getPrice_h());
//            data.setLow(m.getPrice_l());
//            data.setVol(m.getVolume());
//            try {
//                data.setDate(sFormat2.parse(m.getTime()).getTime());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            hisData.add(data);
//        }
//        return hisData;
//    }
}
