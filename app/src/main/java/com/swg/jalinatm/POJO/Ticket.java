package com.swg.jalinatm.POJO;

import android.os.Parcelable;

import org.parceler.Parcel;

/**
 * Created by Andrew Widjaja on 5/25/2018.
 */

@Parcel
public class Ticket{

    private String ticketNumber;
    private String summary;
    private String description;
    private String machineNumber;
    private String ticketState;

    public Ticket() {
    }

    public Ticket(String ticketNumber, String summary, String description, String machineNumber, String ticketState) {
        this.ticketNumber = ticketNumber;
        this.summary = summary;
        this.description = description;
        this.machineNumber = machineNumber;
        this.ticketState = ticketState;
    }

//    protected Ticket(android.os.Parcel in) {
//        ticketNumber = in.readString();
//        summary = in.readString();
//        description = in.readString();
//        machineNumber = in.readString();
//        ticketState = in.readString();
//    }

//    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
//        @Override
//        public Ticket createFromParcel(android.os.Parcel in) {
//            return new Ticket(in);
//        }
//
//        @Override
//        public Ticket[] newArray(int size) {
//            return new Ticket[size];
//        }
//    };

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public String getTicketState() {
        return ticketState;
    }

    public void setTicketState(String ticketState) {
        this.ticketState = ticketState;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(android.os.Parcel dest, int flags) {
//        dest.writeString(ticketNumber);
//        dest.writeString(summary);
//        dest.writeString(description);
//        dest.writeString(machineNumber);
//        dest.writeString(ticketState);
//    }
}
