package eu.siacs.conversations.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eu.siacs.conversations.Config;
import eu.siacs.conversations.R;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.ui.ConversationsOverviewFragment;
import eu.siacs.conversations.ui.XmppActivity;
import eu.siacs.conversations.ui.adapter.ConversationAdapter;
import eu.siacs.conversations.ui.util.PendingActionHelper;
import eu.siacs.conversations.ui.util.PendingItem;
import eu.siacs.conversations.ui.util.ScrollState;

public class ChatFragment extends XmppFragmentnew {

    private static final String STATE_SCROLL_POSITION = ConversationsOverviewFragment.class.getName()+".scroll_state";

    private final List<Conversation> conversations = new ArrayList<>();
    private final PendingItem<Conversation> swipedConversation = new PendingItem<>();
    private final PendingItem<ScrollState> pendingScrollState = new PendingItem<>();
    private ConversationAdapter conversationsAdapter;
    private XmppActivity activity;
    private float mSwipeEscapeVelocity = 0f;
    private final PendingActionHelper pendingActionHelper = new PendingActionHelper();

    private RecyclerView list;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat, container, false);
        list=v.findViewById(R.id.list);
        return v;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.conversationsAdapter = new ConversationAdapter(this.activity, this.conversations);
        this.list.setAdapter(this.conversationsAdapter);
        this.list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
       // pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Log.d(Config.LOGTAG,"Chat.onAttach");
        if (activity instanceof XmppActivity) {
            this.activity = (XmppActivity) activity;
        } else {
            throw new IllegalStateException("Trying to attach fragment to activity that is not an XmppActivity");
        }
    }
    @Override
    public void onDestroyView() {
        Log.d(Config.LOGTAG,"Chat.onDestroyView()");
        super.onDestroyView();
        //this.binding = null;
        //this.conversationsAdapter = null;
        // this.touchHelper = null;
    }

    @Override
    public void onDestroy() {
        Log.d(Config.LOGTAG,"Chat.onDestroy()");
        super.onDestroy();

    }
    @Override
    public void onPause() {
        Log.d(Config.LOGTAG,"Chat.onPause()");
        pendingActionHelper.execute();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void checkdata(){
        this.conversationsAdapter.notifyDataSetChanged();
    }
    @Override
    public void refresh() {
        Log.d(Config.LOGTAG,"Chat.refresh()"+conversationsAdapter);
        //this.conversationsAdapter.notifyDataSetChanged();
        /*((XmppActivity)getActivity()).xmppConnectionService.populateWithOrderedConversations(this.conversations);
        this.conversationsAdapter.notifyDataSetChanged();*/
        if ( this.activity == null) {
            Log.d(Config.LOGTAG,"Chat.refresh() skipped updated because view binding or activity was null");
            return;
        }
        ((XmppActivity)getActivity()).xmppConnectionService.populateWithOrderedConversations(this.conversations);
        this.activity.xmppConnectionService.populateWithOrderedConversations(this.conversations);
        /*Conversation removed = this.swipedConversation.peek();
        if (removed != null) {
            if (removed.isRead()) {
                this.conversations.remove(removed);
            } else {
                pendingActionHelper.execute();
            }
        }*/
        this.conversationsAdapter.notifyDataSetChanged();
        ScrollState scrollState = pendingScrollState.pop();
        if (scrollState != null) {
            setScrollPosition(scrollState);
        }
    }

    @Override
    public void onBackendConnected() {
        Log.d(Config.LOGTAG,"Chat.refresh()------");
        refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        ScrollState scrollState = getScrollState();
        if (scrollState != null) {
            bundle.putParcelable(STATE_SCROLL_POSITION, scrollState);
        }
    }

    private ScrollState getScrollState() {
        if (list == null) {
            return null;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.list.getLayoutManager();
        if (layoutManager==null){
            return null;
        }
        int position = layoutManager.findFirstVisibleItemPosition();
        final View view = list.getChildAt(0);
        if (view != null) {
            return new ScrollState(position,view.getTop());
        } else {
            return new ScrollState(position, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Config.LOGTAG, "Chat.onStart()");
        if (activity.xmppConnectionService != null) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Config.LOGTAG, "Chat.onResume()");
    }

    private void setScrollPosition(ScrollState scrollPosition) {
        if (scrollPosition != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager)list.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset);
        }
    }


}
