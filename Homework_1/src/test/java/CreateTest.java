import org.example.command.BaseCommandAbs;
import org.example.command.Create;
import org.example.enumManagment.ResponseEnum;
import org.example.managment.*;
import org.example.model.Chamber;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;


public class CreateTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;
    @Mock
    private Chamber mockChamber;

    String formatterBookPattern = "dd.MM.yyyy HH.mm";
    String formatterPeriodPattern = "dd.MM.yyyy";
    DateTimeFormatter formatterBook = DateTimeFormatter.ofPattern(formatterBookPattern);
    DateTimeFormatter formatterPeriod = DateTimeFormatter.ofPattern(formatterPeriodPattern);
    private UserManager realUserManager = new UserManager();
    private ChamberManager realChamberManager = new ChamberManager();
    private Chamber chamber = new Chamber();
    private CommandManager commandManager = new CommandManager();

    @InjectMocks
    private Create createCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWhenNotAuthorized() {
        createCommand = new Create(mockChamberManager, mockUserManager);
        ResultResponse result = createCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_AUTHORIZATION_YET);
    }

    @Test
    public void testRoomsOptionsNoRooms() {
        ResultResponse result = createCommand.roomsOption();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.NO_ROOMS_DETECTED);

    }

    @Test
    public void testRoomsOptionsWithRooms() {
        realChamberManager.registerChambers();
        createCommand = new Create(realChamberManager, mockUserManager);
        StringBuilder answer = new StringBuilder();

        for (Map.Entry<Integer, Chamber> chamber : realChamberManager.getChamberList().entrySet()) {
            answer.append("Аудитория ").append(chamber.getKey()).append(". ").append(chamber.getValue().getName()).append(" - ").append(chamber.getValue().getDescription()).append("\n");
        }
        ResultResponse result = createCommand.roomsOption();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.TEXT);
        assertThat(result.getData()).isEqualTo(answer.toString());

    }

    @Test
    public void testBookOption() throws GettingBackToMain {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        commandManager.registerChambers(realChamberManager);
        String input = "2024-07-23  - 2024-07-23\n23.07.2024 10.00 - 23.07.2024 12.00\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));


        //Успешная бронь зала
        BaseCommandAbs.setSc(new Scanner(System.in));
        createCommand = new Create(realChamberManager, realUserManager);
        ResultResponse resultResponse = createCommand.bookOption(realChamberManager.getChamberList().get(1));
        String wrongFormat = outContent.toString().split("\n")[1];
        assertThat(wrongFormat).isEqualTo(ResponseEnum.WRONG_FORMAT + ". Пожалуйста, следуйте формату: " + formatterBookPattern + " - " + formatterBookPattern);
        //Пересечение дат брони зала


        //Успешная бронь коворкинга
    }

    @Test
    public void testCreateActionBookHall() {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        commandManager.registerChambers(realChamberManager);
        String waitingAnswer_1 = "Неверный формат номера аудитории или команды";
        String waitingAnswer_2 = "Данная аудитория отстутсвует";
        String input = "sac\n" + //"Неверный формат номера аудитории или команды"
                "9\n" + //"Данная аудитория отстутсвует"
                "1\n" +
                "Book\n" +
                "23.07.2024 10.00 - 23.07.2024 12.00\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        createCommand = new Create(realChamberManager, realUserManager);
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isTrue();
        ResultResponse resultResponse = createCommand.action();
        ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));


        assertThat(resultResponse.getResponse()).isEqualTo(ResponseEnum.SUCCESS_BOOKING);
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isFalse();
        assertThat(output.contains(waitingAnswer_1)).isTrue();
        assertThat(output.contains(waitingAnswer_2)).isTrue();
    }

    @Test
    public void testCreateActionTableFreeDay() {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        commandManager.registerChambers(realChamberManager);
        String waitingAnswer_1 = ResponseEnum.UNKNOWN_COMMAND.toString();
        String waitingAnswer_2 = "2024-07-23 Свободно весь день";
        String input = """
                1
                Table
                xv
                Period
                23.07.2024 - 23.07.2024
                """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        createCommand = new Create(realChamberManager, realUserManager);
        try {
            createCommand.action();
        } catch (NoSuchElementException e) {
            ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));
            assertThat(output.contains(waitingAnswer_1)).isTrue();
            assertThat(output.contains(waitingAnswer_2)).isTrue();
        }

    }

    @Test
    public void testCreateActionTableNotFreeDay() {
        realUserManager.authorizing("a", "a");
        realChamberManager.registerChambers();
        commandManager.registerChambers(realChamberManager);
        String waitingAnswer_1 = "Неверный формат номера аудитории или команды";
        String waitingAnswer_2 = "Данная аудитория отстутсвует";
        String waitingAnswer_3 = "Свободные слоты: 00:00 - 10:00, 12:00 - 23:59, \n";
        String input = """
                sc
                9
                1
                Book
                23.07.2024 10.00 - 23.07.2024 12.00
                Create
                1
                Table
                Period
                23.07.2024 - 23.07.2024
                """;

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        BaseCommandAbs.setSc(new Scanner(System.in));
        createCommand = new Create(realChamberManager, realUserManager);
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isTrue();
        ResultResponse resultResponse = createCommand.action();
        ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));


        assertThat(resultResponse.getResponse()).isEqualTo(ResponseEnum.SUCCESS_BOOKING);
        assertThat(realUserManager.getUser().getBookingList().isEmpty()).isFalse();
        assertThat(output.contains(waitingAnswer_1)).isTrue();
        assertThat(output.contains(waitingAnswer_2)).isTrue();
        try {
            createCommand.action();
        } catch (NoSuchElementException e) {
            output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));
//            assertThat(output.contains(waitingAnswer_1)).isTrue();
            assertThat(output.contains(waitingAnswer_3)).isTrue();
        }

    }

}
