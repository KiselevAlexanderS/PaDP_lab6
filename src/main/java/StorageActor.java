import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class StorageActor extends AbstractActor {

    public List<String> storage;
    private Random randServer;
    private Logger log = Logger.getLogger(StorageActor.class.getName());

    public StorageActor() {
        this.storage = new ArrayList<>();
        this.randServer = new Random();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ListOfServersMessage.class, this::receiveListOfServers)
                .match(GetRandomServerMessage.class, this::receiveGetRandomServerMessage)
                .match(DeleteServerMessage.class, this::receiveDeleteMessage).build();
    }

    private void receiveListOfServers(ListOfServersMessage msg) {
        log.info("List of servers: " + msg.getServersList());
        this.storage.clear();
        this.storage.addAll(msg.getServersList());
    }

    private void receiveGetRandomServerMessage(GetRandomServerMessage msg) {
        getSender().tell(
                new ReturnRandomServerMessage(storage.get(randServer.nextInt(storage.size()))), ActorRef.noSender()
        );
    }

    private void receiveDeleteMessage(DeleteServerMessage msg) {
        this.storage.remove(msg.getServer());
    }
}
