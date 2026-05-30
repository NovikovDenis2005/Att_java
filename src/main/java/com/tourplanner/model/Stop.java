package com.tourplanner.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Stop {

    private Long id;
    private Long tourId;
    private String place;
    private StopType type;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int position;
    private LocalDateTime createdAt;

    public enum StopType {
        START("start"),
        WAYPOINT("waypoint"),
        FINISH("finish");

        private final String value;

        StopType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static StopType fromString(String value) {
            for (StopType t : values()) {
                if (t.value.equalsIgnoreCase(value)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Неизвестный тип точки: " + value);
        }
    }

    public Stop() {
    }

    public Stop(String place, StopType type, LocalTime arrivalTime, LocalTime departureTime, int position) {
        this.place = place;
        this.type = type;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public StopType getType() {
        return type;
    }

    public void setType(StopType type) {
        this.type = type;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Stop{id=" + id + ", place='" + place + "', type=" + type + ", position=" + position + "}";
    }
}
