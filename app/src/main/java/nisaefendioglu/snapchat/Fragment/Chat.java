package nisaefendioglu.snapchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import nisaefendioglu.snapchat.R;
import nisaefendioglu.snapchat.Adapter.ChatAdapter;
import nisaefendioglu.snapchat.DataList.ChatList;

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
        chatListArrayList.add(new ChatList("West Plaza", "Tap to navigate", R.drawable.westplaza));
        chatListArrayList.add(new ChatList("Union Market", "Tap to navigate", R.drawable.unionmarket));
        chatListArrayList.add(new ChatList("Espress-oh", "Tap to navigate", R.drawable.espressooh));
        chatListArrayList.add(new ChatList("Woody's Tavern", "Tap to navigate", R.drawable.woodystavern));
        chatListArrayList.add(new ChatList("Brutus Buckeye Statue", "Tap to navigate", R.drawable.brutusstatue));
        chatListArrayList.add(new ChatList("Ben and Arlene Lounge", "Tap to navigate", R.drawable.benandarleenlounge));
        chatListArrayList.add(new ChatList("Sloopy's Diner", "Tap to navigate", R.drawable.sloopysdiner));
        chatListArrayList.add(new ChatList("US Bank Conference Theater", "Tap to navigate", R.drawable.usbankconferencetheater));
        chatListArrayList.add(new ChatList("Performance Hall", "Tap to navigate", R.drawable.performancehall));
        chatListArrayList.add(new ChatList("Information Center", "Tap to navigate", R.drawable.informationdesk));
        chatListArrayList.add(new ChatList("Multi-Cultural Center", "Tap to navigate", R.drawable.multiculturalcenter));
        chatListArrayList.add(new ChatList("Book Store", "Tap to navigate", R.drawable.bookstore));
        chatListArrayList.add(new ChatList("US Bank", "Tap to navigate", R.drawable.usbank));
        chatListArrayList.add(new ChatList("Great Hall Meeting Room", "Tap to navigate", R.drawable.greathall));

        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatListArrayList);
        if (chatList != null) {
            chatList.setAdapter(chatAdapter);
        }

        return view;


    }


}
