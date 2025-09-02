# Pogrebok <br/>

Telegram бот на Spring Boot и микросервисной архитектуре.
_________________

## Создано с помощью:
- Java 21
- Spring Boot 3.5.5
- Slf4j
- Telegram Bots Long Polling и Telegram Bots Client v. 9.0.0
- Apache Kafka

## План:
- [x] Создать dispatcher
- [x] Подключить брокер сообщений
- [ ] Создать Node-микросервис
- [ ] Создать REST API микросервис
- [ ] Создать mail-микросервис
- [ ] Нагрузочное тестирование
- [ ] Развернуть на сервере
- [ ] Оформить README (описание + схема)


> docker-compose up -d <br/>
> docker ps
> 
> kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic bot-messages <br/>
> kafka-topics.sh --bootstrap-server localhost:9092 --list
> 
UI для кафки (Kafdrop): http://localhost:9000