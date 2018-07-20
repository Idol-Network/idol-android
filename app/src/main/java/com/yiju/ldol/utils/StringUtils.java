package com.yiju.ldol.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class StringUtils {

    @SuppressLint("NewApi")
    public static boolean isEmpty(String str) {
        return (null == str) || (str.trim().equals("") || (str.length() <= 0) || str.isEmpty());
    }

    /**
     * 验证码是否有效(6位数字)
     *
     * @param s
     * @return
     */
    public static boolean isValideVerify(String s) {
        return s.matches("^\\d{6}$");
    }

    /**
     * 验证密码是否有效(6-12位字符)
     *
     * @param s
     * @return
     */
    public static boolean isValidePwd(String s) {
        return s.matches("^\\w{6,12}$");
    }


    /**
     * 判断手机号码是否合法
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或4或5或7或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][34578]\\d{9}";
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else {
            if (TextUtils.isDigitsOnly(mobiles) && mobiles.length() == 11) {
                return mobiles.matches(telRegex);
            } else {
                return false;
            }
        }
    }

    /**
     * @param price 价格的整数最多可以输入8位   带小数点的最多可以输入11位
     * @return
     */
    public static boolean priceIsOk(String price) {
        return price.matches("((^[-]?([1-9]\\d{0,6}))|^0)(\\.\\d{1,2})?$|(^[-]0\\.\\d{1,2}$)");
    }

    /**
     * 代言奖励百分比
     *
     * @param s
     * @return
     */
    public static boolean agentRewardPercentIsOk(String s) {
        return s.matches("^(([1-7]\\d?)+(\\.[0-9]{1})?|80|80.0)$");
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    /**
     * Double类型保留一位小数，返回String类型（注意四舍五入的影响）
     */
    public static String formatDoubleToString(Double d) {
        if (d == null) {
            return "0.0";
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(d);
    }

    /**
     * Double类型保留一位小数，返回double类型（四舍五入）
     */
    public static double formatDouble(double d) {
        return formatDouble(d, 1);
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入） newScale 为指定的位数
     */
    public static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String formatString(String d1, int newScale) {
        DecimalFormat f = null;
        double d = Double.valueOf(d1);
        switch (newScale) {
            case 0:
                f = new DecimalFormat("###0");
                break;
            case 1:
                d = formatDouble(d, 1);
                f = new DecimalFormat("###0.0");
                break;
            case 2:
                d = formatDouble(d, 2);
                f = new DecimalFormat("###0.00");
                break;
            default:
                break;
        }
        return (f.format(d));
    }

    /**
     * 保留小数（注意四舍五入的影响）
     *
     * @param d , decimal
     * @return
     */
    public static String getDecimal(double d, int decimal) {
        DecimalFormat f = null;
        switch (decimal) {
            case 0:
                f = new DecimalFormat("###0");
                break;
            case 1:
                d = formatDouble(d, 1);
                f = new DecimalFormat("###0.0");
                break;
            case 2:
                d = formatDouble(d, 2);
                f = new DecimalFormat("###0.00");
                break;
            default:
                break;
        }
        return (f.format(d));
    }


    /**
     * 获取AppKey
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }


    public static String getTime(String time) {

        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;

    }

    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        return date;
    }

    /**
     * 将秒转换为时间格式
     *
     * @param seconds exp:110s
     * @return exp: 01:50
     */
    public static String timeFormat(int seconds) {
        StringBuffer sbf = new StringBuffer();
        int i = seconds / 60;
        int j = seconds % 60;
        if (i < 10) {
            sbf.append("0");
        }
        sbf.append(i).append(":");
        if (j < 10) {
            sbf.append("0");
        }
        sbf.append(j);
        return sbf.toString();
    }

    private static String ordercode;

    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        ordercode = key.substring(0, 15);
        return ordercode;
    }

    public static String replaceString(String s) {
        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 0x4e00) && (c <= 0x9fbb)) {
                temp += c;
            }
        }
        return temp;
    }


    /**
     * Convert time to a string
     *
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String millisToString(long millis) {
        return StringUtils.millisToString(millis, false);
    }


    static String millisToString(long millis, boolean text) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (text) {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + "h" + format.format(min) + "min";
            else if (min > 0)
                time = (negative ? "-" : "") + min + "min";
            else
                time = (negative ? "-" : "") + sec + "s";
        } else {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
            else
                time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
