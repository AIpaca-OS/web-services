package com.aipaca.rumbo.routes.domain.model;
import jakarta.persistence.*;
import java.util.UUID;
@Embeddable
public class TripPassenger {
    @Column(name="student_id",nullable=false,length=100) private String studentId;
    @Column(name="stop_id",nullable=false) private UUID stopId;
    @Column(name="stop_position",nullable=false) private int stopPosition;
    @Column(name="stop_address",nullable=false,length=350) private String stopAddress;
    @Column(name="absent",nullable=false) private boolean absent;
    @Column(name="stop_served",nullable=false) private boolean stopServed;
    protected TripPassenger(){}
    public TripPassenger(String studentId,UUID stopId,int stopPosition,String stopAddress){
        this.studentId=studentId;this.stopId=stopId;this.stopPosition=stopPosition;this.stopAddress=stopAddress;
    }
    public String getStudentId(){return studentId;}public UUID getStopId(){return stopId;}
    public int getStopPosition(){return stopPosition;} public String getStopAddress(){return stopAddress;}
    public boolean isAbsent(){return absent;}public boolean isStopServed(){return stopServed;}
    public void markAbsent(){if(stopServed)throw new IllegalArgumentException("Stop has already been served"); absent=true;}
    public void markStopServed(){stopServed=true;}
}
