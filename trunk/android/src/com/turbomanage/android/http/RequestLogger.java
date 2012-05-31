
package com.turbomanage.android.http;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * HTTP request logger used by {@link BasicHttpClient}.
 * 
 * @author David M. Chandler
 */
public interface RequestLogger {
    
    /**
     * Determine whether requests should be logged.
     * 
     * @return true if enabled
     */
    boolean isLoggingEnabled();

    /**
     * Log the HTTP request and content to be sent with the request.
     * 
     * @param urlConnection
     * @param content
     * @throws IOException
     */
    void logRequest(HttpURLConnection urlConnection, Object content) throws IOException;

    /**
     * Logs the HTTP response.
     * 
     * @param urlConnection
     * @throws IOException
     */
    void logResponse(HttpURLConnection urlConnection) throws IOException;

}
