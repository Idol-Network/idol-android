package com.netease.nim.uikit.bean;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 订单消息
 * Created by zhanghengzhen on 2017/9/1.
 */
public class OrderMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String CONTENT = "content";
    private static final String TITLE = "title";//标题
    private static final String ID = "targetId";//订单编号
    private static final String LIST = "list";//展示数据的键值对 格式为list:[{key:"名称",value:"内容"}]
    private static final String URL = "url";//链接地址


    private String title;
    private String id;
    private ArrayList<String> jsonList;
    private String url;

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public OrderMsgAttachment() {
        super(CustomAttachmentType.TYPE_ORDER);//消息类型
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<String> getJsonList() {
        return jsonList;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        JSONObject content = data.optJSONObject(CONTENT);
        if (content != null) {
            title = content.optString(TITLE);
            id = content.optString(ID);
            url = content.optString(URL);
            JSONArray list = content.optJSONArray(LIST);
            jsonList = new ArrayList<>();
            for (int i = 0; i < list.length(); i++) {
                jsonList.add(list.optString(i));
            }
        }
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            JSONObject i = new JSONObject();
//            i.put("key", "订单号");
//            i.put("value", "122132446545");
//            JSONObject j = new JSONObject();
//            j.put("key", "订单号");
//            j.put("value", "122132446545");
//            JSONObject k = new JSONObject();
//            k.put("key", "订单号");
//            k.put("value", "122132446545");
//            JSONArray array = new JSONArray();
//            array.put(i);
//            array.put(j);
//            array.put(k);
//            JSONObject content = new JSONObject();
//            content.put("list", array);
//            content.put(ID, "E39845701341234");
//            content.put(TITLE, "待签收");
//            jsonObject.put(CONTENT, content);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
        return null;
    }

}
