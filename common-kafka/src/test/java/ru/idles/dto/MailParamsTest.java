package ru.idles.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author a.zharov
 */
class MailParamsJsonTest {

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void serializeAndDeserialize() throws Exception {
        MailParams src = new MailParams("user@example.com", "abc123");
        String json = om.writeValueAsString(src);
        MailParams back = om.readValue(json, MailParams.class);

        Assertions.assertEquals(src.getMailTo(), back.getMailTo());
        Assertions.assertEquals(src.getId(), back.getId());
    }
}
