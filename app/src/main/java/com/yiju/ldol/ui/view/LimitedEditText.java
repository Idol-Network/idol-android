package com.yiju.ldol.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import com.yiju.idol.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhanghengzhen on 2016/8/15.
 */
public class LimitedEditText extends AppCompatEditText {

    public LimitedEditText(Context context) {
        this(context, null);
    }

    public LimitedEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);

    }

    public LimitedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.LimitedEditText);
            setFilter(typedArray
                    .getInt(R.styleable.LimitedEditText_limitBytes, 0));
            typedArray.recycle();
        }
    }

    /**
     * 设置最大输入字节数
     *
     * @param limitBytes 如果limitBytes<=0，则无法发挥作用
     */
    public void setFilter(final int limitBytes) {
        this.setFilters(new InputFilter[]{new InputFilter() {

                    private int keep;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // end是要输入的字符长度 dstart,dend默认再输入字符的时候二个长度是相等的
                        // 在输入法中执行删除字符时 dstart显示的是删除后的字符长度,dend显示的是删除前的字符长度
                        // 仅当limitBytes>0时有效
                        if (limitBytes > 0) {
                            try {
                                keep = limitBytes
                                        - (dest.toString().getBytes("GBK").length - (dend - dstart));
                                if (keep <= 0) {
                                    // 此处判断已输入的字符长度是否大于等于最大长度
                                    return "";
                                } else if (keep > source.toString().getBytes("GBK").length
                                        - start) {
                                    // 判断输入的字符长度要是小于最大长度则返回输入的字符
                                    return null;
                                } else {
                                    return source.subSequence(start, (start + keep) / 2);// 要是输入的长度大于最大长度则截取前面的几个字符输出
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }
                }
        );
    }
}
