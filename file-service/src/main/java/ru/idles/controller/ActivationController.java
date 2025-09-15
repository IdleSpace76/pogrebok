package ru.idles.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.idles.service.UserActivationService;

/**
 * @author a.zharov
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ActivationController {

    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<String> activation(@RequestParam("id") String id) {
        boolean ok = userActivationService.activateUser(id);
        if (!ok) {
            return ResponseEntity.badRequest()
                    .body("Некорректная ссылка активации.");
        }
        return ResponseEntity.ok("Активация успешно завершена");
    }
}
