package com.tourplanner;

import com.tourplanner.dao.StopDao;
import com.tourplanner.dao.TourDao;
import com.tourplanner.model.Stop;
import com.tourplanner.model.Tour;

import java.time.LocalTime;

public class TourPlannerApp {

    public static void main(String[] args) {
        System.out.println("=== Tour Planner ===");
        System.out.println();

        try {
            TourDao tourDao = new TourDao();
            StopDao stopDao = new StopDao();

            System.out.println("Создаём туры...");
            Tour t1 = tourDao.save(new Tour("EXC-1", "Золотое кольцо"));
            Tour t2 = tourDao.save(new Tour("EXC-2", "Вечерний Петербург"));
            Tour t3 = tourDao.save(new Tour("EXC-3", "Горный поход выходного дня"));

            System.out.println("Туры созданы:");
            System.out.println("  " + t1.getCode() + ": " + t1.getTitle());
            System.out.println("  " + t2.getCode() + ": " + t2.getTitle());
            System.out.println("  " + t3.getCode() + ": " + t3.getTitle());
            System.out.println();

            System.out.println("Добавляем точки в тур " + t1.getCode() + "...");
            addStop(stopDao, t1.getId(), "Москва, сбор группы", Stop.StopType.START, null, "07:00", 1);
            addStop(stopDao, t1.getId(), "Сергиев Посад", Stop.StopType.WAYPOINT, "09:00", "11:30", 2);
            addStop(stopDao, t1.getId(), "Переславль-Залесский", Stop.StopType.WAYPOINT, "13:00", "15:00", 3);
            addStop(stopDao, t1.getId(), "Ростов Великий", Stop.StopType.FINISH, "16:30", null, 4);
            System.out.println("Точки добавлены.");
            System.out.println();

            System.out.println("Все туры:");
            for (Tour t : tourDao.findAll()) {
                System.out.println("  [" + t.getId() + "] " + t.getCode() + " - " + t.getTitle());
            }
            System.out.println();

            System.out.println("Маршрут тура " + t1.getCode() + ":");
            for (Stop s : stopDao.findByTourId(t1.getId())) {
                System.out.println("  " + s.getPosition() + ". " + s.getPlace() + " (" + s.getType() + ")");
                if (s.getArrivalTime() != null) {
                    System.out.println("     прибытие: " + s.getArrivalTime());
                }
                if (s.getDepartureTime() != null) {
                    System.out.println("     убытие: " + s.getDepartureTime());
                }
            }

            System.out.println();
            System.out.println("Готово.");

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addStop(StopDao stopDao, Long tourId, String place, Stop.StopType type,
                                String arrival, String departure, int position) throws Exception {
        Stop stop = new Stop();
        stop.setTourId(tourId);
        stop.setPlace(place);
        stop.setType(type);
        stop.setPosition(position);
        if (arrival != null) {
            stop.setArrivalTime(LocalTime.parse(arrival));
        }
        if (departure != null) {
            stop.setDepartureTime(LocalTime.parse(departure));
        }
        stopDao.save(stop);
    }
}
