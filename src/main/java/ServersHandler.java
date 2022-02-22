import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;

public class ServersHandler {

    private String serversPath;
    private ActorRef serversStrorage;
    private ZooKeeper zoo;

    private ServersHandler(ZooKeeper zoo, ActorRef serversStrorage, String serversPath) {
        this.zoo = zoo;
        this.serversStrorage = serversStrorage;
        this.serversPath = serversPath;
    }
}
