package com.yiju.ldol.utils;

import android.text.TextUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式 工具类
 */
public class RegexUtils {
    private static final String NUMBER_PATTERN = "^[0-9]+(.[0-9]{0,1})?$";// 判断小数点后一位的数字的正则表达式

    private static final String CNUMBER_PATTERN = "^[0-9]*$";// 判断数字的正则表达式

//	private static final String POSITIVE = "^[0-9]*[1-9][0-9]*$";//必须是1-9开头的

    public static void main(String[] args) {

        System.out.println("--------->" + isDecimalNumber("1.0"));
        System.out.println("--------->" + isInteger("1"));
        System.out.println("--------->" + isMobileNO("13552209513"));
    }

    /**
     *  '[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z0-9]+'
     * 校验邮箱是否合法
     *
     * @param paramString
     * @return
     */
    public static boolean isValidEmail(String paramString) {

        String regex = "[a-zA-Z0-9_\\.]{1,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
        return paramString.matches(regex);
    }

    public static boolean isPwd(String pwd) {
        String regex = "[0-9]*(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))+[a-zA-Z]*";
        return pwd.matches(regex);
    }

    /**
     * 校验身份证号15/18位是否合法
     *
     * @param paramString
     * @return
     */
    public static boolean isValidIdNumber(String paramString) {
        String regex = "^\\d{15}(\\d{2}[0-9xX])?$";
        // String regex = "^\\d{15}|\\d{18}$";
        return paramString.matches(regex);
    }

    /**
     * 校验银行卡号位数
     *
     * @param paramString
     * @return
     */
    public static boolean isValidBankCardNumber(String paramString) {

//		String regex = "^(\\d{4}(.)\\d{4}(.)\\d{4}(.)\\d{4}|\\d{4}(.)\\d{4}(.)\\d{4}(.)\\d{4}(.)\\d{2}|\\d{4}(.)\\d{4}(.)\\d{4}(.)\\d{4}(.)\\d{3})$";
        String regex = "^(\\d{15,19})$";
        return paramString.matches(regex);
    }


    /**
     * 校验中文字符
     *
     * @param paramString
     * @return
     */
    public static boolean isValidChinaChar(String paramString) {

        String regex = "[\u4e00-\u9fa5]{2,}";
        return paramString.matches(regex);
    }

    /**
     * 验证昵称是否合法 只能为字母数字下划线 汉字
     *
     * @param paramString
     * @return
     */
    public static boolean isValidNickName(String paramString) {
        String regex = "[^-+*/)#`~@!(%&',;=?$\\x22]+";
        return paramString.matches(regex);
    }

    /**
     * 验证昵称是否合法 只能为字母数字下划线 // 4-20个字符{4,20}
     *
     * @param paramString
     * @return
     */
    public static boolean isValidNickNameNotChinese(String paramString) {
        String regex = "^[a-zA-Z0-9_]*$";
        return paramString.matches(regex);
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int getStringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
				/* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 验证是不是数字(验证到小数点后一位)
     *
     * @param number
     * @return
     */
    public static boolean isDecimalNumber(String number) {
        return match(NUMBER_PATTERN, number);
    }

    /**
     * 验证是不是数字(没有小数点)
     *
     * @param number
     * @return
     */
    public static boolean isInteger(String number) {
        return match(CNUMBER_PATTERN, number);
    }
    /**
     * 验证是不是正整数(没有小数点)
     *
     * @param number
     * @return
     */
//	public static boolean isPositiveInteger(String number) {
//		return match(POSITIVE, number);
//	}

    /**
     * 执行正则表达式
     *
     * @param pattern 表达式
     * @param str     待验证字符串
     * @return 返回 <b>true </b>,否则为 <b>false </b>
     */
    private static boolean match(String pattern, String str) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.find();
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
     * @param phoneNum String类型
     * @return boolean
     * @date 创建时间：2015-01-18
     * @Description 获取以“+86”开头的11位手机号码
     */
    public static boolean isPhonePre(String phoneNum) {
        Pattern p2 = Pattern
                .compile("^(\\+?86)\\d{11}$");
        Matcher m = p2.matcher(phoneNum);

        if (m.matches()) {
            String mobile = phoneNum.substring(3);
            return mobile.startsWith("1");
        }
        return false;
    }



    /**
     * 生成指定长度的随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 生成六位随机数
     *
     * @return
     */
    public static int getRandomNumber() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++)
            result = result * 10 + array[i];
        return result;
    }

    /**
     * 获取每四位分隔的银行卡号码
     *
     * @param bankId
     */
    public static String getFormatedBankCardId(String bankId) {
        String arrs[] = bankId.replace(" ", "").split("");
        int len = arrs.length;
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < len; i++) {
            sb.append(arrs[i]);
            if (i % 4 == 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
