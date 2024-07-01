import org.example.command.BaseCommandAbs;
import org.example.command.Delete;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для класса удаления брони")
public class DeleteTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;

    private final UserManager realUserManager = new UserManager();
    private final ChamberManager realChamberManager = new ChamberManager();


    @InjectMocks
    private Delete deleteCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Проверка поведения команды, если пользователь не авторизован")
    @Test
    public void testDeleteWhenNotAuthorized() {
        deleteCommand = new Delete(mockChamberManager, mockUserManager);
        ResultResponse result = deleteCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }
    @DisplayName("Проверка поведения команды при отсутсвии брони")
    @Test
    public void testDeleteWhenAuthorizedAndEmpty() {
        realUserManager.authorizing("b", "b");
        realUserManager.authorizing("a", "a");
        deleteCommand = new Delete(mockChamberManager, realUserManager);
        ResultResponse result = deleteCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_BOOKED_ROOMS);
    }
    @DisplayName("Проверка поведения команды при успешном удалении брони")
    @Test
    public void testDeleteWhenAuthorizedAndBookingsExist() {

        realUserManager.authorizing("a", "a");
//        realChamberManager.registerChambers();
        User user = realUserManager.getUser();
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = date.plusDays(2);
        int chamberNum = 1;
//        realChamberManager.getChamberList().get(chamberNum).add(new Booking(user, date, endDate, chamberNum));
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isFalse();

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        deleteCommand = new Delete(realChamberManager, realUserManager);
        ResultResponse result = deleteCommand.action();


        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_DELETE);
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isTrue();
    }
}
