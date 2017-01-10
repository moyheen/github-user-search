package com.moyinoluwa.githubusersearch.data;

import com.moyinoluwa.githubusersearch.data.remote.GithubUserRestService;
import com.moyinoluwa.githubusersearch.data.remote.model.User;
import com.moyinoluwa.githubusersearch.data.remote.model.UsersList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moyinoluwa on 1/10/17.
 */
public class UserRepositoryImplTest {

    private static final String USER_LOGIN_MOYHEEN = "moyheen";
    private static final String USER_LOGIN_2_MOYHEEN = "moyinoluwa";

    private UserRepository userRepository;

    @Mock
    GithubUserRestService githubUserRestService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepositoryImpl(githubUserRestService);
    }

    @Test
    public void searchUsers_200OKResponse_InvokesCorrectApiCalls() {
        // Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(Observable.just
                (githubUserList()));
        when(githubUserRestService.getUser(anyString())).thenReturn(Observable.just
                (user1FullDetails()), Observable.just(user2FullDetails()));

        // When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_MOYHEEN).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<User>> onNextEvents = subscriber.getOnNextEvents();
        List<User> users = onNextEvents.get(0);
        Assert.assertEquals(USER_LOGIN_MOYHEEN, users.get(0).getLogin());
        Assert.assertEquals(USER_LOGIN_2_MOYHEEN, users.get(1).getLogin());
        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService).getUser(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService).getUser(USER_LOGIN_2_MOYHEEN);
    }

    private UsersList githubUserList() {
        User user = new User(USER_LOGIN_MOYHEEN);

        User user2 = new User(USER_LOGIN_2_MOYHEEN);

        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user);
        githubUsers.add(user2);
        UsersList usersList = new UsersList();
        usersList.setItems(githubUsers);
        return usersList;
    }

    private User user1FullDetails() {
        User user = new User(USER_LOGIN_MOYHEEN, "Moy Adeyemi", "avatar_url", "Bio1");
        return user;
    }

    private User user2FullDetails() {
        User user = new User(USER_LOGIN_2_MOYHEEN, "Moyin Adeyemi", "avatar_url2", "Bio2");
        return user;
    }

    @Test
    public void searchUsers_IOExceptionThenSuccess_SearchUsersRetried() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()), Observable.just(user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_MOYHEEN).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService).getUser(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService).getUser(USER_LOGIN_2_MOYHEEN);
    }

    private Observable getIOExceptionError() {
        return Observable.error(new IOException());
    }

    @Test
    public void searchUsers_GetUserIOExceptionThenSuccess_SearchUsersRetried() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(user1FullDetails()),
                        Observable.just(user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_MOYHEEN).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService, times(2)).getUser(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService).getUser(USER_LOGIN_2_MOYHEEN);
    }

    @Test
    public void searchUsers_OtherHttpError_SearchTerminatedWithError() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(get403ForbiddenError());

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_MOYHEEN).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService, never()).getUser(USER_LOGIN_MOYHEEN);
        verify(githubUserRestService, never()).getUser(USER_LOGIN_2_MOYHEEN);
    }

    private Observable<UsersList> get403ForbiddenError() {
        return Observable.error(new HttpException(Response.error(403, ResponseBody.create
                (MediaType.parse("application/json"), "Forbidden"))));
    }

}