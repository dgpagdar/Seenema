package com.seenema.backend.utils;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;

public class UserFunctions {

    /**
     * Checks whether a user with the given username (email) exists in DynamoDB.
     *
     * @param username  the user's email address used as the DynamoDB partition key
     * @param ddbClient the DynamoDB client instance
     * @return {@code true} if the user exists
     * @throws UserNotFoundException if no item is found for the given username
     */
    public static boolean userExists(String username, DynamoDbClient ddbClient) {
        GetItemResponse getItemResponse = ddbClient.getItem(GetItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(Map.of("Email", AttributeValue.builder().s(username).build()))
                .build());

        if (!getItemResponse.hasItem()) {
            throw new UserNotFoundException(String.format("User %s does not exist", username));
        } else {
            return true;
        }
    }
}
