package com.moyinoluwa.githubusersearch.presentation.search;

import com.moyinoluwa.githubusersearch.data.remote.model.User;
import com.moyinoluwa.githubusersearch.presentation.base.MvpPresenter;
import com.moyinoluwa.githubusersearch.presentation.base.MvpView;

import java.util.List;

/**
 * Created by moyinoluwa on 1/10/17.
 */

public interface UserSearchContract {

    interface View extends MvpView {
        void showSearchResults(List<User> githubUserList);

        void showError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void search(String term);
    }
}
