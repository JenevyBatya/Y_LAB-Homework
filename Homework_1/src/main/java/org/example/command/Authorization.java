package org.example.command;

import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import static org.example.managment.UserManager.*;

/**
 * Класс Authorization отвечает за выполнение команды авторизации пользователя.
 * Он наследует {@link BaseCommandAbs} и реализует интерфейс {@link BaseCommand}.
 */
public class Authorization extends BaseCommandAbs implements BaseCommand {

    /**
     * Конструктор класса Authorization.
     *
     * @param chamberManager объект {@link ChamberManager}, используемый для управления аудиториями.
     * @param userManager объект {@link UserManager}, используемый для управления пользователями.
     */
    public Authorization(ChamberManager chamberManager, UserManager userManager) {
        super(chamberManager, userManager, Authorization.class.getName());
    }


    /**
     * Выполняет авторизацию пользователя.
     *
     * @return объект {@link ResultResponse} с результатом выполнения команды.
     */
    @Override
    public ResultResponse action() {
        if (userManager.isAuthorized()) {
            return new ResultResponse(false, ResponseEnum.ALREADY_AUTHORIZED);
        }
        while (true) {
            try {
                String email = gettingEmail().getData();
                String password = gettingPassword().getData();
                ResultResponse resultResponse = userManager.authorizing(email, password);
                if (resultResponse.getResponse().equals(ResponseEnum.SUCCESS_AUTH)) {
                    return resultResponse;
                } else {
                    System.out.println(resultResponse.getResponse().toString());
                }

            } catch (GettingBackToMain e) {
                return new ResultResponse(true, ResponseEnum.BACK_TO_MAIN);
            }
        }
    }
}
