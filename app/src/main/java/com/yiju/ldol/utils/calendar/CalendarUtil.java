package com.yiju.ldol.utils.calendar;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;

import com.yiju.idol.bean.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {

    private CalendarUtil() {
    }

    /**
     * 获得当月显示的日期（上月 + 当月 + 下月）
     *
     * @return
     */
    public static List<DateBean> getMonthDate(long current, List<Long> signedDates) {
        SparseBooleanArray signedArrays = getSignedDates(signedDates);
        // 获取日期实例
        Calendar calendar = Calendar.getInstance();
        // 将日历设置为指定的时间
        calendar.setTimeInMillis(current);
        //获取当前日期
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        // 获取当前年份
        int year = calendar.get(Calendar.YEAR);
        // 这里要注意，月份是从0开始，实际月份+1
        int month = calendar.get(Calendar.MONTH) + 1;
        List<DateBean> datas = new ArrayList<>();
        int week = SolarUtil.getFirstWeekOfMonth(year, month - 1);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = SolarUtil.getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = SolarUtil.getMonthDays(year, month);//当前月总天数

        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }

        for (int i = 0; i < week; i++) {
            //是否已签到
            boolean signed = signedArrays.get(lastMonth * 100 + lastMonthDays - week + 1 + i);
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, false, signed, 0));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            boolean signed = signedArrays.get(month * 100 + i + 1);
            datas.add(initDateBean(year, month, i + 1, i + 1 == date, signed, 1));
        }

        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
            boolean signed = signedArrays.get(nextMonth * 100 + i + 1);
            datas.add(initDateBean(nextYear, nextMonth, i + 1, false, signed, 2));
        }

        return datas;
    }

    private static SparseBooleanArray getSignedDates(List<Long> signedDates) {
        if (signedDates == null) {
            return new SparseBooleanArray(0);
        }
        SparseBooleanArray array = new SparseBooleanArray(signedDates.size());
        for (long date : signedDates) {
            int[] solar = getDate(date);
            int i = solar[0] * 100 + solar[1];//将日期转成数字，如2月3日：203 方便存入SparseBooleanArray与指定日期进行比较
            array.put(i, true);
        }
        return array;
    }

    private static DateBean initDateBean(int year, int month, int day, boolean isToday, boolean isSigned, int type) {
        DateBean dateBean = new DateBean();
        dateBean.setSolar(year, month, day);
        dateBean.setIsToday(isToday);
        dateBean.setSigned(isSigned);
        dateBean.setType(type);
        return dateBean;
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthRows(int year, int month) {
        int items = SolarUtil.getFirstWeekOfMonth(year, month - 1) + SolarUtil.getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
        if (rows == 4) {
            rows = 5;
        }
        return rows;
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position
     * @return
     */
    public static int[] positionToDate(int position, int startY, int startM) {
        int year = position / 12 + startY;
        int month = position % 12 + startM;

        if (month > 12) {
            month = month % 12;
            year = year + 1;
        }

        return new int[]{year, month};
    }

    /**
     * 根据年月得到ViewPager position
     *
     * @param year
     * @param month
     * @return
     */
    public static int dateToPosition(int year, int month, int startY, int startM) {
        return (year - startY) * 12 + month - startM;
    }

    /**
     * 计算日期
     *
     * @return
     */
    public static int[] getDate(long current) {
        Calendar calendar = Calendar.getInstance();
        // 将日历设置为指定的时间
        calendar.setTimeInMillis(current);
        return new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
//        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }

    public static int[] strToArray(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] strArray = str.split("\\.");
            int[] result = new int[strArray.length];
            for (int i = 0; i < strArray.length; i++) {
                result[i] = Integer.valueOf(strArray[i]);
            }
            return result;
        }
        return null;
    }

    public static long dateToMillis(int[] date) {
        int day = date.length == 2 ? 1 : date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(date[0], date[1], day);
        return calendar.getTimeInMillis();
    }

    public static int getPxSize(Context context, int size) {
        return size * context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getTextSize1(Context context, int size) {
        return (int) (size * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getTextSize(Context context, int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, context.getResources().getDisplayMetrics());

    }
}
