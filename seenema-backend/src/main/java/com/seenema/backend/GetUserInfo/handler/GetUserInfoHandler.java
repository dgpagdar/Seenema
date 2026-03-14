package com.seenema.backend.GetUserInfo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.google.gson.Gson;
import com.seenema.backend.GetUserInfo.model.RequestBody;
import com.seenema.backend.GetUserInfo.model.Response;
import com.seenema.backend.utils.Constants;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUserInfoHandler implements RequestHandler<APIGatewayV2HTTPEvent, String> {

    Gson           gson           = new Gson();
    DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public String handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        try {
            RequestBody requestBody = gson.fromJson(input.getBody(), RequestBody.class);
            String userEmail = requestBody.getEmail();
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("Email", AttributeValue.builder().s(userEmail).build());

            GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(Constants.DYNAMODB_TABLE)
                    .key(key)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(getItemRequest);

            if (response.hasItem()) {
                Map<String, AttributeValue> item = response.item();
                String email = item.get("Email").s();
                String lastName = item.get("LastName").s();
                String firstName = item.get("FirstName").s();
                List<String> friends = item.getOrDefault("Friends", AttributeValue.builder().ss().build()).ss();
                List<String> movies = item.getOrDefault("Movies", AttributeValue.builder().ss().build()).ss();
                List<String> movieSuggestionsList = item.getOrDefault("MovieSuggestionsList", AttributeValue.builder().ss().build()).ss();

                return gson.toJson(new Response(email, lastName, firstName, friends, movies, movieSuggestionsList));
            } else {
                return gson.toJson(new Response("User not found"));
            }

        } catch (DynamoDbException e) {
            context.getLogger().log("DynamoDB error while fetching user info: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving user information"));
        }
    }
}
