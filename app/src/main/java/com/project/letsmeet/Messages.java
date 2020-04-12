package com.project.letsmeet;

import java.util.Date;

public class Messages {
    private String message_text;
    private long timeStamp;
    private String message_user;

    public Messages(String message_text, String message_user){
        this.message_text=message_text;
        this.message_user=message_user;

        timeStamp=new Date().getTime();
    }
    public Messages(){

    }

    public String getMessage_text(){
        return message_text;
    }

    public void setMessage_text(String message_text){
        this.message_text=message_text;
    }
    public String getMessage_user(){
        return message_user;
    }
    public void setMessage_user(){
        this.message_user=message_user;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public void setmessageTime(long timeStamp){
        this.timeStamp=timeStamp;
    }
}