import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServersHandler {

    private static Logger log = Logger.getLogger(ServersHandler.class.getName());
    private String serversPath;
    private ActorRef serversStrorage;
    private ZooKeeper zoo;

    public ServersHandler(ZooKeeper zoo, ActorRef serversStrorage, String serversPath) {
        this.zoo = zoo;
        this.serversStrorage = serversStrorage;
        this.serversPath = serversPath;

        watchChildrenCallback(null);
    }

    public void startServer(String name, String host, int port) throws InterruptedException, KeeperException {
        String serverPath = zoo.create(serversPath+"/"+name,
                (host+":"+port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        log.info("Path connected");
    }

    public void removeAllWatches() throws InterruptedException, KeeperException {
        zoo.removeAllWatches(serversPath, Watcher.WatcherType.Any, true);
    }

    private void watchChildrenCallback(WatchedEvent event) {
        try {
            saveServer(
                    zoo.getChildren(serversPath, this::watchChildrenCallback).stream()
                            .map(s -> serversPath+"/"+s)
                            .collect(Collectors.toList())
            );
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveServer(List<String> servers) {
        this.serversStrorage.tell(new ListOfServersMessage(servers), ActorRef.noSender());
    }
}
