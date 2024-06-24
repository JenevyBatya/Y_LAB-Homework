import org.example.command.BaseCommandAbs;
import org.example.command.Read;
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

@DisplayName("Тест для класса получения информации о резервациях в выбранной аудитории")
public class ReadTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;

    private final UserManager realUserManager = new UserManager();
    private final ChamberManager realChamberManager = new ChamberManager();


    @InjectMocks
    private Read readCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Проверка поведения команды, если пользователь не авторизован")
    @Test
    public void testReadWhenNotAuthorized() {
        readCommand = new Read(mockChamberManager, mockUserManager);
        ResultResponse result = readCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }
    @DisplayName("Проверка поведения команды при существующей брони")
    @Test
    public void testReadWhenAuthorized() {
        realUserManager.authorizing("b", "b");
        realChamberManager.registerChambers();
        User user = realUserManager.getUser();
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = date.plusDays(2);
        int chamberNum = 1;
        realChamberManager.getChamberList().get(chamberNum).add(new Booking(user, date, endDate, chamberNum));
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isFalse();

        String waitingAnswer = "Аудитория 1: " + date + " - " + endDate;
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        BaseCommandAbs.setSc(new Scanner(System.in));
        readCommand = new Read(realChamberManager, realUserManager);
        ResultResponse result = readCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.TEXT);
        assertThat(result.getData()).isEqualTo(waitingAnswer);
    }
}
