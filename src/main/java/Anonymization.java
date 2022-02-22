import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import java.util.concurrent.Future;

public class Anonymization extends AllDirectives {
    private ActorRef storage;
    private AsyncHttpClient http;
    private ZooKeeper zoo;

    public Anonymization(ActorRef storage, AsyncHttpClient http, ZooKeeper zoo) {
        this.storage = storage;
        this.http = http;
        this.zoo = zoo;
    }


}
