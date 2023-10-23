package dev.mccue.microhttp.handler;

import org.microhttp.Request;

import java.util.List;
import java.util.Objects;

/**
 * Template for a class which has a list of handlers that it tries
 * in order.
 */
public class DelegatingHandler implements Handler {

    private final List<Handler> handlers;
    private final IntoResponse notHandled;

    /**
     *
     * @param handlers The list of handlers, in the order they should be tried.
     * @param notHandled The {@link IntoResponse} to use if no handler matches the request.
     */
    public DelegatingHandler(
            List<Handler> handlers,
            IntoResponse notHandled
    ) {
        this.handlers = List.copyOf(handlers);
        this.notHandled = Objects.requireNonNull(notHandled);
    }

    @Override
    public IntoResponse handle(Request request) throws Exception {
        try {
            for (var handler : handlers) {
                var intoResponse = handler.handle(request);
                if (intoResponse != null) {
                    return intoResponse;
                }
            }
        } catch (Exception e) {
            if (e instanceof IntoResponse intoResponse) {
                return intoResponse;
            }
            throw e;
        }


        return notHandled;
    }
}
