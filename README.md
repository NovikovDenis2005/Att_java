# Tour Planner

Веб-приложение для планирования экскурсионных туров и их маршрутов (точек остановки).

## Что нужно для запуска

- Java 21
- Maven

## Стек

- Java 21, сервлеты (Jakarta Servlet API), JDBC
- Встроенная база H2 (в памяти)
- Встроенный сервер Jetty
- Фронтенд: HTML, CSS, JavaScript

## Запуск

Демо в консоли (создаёт туры и выводит маршрут):

```
mvn compile exec:java@run-app
```

Веб-интерфейс:

```
mvn compile exec:java
```

После запуска открыть в браузере http://localhost:8080/tour-planner

## API

Туры:
- `GET /api/tours/` — список туров
- `GET /api/tours/{id}` — тур по id
- `POST /api/tours/` — создать тур
- `PUT /api/tours/{id}` — изменить тур
- `DELETE /api/tours/{id}` — удалить тур

Точки маршрута:
- `GET /api/stops/?tourId={id}` — точки тура
- `POST /api/stops/` — добавить точку
- `PUT /api/stops/{id}` — изменить точку
- `DELETE /api/stops/{id}` — удалить точку

## Структура

```
src/main/
├── java/com/tourplanner/
│   ├── model/      Tour, Stop
│   ├── dao/        DatabaseManager, TourDao, StopDao
│   ├── servlet/    TourServlet, StopServlet
│   ├── server/     JettyLauncher
│   └── TourPlannerApp.java   демо в консоли
└── webapp/         index.html, css, js, web.xml
```

База H2 работает в памяти, таблицы создаются автоматически при первом обращении.
