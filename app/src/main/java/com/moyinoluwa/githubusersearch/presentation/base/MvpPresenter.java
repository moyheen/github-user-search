package com.moyinoluwa.githubusersearch.presentation.base;

/**
 * Created by moyinoluwa on 1/13/17.
 */

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
