package com.example.pmisi.tumblr_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.view.View;

import java.io.InputStream;

public class WebService extends View {
    private long mMoviestart;
    private Movie mMovie;

    public WebService(Context context, InputStream stream) {
        super(context);
        mMovie = Movie.decodeStream(stream);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();

        if (mMoviestart == 0) {
            mMoviestart = now;
        }
        final int relTime = (int) ((now - mMoviestart) % mMovie.duration());
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 10, 10);
        this.invalidate();
    }

}