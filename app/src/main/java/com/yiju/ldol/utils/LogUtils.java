package com.yiju.ldol.utils;


import android.text.TextUtils;
import android.util.Log;

import com.yiju.idol.base.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class wraps around Android Log class to add additional thread name/id
 * information is desirable.
 */
public class LogUtils {

    public static boolean DEBUG_MODE = Constant.DEBUG_MODE;

    public static String getPrefix() {
        if (DEBUG_MODE) {
            return "[" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + "] ";
        } else {
            return "";
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG_MODE)
            Log.e(tag, getPrefix() + message);
    }

    public static void e(String tag, String message, Exception e) {
        if (DEBUG_MODE)
            Log.e(tag, getPrefix() + message, e);
    }

    public static void e(String tag, String message, Throwable tr) {
        if (DEBUG_MODE)
            Log.e(tag, getPrefix() + message, tr);
    }

    public static void w(String tag, String message) {
        if (DEBUG_MODE)
            Log.w(tag, getPrefix() + message);
    }

    public static void i(String tag, String message) {
        if (DEBUG_MODE)
            Log.i(tag, getPrefix() + message);
    }

    public static void d(String tag, String message) {
        if (DEBUG_MODE)
            Log.d(tag, getPrefix() + message);
    }

    public static void v(String tag, String message) {
        if (DEBUG_MODE)
            Log.v(tag, getPrefix() + message);
    }

    /**
     * Json格式化输出
     *
     * @param tag
     * @param message                 内容
     * @param isOutputOriginalContent 是否输入原内容
     */
    public static void iJsonFormat(String tag, String message, boolean isOutputOriginalContent) {
        if (DEBUG_MODE && !TextUtils.isEmpty(message)) {
            if (isOutputOriginalContent) {
                Log.i(tag, message);
            }
            Log.e(tag, format(convertUnicode(message)));
        }
    }

    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
    public static void printJson(String tag, String msg, String headString) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        printLine(tag, false);
    }
}
