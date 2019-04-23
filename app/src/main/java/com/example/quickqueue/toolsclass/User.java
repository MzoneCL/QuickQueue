package com.example.quickqueue.toolsclass;

import com.avos.avoscloud.AVFile;

public class User {

    private String account;

    private String nickname;

    private int seq_number;

    private int waiting_time;

    private AVFile head_sculpture;

    public AVFile getHead_sculpture() {
        return head_sculpture;
    }

    public String getAccount() {
        return account;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSeq_number() {
        return seq_number;
    }

    public int getWaiting_time() {
        return waiting_time;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSeq_number(int seq_number) {
        this.seq_number = seq_number;
    }

    public void setWaiting_time(int waiting_time) {
        this.waiting_time = waiting_time;
    }

    public void setHead_sculpture(AVFile head_sculpture) {
        this.head_sculpture = head_sculpture;
    }
}
