package com.seenema.backend.GetUserInfo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import com.seenema.backend.GetUserInfo.model.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({DynamoDbClient.class})
public class GetUserInfoHandlerTest {
    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private DynamoDbClientBuilder mockDynamoDbClientBuilder;

    @Mock
    private Context mockContext;

    @Mock
    private LambdaLogger mockLambdaLogger;

    private       GetUserInfoHandler           getUserInfoHandler;
    private final Gson                         gson = new Gson();
    private       MockedStatic<DynamoDbClient> mockedStaticDDBClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedStaticDDBClient = mockStatic(DynamoDbClient.class);
        Mockito.when(DynamoDbClient.builder()).thenReturn(mockDynamoDbClientBuilder);
        Mockito.when(mockDynamoDbClientBuilder.build()).thenReturn(mockDynamoDbClient);

        getUserInfoHandler = new GetUserInfoHandler();
    }

    @After
    public void tearDown() {
        mockedStaticDDBClient.close();
    }

    @Test
    public void testHandleRequest_userExists_returnsFullUserProfile() {
        APIGatewayV2HTTPEvent apiGatewayEvent = APIGatewayV2HTTPEvent.builder()
                .withBody("{" +
                        "\"Email\": \"lpagdar@uw.edu\"" +
                        "}")
                .build();

        when(mockContext.getLogger()).thenReturn(mockLambdaLogger);

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder()
                        .item(Map.of(
                                "Email", AttributeValue.fromS("test@example.com"),
                                "FirstName", AttributeValue.fromS("John"),
                                "LastName", AttributeValue.fromS("Doe"),
                                "Friends", AttributeValue.fromSs(List.of("Friend1", "Friend2", "Friend3")),
                                "Movies", AttributeValue.fromSs(List.of("12345", "67891", "13425")),
                                "MovieSuggestionsList", AttributeValue.fromSs(List.of("9876", "4657", "3333"))))
                        .build()
                );
        when(mockDynamoDbClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(UpdateItemResponse.builder().build());

        String responseStr = getUserInfoHandler.handleRequest(apiGatewayEvent, mockContext);
        Response response = gson.fromJson(responseStr, Response.class);

        assertEquals(response, new Response("test@example.com", "Doe", "John",
                List.of("Friend1", "Friend2", "Friend3"),
                List.of("12345", "67891", "13425"),
                List.of("9876", "4657", "3333")));
    }
}
