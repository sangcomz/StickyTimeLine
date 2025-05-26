package xyz.sangcomz.stickytimeline;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.github.sangcomz.stickytimeline.data.Singer;
import io.github.sangcomz.stickytimeline.data.SingerRepo;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.callback.SectionCallback;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class JavaExampleActivity extends AppCompatActivity {

    private Drawable icFinkl, icBuzz, icWannaOne, icGirlsGeneration, icSolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawable();

        TimeLineRecyclerView recyclerView = findViewById(R.id.vertical_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        List<Singer> singerList = getSingerList();

        recyclerView.addItemDecoration(getSectionCallback(singerList));

        recyclerView.setAdapter(new SingerAdapter(getLayoutInflater(), singerList, R.layout.recycler_vertical_row));
    }

    private SectionCallback getSectionCallback(final List<Singer> singerList) {
        return new SectionCallback() {

            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                Singer singer = singerList.get(position);
                Drawable dot;
                switch (singer.getGroup()) {
                    case "FIN.K.L": {
                        dot = icFinkl;
                        break;
                    }
                    case "Girls' Generation": {
                        dot = icGirlsGeneration;
                        break;
                    }
                    case "Buzz": {
                        dot = icBuzz;
                        break;
                    }
                    case "Wanna One": {
                        dot = icWannaOne;
                        break;
                    }
                    default: {
                        dot = icSolo;
                    }
                }
                return new SectionInfo(singer.getDebuted(), singer.getGroup(), dot);
            }

            @Override
            public boolean isSection(int position) {
                return !singerList.get(position).getDebuted().equals(singerList.get(position - 1).getDebuted());
            }
        };
    }

    private List<Singer> getSingerList() {
        return new SingerRepo().getSingerList();
    }

    private void initDrawable() {
        icFinkl = AppCompatResources.getDrawable(this, R.drawable.ic_finkl);
        icBuzz = AppCompatResources.getDrawable(this, R.drawable.ic_buzz);
        icWannaOne = AppCompatResources.getDrawable(this, R.drawable.ic_wannaone);
        icGirlsGeneration = AppCompatResources.getDrawable(this, R.drawable.ic_girlsgeneration);
        icSolo = AppCompatResources.getDrawable(this, R.drawable.ic_wannaone);
    }
}
