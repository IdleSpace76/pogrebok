package ru.idles.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import ru.idles.dao.BotUserRepository;
import ru.idles.entity.BotUser;
import ru.idles.enums.UserState;

import java.util.Optional;

/**
 * @author a.zharov
 */
@DataJpaTest
@ContextConfiguration(classes = BotUserRepositoryTest.TestConfig.class)
class BotUserRepositoryTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackages = "ru.idles.dao")
    @EntityScan(basePackages = "ru.idles.entity")
    static class TestConfig { }

    @Autowired
    BotUserRepository repo;

    @Test
    void saveAndFindByIdAndEmailAndTelegramId() {
        BotUser u = new BotUser();
        u.setTelegramUserId(123456L);
        u.setEmail("test@example.com");
        u.setUserName("tester");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setState(UserState.WAIT_FOR_EMAIL_STATE);

        BotUser saved = repo.save(u);

        Optional<BotUser> foundById = repo.findById(saved.getId());
        Assertions.assertTrue(foundById.isPresent());
        Assertions.assertEquals("tester", foundById.get().getUserName());

        Assertions.assertTrue(repo.findBotUserByTelegramUserId(123456L).isPresent());
        Assertions.assertTrue(repo.findByEmail("test@example.com").isPresent());
    }
}
