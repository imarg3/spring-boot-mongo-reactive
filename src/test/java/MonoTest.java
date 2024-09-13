import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

// Note: If you run these tests without subscribing to the publisher, you will not see any output
// Remember nothing happens until you are subscribed.
public class MonoTest {

    @Test
    @DisplayName("This method publishes the supplied argument")
    void firstMono() {
        // It takes only one element
        Mono.just("A");
    }

    @Test
    @DisplayName("This method publishes the supplied argument")
    void monoWithConsumer() {
        // A handy way to peek at every event of the sequence is using the log operator
        // The log operator uses logging frameworks like Log4j, but if no logging framework is
        // available, it logs to the console.
        Mono.just("A")
                .log()
                .subscribe(s -> System.out.println(s));
    }

    @Test
    void monoWithDoOn() {
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("Subscribed: " + subs))
                .doOnRequest(request -> System.out.println("Request: " + request))
                .doOnSuccess(complete -> System.out.println("Complete: " + complete))
                .subscribe(System.out::println);
    }

    @Test
    void emptyMono() {
        Mono.empty()
                .log()
                .subscribe(System.out::println);
    }

    @Test
    void emptyCompleteConsumerMono() {
        Mono.empty()
                .log()
                .subscribe(System.out::println,
                        null,
                        () -> System.out.println("Done"));
    }

    @Test
    void errorRuntimeExceptionMono() {
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    @Test
    void errorExceptionMono() {
        Mono.error(new Exception())
                .log()
                .subscribe();
    }

    @Test
    void errorConsumerMono() {
        Mono.error(new Exception())
                .log()
                .subscribe(System.out::println,
                        e -> System.out.println("Error: " + e));
    }

    @Test
    void errorDoOnErrorMono() {
        Mono.error(new Exception())
                .doOnError(e -> System.out.println("Error: " + e))
                .log()
                .subscribe();
    }

    @Test
    void errorOnErrorResumeMono() {
        Mono.error(new Exception())
                .onErrorResume(e -> {
                    System.out.println("Caught: " + e);
                    return Mono.just("B");
                })
                .log()
                .subscribe();
    }

    @Test
    void errorOnErrorReturnMono() {
        Mono.error(new Exception())
                .onErrorReturn("B")
                .log()
                .subscribe();
    }
}
