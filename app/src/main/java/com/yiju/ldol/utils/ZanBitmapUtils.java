package com.yiju.ldol.utils;


import com.yiju.idol.R;

import java.util.Random;

/**
 * Created by lxmpc on 2016/11/15.
 */

public class ZanBitmapUtils {

    private static int[] mZanNormalDrawables = new int[]{R.drawable.zan0001, R.drawable.zan0002, R.drawable.zan0003, R.drawable.zan0004, R.drawable.zan0005};
    private static Random random = new Random();

    public static int getZanRandomBitmapIndex(){
//        return  mZanNormalDrawables[random.nextInt(5)];
        return  random.nextInt(mZanNormalDrawables.length)+1;
    }


    public static int getZanIndexBitmap(int index){

        return  mZanNormalDrawables[index-1];
    }
}
