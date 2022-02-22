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
import org.asynchttpclient.AsyncHttpClient;

import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpServer {
    private ActorSystem system = ActorSystem.create("routes");
    private ActorRef storageActor;

    public AkkaHttpServer() {
        this.storageActor = system.actorOf(Props.create(StorageActor.class), "Storage");
    }

    public void start() {
        final Http http = Http.get(system);
        final ActorMaterializer materia = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = ServerRoutes.createRoute(system).flow(system, materia);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8000),
                materia
        );
    }

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = test.createFlow();

    //System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
    //System.in.read();

    binding
            .thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());
}
