package com.yiju.ldol.ui.view.mychart;

import java.util.List;

public class KLineData {


    public DataBean data;

    public static class DataBean {

        public Sz002081Bean sz002081;

        public static class Sz002081Bean {
            public List<List<String>> day;
        }
    }
}

