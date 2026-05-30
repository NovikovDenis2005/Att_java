package com.tourplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tour {

    private Long id;
    private String code;
    private String title;
    private List<Stop> stops;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tour() {
        stops = new ArrayList<>();
    }

    public Tour(String code, String title) {
        this();
        this.code = code;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        stops.add(stop);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Tour{id=" + id + ", code='" + code + "', title='" + title
                + "', stops=" + stops.size() + "}";
    }
}
