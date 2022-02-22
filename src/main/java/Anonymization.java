import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import javax.xml.ws.Response;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class Anonymization extends AllDirectives {
    private ActorRef storage;
    private AsyncHttpClient http;
    private ZooKeeper zoo;
    private static Logger log = Logger.getLogger(Anonymization.class.getName());

    public Anonymization(ActorRef storage, AsyncHttpClient http, ZooKeeper zoo) {
        this.storage = storage;
        this.http = http;
        this.zoo = zoo;
    }

    public Route createRoute(ActorSystem system) {
        return route(
                get(() ->
                        parameter("url", url ->
                                parameter("count", c -> {
                                            int count = Integer.parseInt(c);
                                            return count == 0 ?
                                                    completeWithFuture(urlRequest(url, system))
                                                    :
                                                    completeWithFuture(requestWithLowerCount(url, count-1));
                                        }
                                )
                        )
                );
    }

    private static CompletionStage<HttpResponse> urlRequest(String url, ActorSystem system) {
        log.info("Request "+url);
        return Http.get(system).singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<Response> requestWithLowerCount(String url, int count) {
        return Patterns.ask(storage, new GetRandomServerMessage(), Duration.ofSeconds(3))
                .thenApply(obj -> ((ReturnRandomServerMessage)obj).getServer())
                .thenCompose(msg -> urlRequest(system, getUri(msg))
        ())
    }
    
    public static Uri getUri(String adr) {
        return Uri.create("http://"+adr);
    }
}
