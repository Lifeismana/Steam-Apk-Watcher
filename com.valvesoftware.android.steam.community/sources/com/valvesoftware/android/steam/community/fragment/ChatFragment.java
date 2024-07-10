package com.valvesoftware.android.steam.community.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.valvesoftware.android.steam.community.ChatStateListener;
import com.valvesoftware.android.steam.community.LocalDb;
import com.valvesoftware.android.steam.community.LoggedInStatusChangedListener;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.NotificationSender;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.UmqCommunicator;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.jsontranslators.PersonaTranslator;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import com.valvesoftware.android.steam.community.views.SteamMenuItem;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ChatFragment extends Fragment {
    private View chatIsOfflineNotice;
    private String chatPartnerAvatarUrl;
    private String chatPartnerPersonaName;
    private String chatPartnerSteamId;
    private LocalDb localDb;
    private Toast toast;
    private UmqCommunicator umqCommunicator;
    private final List<UmqMessage> toBeSentMessages = new ArrayList();
    private final List<UmqMessage> successfullySentAndReceivedMessages = new ArrayList();
    private final List<UmqMessage> messageListForDisplay = new ArrayList();
    private ListView chatViewContents = null;
    private ChatViewAdapter chatViewAdapter = null;
    private EditText chatViewMessageTextBox = null;
    private boolean passwordWarningShown = false;
    private View.OnClickListener clearChatHistoryListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            new AlertDialog.Builder(ChatFragment.this.getActivity()).setTitle(R.string.Chat_clear_history).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.1.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ChatFragment.this.clearChatHistory();
                }
            }).setNegativeButton(android.R.string.no, (DialogInterface.OnClickListener) null).show();
        }
    };

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.chat_fragment, viewGroup, false);
        setupControls(inflate);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        if (ActivityHelper.fragmentIsActive(this)) {
            this.localDb = SteamCommunityApplication.GetInstance().getLocalDb();
            Bundle arguments = getArguments();
            if (arguments != null) {
                this.chatPartnerSteamId = arguments.getString("chatPartnerSteamIdKey") != null ? arguments.getString("chatPartnerSteamIdKey") : this.chatPartnerSteamId;
                this.chatPartnerAvatarUrl = arguments.getString("chatPartnerAvatarUrl") != null ? arguments.getString("chatPartnerAvatarUrl") : this.chatPartnerAvatarUrl;
                this.chatPartnerPersonaName = arguments.getString("chatPartnerPersonaNameKey") != null ? arguments.getString("chatPartnerPersonaNameKey") : this.chatPartnerPersonaName;
            }
            if (this.chatPartnerPersonaName == null || this.chatPartnerAvatarUrl == null) {
                loadChatParticipantInfo(this.chatPartnerSteamId);
            }
            this.toBeSentMessages.clear();
            showCompleteListOfMessagesAndToBeSentMessages();
            loadChats();
            this.umqCommunicator = UmqCommunicator.getInstance();
            this.umqCommunicator.setChatStateListener(new ChatStateListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.2
                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public boolean listenerWillHandleAllVisualChatNotifications() {
                    return false;
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void personaStateChanged(List<String> list) {
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void relationshipStateChanged(List<String> list) {
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public boolean listenerWillHandleVisualChatNotificationForSteamId(String str) {
                    return ActivityHelper.fragmentIsActive(ChatFragment.this) && str != null && str.equals(ChatFragment.this.chatPartnerSteamId);
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void newMessagesSaved(List<UmqMessage> list) {
                    if (ActivityHelper.fragmentIsActive(ChatFragment.this)) {
                        ChatFragment.this.appendToSuccessFullySentAndReceivedList(list);
                    }
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void messageSent(UmqMessage umqMessage) {
                    if (ActivityHelper.fragmentIsActive(ChatFragment.this)) {
                        ChatFragment.this.toBeSentMessages.remove(umqMessage);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(umqMessage);
                        ChatFragment.this.appendToSuccessFullySentAndReceivedList(arrayList);
                    }
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void messageSendFailed(UmqMessage umqMessage) {
                    umqMessage.hadSendError = true;
                    ChatViewAdapter chatViewAdapter = ChatFragment.this.getChatViewAdapter();
                    if (chatViewAdapter != null) {
                        chatViewAdapter.notifyDataSetChanged();
                    }
                }

                @Override // com.valvesoftware.android.steam.community.ChatStateListener
                public void isTypingMessageReceived(List<UmqMessage> list) {
                    Iterator<UmqMessage> it = list.iterator();
                    boolean z = false;
                    while (it.hasNext()) {
                        if (it.next().chatPartnerSteamId.equals(ChatFragment.this.chatPartnerSteamId)) {
                            z = true;
                        }
                    }
                    if (!z || ChatFragment.this.chatViewAdapter == null) {
                        return;
                    }
                    ChatFragment.this.chatViewAdapter.showIsTyping();
                    new Handler().postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ChatFragment.this.chatViewAdapter.hideIsTyping();
                        }
                    }, 4000L);
                }
            });
            this.umqCommunicator.updateChatMessages(this.chatPartnerSteamId);
            this.umqCommunicator.setChatLoggedInStatusChangedListener(new LoggedInStatusChangedListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.3
                @Override // com.valvesoftware.android.steam.community.LoggedInStatusChangedListener
                public void loggedOff() {
                    if (ActivityHelper.fragmentIsActive(ChatFragment.this) && ChatFragment.this.chatIsOfflineNotice != null) {
                        ChatFragment.this.chatIsOfflineNotice.setVisibility(0);
                    }
                }

                @Override // com.valvesoftware.android.steam.community.LoggedInStatusChangedListener
                public void loggedIn() {
                    if (ActivityHelper.fragmentIsActive(ChatFragment.this) && ChatFragment.this.chatIsOfflineNotice != null) {
                        ChatFragment.this.chatIsOfflineNotice.setVisibility(8);
                    }
                }
            });
            this.chatIsOfflineNotice.setVisibility(this.umqCommunicator.isLoggedInToChat() ? 8 : 0);
            if (!this.passwordWarningShown) {
                this.toast = Toast.makeText(getActivity(), R.string.Chat_Headline_Message, 1);
                this.toast.setGravity(49, 0, 110);
                this.toast.show();
                this.passwordWarningShown = true;
            }
            setupDeleteButton();
            setTitle();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onPause() {
        Toast toast = this.toast;
        if (toast != null) {
            toast.cancel();
        }
        NotificationSender.getInstance().clearRecentNotificationsTrackingFor(this.chatPartnerPersonaName);
        super.onPause();
    }

    private void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }

    private void setupControls(View view) {
        this.chatIsOfflineNotice = view.findViewById(R.id.chat_is_offline_notice);
        this.chatViewContents = (ListView) view.findViewById(R.id.chat_view_contents);
        this.chatViewContents.setTranscriptMode(2);
        this.chatViewContents.setStackFromBottom(true);
        this.chatViewMessageTextBox = (EditText) view.findViewById(R.id.chat_view_say_text);
        ((Button) view.findViewById(R.id.chat_view_say_button)).setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                ChatFragment.this.sendMessage();
            }
        });
        this.chatViewMessageTextBox.addTextChangedListener(new TextWatcher() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.5
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                ChatFragment.this.sendTypingNotification();
            }
        });
        this.chatViewMessageTextBox.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.6
            @Override // java.lang.Runnable
            public void run() {
                ChatFragment.this.chatViewMessageTextBox.requestFocusFromTouch();
            }
        });
    }

    public void resendMessage(UmqMessage umqMessage) {
        if (umqMessage == null) {
            return;
        }
        umqMessage.hadSendError = false;
        ChatViewAdapter chatViewAdapter = getChatViewAdapter();
        if (chatViewAdapter != null) {
            chatViewAdapter.notifyDataSetChanged();
        }
        this.umqCommunicator.sendMessage(umqMessage, this.chatPartnerSteamId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage() {
        UmqMessage typedMessage = getTypedMessage();
        clearMessageBox();
        if (typedMessage == null || typedMessage.isEmpty()) {
            return;
        }
        this.toBeSentMessages.add(typedMessage);
        showCompleteListOfMessagesAndToBeSentMessages();
        this.umqCommunicator.sendMessage(typedMessage, this.chatPartnerSteamId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ChatViewAdapter getChatViewAdapter() {
        FragmentActivity activity = getActivity();
        if (this.chatViewAdapter == null) {
            this.chatViewAdapter = new ChatViewAdapter(this.messageListForDisplay, activity.getLayoutInflater(), activity, this, this.chatPartnerAvatarUrl);
        }
        this.chatViewAdapter.attach(this.chatViewContents);
        return this.chatViewAdapter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void appendToSuccessFullySentAndReceivedList(List<UmqMessage> list) {
        if (list != null) {
            if (list.size() != 0) {
                for (UmqMessage umqMessage : list) {
                    if (umqMessage.chatPartnerSteamId != null && umqMessage.chatPartnerSteamId.equals(this.chatPartnerSteamId)) {
                        this.successfullySentAndReceivedMessages.add(umqMessage);
                    }
                }
                showCompleteListOfMessagesAndToBeSentMessages();
            }
        }
    }

    private void loadChats() {
        if (ActivityHelper.fragmentOrActivityIsActive(getActivity())) {
            final Handler handler = new Handler();
            SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.7
                @Override // java.lang.Runnable
                public void run() {
                    final List<UmqMessage> messages = ChatFragment.this.localDb.getMessages(LoggedInUserAccountInfo.getLoginSteamID(), ChatFragment.this.chatPartnerSteamId);
                    handler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.7.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ChatFragment.this.addMessagesReloadedFromDb(messages);
                        }
                    });
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void addMessagesReloadedFromDb(List<UmqMessage> list) {
        this.successfullySentAndReceivedMessages.clear();
        this.successfullySentAndReceivedMessages.addAll(list);
        showCompleteListOfMessagesAndToBeSentMessages();
    }

    private synchronized void showCompleteListOfMessagesAndToBeSentMessages() {
        HashSet hashSet = new HashSet();
        if (ActivityHelper.fragmentIsActive(this)) {
            hashSet.addAll(this.successfullySentAndReceivedMessages);
            hashSet.addAll(this.toBeSentMessages);
            this.messageListForDisplay.clear();
            this.messageListForDisplay.addAll(hashSet);
            getChatViewAdapter().notifyDataSetChanged();
            markMessagesRead();
        }
    }

    private void markMessagesRead() {
        SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.8
            @Override // java.lang.Runnable
            public void run() {
                ChatFragment.this.localDb.markMessagesRead(LoggedInUserAccountInfo.getLoginSteamID(), ChatFragment.this.chatPartnerSteamId);
            }
        });
        RequestBuilder markMessagesReadRequestBuilder = Endpoints.getMarkMessagesReadRequestBuilder(this.chatPartnerSteamId);
        markMessagesReadRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.9
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
            }
        });
        sendRequest(markMessagesReadRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTypingNotification() {
        UmqCommunicator umqCommunicator = this.umqCommunicator;
        if (umqCommunicator == null) {
            return;
        }
        umqCommunicator.sendTypingNotification(this.chatPartnerSteamId);
    }

    private void clearMessageBox() {
        this.chatViewMessageTextBox.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearChatHistory() {
        this.localDb.deleteMessages(LoggedInUserAccountInfo.getLoginSteamID(), this.chatPartnerSteamId);
        if (ActivityHelper.fragmentIsActive(this)) {
            this.messageListForDisplay.clear();
            this.successfullySentAndReceivedMessages.clear();
            getChatViewAdapter().notifyDataSetChanged();
        }
    }

    private UmqMessage getTypedMessage() {
        UmqMessage umqMessage = new UmqMessage();
        umqMessage.text = this.chatViewMessageTextBox.getText().toString().trim();
        umqMessage.chatPartnerSteamId = this.chatPartnerSteamId;
        return umqMessage;
    }

    private void setTitle(CharSequence charSequence) {
        if (charSequence != null && ActivityHelper.fragmentIsActive(this)) {
            getActivity().setTitle(charSequence);
        }
    }

    private void loadChatParticipantInfo(final String str) {
        if (str == null) {
            return;
        }
        RequestBuilder userSummaryRequestBuilder = Endpoints.getUserSummaryRequestBuilder(str);
        userSummaryRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatFragment.10
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                List<Persona> translateList = PersonaTranslator.translateList(jSONObject);
                if (translateList == null || translateList.size() == 0) {
                    return;
                }
                Persona persona = translateList.get(0);
                if (!str.equalsIgnoreCase(LoggedInUserAccountInfo.getLoginSteamID())) {
                    ChatFragment.this.chatPartnerAvatarUrl = persona.mediumAvatarUrl;
                    if (ActivityHelper.fragmentIsActive(ChatFragment.this)) {
                        ChatFragment.this.getChatViewAdapter().setChatPartnerAvatarUrl(persona.mediumAvatarUrl);
                    }
                    ChatFragment.this.chatPartnerPersonaName = persona.personaName;
                }
                ChatFragment.this.setTitle();
            }
        });
        sendRequest(userSummaryRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTitle() {
        if (((MainActivity) getActivity()) != null) {
            setTitle(this.chatPartnerPersonaName);
        }
    }

    private void setupDeleteButton() {
        MainActivity mainActivity = (MainActivity) getActivity();
        SteamMenuItem steamMenuItem = new SteamMenuItem();
        steamMenuItem.iconResourceId = R.drawable.ic_action_delete;
        steamMenuItem.onClickListener = this.clearChatHistoryListener;
        mainActivity.setExtraToolbarItem(steamMenuItem);
    }
}
