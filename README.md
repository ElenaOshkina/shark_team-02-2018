# shark_team-02-2018 [![Build Status](https://travis-ci.org/3kybika/shark_team-02-2018.svg?branch=master)](https://travis-ci.org/3kybika/shark_team-02-2018)

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
| Регистрация | POST, /api/users/signup | {"loginField", "emailField", "passwordField"} | {"id","login", "email", "avatar"} |
| Авторизация | POST, /api/users/signin | {"loginField", "passwordField"} | {"id","login", "email", "avatar"} |
| Выход | POST, /api/users/logout |  | {"msg":"User is successfully log out!"} |
| Запросить данные текущего пользователя | GET, /api/users/me | | {"id","login", "email", "avatar"} | |
| Изменить данные текущего пользователя | POST, /api/users/me | {"loginField", "emailField", "passwordField"}| {"id","login", "email", "avatar"} | |
| Изменить аватар | POST, /api/avatars/upload | Content-Type:"multipart/form-data"; file: <file>| | {"msg":"Image saved!"}  |
| Изменить аватар | GET, /api/avatars/<UserId> | | | filename= "<filename>"; body: <file>  |
