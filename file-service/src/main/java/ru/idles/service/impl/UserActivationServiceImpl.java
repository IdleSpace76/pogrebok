package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idles.dao.BotUserRepository;
import ru.idles.service.UserActivationService;
import ru.idles.utils.CryptoTool;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final BotUserRepository botUserRepository;
    private final CryptoTool cryptoTool;

    @Transactional
    @Override
    public boolean activateUser(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        if (userId == null) {
            return false;
        }

        return botUserRepository.findById(userId)
                .map(user -> {
                    if (Boolean.TRUE.equals(user.getIsActive())) {
                        return true;
                    }
                    user.setIsActive(true);
                    botUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
