package dev.mccue.microhttp.handler;

import org.jspecify.annotations.Nullable;
import org.microhttp.Request;

/**
 * Handler for requests.
 */
public interface Handler {
    /**
     * Handles the request.
     *
     * <p>If {@code null} is returned, that means that the handler is signaling that it does
     * not want to handle the {@link Request}. This would signal that another {@link Handler} should
     * be consulted or a default response should be used</p>
     *
     * @param request The request to handle.
     * @return Something which can be converted into a response or null.
     * @throws Exception If something goes wrong.
     */
    @Nullable IntoResponse handle(Request request) throws Exception;
}
