package com.fleetmanagment.model;

import java.io.Serializable;

public class WorkloadData implements Serializable {
    public String id;
    public IdName commodity;
    public String endDate;
    public String lastPositionLatitude;
    public String lastPositionLongitude;
    public String loadTime;
    public String restTime;
    public String selectedRoute;
    public StaffData staffData;
    public String startDate;
    public IdName tripType;
    public Vehicle vehicle;
    public String waitTime;
    public IdName workloadStatus;
    public Landmark destinationLandmark;
    public Landmark landmark;
    public Landmark sourceLandmark;

}
