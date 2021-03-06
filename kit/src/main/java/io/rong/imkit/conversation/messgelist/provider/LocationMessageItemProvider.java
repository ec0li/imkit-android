package io.rong.imkit.conversation.messgelist.provider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.feature.location.AMapPreviewActivity;
import io.rong.imkit.feature.location.AMapPreviewActivity2D;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.RCMessageFrameLayout;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imkit.widget.glide.RoundedCornersTransform;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.location.message.LocationMessage;

import static io.rong.imkit.conversation.messgelist.provider.SightMessageItemProvider.dip2pix;

public class LocationMessageItemProvider extends BaseMessageItemProvider<LocationMessage> {
    private static final String TAG = LocationMessageItemProvider.class.getSimpleName();

    public LocationMessageItemProvider() {
        mConfig.showReadState = true;
        mConfig.showContentBubble = false;
    }


    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_location_message, null);
        return new ViewHolder(parent.getContext(), view);
    }

    @Override
    protected void bindMessageContentViewHolder(ViewHolder holder, ViewHolder parentHolder, LocationMessage locationMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        final Uri uri = locationMessage.getImgUri();
        RLog.d(TAG, "uri = " + uri);
        ImageView img = holder.getView(R.id.rc_img);
        if (uri == null || !("file").equals(uri.getScheme())) {
            img.setImageResource(R.drawable.rc_ic_location_item_default);
        } else {
            int px = dip2pix(IMCenter.getInstance().getContext(), 8);
            RoundedCornersTransform roundedTransform = null;
            if (uiMessage.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND)) {
                roundedTransform = new RoundedCornersTransform(px, 0, px, px);
            } else {
                roundedTransform = new RoundedCornersTransform(0, px, px, px);
            }
            Glide.with(img)
                    .load(uri)
                    .transform(roundedTransform)
                    .error(R.drawable.rc_ic_location_item_default)
                    .placeholder(R.drawable.rc_ic_location_item_default)
                    .into(img);
        }
        TextView address = holder.getView(R.id.rc_location_content);
        address.setText(locationMessage.getPoi());
        RCMessageFrameLayout layout = holder.getView(R.id.rc_location);
//        if (uiMessage.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND)) {
//            layout.setBackgroundResource(R.drawable.rc_ic_bubble_right);
//        } else {
//            layout.setBackgroundResource(R.drawable.rc_ic_bubble_left);
//        }
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, LocationMessage locationMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        try {
            String clsName = "com.amap.api.netlocation.AMapNetworkLocationClient";
            Class<?> locationCls = Class.forName(clsName);
            Intent intent;
            Context context = holder.getContext();
            if (context.getResources().getBoolean(R.bool.rc_location_2D)) {
                intent = new Intent(context, AMapPreviewActivity2D.class);
            } else {
                intent = new Intent(context, AMapPreviewActivity.class);
            }
            intent.putExtra("location", locationMessage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            RLog.i(TAG, "Not default AMap Location");
            RLog.e(TAG, "onItemClick", e);
        }
        return false;
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof LocationMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, LocationMessage locationMessage) {
        return new SpannableString(context.getResources().getString(R.string.rc_message_content_location));
    }
}
