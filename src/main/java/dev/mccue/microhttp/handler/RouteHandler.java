package dev.mccue.microhttp.handler;

import org.jspecify.annotations.Nullable;
import org.microhttp.Request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template for a handler that handles a single route by matching
 * on the request's uri path and filtering on the request's method.
 */
public abstract class RouteHandler implements Handler {
    private final String method;
    private final Pattern pattern;

    protected RouteHandler(String method, Pattern pattern) {
        this.method = Objects.requireNonNull(method);
        this.pattern = Objects.requireNonNull(pattern);
    }

    protected abstract @Nullable IntoResponse handleRoute(
            Matcher routeMatch,
            Request request
    ) throws Exception;

    public static RouteHandler of(String method, Pattern pattern, Handler handler) {
        return new RouteHandler(method, pattern) {
            @Override
            protected @Nullable IntoResponse handleRoute(
                    Matcher routeMatch,
                    Request request
            ) throws Exception {
                return handler.handle(request);
            }
        };
    }

    public interface MatcherHandler {
        @Nullable IntoResponse handleRoute(
                Matcher routeMatch,
                Request request
        ) throws Exception;
    }

    public static RouteHandler of(
            String method,
            Pattern pattern,
            MatcherHandler handler
    ) {
        return new RouteHandler(method, pattern) {
            @Override
            protected @Nullable IntoResponse handleRoute(
                    Matcher routeMatch,
                    Request request
            ) throws Exception {
                return handler.handleRoute(routeMatch, request);
            }
        };
    }

    @Override
    public final @Nullable IntoResponse handle(Request request) throws Exception {
        if (!method.equalsIgnoreCase(request.method())) {
            return null;
        }

        try {
            var matcher = pattern.matcher(new URI(request.uri()).getPath());

            if (!matcher.matches()) {
                return null;
            }

            return handleRoute(matcher, request);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}