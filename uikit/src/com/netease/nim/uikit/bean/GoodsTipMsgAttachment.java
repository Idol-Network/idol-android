package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * 自定义的发送宝贝链接的消息 只在咨询时对买家自己展示
 * Created by zhanghengzhen on 2017/9/1.
 */
public class GoodsTipMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CONTENT = "content";
    private static final String TITLE = "title";//商品标题
    private static final String PRICE = "price";//价格区间
    private static final String ID = "targetId";//商品id
    private static final String UID = "uid";//店铺用户id
    private static final String NAME = "name";//店铺名称
    private static final String PIC = "pic";//商品图片
    private static final String URL = "url";//链接地址

    private String title;
    private String price;
    private long id;
    private int uid;
    private String name;
    private String pic;
    private String url;

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public GoodsTipMsgAttachment() {
        super(CustomAttachmentType.TYPE_GOODS_URL_TIP);//消息类型
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        JSONObject content = data.optJSONObject(CONTENT);
        if (content != null) {
            title = content.optString(TITLE);
            price = content.optString(PRICE);
            id = content.optLong(ID);
            uid = content.optInt(UID);
            name = content.optString(NAME);
            pic = content.optString(PIC);
            url = content.optString(URL);
        }
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();
        try {
            content.put(TITLE, title);
            content.put(PRICE, price);
            content.put(ID, id);
            content.put(UID, uid);
            content.put(NAME, name);
            content.put(PIC, pic);
            content.put(URL, url);
            data.put(CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}
