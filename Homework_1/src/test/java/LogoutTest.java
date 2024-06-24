import org.example.command.Delete;
import org.example.command.Logout;
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

import static org.assertj.core.api.Assertions.assertThat;

public class LogoutTest {
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
    private Logout logoutCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogoutWhenNotAuthorized() {
        logoutCommand = new Logout(mockChamberManager, mockUserManager);
        ResultResponse result = logoutCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }
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
