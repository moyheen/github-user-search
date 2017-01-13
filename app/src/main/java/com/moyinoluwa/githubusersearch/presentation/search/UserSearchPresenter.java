package com.moyinoluwa.githubusersearch.presentation.search;

import com.moyinoluwa.githubusersearch.data.UserRepository;
import com.moyinoluwa.githubusersearch.data.remote.model.User;
import com.moyinoluwa.githubusersearch.presentation.base.BasePresenter;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by moyinoluwa on 1/10/17.
 */

public class UserSearchPresenter extends BasePresenter<UserSearchContract.View> implements
        UserSearchContract.Presenter {

    private final Scheduler mainScheduler, ioScheduler;
    private UserRepository userRepository;

    public UserSearchPresenter(UserRepository userRepository, Scheduler ioScheduler, Scheduler
            mainScheduler) {
        this.userRepository = userRepository;
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
    }

    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addSubscription(userRepository.searchUsers(term).subscribeOn(ioScheduler).observeOn
                (mainScheduler).subscribe(new Subscriber<List<User>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getView().hideLoading();
                getView().showError(e.getMessage());
            }

            @Override
            public void onNext(List<User> users) {
                getView().hideLoading();
                getView().showSearchResults(users);
            }
        }));
    }
}
