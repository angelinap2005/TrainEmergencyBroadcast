package userControl;

import util.graph.GraphGenerator;
import dto.message.EmergencyMessage;
import util.graph.message.MessageSender;

import java.util.Scanner;

public class UserControl {
    private Scanner scanner = new Scanner(System.in);
    private GraphGenerator graphGenerator;
    private EmergencyMessage message;
    private MessageSender messageSender;
    public UserControl(GraphGenerator graphGenerator) {
        this.graphGenerator = graphGenerator;
        messageSender = new MessageSender(graphGenerator.getGraph());
    }

    public void start() {
        boolean result = false;
        while (!result) {
            result = displayCommands();
        }
    }

    private boolean displayCommands() {
        boolean result;
        System.out.println("Please enter the emergency message to broadcast: ");
        String input = scanner.nextLine();
        if (input != null && input.length() > 0) {
            message = new EmergencyMessage(input);
            System.out.println("Emergency message broadcasted: " + input);
        } else {
            System.out.println("Please enter a valid message.");
        }
        result = sendMessage();
        return result;
    }

    private boolean sendMessage() {
        boolean result = false;
        if (message != null) {
            System.out.println("Sending message: " + message.getMessage());
            result = messageSender.control(message.getMessage());
        } else {
            System.out.println("No message to send.");
        }
        return result;
    }
}
