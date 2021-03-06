package eu.siacs.conversations.demo;


import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import eu.siacs.conversations.Config;
import eu.siacs.conversations.R;
import eu.siacs.conversations.databinding.ActivityAssignmentChatWeatherBinding;
import eu.siacs.conversations.demo.adapter.CustomViewPagerAdapter;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.services.XmppConnectionService;
import eu.siacs.conversations.ui.XmppActivity;
import eu.siacs.conversations.ui.interfaces.OnBackendConnected;
import eu.siacs.conversations.ui.interfaces.OnConversationArchived;
import eu.siacs.conversations.ui.interfaces.OnConversationRead;
import eu.siacs.conversations.ui.interfaces.OnConversationSelected;
import eu.siacs.conversations.ui.interfaces.OnConversationsListItemUpdated;
import eu.siacs.conversations.ui.util.ActivityResult;
import eu.siacs.conversations.ui.util.PendingItem;
import eu.siacs.conversations.utils.SignupUtils;
import eu.siacs.conversations.xmpp.Jid;
import eu.siacs.conversations.xmpp.OnUpdateBlocklist;

public class AssignmentChatWeatherActivity extends XmppActivity implements OnConversationSelected, OnConversationArchived, OnConversationsListItemUpdated, OnConversationRead, XmppConnectionService.OnAccountUpdate, XmppConnectionService.OnConversationUpdate, XmppConnectionService.OnRosterUpdate, OnUpdateBlocklist, XmppConnectionService.OnShowErrorToast, XmppConnectionService.OnAffiliationChanged {

    private ActivityAssignmentChatWeatherBinding binding;
    private CustomViewPagerAdapter viewPagerAdapter;


    private final PendingItem<Intent> pendingViewIntent = new PendingItem<>();
    private final PendingItem<ActivityResult> postponedActivityResult = new PendingItem<>();
    private final AtomicBoolean mRedirectInProcess = new AtomicBoolean(false);

    private boolean mActivityPaused = true;


    @Override
    protected void refreshUiReal() {

    }

    @Override
    public void onBackendConnected() {
        if (performRedirectIfNecessary(true)) {
            return;
        }
        xmppConnectionService.getNotificationService().setIsInForeground(true);

        Intent intent = pendingViewIntent.pop();

        notifyFragmentOfBackendConnected();
        if (intent != null) {

        }

        binding.startConversationViewPagerDemo.setCurrentItem(0);
       /* Fragment fragment = viewPagerAdapter.getItem(binding.startConversationViewPagerDemo.getCurrentItem());
        if (fragment instanceof ChatFragment) {
            ((ChatFragment) fragment).refresh();
        }*/
    }

    @Override
    protected void onStart() {
        final int theme = findTheme();
        if (this.mTheme != theme) {
            this.mSkipBackgroundBinding = true;
            recreate();
        } else {
            this.mSkipBackgroundBinding = false;
        }
        mRedirectInProcess.set(false);
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_assignment_chat_weather);
        setSupportActionBar(binding.toolbarDemo);
        configureActionBar(getSupportActionBar());
        viewPagerAdapter=new CustomViewPagerAdapter(getSupportFragmentManager());

        binding.startConversationViewPagerDemo.setAdapter(viewPagerAdapter);
        binding.startConversationViewPagerDemo.setSaveFromParentEnabled(false);
        binding.tabLayoutDemo.setupWithViewPager(binding.startConversationViewPagerDemo);

    }

    @Override
    public void onAffiliationChangedSuccessful(Jid jid) {
        Log.d(Config.LOGTARAM, "onAffiliationChangedSuccessful");
    }

    @Override
    public void onAffiliationChangeFailed(Jid jid, int resId) {
        Log.d(Config.LOGTARAM, "onConversationUpdate");
        displayToast(getString(resId, jid.asBareJid().toString()));
    }

    @Override
    public void onConversationUpdate() {
        Log.d(Config.LOGTARAM, "onConversationUpdate--------------------------"+binding.startConversationViewPagerDemo.getCurrentItem());
        if (performRedirectIfNecessary(false)) {
            return;
        }
        this.refreshUi();
        Fragment fragment = viewPagerAdapter.getItem(binding.startConversationViewPagerDemo.getCurrentItem());
        if (fragment instanceof ChatFragment) {
            ((ChatFragment) fragment).refresh();
        }

    }
    private void notifyFragmentOfBackendConnected() {
        final Fragment fragment = viewPagerAdapter.getItem(binding.startConversationViewPagerDemo.getCurrentItem());
        if (binding.startConversationViewPagerDemo.getCurrentItem()==0){
            if (fragment instanceof OnBackendConnected) {
                ((OnBackendConnected) fragment).onBackendConnected();
            }
        }

    }
    @Override
    public void onAccountUpdate() {
        Log.d(Config.LOGTARAM, "onAccountUpdate----------------------"+binding.startConversationViewPagerDemo.getCurrentItem());
        this.refreshUi();
        Fragment fragment = viewPagerAdapter.getItem(binding.startConversationViewPagerDemo.getCurrentItem());
        if (fragment instanceof FirstFragment) {
            ((FirstFragment) fragment).refresh();
        }
    }

    @Override
    public void onRosterUpdate() {
        Log.d(Config.LOGTARAM, "onRosterUpdate");
        this.refreshUi();
    }

    @Override
    public void onShowErrorToast(int resId) {
        Log.d(Config.LOGTARAM, "onShowErrorToast");
        runOnUiThread(() -> Toast.makeText(this, resId, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onConversationArchived(Conversation conversation) {
        Log.d(Config.LOGTARAM, "onConversationArchived");
    }

    @Override
    public void onConversationRead(Conversation conversation, String upToUuid) {
        Log.d(Config.LOGTARAM, "onConversationRead");
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        Log.d(Config.LOGTARAM, "onConversationSelected");
    }

    @Override
    public void onConversationsListItemUpdated() {
        Log.d(Config.LOGTARAM, "onConversationsListItemUpdated");
        Fragment fragment = viewPagerAdapter.getItem(binding.startConversationViewPagerDemo.getCurrentItem());
        if (fragment instanceof FirstFragment) {
            ((FirstFragment) fragment).refresh();
        }
    }

    @Override
    public void OnUpdateBlocklist(Status status) {
        Log.d(Config.LOGTARAM, "OnUpdateBlocklist");

            this.refreshUi();
    }
    private boolean performRedirectIfNecessary(boolean noAnimation) {
        return performRedirectIfNecessary(null, noAnimation);
    }
    private void displayToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(AssignmentChatWeatherActivity.this, msg, Toast.LENGTH_SHORT).show());
    }
    private boolean performRedirectIfNecessary(final Conversation ignore, final boolean noAnimation) {
        if (xmppConnectionService == null) {
            return false;
        }
        boolean isConversationsListEmpty = xmppConnectionService.isConversationsListEmpty(ignore);
        if (isConversationsListEmpty && mRedirectInProcess.compareAndSet(false, true)) {
            final Intent intent = SignupUtils.getRedirectionIntent(this);
            if (noAnimation) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
            runOnUiThread(() -> {
                startActivity(intent);
                if (noAnimation) {
                    overridePendingTransition(0, 0);
                }
            });
        }
        return mRedirectInProcess.get();
    }

    @Override
    public void onPause() {
        this.mActivityPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mActivityPaused = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(Config.LOGTARAM,"onActivityresult");
        ActivityResult activityResult = ActivityResult.of(requestCode, resultCode, data);
        if (xmppConnectionService != null) {
           // handleActivityResult(activityResult);
        } else {
            this.postponedActivityResult.push(activityResult);
        }
    }
}