package massive_navigation.snapchat.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.massivenavigationnodes.NodeManager;

import java.util.ArrayList;

import massive_navigation.snapchat.MainActivity;
import massive_navigation.snapchat.R;
import massive_navigation.snapchat.Adapter.ChatAdapter;
import massive_navigation.snapchat.DataList.ChatList;

public class Chat extends Fragment {
    View view;
    ListView chatList;
    final ArrayList<ChatList> chatListArrayList = new ArrayList<>();

    public static Fragment create() {
        return new Chat();    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.chatlist, container, false);

        chatList = view.findViewById(R.id.chatList);
        chatListArrayList.add(new ChatList("US Bank", "Tap to navigate", R.drawable.usbank));
        chatListArrayList.add(new ChatList("Bookstore", "Tap to navigate", R.drawable.bookstore));
        chatListArrayList.add(new ChatList("Bookstore Stairs", "Tap to navigate", R.drawable.bookstore));
        chatListArrayList.add(new ChatList("Great Hall", "Tap to navigate", R.drawable.greathall));
        chatListArrayList.add(new ChatList("MultiCultural", "Tap to navigate", R.drawable.multiculturalcenter));
        chatListArrayList.add(new ChatList("E&W Doors", "Tap to navigate", R.drawable.westplaza));
        chatListArrayList.add(new ChatList("High Street Entrance", "Tap to navigate", R.drawable.westplaza));
        chatListArrayList.add(new ChatList("Information Center", "Tap to navigate", R.drawable.informationdesk));
        chatListArrayList.add(new ChatList("Multicultural Doors", "Tap to navigate", R.drawable.westplaza));

        chatListArrayList.add(new ChatList("West Plaza", "Tap to navigate", R.drawable.westplaza));
        chatListArrayList.add(new ChatList("Union Market", "Tap to navigate", R.drawable.unionmarket));
        chatListArrayList.add(new ChatList("Espress-oh", "Tap to navigate", R.drawable.espressooh));
        chatListArrayList.add(new ChatList("Woody's Tavern", "Tap to navigate", R.drawable.woodystavern));
        chatListArrayList.add(new ChatList("Brutus Buckeye Statue", "Tap to navigate", R.drawable.brutusstatue));
        chatListArrayList.add(new ChatList("Ben and Arlene Lounge", "Tap to navigate", R.drawable.benandarleenlounge));
        chatListArrayList.add(new ChatList("Sloopy's Diner", "Tap to navigate", R.drawable.sloopysdiner));
        chatListArrayList.add(new ChatList("Performance Hall", "Tap to navigate", R.drawable.performancehall));

        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatListArrayList);
        if (chatList != null) {
            chatList.setAdapter(chatAdapter);
        }
        chatList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            intent.putExtra("@destination", ((ChatList)adapterView.getItemAtPosition(i)).getName());
            startActivity(intent);
        });
        return view;


    }


}
