package com.seenema.backend.AddFriend.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.seenema.backend.AddFriend.model.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@PrepareForTest({DynamoDbClient.class})
public class AddFriendTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private DynamoDbClientBuilder mockDynamoDbClientBuilder;

    @Mock
    private Context mockContext;

    @Mock
    private LambdaLogger mockLambdaLogger;

    private AddFriendHandler             addFriendHandler;
    private MockedStatic<DynamoDbClient> mockedStaticDDBClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedStaticDDBClient = mockStatic(DynamoDbClient.class);
        when(DynamoDbClient.builder()).thenReturn(mockDynamoDbClientBuilder);
        when(mockDynamoDbClientBuilder.build()).thenReturn(mockDynamoDbClient);

        addFriendHandler = new AddFriendHandler();
    }

    @After
    public void tearDown() {
        mockedStaticDDBClient.close();
    }

    @Test
    public void testHandleRequest_bothUsersExist_returnsFriendAddedResponse() {
        APIGatewayV2HTTPEvent apiGatewayEvent = APIGatewayV2HTTPEvent.builder()
                .withBody("{" +
                        "\"username\": \"lpagdar@uw.edu\"," +
                        "\"friendUsername\": \"dgpagdar@uw.edu\"" +
                        "}")
                .build();

        when(mockContext.getLogger()).thenReturn(mockLambdaLogger);

        // Both users exist in DynamoDB
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder()
                        .item(Map.of("Email", AttributeValue.fromS("lpagdar@uw.edu")))
                        .build());
        when(mockDynamoDbClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(UpdateItemResponse.builder().build());

        Response response = addFriendHandler.handleRequest(apiGatewayEvent, mockContext);

        assertEquals(response, new Response("Friend added successfully."));
    }
}
