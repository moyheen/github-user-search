package com.moyinoluwa.githubusersearch.presentation.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by moyinoluwa on 1/10/17.
 */

public class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    private T view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        compositeSubscription.clear();
        view = null;
    }

    public T getView() {
        return view;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new MvpViewNotAttachedException();
        }
    }

    private boolean isViewAttached() {
        return view != null;
    }

    protected void addSubscription(Subscription subscription) {
        this.compositeSubscription.add(subscription);
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call presenter.attachView(MvpView) before requesting data from the " +
                    "Presenter");
        }
    }
}
