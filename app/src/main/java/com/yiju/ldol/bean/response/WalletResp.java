package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

/**
 * 用户钱包信息
 * Created by zhanghengzhen on 2018/5/5.
 */

public class WalletResp extends BaseReslut {
    public long idolNum;//IDOL币数量
    public String idolNumStr;//IDOL币数量(字符串)
    public int personCurrencyId;//明星代币Id
    public long personCurrencyNum;//明星代币数量
    public String personCurrencyNumStr;//明星代币数量(字符串)
}
