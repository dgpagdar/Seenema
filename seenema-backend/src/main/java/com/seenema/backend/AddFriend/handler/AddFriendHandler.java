package com.seenema.backend.AddFriend.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import com.seenema.backend.AddFriend.model.RequestBody;
import com.seenema.backend.AddFriend.model.Response;
import com.seenema.backend.utils.Constants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;

import static com.seenema.backend.utils.UserFunctions.userExists;


public class AddFriendHandler implements RequestHandler<APIGatewayV2HTTPEvent, Response> {

    Gson           gson           = new Gson();
    DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public Response handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        RequestBody requestBody = gson.fromJson(input.getBody(), RequestBody.class);

        // Validate that both users exist before creating the friendship
        if (userExists(requestBody.getUsername(), dynamoDbClient)
                && userExists(requestBody.getFriendUsername(), dynamoDbClient)) {

            // Add friend for the main user
            addFriend(requestBody.getUsername(), requestBody.getFriendUsername());

            // Add reverse friendship to keep the relationship bidirectional
            addFriend(requestBody.getFriendUsername(), requestBody.getUsername());

            context.getLogger().log(String.format("Friendship created between %s and %s.",
                    requestBody.getUsername(), requestBody.getFriendUsername()));

            return new Response("Friend added successfully.");
        } else {
            context.getLogger().log(String.format("Failed to add friend: one or both users (%s, %s) not found.",
                    requestBody.getUsername(), requestBody.getFriendUsername()));
            return new Response("Either username or friendUsername does not exist in the DynamoDB table.");
        }
    }

    // Appends friendUsername to the Friends set of the given user
    private void addFriend(String username, String friendUsername) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Email", AttributeValue.builder().s(username).build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(key)
                .updateExpression("ADD Friends :friend")
                .expressionAttributeValues(Map.of(
                        ":friend", AttributeValue.builder().ss(friendUsername).build()))
                .build();

        dynamoDbClient.updateItem(updateItemRequest);
    }
}
