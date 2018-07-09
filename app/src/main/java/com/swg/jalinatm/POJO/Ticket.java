package com.swg.jalinatm.POJO;

import android.os.Parcelable;

import org.parceler.Parcel;

/**
 * Created by Andrew Widjaja on 5/25/2018.
 */

@Parcel
public class Ticket{

    private String ticketNumber;
    private String incidentName;
    private String description;
    private Long errorCode;
    private String atm_id;
    private Long status;
    private Long reportedTime;

    public Ticket() {
    }

    public Ticket(String ticketNumber, Long errorCode, String incidentName, String description, String atm_id, Long status, Long reportedTime) {
        this.ticketNumber = ticketNumber;
        this.errorCode = errorCode;
        this.incidentName = incidentName;
        this.description = description;
        this.atm_id = atm_id;
        this.status = status;
        this.reportedTime = reportedTime;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public Long getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(Long reportedTime) {
        this.reportedTime = reportedTime;
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

    public String getIncidentName() {
        return incidentName;
    }

    public void setIncidentName(String incidentName) {
        this.incidentName = incidentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAtm_id() {
        return atm_id;
    }

    public void setAtm_id(String atm_id) {
        this.atm_id = atm_id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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
