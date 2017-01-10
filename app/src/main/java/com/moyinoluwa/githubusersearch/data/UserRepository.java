package com.moyinoluwa.githubusersearch.data;

import com.moyinoluwa.githubusersearch.data.remote.model.User;

import java.util.List;

import rx.Observable;

/**
 * Created by moyinoluwa on 1/10/17.
 */

public interface UserRepository {

    Observable<List<User>> searchUsers(String searchTerm);
}
