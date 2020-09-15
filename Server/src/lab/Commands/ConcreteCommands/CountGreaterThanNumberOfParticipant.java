package lab.Commands.ConcreteCommands;


import lab.Commands.Command;
import lab.Commands.CommandReceiver;
import lab.Commands.SerializedCommands.Message;

import java.io.IOException;

public class CountGreaterThanNumberOfParticipant extends Command {
    private static final long serialVersionUID = 33L;

    @Override
    public String execute(Object argObject) throws IOException {
        int NoP = ((Message)argObject).getMusicBand().getNumberOfParticipants();
        CommandReceiver commandReceiver = new CommandReceiver();
        return commandReceiver.countGreaterThanNumberOfParticipant(NoP);
    }
}
