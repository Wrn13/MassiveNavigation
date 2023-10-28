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
        chatListArrayList.add(new ChatList("West Plaza","Tap to navigate",R.drawable.avatar8));
        chatListArrayList.add(new ChatList("Union Market","Tap to navigate",R.drawable.avatar9));
        chatListArrayList.add(new ChatList("Espress-oh","Tap to navigate",R.drawable.avatar));
        chatListArrayList.add(new ChatList("Woody's Tavern","Tap to navigate",R.drawable.avatar3));
        chatListArrayList.add(new ChatList("Brutus Buckeye Statue","Tap to navigate",R.drawable.avatar6));
        chatListArrayList.add(new ChatList("Ben and Arlene Lounge","Tap to navigate",R.drawable.avatar4));
        chatListArrayList.add(new ChatList("Sloopy's Diner","Tap to navigate",R.drawable.avatar5));
        chatListArrayList.add(new ChatList("US Bank Conference Theater","Tap to navigate",R.drawable.avatar2));
        chatListArrayList.add(new ChatList("Performance Hall","Tap to navigate",R.drawable.avatar));
        chatListArrayList.add(new ChatList("Information Center","Tap to navigate",R.drawable.avatar8));
        chatListArrayList.add(new ChatList("Multi-Cultural Center","Tap to navigate",R.drawable.avatar9));
        chatListArrayList.add(new ChatList("Book Store","Tap to navigate",R.drawable.avatar));
        chatListArrayList.add(new ChatList("US Bank","Tap to navigate",R.drawable.avatar3));
        chatListArrayList.add(new ChatList("Great Hall Meeting Room","Tap to navigate",R.drawable.avatar6));

        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatListArrayList);
        if (chatList != null) {
            chatList.setAdapter(chatAdapter);
        }

        return view;


    }


}
