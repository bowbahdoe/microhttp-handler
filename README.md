# microhttp-handler

Interfaces for composable handling of requests in microhttp.

## Dependency Information

### Maven

```xml
<dependency>
    <groupId>dev.mccue</groupId>
    <artiactId>microhttp-handler</artiactId>
    <version>0.0.6</version>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation('dev.mccue:microhttp-handler:0.0.6')
}
```

## Usage

First, make an implementation of the `Handler` interface
provided by this library.

```java
import dev.mccue.microhttp.handler.Handler;
import dev.mccue.microhttp.handler.IntoResponse;
import org.jspecify.annotations.Nullable;
import org.microhttp.Request;
import org.microhttp.Response;

public class RootHandler implements Handler {
    @Override
    public IntoResponse handle(Request request) {
        return () -> new Response(200, "OK", List.of(), "Hello, world");
    }
}
```

Then, at the top-level of your microhttp server, set up the 
event-loop to use that handler to handle requests.

```java
import org.microhttp.EventLoop;
import org.microhttp.Response;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var genericError = new Response(
                500, "ERR", List.of(), "Internal Error".getBytes()
        );
        
        var handler = new RootHandler();
        var eventLoop = new EventLoop((request, callback) -> {
            Thread.startVirtualThread(() -> {
                try {
                    callback.accept(handler.handle(request).intoResponse());
                } catch (Exception e) {
                    callback.accept(genericError);
                }
            });
        });
    }
}
```

Each `Handler` needs to return something which can be converted into a response.
That is what the `IntoResponse` interface is for, and you can use it
to create shorthands for certain sorts of responses.

In order to handle requests on particular routes, one way provided is
to subclass `RouteHandler`. This creates a `Handler` that will return
`null` if called with a `Request` that doesn't match the given method
and route pattern.

```java
import dev.mccue.microhttp.handler.IntoResponse;
import dev.mccue.microhttp.handler.RouteHandler;
import org.jspecify.annotations.Nullable;
import org.microhttp.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTodosHandler extends RouteHandler {
    public GetTodosHandler() {
        super("GET", Pattern.compile("/todos"));
    }

    @Override
    protected IntoResponse handleRoute(Matcher routeMatch, Request request) {
        return () -> new Response(200, "OK", List.of(), "Todos Route");
    }
}
```

With multiple `Handler`s that return `null` for a route they don't want to handle, you can
use the provided `DelegatingHandler`.

```java
import dev.mccue.microhttp.handler.DelegatingHandler;
import org.microhttp.EventLoop;
import org.microhttp.Response;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var genericError = new Response(
                500, "ERR", List.of(), "Internal Error".getBytes()
        );
        var notFound = new Response(
                404, "Not Found", List.of(), "Not Found".getBytes()
        );

        var handler = new DelegatingHandler(
                List.of(
                        // Handles GET /todos
                        new GetTodosHandler()
                ),
                () -> notFound
        );
        var eventLoop = new EventLoop((request, callback) -> {
            Thread.startVirtualThread(() -> {
                try {
                    callback.accept(handler.handle(request).intoResponse());
                } catch (Exception e) {
                    callback.accept(genericError);
                }
            });
        });
    }
}
```

You can also make `RouteHandler`s using the provided static methods.

```java
import dev.mccue.microhttp.handler.DelegatingHandler;
import dev.mccue.microhttp.handler.IntoResponse;
import dev.mccue.microhttp.handler.RouteHandler;
import org.microhttp.EventLoop;
import org.microhttp.Request;
import org.microhttp.Response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static IntoResponse getTodos(Request request) {
        return () -> new Response(
                200, "OK", List.of(), "TODOS".getBytes()
        );
    }

    static IntoResponse getTodo(Matcher matcher, Request request) {
        return () -> new Response(
                200, "OK", List.of(), matcher.group("id").getBytes()
        );
    }

    public static void main(String[] args) {
        var genericError = new Response(
                500, "ERR", List.of(), "Internal Error".getBytes()
        );
        var notFound = new Response(
                404, "Not Found", List.of(), "Not Found".getBytes()
        );

        var handler = new DelegatingHandler(
                List.of(
                        // Handles GET /todos
                        RouteHandler.of(
                                "GET", 
                                Pattern.compile("/todo"), 
                                Main::getTodos
                        ),
                        RouteHandler.of(
                                "GET",
                                Pattern.compile("/todo/(?<id>.+)"),
                                Main::getTodo
                        )
                ),
                () -> notFound
        );
        var eventLoop = new EventLoop((request, callback) -> {
            Thread.startVirtualThread(() -> {
                try {
                    callback.accept(handler.handle(request).intoResponse());
                } catch (Exception e) {
                    callback.accept(genericError);
                }
            });
        });
    }
}
```

