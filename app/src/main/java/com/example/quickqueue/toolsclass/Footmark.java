package com.example.quickqueue.toolsclass;

public class Footmark {

    public String account_user;
    public String account_merchant;
    public String date;
    public String waited_time;
    public String merchant_name;
    public String waited_number;

    public Footmark(String account_user, String account_merchant, String date, String waited_number, String waited_time, String merchant_name){
        this.account_merchant = account_merchant;
        this.account_user = account_user;
        this.date = date;
        this.waited_number = waited_number;
        this.waited_time = waited_time;
        this.merchant_name = merchant_name;
    }


    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getAccount_user() {
        return account_user;
    }

    public void setAccount_user(String account_user) {
        this.account_user = account_user;
    }

    public String getAccount_merchant() {
        return account_merchant;
    }

    public void setAccount_merchant(String account_merchant) {
        this.account_merchant = account_merchant;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWaited_time() {
        return waited_time;
    }

    public void setWaited_time(String waited_time) {
        this.waited_time = waited_time;
    }

    public String getWaited_number() {
        return waited_number;
    }

    public void setWaited_number(String waited_number) {
        this.waited_number = waited_number;
    }




}
