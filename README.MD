# Pogrebok <br/>

Telegram бот на Spring Boot и микросервисной архитектуре.
_________________

## Создано с помощью:
- Java 21
- Spring Boot 3.5.5
- Slf4j
- Telegram Bots Long Polling и Telegram Bots Client v. 9.0.0
- Apache Kafka
- Docker
- Postgres
- 

## План:
- [x] Создать dispatcher
- [x] Подключить брокер сообщений
- [ ] Создать Node-микросервис
- [ ] Создать REST API микросервис
- [ ] Создать mail-микросервис
- [ ] Нагрузочное тестирование
- [ ] Развернуть на сервере
- [ ] Оформить README (описание + схема)

Переменные среды для ТГ бота<br/>
Liquibase?

> docker-compose up -d <br/>
> docker-compose down -v <br/>
> docker ps
> 
> docker exec -it postgres_db psql -U myuser -d mydatabase
> 
> kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic bot-messages <br/>
> kafka-topics.sh --bootstrap-server localhost:9092 --list
> 
UI для кафки (Kafdrop): http://localhost:9000