# Pogrebok <br/>

Telegram бот на Spring Boot и микросервисной архитектуре.
_________________

## Создано с помощью:
- Java 21
- Spring Boot 3.5.5
- Telegram Bots Long Polling и Telegram Bots Client v. 9.0.0
- Apache Kafka
- Docker
- Postgres
- 

## План:
- [x] Создать dispatcher
- [x] Подключить брокер сообщений
- [x] Создать Node-микросервис
- [x] Создать REST API микросервис
- [ ] Создать mail-микросервис
- [ ] Нагрузочное тестирование
- [ ] Развернуть на сервере
- [ ] Оформить README (описание + схема)

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

Что можно сделать лучше:
1) при загрузке файла из ТГ не забивать им память, а стримить напрямую в бд
2) UUID для сущностей, чтобы готовые адреса точно были разными
3) ликвибейс для миграции
4) контейнеризация