import org.example.command.Authorization;
import org.example.command.BaseCommandAbs;
import org.example.command.Create;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Chamber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;
    @Mock
    private Chamber mockChamber;

    private UserManager realUserManager = new UserManager();
    private ChamberManager realChamberManager = new ChamberManager();
    private Chamber chamber = new Chamber();

    @InjectMocks
    private Authorization authorizationCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthWhenNotAuthorized() {

        String input = "a\na\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        authorizationCommand = new Authorization(mockChamberManager, realUserManager);
        ResultResponse result = authorizationCommand.action();

        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_AUTH);
    }
    @DisplayName("")
    @Test
    public void testAuthWhenAuthorized() {
        realUserManager.authorizing("b","b");
        authorizationCommand = new Authorization(mockChamberManager, realUserManager);
        ResultResponse result = authorizationCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.ALREADY_AUTHORIZED);
    }
    @DisplayName("Проверка команды для отмены")
    @Test
    public void testAuthBack() {

        String input = "Back\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        authorizationCommand = new Authorization(mockChamberManager, realUserManager);
        ResultResponse result = authorizationCommand.action();

        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.BACK_TO_MAIN);
    }
}
