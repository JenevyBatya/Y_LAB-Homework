import org.example.command.BaseCommandAbs;
import org.example.command.ExpertMode;
import org.example.command.Help;
import org.example.command.Logout;
import org.example.enumManagment.CommandNameEnum;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.ChamberManager;
import org.example.managment.GettingBackToMain;
import org.example.managment.ResultResponse;
import org.example.managment.UserManager;
import org.example.model.Chamber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpertModeTest {
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
    private ExpertMode expertMode;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExpertModeWhenNotAuthorized() {
        expertMode = new ExpertMode(mockChamberManager, mockUserManager);
        ResultResponse result = expertMode.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }

    @Test
    public void testExpertModeWhenAuthorizedAndNotAdmin() {
        realUserManager.authorizing("b", "b");
        expertMode = new ExpertMode(mockChamberManager, realUserManager);
        ResultResponse result = expertMode.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.ACCESS_DENIED);
    }

    @Test
    public void testExpertModeWhenAuthorizedAddChamber() throws GettingBackToMain {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        String waitingAnswer_1 = "Данная аудитория уже доступна для резервации";
        String waitingAnswer_2 = ResponseEnum.WRONG_FORMAT.toString();
        String input = "1\nks\n7\nПодвал\nПрекрасное место для детишек\nКоворкинг\n1000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        expertMode = new ExpertMode(realChamberManager, realUserManager);
        ResultResponse result = expertMode.addChamber();
        ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));
        assertThat(output.contains(waitingAnswer_1)).isTrue();
        assertThat(output.contains(waitingAnswer_2)).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_ADD);
        assertThat(realChamberManager.getChamberList().size()).isEqualTo(5);
    }

    @Test
    public void testExpertModeWhenAuthorizedDeleteChamber() throws GettingBackToMain {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        String waitingAnswer = "Данной аудитории не существует";
        String input = "8\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        expertMode = new ExpertMode(realChamberManager, realUserManager);
        ResultResponse result = expertMode.deleteChamber();

        ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));
        assertThat(output.contains(waitingAnswer)).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_DELETE_CHAMBER);
        assertThat(realChamberManager.getChamberList().size()).isEqualTo(3);
    }

    @Test
    public void testExpertModeAction() throws GettingBackToMain {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        String waitingAnswer = ResponseEnum.UNKNOWN_COMMAND.toString();
        String input = """
                sac
                Delete
                1
                """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        expertMode = new ExpertMode(realChamberManager, realUserManager);
        ResultResponse result = expertMode.action();
        ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));
        assertThat(output.contains(waitingAnswer)).isTrue();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.SUCCESS_DELETE_CHAMBER);
        assertThat(realChamberManager.getChamberList().size()).isEqualTo(3);
    }
}
