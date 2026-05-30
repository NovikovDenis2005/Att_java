var API = '/tour-planner/api';

var tours = [];
var selectedTourId = null;

document.addEventListener('DOMContentLoaded', function () {
    loadTours();
});

async function request(url, method, body) {
    var options = {};
    options.headers = { 'Content-Type': 'application/json' };
    if (method) {
        options.method = method;
    }
    if (body) {
        options.body = body;
    }

    var resp = await fetch(url, options);
    if (!resp.ok) {
        var err = await resp.json();
        throw new Error(err.error || ('HTTP ' + resp.status));
    }
    return await resp.json();
}

async function loadTours() {
    var tbody = document.getElementById('toursTableBody');
    tbody.innerHTML = '<tr><td colspan="6" class="empty-message">Загрузка...</td></tr>';

    try {
        tours = await request(API + '/tours/');
        renderTours(tours);
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-message error">Ошибка загрузки: ' + e.message + '</td></tr>';
        alert('Не удалось загрузить туры');
    }
}

function renderTours(list) {
    var tbody = document.getElementById('toursTableBody');

    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-message">Туров пока нет. Создайте первый!</td></tr>';
        return;
    }

    var html = '';
    for (var i = 0; i < list.length; i++) {
        var tour = list[i];
        var stopsCount = tour.stops ? tour.stops.length : 0;
        html += '<tr>' +
            '<td>' + tour.id + '</td>' +
            '<td><strong>' + tour.code + '</strong></td>' +
            '<td>' + tour.title + '</td>' +
            '<td>' + stopsCount + '</td>' +
            '<td>' + formatDate(tour.createdAt) + '</td>' +
            '<td><div class="station-actions">' +
            '<button class="btn btn-sm btn-secondary" onclick="viewTour(' + tour.id + ')">Просмотр</button>' +
            '<button class="btn btn-sm btn-secondary" onclick="editTour(' + tour.id + ')">Изменить</button>' +
            '<button class="btn btn-sm btn-danger" onclick="deleteTour(' + tour.id + ')">Удалить</button>' +
            '</div></td>' +
            '</tr>';
    }
    tbody.innerHTML = html;
}

async function viewTour(id) {
    selectedTourId = id;

    try {
        var tour = await request(API + '/tours/' + id);

        document.getElementById('tourInfo').innerHTML =
            'Тур <strong>' + tour.code + '</strong> — ' + tour.title;

        var stopsList = document.getElementById('stopsList');

        if (!tour.stops || tour.stops.length === 0) {
            stopsList.innerHTML = '<div class="empty-message">Точек нет. Добавьте первую!</div>';
        } else {
            var html = '';
            for (var i = 0; i < tour.stops.length; i++) {
                var stop = tour.stops[i];
                var cls = stop.type.toLowerCase();
                var times = '';
                if (stop.arrivalTime) {
                    times += '<span>Прибытие: ' + stop.arrivalTime + '</span>';
                }
                if (stop.departureTime) {
                    times += '<span>Убытие: ' + stop.departureTime + '</span>';
                }

                html += '<div class="station-card ' + cls + '">' +
                    '<div class="station-info">' +
                    '<h4>' + (i + 1) + '. ' + stop.place + '</h4>' +
                    '<div class="station-meta">' +
                    '<span class="station-type-badge badge-' + cls + '">' + typeLabel(stop.type) + '</span>' +
                    times +
                    '</div></div>' +
                    '<div class="station-actions">' +
                    '<button class="btn btn-sm btn-secondary" onclick="editStop(' + stop.id + ')">Изменить</button>' +
                    '<button class="btn btn-sm btn-danger" onclick="deleteStop(' + stop.id + ')">Удалить</button>' +
                    '</div></div>';
            }
            stopsList.innerHTML = html;
        }

        var details = document.getElementById('tourDetails');
        details.style.display = 'block';
        details.scrollIntoView();

    } catch (e) {
        alert('Не удалось загрузить тур');
    }
}

function editTour(id) {
    var tour = null;
    for (var i = 0; i < tours.length; i++) {
        if (tours[i].id === id) {
            tour = tours[i];
            break;
        }
    }
    if (!tour) {
        return;
    }

    document.getElementById('tourModalTitle').textContent = 'Изменить тур';
    document.getElementById('tourId').value = tour.id;
    document.getElementById('tourCode').value = tour.code;
    document.getElementById('tourTitle').value = tour.title;

    document.getElementById('tourModal').classList.add('active');
}

async function deleteTour(id) {
    if (!confirm('Удалить тур? Все его точки тоже будут удалены.')) {
        return;
    }
    try {
        await request(API + '/tours/' + id, 'DELETE');
        alert('Тур удалён');

        if (selectedTourId === id) {
            document.getElementById('tourDetails').style.display = 'none';
            selectedTourId = null;
        }
        loadTours();
    } catch (e) {
        alert('Не удалось удалить тур');
    }
}

async function editStop(id) {
    try {
        var stop = await request(API + '/stops/' + id);

        document.getElementById('stopModalTitle').textContent = 'Изменить точку';
        document.getElementById('stopId').value = stop.id;
        document.getElementById('currentTourId').value = stop.tourId;
        document.getElementById('stopPlace').value = stop.place;
        document.getElementById('stopType').value = stop.type.toLowerCase();
        document.getElementById('arrivalTime').value = stop.arrivalTime || '';
        document.getElementById('departureTime').value = stop.departureTime || '';
        document.getElementById('stopPosition').value = stop.position;

        document.getElementById('stopModal').classList.add('active');
    } catch (e) {
        alert('Не удалось загрузить точку');
    }
}

async function deleteStop(id) {
    if (!confirm('Удалить эту точку?')) {
        return;
    }
    try {
        await request(API + '/stops/' + id, 'DELETE');
        alert('Точка удалена');

        if (selectedTourId) {
            viewTour(selectedTourId);
        }
        loadTours();
    } catch (e) {
        alert('Не удалось удалить точку');
    }
}

function openTourModal() {
    document.getElementById('tourModalTitle').textContent = 'Новый тур';
    document.getElementById('tourForm').reset();
    document.getElementById('tourId').value = '';
    document.getElementById('tourModal').classList.add('active');
}

function closeTourModal() {
    document.getElementById('tourModal').classList.remove('active');
}

function openStopModal() {
    if (!selectedTourId) {
        alert('Сначала выберите тур');
        return;
    }
    document.getElementById('stopModalTitle').textContent = 'Новая точка';
    document.getElementById('stopForm').reset();
    document.getElementById('stopId').value = '';
    document.getElementById('currentTourId').value = selectedTourId;
    document.getElementById('stopModal').classList.add('active');
}

function closeStopModal() {
    document.getElementById('stopModal').classList.remove('active');
}

async function saveTour(event) {
    event.preventDefault();

    var id = document.getElementById('tourId').value;
    var data = {
        code: document.getElementById('tourCode').value.trim(),
        title: document.getElementById('tourTitle').value.trim()
    };
    if (id) {
        data.id = parseInt(id);
    }

    try {
        var method = id ? 'PUT' : 'POST';
        var url = id ? (API + '/tours/' + id) : (API + '/tours/');

        await request(url, method, JSON.stringify(data));

        alert(id ? 'Тур обновлён' : 'Тур создан');
        closeTourModal();
        loadTours();
    } catch (e) {
        alert('Не удалось сохранить тур: ' + e.message);
    }
}

async function saveStop(event) {
    event.preventDefault();

    var id = document.getElementById('stopId').value;
    var data = {
        tourId: parseInt(document.getElementById('currentTourId').value),
        place: document.getElementById('stopPlace').value.trim(),
        type: document.getElementById('stopType').value.toUpperCase(),
        arrivalTime: document.getElementById('arrivalTime').value || null,
        departureTime: document.getElementById('departureTime').value || null,
        position: parseInt(document.getElementById('stopPosition').value)
    };
    if (id) {
        data.id = parseInt(id);
    }

    try {
        var method = id ? 'PUT' : 'POST';
        var url = id ? (API + '/stops/' + id) : (API + '/stops/');

        await request(url, method, JSON.stringify(data));

        alert(id ? 'Точка обновлена' : 'Точка добавлена');
        closeStopModal();

        if (selectedTourId) {
            viewTour(selectedTourId);
        }
        loadTours();
    } catch (e) {
        alert('Не удалось сохранить точку: ' + e.message);
    }
}

function formatDate(value) {
    if (!value) {
        return '-';
    }
    var d = new Date(value);
    return d.toLocaleString('ru-RU');
}

function typeLabel(type) {
    var t = type.toUpperCase();
    if (t === 'START') {
        return 'Старт';
    } else if (t === 'WAYPOINT') {
        return 'Промежуточная';
    } else if (t === 'FINISH') {
        return 'Финиш';
    }
    return type;
}
