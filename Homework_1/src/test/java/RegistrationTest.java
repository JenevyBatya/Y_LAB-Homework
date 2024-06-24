import org.example.command.Authorization;
import org.example.command.BaseCommandAbs;
import org.example.command.Registration;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Chamber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTest {
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
    private Registration registrationCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegWhenNotAuthorizedAndAlreadyExist() {

        String input = "m m\na\n89999999999\na";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        registrationCommand = new Registration(mockChamberManager, realUserManager);
        ResultResponse result = registrationCommand.action();

        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.ALREADY_REGISTRATED);
    }
    @Test
    public void testRegWhenNotAuthorizedAndNew() {

        String input = "m m\nc\n89999999999\nc";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        registrationCommand = new Registration(mockChamberManager, realUserManager);
        ResultResponse result = registrationCommand.action();

        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_AUTH);
    }

    @Test
    public void testRegWhenAuthorized() {
        realUserManager.authorizing("b","b");
        registrationCommand = new Registration(mockChamberManager, realUserManager);
        ResultResponse result = registrationCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.ALREADY_AUTHORIZED);
    }
}
