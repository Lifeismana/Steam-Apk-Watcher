package com.valvesoftware.android.steam.community.fragment;

import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.valvesoftware.android.steam.community.AndroidUtils;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import com.valvesoftware.android.steam.community.model.UmqMessageType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/* loaded from: classes.dex */
public class ChatViewAdapter extends ArrayAdapter<UmqMessage> {
    private static UmqMessage chatPartnerIsTypingMessage;
    private final FragmentActivity activity;
    private final ChatFragment chatFragment;
    private String chatPartnerAvatarUrl;
    private ImageLoader imageLoader;
    private boolean m_bTyping;
    private LayoutInflater m_layoutInflater;
    private List<UmqMessage> m_list;
    private View.OnLongClickListener m_longClickHandler;
    private int m_numSecondsTimestamps;
    private final Comparator<UmqMessage> messageOrderComparator;

    public void setChatPartnerAvatarUrl(String str) {
        String str2 = this.chatPartnerAvatarUrl;
        if (str2 == null || !str2.equals(str)) {
            this.chatPartnerAvatarUrl = str;
            notifyDataSetChanged();
        }
    }

    public ChatViewAdapter(List<UmqMessage> list, LayoutInflater layoutInflater, final FragmentActivity fragmentActivity, ChatFragment chatFragment, String str) {
        super(fragmentActivity, -1, list);
        this.m_numSecondsTimestamps = 900;
        this.m_bTyping = false;
        this.m_longClickHandler = null;
        this.messageOrderComparator = new Comparator<UmqMessage>() { // from class: com.valvesoftware.android.steam.community.fragment.ChatViewAdapter.1
            private long maxTimeForSortingPurposes = 2147483647L;

            @Override // java.util.Comparator
            public int compare(UmqMessage umqMessage, UmqMessage umqMessage2) {
                return (int) ((umqMessage.utcTimeStamp == 0 ? this.maxTimeForSortingPurposes : umqMessage.utcTimeStamp) - (umqMessage2.utcTimeStamp == 0 ? this.maxTimeForSortingPurposes : umqMessage2.utcTimeStamp));
            }
        };
        Collections.sort(list, this.messageOrderComparator);
        this.chatFragment = chatFragment;
        this.chatPartnerAvatarUrl = str;
        this.m_list = list;
        this.m_layoutInflater = layoutInflater;
        this.activity = fragmentActivity;
        this.imageLoader = SteamCommunityApplication.GetInstance().imageLoader;
        this.m_longClickHandler = new View.OnLongClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatViewAdapter.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) fragmentActivity.getSystemService("clipboard");
                    if (clipboardManager != null && (view instanceof TextView)) {
                        clipboardManager.setText(((TextView) view).getText().toString());
                        int[] iArr = {0, 0};
                        view.getLocationOnScreen(iArr);
                        Toast makeText = Toast.makeText(fragmentActivity, R.string.notification_chat_copied, 0);
                        makeText.setGravity(49, 0, iArr[1]);
                        makeText.show();
                        return true;
                    }
                } catch (Exception unused) {
                }
                return false;
            }
        };
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        Collections.sort(this.m_list, this.messageOrderComparator);
        super.notifyDataSetChanged();
    }

    public void showIsTyping() {
        showIsTyping(true);
    }

    public void hideIsTyping() {
        showIsTyping(false);
    }

    private void showIsTyping(boolean z) {
        if (this.m_bTyping != z) {
            this.m_bTyping = z;
            if (this.m_bTyping) {
                this.m_list.add(getTypingMessage());
            } else {
                this.m_list.remove(getTypingMessage());
            }
            notifyDataSetChanged();
        }
    }

    public void attach(ListView listView) {
        listView.setAdapter((ListAdapter) this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatViewHolder chatViewHolder;
        DateFormat dateTimeInstance;
        final UmqMessage umqMessage = this.m_list.get(i);
        Object[] objArr = 0;
        if (view == null) {
            view = this.m_layoutInflater.inflate(R.layout.chat_simple_entry, (ViewGroup) null);
            chatViewHolder = new ChatViewHolder();
            chatViewHolder.chatPartnerLayoutContainer = view.findViewById(R.id.chat_partner_layout);
            chatViewHolder.loggedInUserLayoutContainer = view.findViewById(R.id.chat_logged_in_user_layout);
            chatViewHolder.chatPartnerAvatar = (NetworkImageView) view.findViewById(R.id.avatar_chat_partner);
            chatViewHolder.chatPartnerTextView = (TextView) view.findViewById(R.id.chat_text_chat_partner);
            chatViewHolder.loggedInUserTextView = (TextView) view.findViewById(R.id.chat_text_logged_in_user);
            chatViewHolder.extraPaddingView = view.findViewById(R.id.extra_padding);
            chatViewHolder.loggedInUserTimestampTextView = (TextView) view.findViewById(R.id.chat_text_logged_in_user_timestamp);
            chatViewHolder.chatPartnerTimestampTextView = (TextView) view.findViewById(R.id.chat_text_partner_timestamp);
            chatViewHolder.chatPartnerTextView.setOnLongClickListener(this.m_longClickHandler);
            chatViewHolder.sendErrorTextView = (TextView) view.findViewById(R.id.chat_text_send_error);
            chatViewHolder.umqMessage = umqMessage;
            view.setTag(chatViewHolder);
        } else {
            chatViewHolder = (ChatViewHolder) view.getTag();
        }
        if (System.currentTimeMillis() / 86400000 == umqMessage.getUtcTimeStampInMilliseconds() / 86400000) {
            dateTimeInstance = SimpleDateFormat.getTimeInstance(3);
        } else {
            dateTimeInstance = SimpleDateFormat.getDateTimeInstance(3, 3);
        }
        Date date = new Date(umqMessage.getUtcTimeStampInMilliseconds());
        String format = date.getTime() > 0 ? dateTimeInstance.format(date) : "";
        if (umqMessage.isIncoming) {
            chatViewHolder.loggedInUserLayoutContainer.setVisibility(8);
            chatViewHolder.chatPartnerLayoutContainer.setVisibility(0);
            chatViewHolder.chatPartnerAvatar.setImageUrl(this.chatPartnerAvatarUrl, this.imageLoader);
            FormatMessageText(umqMessage, chatViewHolder.chatPartnerTextView);
            if (umqMessage != getTypingMessage()) {
                chatViewHolder.chatPartnerTimestampTextView.setText(format);
                chatViewHolder.chatPartnerTimestampTextView.setVisibility(0);
            } else {
                chatViewHolder.chatPartnerTimestampTextView.setVisibility(8);
            }
        } else {
            chatViewHolder.chatPartnerLayoutContainer.setVisibility(8);
            chatViewHolder.loggedInUserLayoutContainer.setVisibility(0);
            FormatMessageText(umqMessage, chatViewHolder.loggedInUserTextView);
            if (!umqMessage.hadSendError) {
                chatViewHolder.loggedInUserTimestampTextView.setText(format);
                chatViewHolder.loggedInUserTimestampTextView.setVisibility(0);
                chatViewHolder.sendErrorTextView.setVisibility(8);
                chatViewHolder.loggedInUserTextView.setOnLongClickListener(this.m_longClickHandler);
            } else {
                View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.ChatViewAdapter.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        if (ChatViewAdapter.this.chatFragment != null) {
                            ChatViewAdapter.this.chatFragment.resendMessage(umqMessage);
                        }
                    }
                };
                chatViewHolder.loggedInUserLayoutContainer.setOnClickListener(onClickListener);
                chatViewHolder.loggedInUserTextView.setOnClickListener(onClickListener);
                chatViewHolder.sendErrorTextView.setOnClickListener(onClickListener);
                chatViewHolder.sendErrorTextView.setVisibility(0);
                chatViewHolder.loggedInUserTimestampTextView.setVisibility(8);
            }
        }
        UmqMessage umqMessage2 = i > 0 ? this.m_list.get(i - 1) : null;
        if (umqMessage2 != null && umqMessage2.isIncoming != umqMessage.isIncoming) {
            chatViewHolder.extraPaddingView.setVisibility(0);
        } else {
            chatViewHolder.extraPaddingView.setVisibility(8);
        }
        return view;
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        return getView(i, view, viewGroup);
    }

    private void FormatMessageText(UmqMessage umqMessage, TextView textView) {
        String str = null;
        try {
            if (this.m_numSecondsTimestamps <= 0) {
                str = SimpleDateFormat.getTimeInstance(3).format(new Date(umqMessage.getUtcTimeStampInMilliseconds())) + " : ";
            }
        } catch (Exception unused) {
        }
        try {
            SpannableString valueOf = str != null ? SpannableString.valueOf(str + umqMessage.text) : SpannableString.valueOf(umqMessage.text);
            Linkify.addLinks(valueOf, 15);
            Object[] spans = valueOf.getSpans(0, valueOf.length(), Object.class);
            if (spans != null && spans.length > 0) {
                boolean booleanValue = SteamCommunityApplication.GetInstance().GetSettingInfoDB().m_settingChatsAlertLinks.getBooleanValue(SteamCommunityApplication.GetInstance().getApplicationContext());
                for (Object obj : spans) {
                    int spanStart = valueOf.getSpanStart(obj);
                    int spanEnd = valueOf.getSpanEnd(obj);
                    int spanFlags = valueOf.getSpanFlags(obj);
                    if (obj instanceof URLSpan) {
                        valueOf.removeSpan(obj);
                        if (str != null && spanStart < str.length()) {
                            spanStart = str.length();
                        }
                        if (spanEnd > spanStart) {
                            valueOf.setSpan(new UnsafeClickableURL((URLSpan) obj, booleanValue && UrlChecker.isUrlUnsafe((URLSpan) obj), this.activity), spanStart, spanEnd, spanFlags);
                        }
                    } else if (str != null && spanStart < str.length()) {
                        int length = str.length();
                        valueOf.removeSpan(obj);
                        if (spanEnd > length) {
                            valueOf.setSpan(obj, length, spanEnd, spanFlags);
                        }
                    }
                }
            }
            try {
                textView.setText(valueOf);
                MovementMethod movementMethod = textView.getMovementMethod();
                if (movementMethod == null || !(movementMethod instanceof LinkMovementMethod)) {
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }
            } catch (Exception unused2) {
                textView.setText("");
            }
        } catch (Exception unused3) {
            if (str != null) {
                AndroidUtils.setTextViewText(textView, str + umqMessage.text);
                return;
            }
            AndroidUtils.setTextViewText(textView, umqMessage.text);
        }
    }

    private static UmqMessage getTypingMessage() {
        if (chatPartnerIsTypingMessage == null) {
            chatPartnerIsTypingMessage = new UmqMessage();
            UmqMessage umqMessage = chatPartnerIsTypingMessage;
            umqMessage.isIncoming = true;
            umqMessage.text = "...";
            umqMessage.type = UmqMessageType.TYPING;
            chatPartnerIsTypingMessage.utcTimeStamp = 2147483647L;
        }
        return chatPartnerIsTypingMessage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ChatViewHolder {
        public NetworkImageView chatPartnerAvatar;
        public View chatPartnerLayoutContainer;
        public TextView chatPartnerTextView;
        public TextView chatPartnerTimestampTextView;
        public View extraPaddingView;
        public View loggedInUserLayoutContainer;
        public TextView loggedInUserTextView;
        public TextView loggedInUserTimestampTextView;
        public TextView sendErrorTextView;
        public UmqMessage umqMessage;

        private ChatViewHolder() {
        }
    }
}
