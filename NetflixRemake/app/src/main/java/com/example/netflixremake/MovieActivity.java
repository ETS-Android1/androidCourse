package com.example.netflixremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflixremake.model.Movie;
import com.example.netflixremake.model.MovieDetail;
import com.example.netflixremake.util.ImageDownloadTask;
import com.example.netflixremake.util.MovieDetailTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {
    RecyclerView rvSimilar;
    MovieSimilarAdapter movieSimilarAdapter;
    TextView textViewTitleCover;
    TextView textViewDesc;
    TextView textViewCast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        rvSimilar = findViewById(R.id.rv_similar);
        textViewTitleCover = findViewById(R.id.text_view_title_cover);
        textViewCast = findViewById(R.id.text_view_cast);
        textViewDesc = findViewById(R.id.text_view_desc);

        Toolbar toolbar = findViewById(R.id.toolbar);
        /*Foi necessario colocar manualmente o import: import android.widget.Toolbar;
        O setSupportActionBar tem uma compatibilidade maior com dispositivos antigos */
        setSupportActionBar(toolbar);

        //Verifica se o ActionBar foi adicionada
        if (getSupportActionBar() != null) {

            //Exibe o icone de home
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //Adiciona a seta para voltar
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

            //Oculta o titulo
            getSupportActionBar().setTitle(null);
        }

        //Metodo para manipular um drawble, nao foi usado apenas o getDrawble pois o ContextCompat tem metodos com mais compatibilidade a dispositivos antigos
        //Foi feito um cast para (LayerDrawable), pois o retorno do ContextCompat.getDrawable seria apenas um drawable
        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.shadows);

        //Verifica se eh diferente de null para nao dar nullpointer no codigo
        if (drawable != null) {
            Drawable movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4);

            //Acessando o item das sombras e trocando a sua propriedade
            drawable.setDrawableByLayerId(R.id.cover_drawable, movieCover);

            //Acessa o ImageView do movieItem.xlm e troca a imagem de fundo.
            ((ImageView)findViewById(R.id.image_view_cover_play)).setImageDrawable(drawable);
        }
        //Eh necessario passar uma lista de filmes, mesmo que vazia como construtor, porque o metodo getItemCount() da MovieSimilarAdapter sera chamado antes
        //de ser inserida a lista de filmes definitiva.
        List<Movie> movies = new ArrayList<>();
        movieSimilarAdapter = new MovieSimilarAdapter(movies);
        rvSimilar.setAdapter(movieSimilarAdapter);
        rvSimilar.setLayoutManager(new GridLayoutManager(this, 3));


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }


    }

    @Override
    public void onResult(MovieDetail movieDetail) {
        textViewDesc.setText(movieDetail.getMovie().getDesc());
        textViewCast.setText(movieDetail.getMovie().getCast());
        textViewTitleCover.setText(movieDetail.getMovie().getTitle());

        movieSimilarAdapter.setMovies(movieDetail.getMovieSimilar());
        //Notifica que os todos os dados que estavam sendo esperados já podem ser populados no movieSimilarAdapter
        movieSimilarAdapter.notifyDataSetChanged();

    }

    private class MovieSimilarHolder extends  RecyclerView.ViewHolder {
       private ImageView imageViewSimilarItem;

        public ImageView getImageViewSimilarItem() {
            return imageViewSimilarItem;
        }

        public MovieSimilarHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageViewSimilarItem = itemView.findViewById(R.id.image_view_cover_similar);
        }
    }

    private class MovieSimilarAdapter extends RecyclerView.Adapter<MovieSimilarHolder> {

        private List<Movie> movies;

        public MovieSimilarAdapter(List<Movie> movies) {
            this.movies = movies;
        }


        @NonNull
        @NotNull
        @Override
        public MovieSimilarHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new MovieSimilarHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MovieSimilarHolder holder, int position) {
          Movie movie = movies.get(position);
          new ImageDownloadTask(holder.getImageViewSimilarItem()).execute(movie.getCoverUrl());


        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        public void setMovies(List<Movie> movies) {
            this.movies.clear();
            this.movies.addAll(movies);
        }
    }
}