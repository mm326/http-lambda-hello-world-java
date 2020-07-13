package com.muadmo.di;

import javax.inject.Singleton;

import com.muadmo.handler.DeleteUserHandler;
import com.muadmo.handler.GetAllUserHandler;
import com.muadmo.handler.GetUserHandler;
import com.muadmo.handler.PostUserHandler;
import com.muadmo.handler.UpdateUserHandler;
import com.muadmo.service.DynamoDbService;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Module
public class LambdaModule {
    
    @Provides
    @Singleton
    public PostUserHandler postHandler(DynamoDbService dynamoDbService) {
        return new PostUserHandler(dynamoDbService);
    }
    
    @Provides
    @Singleton
    public GetUserHandler getHandler(DynamoDbService dynamoDbService) {
        return new GetUserHandler(dynamoDbService);
    }
    
    @Provides
    @Singleton
    public GetAllUserHandler getAllUserHandler(DynamoDbService dynamoDbService) {
        return new GetAllUserHandler(dynamoDbService);
    }
    
    @Provides
    @Singleton
    public UpdateUserHandler updateHandler(DynamoDbService dynamoDbService) {
        return new UpdateUserHandler(dynamoDbService);
    }
    
    @Provides
    @Singleton
    public DeleteUserHandler deleteHandler(DynamoDbService dynamoDbService) {
        return new DeleteUserHandler(dynamoDbService);
    }
    
    
    @Provides
    @Singleton
    public DynamoDbService dynamoDbService(DynamoDbClient dynamoDbClientProvider) {
        return new DynamoDbService(dynamoDbClientProvider);
    }
    
    @Provides
    @Singleton
    public DynamoDbClient dynamoDbClientProvider() {
        return DynamoDbClient.builder().build();
    }
}
