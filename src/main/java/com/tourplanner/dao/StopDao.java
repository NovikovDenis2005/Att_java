package com.tourplanner.dao;

import com.tourplanner.model.Stop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StopDao {

    public List<Stop> findByTourId(Long tourId) throws SQLException {
        List<Stop> list = new ArrayList<>();
        String sql = "SELECT id, tour_id, place, stop_type, arrival_time, departure_time, position, created_at " +
                "FROM tour_stops WHERE tour_id = ? ORDER BY position";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toStop(rs));
                }
            }
        }
        return list;
    }

    public Stop findById(Long id) throws SQLException {
        String sql = "SELECT id, tour_id, place, stop_type, arrival_time, departure_time, position, created_at " +
                "FROM tour_stops WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toStop(rs);
                }
            }
        }
        return null;
    }

    public Stop save(Stop stop) throws SQLException {
        if (stop.getId() == null) {
            return insert(stop);
        }
        return update(stop);
    }

    private Stop insert(Stop stop) throws SQLException {
        String sql = "INSERT INTO tour_stops (tour_id, place, stop_type, arrival_time, departure_time, position) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, stop.getTourId());
            ps.setString(2, stop.getPlace());
            ps.setString(3, stop.getType().getValue());
            if (stop.getArrivalTime() != null) {
                ps.setTime(4, Time.valueOf(stop.getArrivalTime()));
            } else {
                ps.setTime(4, null);
            }
            if (stop.getDepartureTime() != null) {
                ps.setTime(5, Time.valueOf(stop.getDepartureTime()));
            } else {
                ps.setTime(5, null);
            }
            ps.setInt(6, stop.getPosition());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                stop.setId(keys.getLong(1));
            }
        }
        return stop;
    }

    private Stop update(Stop stop) throws SQLException {
        String sql = "UPDATE tour_stops SET place = ?, stop_type = ?, arrival_time = ?, " +
                "departure_time = ?, position = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stop.getPlace());
            ps.setString(2, stop.getType().getValue());
            if (stop.getArrivalTime() != null) {
                ps.setTime(3, Time.valueOf(stop.getArrivalTime()));
            } else {
                ps.setTime(3, null);
            }
            if (stop.getDepartureTime() != null) {
                ps.setTime(4, Time.valueOf(stop.getDepartureTime()));
            } else {
                ps.setTime(4, null);
            }
            ps.setInt(5, stop.getPosition());
            ps.setLong(6, stop.getId());
            ps.executeUpdate();
        }
        return stop;
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM tour_stops WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteByTourId(Long tourId) throws SQLException {
        String sql = "DELETE FROM tour_stops WHERE tour_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, tourId);
            return ps.executeUpdate() > 0;
        }
    }

    private Stop toStop(ResultSet rs) throws SQLException {
        Stop stop = new Stop();
        stop.setId(rs.getLong("id"));
        stop.setTourId(rs.getLong("tour_id"));
        stop.setPlace(rs.getString("place"));
        stop.setType(Stop.StopType.fromString(rs.getString("stop_type")));

        Time arr = rs.getTime("arrival_time");
        if (arr != null) {
            stop.setArrivalTime(arr.toLocalTime());
        }
        Time dep = rs.getTime("departure_time");
        if (dep != null) {
            stop.setDepartureTime(dep.toLocalTime());
        }
        stop.setPosition(rs.getInt("position"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            stop.setCreatedAt(created.toLocalDateTime());
        }
        return stop;
    }
}
