package com.cris.nvh.moviedb.ui.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;
import com.cris.nvh.moviedb.data.annotation.CategoryRequest;
import com.cris.nvh.moviedb.data.annotation.GenresKey;
import com.cris.nvh.moviedb.data.model.Genre;
import com.cris.nvh.moviedb.data.model.GenreResponse;
import com.cris.nvh.moviedb.data.model.Movie;
import com.cris.nvh.moviedb.data.model.MovieResponse;
import com.cris.nvh.moviedb.data.repository.MovieRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nvh
 * Contact: toiyeuthethao1997@gmail.com
 */

public class HomeViewModel extends BaseObservable {
    public final ObservableList<Movie> popularMoviesObservable;
    public final ObservableList<Movie> nowPlayingMoviesObservable;
    public final ObservableList<Movie> upComingMoviesObservable;
    public final ObservableList<Movie> topRateMoviesObservable;
    public final ObservableList<Movie> topTrendingMoviesObservable;
    public final ObservableList<Genre> genresObservable;
    public final ObservableList<Movie> genreMoviesObservable;
    public final ObservableList<ObservableList<Movie>> listCategoryMovies;
    public final ObservableList<String> categoryStringObservable;
    public final ObservableField<Genre> genreField;
    public final ObservableBoolean isLoadingSuccess;
    private static final int DEFAULT_PAGE = 1;
    private static final int FIRST_PAGE = 1;
    private static final int FIRST_INDEX = 0;
    private static final int LAST_INDEX = 5;
    public static final String TOP_RATED = "TOP RATE";
    public static final String NOW_PLAYING = "NOW PLAYING";
    public static final String POPULAR = "POPULAR";
    public static final String UPCOMING = "UPCOMING";
    private static final String TRENDING = "TRENDING";
    private MovieRepository mMovieRepository;
    private CompositeDisposable mCompositeDisposable;
    private HomeNavigator mNavigator;

    public HomeViewModel(HomeNavigator navigator, MovieRepository movieRepository) {
        mNavigator = navigator;
        mMovieRepository = movieRepository;
        genreField = new ObservableField<>();
        listCategoryMovies = new ObservableArrayList<>();
        categoryStringObservable = new ObservableArrayList<>();
        popularMoviesObservable = new ObservableArrayList<>();
        nowPlayingMoviesObservable = new ObservableArrayList<>();
        upComingMoviesObservable = new ObservableArrayList<>();
        topRateMoviesObservable = new ObservableArrayList<>();
        topTrendingMoviesObservable = new ObservableArrayList<>();
        genreMoviesObservable = new ObservableArrayList<>();
        genresObservable = new ObservableArrayList<>();
        isLoadingSuccess = new ObservableBoolean();
        mCompositeDisposable = new CompositeDisposable();
        initData();
    }

    public void dispose() {
        mCompositeDisposable.dispose();
    }

    public void onItemSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        genreField.set(genresObservable.get(position));
        loadGenreMovies();
    }

    public void onGenreClick(Genre genre) {
        mNavigator.startMoviesActivity(String.valueOf(genre.getId()), genre.getName(), CategoryRequest.GENRE);
    }

    public void onSearchClick() {
        mNavigator.startSearchActivity();
    }

    public void onFloatingButtonClick() {
        mNavigator.startMoviesActivity(null, TRENDING, CategoryRequest.TRENDING);
    }

    public void onFavoriteImageClick(Movie movie) {
        int movieId = movie.getId();
        if (mMovieRepository.isFavorite(movieId)) {
            mMovieRepository.deleteFromFavorite(movieId);
            return;
        }
        mMovieRepository.insertToFavorite(movie);
    }

    @Bindable
    public ObservableList<ObservableList<Movie>> getListCategoryMovies() {
        return listCategoryMovies;
    }

    public void updateFavoriteMovie() {
        notifyPropertyChanged(BR.listCategoryMovies);
    }

    private void initData() {
        loadTopTrendingMovies();
        loadPopularMovies();
        loadNowPlayingMovies();
        loadUpComingMovies();
        loadTopRateMovies();
        loadGenre();
    }

    private void loadTopRateMovies() {
        Disposable disposable = mMovieRepository.getMoviesByCategory(GenresKey.TOP_RATED, FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        topRateMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                        listCategoryMovies.add(topRateMoviesObservable);
                        categoryStringObservable.add(TOP_RATED);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void loadUpComingMovies() {
        Disposable disposable = mMovieRepository.getMoviesByCategory(GenresKey.UPCOMING, FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        upComingMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                        listCategoryMovies.add(upComingMoviesObservable);
                        categoryStringObservable.add(UPCOMING);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void loadNowPlayingMovies() {
        Disposable disposable = mMovieRepository.getMoviesByCategory(GenresKey.NOW_PLAYING, FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        nowPlayingMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                        listCategoryMovies.add(nowPlayingMoviesObservable);
                        categoryStringObservable.add(NOW_PLAYING);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }

                });
        mCompositeDisposable.add(disposable);
    }

    private void loadPopularMovies() {
        Disposable disposable = mMovieRepository.getMoviesByCategory(GenresKey.POPULAR, FIRST_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        popularMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                        listCategoryMovies.add(popularMoviesObservable);
                        categoryStringObservable.add(POPULAR);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void loadTopTrendingMovies() {
        Disposable disposable = mMovieRepository.getMoviesTrendingByDay()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        topTrendingMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void loadGenre() {
        Disposable disposable = mMovieRepository.getGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GenreResponse>() {
                    @Override
                    public void accept(GenreResponse genreResponse) throws Exception {
                        genresObservable.addAll(genreResponse.getGenres());
                        loadGenreMovies();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void loadGenreMovies() {
        Disposable disposable = mMovieRepository
                .getMoviesByGenre(String.valueOf(genreField.get().getId()), DEFAULT_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        genreMoviesObservable.clear();
                        genreMoviesObservable.addAll(movieResponse.getMovies().subList(FIRST_INDEX, LAST_INDEX));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleError(throwable.getMessage());
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void handleError(String message) {
    }
}
