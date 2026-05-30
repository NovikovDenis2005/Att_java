package com.tourplanner.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tourplanner.dao.StopDao;
import com.tourplanner.dao.TourDao;
import com.tourplanner.model.Tour;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TourServlet extends HttpServlet {

    private TourDao tourDao;
    private StopDao stopDao;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        tourDao = new TourDao();
        stopDao = new StopDao();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        try {
            if (path == null || path.equals("/")) {
                List<Tour> tours = tourDao.findAll();
                mapper.writeValue(out, tours);
            } else {
                String[] parts = path.split("/");
                if (parts.length > 1) {
                    Long id = Long.parseLong(parts[1]);
                    Tour tour = tourDao.findById(id);
                    if (tour == null) {
                        throw new RuntimeException("Tour not found");
                    }
                    mapper.writeValue(out, tour);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(out, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        PrintWriter out = resp.getWriter();

        try {
            Tour tour = mapper.readValue(reader, Tour.class);
            Tour saved = tourDao.save(tour);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(out, saved);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BufferedReader reader = req.getReader();
        PrintWriter out = resp.getWriter();

        try {
            String[] parts = path.split("/");
            Long id = Long.parseLong(parts[1]);

            Tour tour = mapper.readValue(reader, Tour.class);
            tour.setId(id);
            Tour updated = tourDao.save(tour);
            mapper.writeValue(out, updated);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of("error", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        try {
            if (path != null && !path.equals("/")) {
                String[] parts = path.split("/");
                Long id = Long.parseLong(parts[1]);

                stopDao.deleteByTourId(id);
                boolean deleted = tourDao.delete(id);

                if (deleted) {
                    mapper.writeValue(out, Map.of("message", "Tour deleted successfully"));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    mapper.writeValue(out, Map.of("error", "Tour not found"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(out, Map.of("error", e.getMessage()));
        }
    }
}
