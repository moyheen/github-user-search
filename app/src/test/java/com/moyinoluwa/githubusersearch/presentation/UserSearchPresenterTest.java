package com.moyinoluwa.githubusersearch.presentation;

import com.moyinoluwa.githubusersearch.data.UserRepository;
import com.moyinoluwa.githubusersearch.data.remote.model.User;
import com.moyinoluwa.githubusersearch.data.remote.model.UsersList;
import com.moyinoluwa.githubusersearch.presentation.base.BasePresenter;
import com.moyinoluwa.githubusersearch.presentation.search.UserSearchContract;
import com.moyinoluwa.githubusersearch.presentation.search.UserSearchPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by moyinoluwa on 1/13/17.
 */
public class UserSearchPresenterTest {

    private static final String USER_LOGIN_MOYHEEN = "moyheen";
    private static final String USER_LOGIN_2_MOYHEEN = "moyinoluwa";

    @Mock
    UserRepository userRepository;
    @Mock
    UserSearchContract.View view;

    UserSearchPresenter userSearchPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userSearchPresenter = new UserSearchPresenter(userRepository, Schedulers.immediate(),
                Schedulers.immediate());
        userSearchPresenter.attachView(view);
    }

    @Test
    public void search_ValidSearchTerm_ReturnsResults() {
        UsersList userList = getDummyUserList();
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.<List<User>>just(userList.getItems()));

        userSearchPresenter.search("moyheen");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).showSearchResults(userList.getItems());
        verify(view, never()).showError(anyString());
    }

    @Test
    public void search_UserRepositoryError_ErrorMsg() {
        String errorMsg = "No internet";
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.error(new IOException(errorMsg)));

        userSearchPresenter.search("yoruba");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view, never()).showSearchResults(anyList());
        verify(view).showError(errorMsg);
    }

    UsersList getDummyUserList() {
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user1FullDetails());
        githubUsers.add(user2FullDetails());
        UsersList usersList = new UsersList();
        usersList.setItems(githubUsers);
        return usersList;
    }

    private User user1FullDetails() {
        return new User(USER_LOGIN_MOYHEEN, "Moy Adeyemi", "avatar_url", "Bio1");
    }

    private User user2FullDetails() {
        return new User(USER_LOGIN_2_MOYHEEN, "Moyin Adeyemi", "avatar_url2", "Bio2");
    }

    @Test(expected = BasePresenter.MvpViewNotAttachedException.class)
    public void search_NotAttached_ThrowsMvpException() {
        userSearchPresenter.detachView();

        userSearchPresenter.search("test");

        verify(view, never()).showLoading();
        verify(view, never()).showSearchResults(anyList());
    }

}