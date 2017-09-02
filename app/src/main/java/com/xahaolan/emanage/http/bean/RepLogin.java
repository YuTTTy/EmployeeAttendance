package com.xahaolan.emanage.http.bean;

import java.io.Serializable;

/**
 * Created by aiodiy on 2016/12/15.     登陆
 */
public class RepLogin implements Serializable {
    private String account; //账号
    private String username;//用户名
    private String zhifubao;//支付宝账号
    private String weizhifu;//微支付账号
    private int coin;  //游戏币
    private float balance; //账户余额
    private float freeze; //冻结额度

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getZhifubao() {
        return zhifubao;
    }

    public void setZhifubao(String zhifubao) {
        this.zhifubao = zhifubao;
    }

    public String getWeizhifu() {
        return weizhifu;
    }

    public void setWeizhifu(String weizhifu) {
        this.weizhifu = weizhifu;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public float getFreeze() {
        return freeze;
    }

    public void setFreeze(float freeze) {
        this.freeze = freeze;
    }
}