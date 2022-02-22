import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpServer {
    private ActorSystem system;
    private ActorRef storage;
    private CompletionStage<ServerBinding> binding;
    private String host;
    private int port;
    ServersHandler serverHandle;
    private static String connectString = "127.0.0.1:2181";
    private static int sessionTimeout = 3000;
    private static String serversPath = "/servers";
    private Logger log = Logger.getLogger(AkkaHttpServer.class.getName());

    public AkkaHttpServer(String host, int port) {
        this.storage = system.actorOf(Props.create(StorageActor.class), "Storage");
        this.host = host;
        this.port = port;
        this.system = ActorSystem.create("routes");
    }

    public void start() throws IOException {
        final ZooKeeper zoo = new ZooKeeper(connectString, sessionTimeout, watcher -> log.info(watcher.toString()));
        final Http http = Http.get(system);
        final ActorMaterializer materia = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();

        ServersHandler serverHandle = new ServersHandler(zoo, storage, serversPath);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = ServerRoutes.createRoute(system).flow(system, materia);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8000),
                materia
        );
    }

    public void close() {
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
