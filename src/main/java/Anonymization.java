import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.omg.asynchttpclient.Request;
import org.omg.asynchttpclient.Response;

import javax.xml.ws.Response;
import java.net.ConnectException;
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
                                parameter("count", c ->
                                        handleGetWithUrlCount(url, Integer.parseInt(c))
                                )
                        )
                )
        );
    }

    private Route handleGetWithUrlCount(String url, int count) {
        CompletionStage<Response> response = count == 0 ?
                urlRequest(http.prepareGet(url).build())
                :
                requestWithLowerCount(url, count-1);
        return completeOKWithFutureString(response.thenApply(Response::getResponseBody));
    }

    private CompletionStage<Response> urlRequest(Request req) {
        log.info("Request "+req.getUri());
        return http.executeRequest(req).toCompletableFuture();
    }

    private CompletionStage<Response> requestWithLowerCount(String url, int count) {
        return Patterns.ask(storage, new GetRandomServerMessage(), Duration.ofSeconds(3))
                .thenApply(o -> ((ReturnRandomServerMessage)o).getServer())
                .thenCompose(msg ->
                        urlRequest(makeRequest(getServUrl(msg), url, count))
                                .handle((resp, ex) -> handleBadRequest(resp, ex, msg))
                );
    }

    private Object handleBadRequest(Response resp, Throwable ex, String msg) {
        if (ex instanceof ConnectException) {
            storage.tell(new DeleteServerMessage(msg), ActorRef.noSender());
        }
        return resp;
    }

    private Request makeRequest(String servUrl, String url, int count) {
        return http.prepareGet(servUrl).addQueryParam("url", url).addQueryParam("count", Integer.toString(count)).build();
    }

    private String getServUrl(String obj) {
        try {
            return new String(zoo.getData(obj, false, null));
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
