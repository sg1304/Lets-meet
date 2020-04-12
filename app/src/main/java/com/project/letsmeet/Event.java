package com.project.letsmeet;

import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Event implements Serializable, Comparable {
    private String eventId;
    private String eventCreatorId;
    private String eventName;
    private String eventDes;
    private String startDate;
    private String endDate;
    private String latitude;
    private String logitude;
    private String location;
    private List<Attendes> attendesList;
    private String numberOfAttendes;
    private String chatId;

    public Event(){

    }

    public Event(String eventCreatorId, String eventName, String eventDes, String startDate, String endDate, String latitude, String logitude, String location, String numberOfAttendes, String chatId) {
        this.eventCreatorId = eventCreatorId;
        this.eventName = eventName;
        this.eventDes = eventDes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.logitude = logitude;
        this.location = location;
        this.numberOfAttendes = numberOfAttendes;
        this.chatId = chatId;
    }

    public Event(String eventCreatorId, String eventName, String eventDes, String startDate) {
        this.eventCreatorId = eventCreatorId;
        this.eventName = eventName;
        this.eventDes = eventDes;
        this.startDate = startDate;
    }

    public String getNumberOfAttendes() {
        return numberOfAttendes;
    }

    public void setNumberOfAttendes(String numberOfAttendes) {
        this.numberOfAttendes = numberOfAttendes;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventCreatorId() {
        return eventCreatorId;
    }

    public void setEventCreatorId(String eventCreatorId) {
        this.eventCreatorId = eventCreatorId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDes() {
        return eventDes;
    }

    public void setEventDes(String eventDes) {
        this.eventDes = eventDes;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLogitude() {
        return logitude;
    }

    public void setLogitude(String logitude) {
        this.logitude = logitude;
    }

    public List<Attendes> getAttendesList() {
        return attendesList;
    }

    public void setAttendesList(List<Attendes> attendesList) {
        this.attendesList = attendesList;
    }

    @Override
    public int compareTo(Object o) {

        Event event2 = Event.class.cast(o);

        try {
            Date date1=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(this.getStartDate());
            Date date2=new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(event2.getStartDate());
            return date2.compareTo(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
