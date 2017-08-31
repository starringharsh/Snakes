package com.starringharsh.snakes.snakes.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.starringharsh.snakes.snakes.R;
import com.starringharsh.snakes.snakes.adapter.ElementAdapter;
import com.starringharsh.snakes.snakes.model.Element;
import com.starringharsh.snakes.snakes.utils.MethodUtils;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final int UP = 8;
    private final int DOWN = 5;
    private final int RIGHT = 6;
    private final int LEFT = 4;

    private List<Element> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ElementAdapter mAdapter;
    private int n = 15;
    private Activity activity;
    private Vibrator myVib;

    private int score;
    private int highestScore;
    private int direction;
    private int length;
    private int gameover = 0;
    private int headx;
    private int heady;
    private int x = 0;
    private int y = 0;
    private Element snake[] = new Element[30];
    private long timeInterval = 400;
    private int flag = 1;

    private TextView tvScore;
    private TextView tvHighScore;
    private LinearLayout llTop;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        activity = GameActivity.this;

        createGrid();

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        sharedPreferences = getSharedPreferences("Snakes", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recyclerView = (RecyclerView) findViewById(R.id.rvGameGrid);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvHighScore = (TextView) findViewById(R.id.tvHighScore);
        llTop = (LinearLayout) findViewById(R.id.llTop);

        mAdapter = new ElementAdapter(activity, elementList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), n, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        init();

        llTop.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this));

    }

    private void init() {
        gameover = 0;
        direction = RIGHT;
        score = 0;
        highestScore = sharedPreferences.getInt("Highest", 0);
        tvHighScore.setText("Highest: " + highestScore);
        length = 3;
        headx = n / 2;
        heady = n / 2;
        for (int i = 0; i < length; i++) {
            Element e = elementList.get((n / 2) * n + n / 2 - i);
            e.property = 1;
            snake[i] = e;
        }
        newCoin();
    }

    /**
     *
     */
    private void newCoin() {
        while (true) {
            int p = MethodUtils.randInt(0, n - 1);
            int q = MethodUtils.randInt(0, n - 1);
            Element e = elementList.get(q * n + p);
            if (e.property == 0) {
                x = p;
                y = q;
                e.property = 2;
                break;
            }
        }
    }

    void updateScore() {
        tvScore.setText("Score: " + score);
        if (score > highestScore) {
            tvScore.setTextColor(Color.GREEN);
            editor.putInt("Highest", score);
            editor.commit();
        }

    }

    /**
     *
     */
    Handler refreshHandler = new Handler();

    /**
     * This is a runnable thread which will be called from handler to refresh the game board periodically.
     */
    Runnable r = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Code to run on UI thread
//                    elementList.get(MethodUtils.randInt(0, 15 * 15)).property = 1;
                    move();
                    mAdapter.notifyDataSetChanged();
                    //if (gameover == 0)
                    if (snake[1].property == 1)
                        refreshHandler.postDelayed(r, timeInterval);
                }
            });
        }
    };

    /**
     * Algorithm of next move
     */
    private void move() {
        if (snake[length - 1] != null)
            snake[length - 1].property = 0;

        for (int i = length - 1; i > 0; i--) {
            snake[i] = snake[i - 1];
        }

        if (gameover == 0) {
            switch (direction) {
                case UP:
                    if (heady == 0) {
                        heady = n - 1;
                        snake[0] = elementList.get((n - 1) * n + headx);
                    } else {
                        heady = heady - 1;
                        snake[0] = elementList.get((heady) * n + headx);
                    }
                    break;
                case DOWN:
                    if (heady == n - 1) {
                        heady = 0;
                        snake[0] = elementList.get(headx);
                    } else {
                        heady = heady + 1;
                        snake[0] = elementList.get((heady) * n + headx);
                    }
                    break;
                case LEFT:
                    if (headx == 0) {
                        headx = n - 1;
                        snake[0] = elementList.get((heady) * n + headx);
                    } else {
                        headx = headx - 1;
                        snake[0] = elementList.get((heady) * n + headx);
                    }
                    break;
                case RIGHT:
                    if (headx == n - 1) {
                        headx = 0;
                        snake[0] = elementList.get((heady) * n);
                    } else {
                        headx = headx + 1;
                        snake[0] = elementList.get((heady) * n + headx);
                    }
                    break;
            }
            if (snake[0].property == 2) {
                myVib.vibrate(50);
                score++;
                length++;
                newCoin();
                snake[0].property = 1;
                updateScore();
            } else if (snake[0].property == 1) {
                gameover++;
                snake[0] = snake[1];
                timeInterval = 100;
            }
            snake[0].property = 1;
        }
        flag = 1;

    }


    private void createGrid() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                elementList.add(new Element(0));
            }
        }
    }

    public class OnSwipeTouchListener implements OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 150;
            private static final int SWIPE_VELOCITY_THRESHOLD = 150;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (flag == 1) {
                            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) {
                                    onSwipeRight();
                                } else {
                                    onSwipeLeft();
                                }
                                result = true;
                            }
                            flag = 0;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (flag == 1) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                            result = true;
                            flag = 0;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
            if (direction != LEFT)
                direction = RIGHT;
        }

        public void onSwipeLeft() {

            if (direction != RIGHT)
                direction = LEFT;
        }

        public void onSwipeTop() {

            if (direction != DOWN)
                direction = UP;
        }

        public void onSwipeBottom() {

            if (direction != UP)
                direction = DOWN;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHandler.postDelayed(r, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(r);
    }

}
