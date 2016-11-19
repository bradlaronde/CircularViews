package org.bradlaronde.circularviews;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class CircularViewActivity extends AppCompatActivity {
    private ViewGroup dots;
    private Handler handler;
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private int previousPosition;
    private String data[] = {
            "http://www.bodybuilding.com/images/2015/December/fap-icons-small-plans.png",
            "http://www.bodybuilding.com/images/2015/December/fap-icons-small-trainers.png",
            "http://www.bodybuilding.com/images/2015/December/fap-icons-small-lives.png",
            "http://www.bodybuilding.com/images/2015/December/fap-icons-small-apps.png",
            "http://www.bodybuilding.com/images/2015/December/fap-icons-small-free.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "How is my coding?", Snackbar.LENGTH_LONG)
                        .setAction("Give Feedback", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "bradlaronde@gmail.com", null));
                                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback about Circular Views");
                                i.putExtra(Intent.EXTRA_TEXT, "Be honest.");
                                startActivity(Intent.createChooser(i, "Send email..."));
                            }
                        }).show();
            }
        });
        dots = (ViewGroup) findViewById(R.id.dots);
        for (String d : data) {
            Picasso.with(this).load(d).fetch();
            ImageView v = new ImageView(this);
            v.setImageResource(d == data[0] ? R.drawable.dot_on : R.drawable.dot_off);
            v.setPadding(11, 11, 11, 11);
            dots.addView(v);
        }
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View v = findSnapView(layoutManager);
                if (v == null) return RecyclerView.NO_POSITION;
                int p = layoutManager.getPosition(v);
                int t = -1;
                if (layoutManager.canScrollHorizontally()) t = velocityX < 0 ? p - 1 : p + 1;
                if (layoutManager.canScrollVertically()) t = velocityY < 0 ? p - 1 : p + 1;
                return Math.min(layoutManager.getItemCount() - 1, Math.max(t, 0));
            }
        };
        snapHelper.attachToRecyclerView(recycler);
        RecyclerView.Adapter<?> adapter = new Adapter();
        recycler.setAdapter(adapter);
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int p = layoutManager.findFirstCompletelyVisibleItemPosition() % data.length;
                if (p < 0 || p == previousPosition) return;
                ((ImageView) dots.getChildAt(previousPosition)).setImageResource(R.drawable.dot_off);
                ((ImageView) dots.getChildAt(p)).setImageResource(R.drawable.dot_on);
                previousPosition = p;
                autoAdvance();
            }
        });
        layoutManager.scrollToPosition(Integer.MAX_VALUE / 2 / data.length * data.length);
        handler = new Handler();
        autoAdvance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_circular_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_call) {
            try {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "3153451522", null)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(CircularViewActivity.this, "Hold the phone!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void autoAdvance() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int p = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (p > 0) layoutManager.smoothScrollToPosition(recycler, null, p + 1);
            }
        }, 4444);
    }

    private class Holder extends RecyclerView.ViewHolder {
        private Holder() {
            super(new ImageView(CircularViewActivity.this));
            ImageView i = (ImageView) itemView;
            i.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder();
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final String d = data[position % data.length];
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CircularViewActivity.this, "Find a plan!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.bodybuilding.com/fun/find-a-plan.html")));
                }
            });
            Picasso.with(CircularViewActivity.this).load(d).into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }
}
