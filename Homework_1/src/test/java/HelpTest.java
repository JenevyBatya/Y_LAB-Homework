import org.example.command.Delete;
import org.example.command.Help;
import org.example.enumManagment.CommandNameEnum;
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

import static org.assertj.core.api.Assertions.*;

public class HelpTest {
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
    private Help helpCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHelp() {
        helpCommand = new Help(mockChamberManager, mockUserManager);
        StringBuilder waitingAnswer = new StringBuilder();
        for (CommandNameEnum command : CommandNameEnum.values()) {
            waitingAnswer.append(command).append(": ").append(command.getText()).append("\n");
        }
        ResultResponse result = helpCommand.action();
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo(ResponseEnum.TEXT);
        assertThat(result.getData()).isEqualTo(waitingAnswer.toString());
    }
}
