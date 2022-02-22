import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;

public class StorageActor extends AbstractActor {

    public List<String> storage;

    public StorageActor() {
        this.storage = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
