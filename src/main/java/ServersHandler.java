import akka.actor.ActorRef;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ServersHandler {

    private String serversPath;
    private ActorRef serversStrorage;
    private ZooKeeper zoo;

    public ServersHandler(ZooKeeper zoo, ActorRef serversStrorage, String serversPath) {
        this.zoo = zoo;
        this.serversStrorage = serversStrorage;
        this.serversPath = serversPath;
    }

    public void StartServer(String host, int port) {
        String serverPath = zoo.create("/servers/"+host+":"+port, (host+":"+port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,);
    }
}
