package com.yiju.ldol.bean.response;

import com.contrarywind.interfaces.IPickerViewData;
import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by d on 2018/6/28.
 */

public class RegionListResp extends BaseReslut {

    public List<RegionModelBean> countryList;//国家列表
    public List<RegionModelBean> regionModelList;//国内省份列表

    public static class RegionModelBean implements Serializable, IPickerViewData {

        public String areaCode;//地区代码 香港、台湾、澳门有单独的地区代码，其他省份为null。若areaCode不为空，则用省份的areaCode代替国家的areaCode
        public List<RegionModelBean> childRegionModels;//子级地区列表
        public String firstLetter;//首字母
        public String name;//名称
        public int value;//值 表示国家ID、省ID、市ID等

        @Override
        public String getPickerViewText() {
            return name;
        }
    }
}
