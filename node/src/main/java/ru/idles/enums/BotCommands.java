package ru.idles.enums;

import lombok.RequiredArgsConstructor;

/**
 * Команды бота
 *
 * @author a.zharov
 */
@RequiredArgsConstructor
public enum BotCommands {
    HELP("/help", "Список команд"),
    REGISTRATION("/registration", "Регистрация пользователя"),
    CANCEL("/cancel", "Отмена выполнения текущей команды"),
    START("/start", "Начало работы");

    private final String cmd;
    private final String info;

    public boolean isEqual(String cmd) {
        return this.cmd.equalsIgnoreCase(cmd);
    }

    public static BotCommands findByCmd(String cmd) {
        for (BotCommands command : BotCommands.values()) {
            if (command.isEqual(cmd)) {
                return command;
            }
        }
        return null;
    }

    public static String commandHelpNote() {
        StringBuilder sb = new StringBuilder("Доступные команды:\n\n");
        for (BotCommands command : BotCommands.values()) {
            sb.append(command.cmd)
                    .append(" - ")
                    .append(command.info)
                    .append("\n");
        }
        return sb.toString();
    }

}
