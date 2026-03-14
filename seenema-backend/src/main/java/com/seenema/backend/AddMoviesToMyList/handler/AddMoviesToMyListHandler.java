package com.seenema.backend.AddMoviesToMyList.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import com.seenema.backend.AddMoviesToMyList.model.RequestBody;
import com.seenema.backend.AddMoviesToMyList.model.Response;
import com.seenema.backend.utils.Constants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;


public class AddMoviesToMyListHandler implements RequestHandler<APIGatewayV2HTTPEvent, Response> {

    Gson           gson           = new Gson();
    DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public Response handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        RequestBody requestBody = gson.fromJson(input.getBody(), RequestBody.class);

        // Validate user exists before adding the movie
        if (userExists(requestBody.getUsername())) {

            // Add the movie to the user's list
            addMovieToMyList(requestBody.getUsername(), requestBody.getmovieId());

            context.getLogger().log(String.format("Movie %s added to list for user %s.",
                    requestBody.getmovieId(), requestBody.getUsername()));

            return new Response("Movie added successfully.");
        } else {
            context.getLogger().log(String.format("Failed to add movie: user %s not found.", requestBody.getUsername()));
            return new Response("username does not exist in the DynamoDB table.");
        }
    }

    // Returns true if a user record exists in DynamoDB for the given email
    private boolean userExists(String email) {
        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(Map.of("Email", AttributeValue.builder().s(email).build()))
                .build());

        return getItemResponse.hasItem();
    }

    // Appends movieId to the Movies set of the given user
    private void addMovieToMyList(String username, String movieId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Email", AttributeValue.builder().s(username).build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(key)
                .updateExpression("ADD Movies :movie")
                .expressionAttributeValues(Map.of(
                        ":movie", AttributeValue.builder().ss(movieId).build()))
                .build();

        dynamoDbClient.updateItem(updateItemRequest);
    }
}
