import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.logging.Logger;

public class ServersHandler {

    private static Logger log = Logger.getLogger(ServersHandler.class.getName());
    private String serversPath;
    private ActorRef serversStrorage;
    private ZooKeeper zoo;

    public ServersHandler(ZooKeeper zoo, ActorRef serversStrorage, String serversPath) {
        this.zoo = zoo;
        this.serversStrorage = serversStrorage;
        this.serversPath = serversPath;
    }

    public void startServer(String host, int port) throws InterruptedException, KeeperException {
        String serverPath = zoo.create("/servers/"+host+":"+port, (host+":"+port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.info("Path connected");
    }

    public void close() throws InterruptedException, KeeperException {
        zoo.removeAllWatches(serversPath, Watcher.WatcherType.Any, true);
    }
}
