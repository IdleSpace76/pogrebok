package ru.idles.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.idles.dto.MailParams;
import ru.idles.service.MailService;

/**
 * @author a.zharov
 */
@RestController
@RequestMapping("/mail")
@Slf4j
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
        mailService.sendMail(mailParams);
        return ResponseEntity.ok().build();
    }
}
