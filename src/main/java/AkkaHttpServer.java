import akka.NotUsed;
import akka.actor.ActorSystem;
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
    ActorSystem system = ActorSystem.create("routes");

    public AkkaHttpServer() {

    }

    public void start() {
        final ActorMaterializer materia = ActorMaterializer.create(system);
    }

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = test.createFlow();
    final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost("localhost", 8086),
            materializer
    );

    //System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
    //System.in.read();

    binding
            .thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());
}
