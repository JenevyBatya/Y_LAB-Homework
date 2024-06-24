import org.example.command.BaseCommandAbs;
import org.example.command.Logout;
import org.example.command.Read;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Booking;
import org.example.model.Chamber;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadTest {
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
    private Read readCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReadWhenNotAuthorized() {
        readCommand = new Read(mockChamberManager, mockUserManager);
        ResultResponse result = readCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }

    @Test
    public void testReadWhenAuthorized() {
        realUserManager.authorizing("b","b");
        realUserManager.authorizing("a", "a");
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
        readCommand = new Read(realChamberManager,realUserManager);
        ResultResponse result = readCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.TEXT);
        assertThat(result.getData()).isEqualTo(waitingAnswer);
    }
}
