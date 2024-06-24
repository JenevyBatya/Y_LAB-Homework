import org.example.command.BaseCommandAbs;
import org.example.command.Create;
import org.example.enumManagment.CommandNameEnum;
import org.example.managment.ChamberManager;
import org.example.managment.CommandManager;
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
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandManagerTest {
    @Mock
    private ChamberManager mockChamberManager;
    @Mock
    private UserManager mockUserManager;
    @Mock
    private Chamber mockChamber;


    private UserManager realUserManager = new UserManager();
    private CommandManager commandManager = new CommandManager();
    private ChamberManager realChamberManager = new ChamberManager();
    private Chamber chamber = new Chamber();


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCommandManagerRegisterCommands() {
        assertThat(commandManager.getCommandTable().isEmpty()).isTrue();
        commandManager.registerChambers(mockChamberManager);
        commandManager.registerCommands();
        assertThat(commandManager.getCommandTable().size()).isEqualTo(8);
    }

    @Test
    public void testCommandManagerRunHelp() {
        String waitingAnswer_1 = "Неизвестная команда";
        String input = """
                sac
                Help
                """;
        StringBuilder waitingAnswer_2 = new StringBuilder();
        for (CommandNameEnum command : CommandNameEnum.values()) {
            waitingAnswer_2.append(command).append(": ").append(command.getText()).append("\n");
        }
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

//        BaseCommandAbs.setSc(new Scanner(System.in));
        assertThat(commandManager.getCommandTable().isEmpty()).isTrue();
        commandManager.registerChambers(mockChamberManager);
        commandManager.registerCommands();
        try {
            commandManager.run();
        } catch (NoSuchElementException e) {
            ArrayList<String> output = new ArrayList<>(Arrays.asList(outContent.toString().split("\r\n")));

            assertThat(commandManager.getCommandTable().size()).isEqualTo(8);
            assertThat(output.contains(waitingAnswer_1)).isTrue();
            assertThat(output.contains(waitingAnswer_2.toString())).isTrue();
        }


    }
}
