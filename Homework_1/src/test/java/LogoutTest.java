import org.example.command.Logout;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для класса выхода из учетной записи")
public class LogoutTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;

    private final UserManager realUserManager = new UserManager();

    @InjectMocks
    private Logout logoutCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @DisplayName("Проверка поведения команды, если пользователь не авторизован")
    @Test
    public void testLogoutWhenNotAuthorized() {
        logoutCommand = new Logout(mockChamberManager, mockUserManager);
        ResultResponse result = logoutCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }
    @DisplayName("Проверка поведения команды при успешном выходе из учетной записи")
    @Test
    public void testLogoutWhenAuthorized() {
        realUserManager.authorizing("b","b");
        logoutCommand = new Logout(mockChamberManager, realUserManager);
        ResultResponse result = logoutCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_LOGOUT);
        assertThat(realUserManager.isAuthorized()).isFalse();
    }
}
