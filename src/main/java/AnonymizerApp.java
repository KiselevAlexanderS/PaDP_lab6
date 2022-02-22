import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.as

public class AnonymizerApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final AsyncHttpClient asyncHttpClient = a
        final ActorMaterializer materia = ActorMaterializer.create(system);

        final Flow<HttpRequest , HttpResponse, NotUsed> routeFlow =
    }
}
