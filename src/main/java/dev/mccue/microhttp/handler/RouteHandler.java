package dev.mccue.microhttp.handler;

import org.jspecify.annotations.Nullable;
import org.microhttp.Request;

import java.net.URI;
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

    @Override
    public final @Nullable IntoResponse handle(Request request) throws Exception {
        if (!method.equalsIgnoreCase(request.method())) {
            return null;
        }

        var matcher = pattern.matcher(new URI(request.uri()).getPath());

        if (!matcher.matches()) {
            return null;
        }

        return handleRoute(matcher, request);
    }
}