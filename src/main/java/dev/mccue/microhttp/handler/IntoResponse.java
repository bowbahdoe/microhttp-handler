package dev.mccue.microhttp.handler;

import org.microhttp.Response;

/**
 * Something which can be converted into a {@link Response}.
 */
public interface IntoResponse {
    /**
     * Converts this object into a {@link Response}.
     *
     * <p>
     *     This should generally be treated as a terminal operation like {@link AutoCloseable#close()},
     *     but the specifics of that are up to the implementing class.
     * </p>
     *
     * @return A {@link Response}.
     */
    Response intoResponse();
}
