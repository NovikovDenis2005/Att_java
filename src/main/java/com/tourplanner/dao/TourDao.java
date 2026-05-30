package com.tourplanner.dao;

import com.tourplanner.model.Stop;
import com.tourplanner.model.Tour;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDao {

    public List<Tour> findAll() throws SQLException {
        List<Tour> result = new ArrayList<>();
        String sql = "SELECT id, code, title, created_at, updated_at FROM tours";

        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(readTour(rs));
            }
        }
        return result;
    }

    public Tour findById(Long id) throws SQLException {
        String sql = "SELECT id, code, title, created_at, updated_at FROM tours WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tour tour = readTour(rs);
                    tour.setStops(readStops(conn, id));
                    return tour;
                }
            }
        }
        return null;
    }

    public Tour save(Tour tour) throws SQLException {
        if (tour.getId() == null) {
            return insert(tour);
        }
        return update(tour);
    }

    private Tour insert(Tour tour) throws SQLException {
        String sql = "INSERT INTO tours (code, title) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tour.getCode());
            ps.setString(2, tour.getTitle());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                tour.setId(keys.getLong(1));
            }
        }
        return tour;
    }

    private Tour update(Tour tour) throws SQLException {
        String sql = "UPDATE tours SET code = ?, title = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tour.getCode());
            ps.setString(2, tour.getTitle());
            ps.setLong(3, tour.getId());
            ps.executeUpdate();
        }
        return tour;
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM tours WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Tour readTour(ResultSet rs) throws SQLException {
        Tour tour = new Tour();
        tour.setId(rs.getLong("id"));
        tour.setCode(rs.getString("code"));
        tour.setTitle(rs.getString("title"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            tour.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            tour.setUpdatedAt(updated.toLocalDateTime());
        }
        return tour;
    }

    private List<Stop> readStops(Connection conn, Long tourId) throws SQLException {
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT id, tour_id, place, stop_type, arrival_time, departure_time, position, created_at " +
                "FROM tour_stops WHERE tour_id = ? ORDER BY position";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stops.add(readStop(rs));
                }
            }
        }
        return stops;
    }

    private Stop readStop(ResultSet rs) throws SQLException {
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
