package lab.Commands.ConcreteCommands;

import lab.Commands.Command;
import lab.Commands.CommandReceiver;

import java.io.IOException;

/**
 * Конкретная команда удаления по ID.
 */
public class RemoveHead extends Command {
    private static final long serialVersionUID = 33L;

    @Override
    public String execute(Object argObject) throws IOException {
        CommandReceiver commandReceiver = new CommandReceiver();
        return commandReceiver.removeHead();
    }
}
