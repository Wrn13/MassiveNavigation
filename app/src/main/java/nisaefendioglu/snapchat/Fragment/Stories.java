package nisaefendioglu.snapchat.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import nisaefendioglu.snapchat.R;
import nisaefendioglu.snapchat.Adapter.StoriesAdapter;
import nisaefendioglu.snapchat.DataList.StoriesList;

public class Stories  extends Fragment {
    View view;
    ListView storyListView;
    final ArrayList<StoriesList> storiesLists = new ArrayList<>();
    ImageView storyAdd;
    Fragment fragment = null;

    public static Fragment create() {
        return new Stories();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.stories, container, false);
        storyAdd = view.findViewById(R.id.storyAdd);

        storyListView = view.findViewById(R.id.storyListView);
        storiesLists.add(new StoriesList("Thompson Library","Open 8am-10pm",R.drawable.thompson));
        storiesLists.add(new StoriesList("Timashev Family Music Building","Open 7:30am-9pm",R.drawable.timashev));
        storiesLists.add(new StoriesList("Ohio Stadium","Open: Varies.",R.drawable.ohiostadium));

        StoriesAdapter storiesAdapter = new StoriesAdapter(getActivity(), storiesLists);
        if (storyListView != null) {
            storyListView.setAdapter(storiesAdapter);
        }



        storyAdd.setOnClickListener(view -> fragment = new Camera());
        return view;


    }
}
