package eu.siacs.conversations.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import eu.siacs.conversations.Config;
import eu.siacs.conversations.R;
import eu.siacs.conversations.databinding.FragmentConversationsOverviewBinding;
import eu.siacs.conversations.databinding.FragmentFirstBinding;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.ui.ConversationsOverviewFragment;
import eu.siacs.conversations.ui.StartConversationActivity;
import eu.siacs.conversations.ui.XmppActivity;
import eu.siacs.conversations.ui.adapter.ConversationAdapter;
import eu.siacs.conversations.ui.interfaces.OnConversationSelected;
import eu.siacs.conversations.ui.util.PendingActionHelper;
import eu.siacs.conversations.ui.util.PendingItem;
import eu.siacs.conversations.ui.util.ScrollState;

public class FirstFragment extends XmppFragmentnew {
    private static final String STATE_SCROLL_POSITION = ConversationsOverviewFragment.class.getName()+".scroll_state";

    private final List<Conversation> conversations = new ArrayList<>();
    private final PendingItem<Conversation> swipedConversation = new PendingItem<>();
    private final PendingItem<ScrollState> pendingScrollState = new PendingItem<>();
    private FragmentFirstBinding binding;
    private ConversationAdapter conversationsAdapter;
    private XmppActivity activity;
    private float mSwipeEscapeVelocity = 0f;
    private final PendingActionHelper pendingActionHelper = new PendingActionHelper();

    public static Fragment newInstance() {
        return new FirstFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(Config.LOGTAG,"FirstFragment.view");
        // Inflate the layout for this fragment
        this.mSwipeEscapeVelocity = getResources().getDimension(R.dimen.swipe_escape_velocity);
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false);
        //this.binding.fab.setOnClickListener((view) -> StartConversationActivity.launch(getActivity()));

        this.conversationsAdapter = new ConversationAdapter(this.activity, this.conversations);
        this.conversationsAdapter.setConversationClickListener((view, conversation) -> {
            if (activity instanceof OnConversationSelected) {
                ((OnConversationSelected) activity).onConversationSelected(conversation);
            } else {
                Log.w(FirstFragment.class.getCanonicalName(), "Activity does not implement OnConversationSelected");
            }
        });
        this.binding.list.setAdapter(this.conversationsAdapter);
        this.binding.list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
      //  this.touchHelper = new ItemTouchHelper(this.callback);
       //this.touchHelper.attachToRecyclerView(this.binding.list);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Log.d(Config.LOGTAG,"FirstFragment.onAttach");
        if (activity instanceof XmppActivity) {
            this.activity = (XmppActivity) activity;
        } else {
            throw new IllegalStateException("Trying to attach fragment to activity that is not an XmppActivity");
        }
    }
    @Override
    public void onDestroyView() {
        Log.d(Config.LOGTAG,"FirstFragment.onDestroyView()");
        super.onDestroyView();
      //  this.binding = null;
        //this.conversationsAdapter = null;
       // this.touchHelper = null;
    }

    @Override
    public void onDestroy() {
        Log.d(Config.LOGTAG,"FirstFragment.onDestroy()");
        super.onDestroy();

    }
    @Override
    public void onPause() {
        Log.d(Config.LOGTAG,"FirstFragment.onPause()");
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
        Log.d(Config.LOGTAG,"FirstFragment.refresh()"+conversationsAdapter);
        //this.conversationsAdapter.notifyDataSetChanged();
        /*((XmppActivity)getActivity()).xmppConnectionService.populateWithOrderedConversations(this.conversations);
        this.conversationsAdapter.notifyDataSetChanged();*/
        if (this.binding == null || this.activity == null) {
            Log.d(Config.LOGTAG,"FirstFragment.refresh() skipped updated because view binding or activity was null");
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
        Log.d(Config.LOGTAG,"FirstFragment.refresh()------");
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
        if (this.binding == null) {
            return null;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.binding.list.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        final View view = this.binding.list.getChildAt(0);
        if (view != null) {
            return new ScrollState(position,view.getTop());
        } else {
            return new ScrollState(position, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Config.LOGTAG, "FirstFragment.onStart()");
        if (activity.xmppConnectionService != null) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Config.LOGTAG, "FirstFragment.onResume()");
    }

    private void setScrollPosition(ScrollState scrollPosition) {
        if (scrollPosition != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) binding.list.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset);
        }
    }
}