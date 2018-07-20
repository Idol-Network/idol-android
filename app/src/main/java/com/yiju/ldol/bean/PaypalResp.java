package com.yiju.ldol.bean;

public class PaypalResp {


    public ResponseBean response;
    public ClientBean client;
    public String response_type;

    public static class ResponseBean {

        public String state;
        public String id;
        public String create_time;
        public String intent;
    }

    public static class ClientBean {

        public String platform;
        public String paypal_sdk_version;
        public String product_name;
        public String environment;
    }
}
