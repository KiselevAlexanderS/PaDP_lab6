import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class AnonimizerApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final ActorMaterializer materia = ActorMaterializer.create(system);
    }
}
