package com.seenema.backend.AddMoviesToFriendsSuggestionsList.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import com.seenema.backend.AddMoviesToFriendsSuggestionsList.model.RequestBody;
import com.seenema.backend.AddMoviesToFriendsSuggestionsList.model.Response;
import com.seenema.backend.utils.Constants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;

import static com.seenema.backend.utils.UserFunctions.userExists;


public class AddMoviesToFriendsSuggestionsListHandler implements RequestHandler<APIGatewayV2HTTPEvent, Response> {

    Gson           gson           = new Gson();
    DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public Response handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        RequestBody requestBody = gson.fromJson(input.getBody(), RequestBody.class);

        // Check if both username and friendUsername exist
        if (userExists(requestBody.getUsername(), dynamoDbClient)
                && userExists(requestBody.getFriendUsername(), dynamoDbClient)
                && friendExists(requestBody.getUsername(), requestBody.getFriendUsername())) {
            // Add  movie to friend's suggestions list
            addMovieToFriendsSuggestionList(requestBody.getFriendUsername(), requestBody.getMovieId());

            // Log success or handle response accordingly
            context.getLogger().log("Movie added to your friend's suggestion list successfully.");

            // Return a response if needed
            return new Response("Movie added to " + requestBody.getFriendUsername() + "'s suggestion list successfully.");
        } else {
            context.getLogger().log("Either username or friendUsername does not exist in the DynamoDB table." + "OR "
                    + requestBody.getFriendUsername() + "is not" + requestBody.getUsername() + "'s friend.");
            return new Response("Either username or friendUsername does not exist in the DynamoDB table." + "OR "
                    + requestBody.getFriendUsername() + "is not" + requestBody.getUsername() + "'s friend.");
        }
    }

    // Helper method to check if a friend exists in the user's friends list
    private boolean friendExists(String username, String friendUsername) {
        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(Map.of("Email", AttributeValue.builder().s(username).build()))
                .projectionExpression("Friends")
                .build());

        if (!getItemResponse.hasItem()) {
            return false;
        }

        AttributeValue friendsAttributeValue = getItemResponse.item().get("Friends");
        if (friendsAttributeValue != null) {
            return friendsAttributeValue.ss().contains(friendUsername);
        }

        return false;
    }

    // Common function to add a friend for a user
    private void addMovieToFriendsSuggestionList(String friendUsername, String movieId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Email", AttributeValue.builder().s(friendUsername).build());

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":movie", AttributeValue.builder().ss(movieId).build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(key)
                .updateExpression("ADD MovieSuggestionsList :movie")
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        dynamoDbClient.updateItem(updateItemRequest);
    }
}
