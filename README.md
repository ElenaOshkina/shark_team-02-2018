# shark_team-02-2018 
[![Build Status](https://travis-ci.org/3kybika/shark_team-02-2018.svg?branch=master)](https://travis-ci.org/3kybika/shark_team-02-2018)

URL: http://tp-sharkteam-backend.herokuapp.com/

## Описание
Backend сервер для семестрового проекта по курсу Java (второй семестр) Технопарка.
 
## Команда
* Колотовкин Максим
* Минченко Илья
* Алёхин Влад

## API
| Действие | Тип запроса, URL | Тело запроса | Тело ответа |
| --- | --- | --- | --- |
| Регистрация | POST, /api/users/signup | {"login", "email", "password"} | {"id","login", "email"} |
| Авторизация | POST, /api/users/signin | {"login", "password"} | {"id","login", "email"} |
| Выход | POST, /api/users/exit |  | {"msg":"User is successfully log out!"} |
| Запросить данные текущего пользователя | GET, /api/users/ | | {"id","login", "email"} | |
