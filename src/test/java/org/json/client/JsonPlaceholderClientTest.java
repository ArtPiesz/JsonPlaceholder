// test/client/JsonPlaceholderClientTest.java
package org.json.client;

import org.json.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JsonPlaceholderClientTest {

    @Test
    public void shouldReturnEmptyListWhenApiReturnsEmptyArray() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("[]");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        JsonPlaceholderClient client = new JsonPlaceholderClient(
                httpClient,
                objectMapper,
                "http://test.com"
        );

        assertTrue(client.fetchPosts().isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidJson() throws Exception {

        HttpClient httpClient = mock(HttpClient.class);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("invalid-json");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        JsonPlaceholderClient client = new JsonPlaceholderClient(
                httpClient,
                objectMapper,
                "http://test.com"
        );

        assertThrows(ApiException.class, client::fetchPosts);
    }

    @Test
    void shouldRetryOnServerErrorAndEventuallySucceed() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);

        HttpResponse<String> serverError = mock(HttpResponse.class);
        HttpResponse<String> success = mock(HttpResponse.class);

        when(serverError.statusCode()).thenReturn(500);
        when(success.statusCode()).thenReturn(200);
        when(success.body()).thenReturn("[]");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(serverError)
                .thenReturn(success);

        JsonPlaceholderClient client = new JsonPlaceholderClient(
                httpClient, new ObjectMapper(), "http://test.com"
        );

        assertTrue(client.fetchPosts().isEmpty());
        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldThrowAfterMaxRetriesOn500() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);

        HttpResponse<String> serverError = mock(HttpResponse.class);
        when(serverError.statusCode()).thenReturn(500);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(serverError);

        JsonPlaceholderClient client = new JsonPlaceholderClient(
                httpClient, new ObjectMapper(), "http://test.com"
        );

        assertThrows(ApiException.class, client::fetchPosts);
        verify(httpClient, times(3)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}