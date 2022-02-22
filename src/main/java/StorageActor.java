import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StorageActor extends AbstractActor {

    public List<String> storage;
    private Random randServer;

    public StorageActor() {
        this.storage = new ArrayList<>();
        this.randServer = new Random();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ListOfServersMessage.class,);
    }
}
