package com.moyinoluwa.githubusersearch.data;

import com.moyinoluwa.githubusersearch.data.remote.GithubUserRestService;
import com.moyinoluwa.githubusersearch.data.remote.model.User;

import java.io.IOException;
import java.util.List;

import rx.Observable;

/**
 * Created by moyinoluwa on 1/10/17.
 */

public class UserRepositoryImpl implements UserRepository {

    private GithubUserRestService githubUserRestService;

    public UserRepositoryImpl(GithubUserRestService githubUserRestService) {
        this.githubUserRestService = githubUserRestService;
    }

    @Override
    public Observable<List<User>> searchUsers(final String searchTerm) {
        return Observable.defer(() -> githubUserRestService.searchGithubUsers(searchTerm)
                .concatMap(usersList -> Observable.from(usersList.getItems()).concatMap(user ->
                        githubUserRestService.getUser(user.getLogin())).toList()))

                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) {
                        return Observable.just(null);
                    }
                    return Observable.error(o);
                }));
    }
}
