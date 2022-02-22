import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;

public class AkkaHttpServer {
    ActorSystem system = ActorSystem.create("routes");
    final AsyncHttpClient asyncHttpClient = asyncHttpClient();
    final Http http = Http.get(system);
    final ActorMaterializer materia = ActorMaterializer.create(system);

    final TestPerfomer test = new TestPerfomer(materia, system, asyncHttpClient);

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = test.createFlow();

    final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost("localhost", 8000),
            materia
    );
        System.out.println("Server at localhost:8000/\n Press Enter to stop");
        System.in.read();
}