package com.muadmo.di;

import javax.inject.Singleton;

import com.muadmo.handler.DeleteUserHandler;
import com.muadmo.handler.GetAllUserHandler;
import com.muadmo.handler.GetUserHandler;
import com.muadmo.handler.PostUserHandler;
import com.muadmo.handler.UpdateUserHandler;
import com.muadmo.service.DynamoDbService;

import dagger.Component;

@Singleton
@Component(modules = {LambdaModule.class})
public interface LambdaComponent {
    PostUserHandler postHandler();
    DeleteUserHandler deleteHandler();
    GetUserHandler getHandler();
    GetAllUserHandler getAllUserHandler();
    UpdateUserHandler updateHandler();
    DynamoDbService dynamoDbService();
}
