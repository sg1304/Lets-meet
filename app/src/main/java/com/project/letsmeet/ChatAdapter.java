package com.project.letsmeet;

import android.content.Context;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayAdapter<Messages> {

    private Context context;
    private int resource;

    public ChatAdapter(Context context, int resource, List<Messages> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String textMessage = getItem(position).getMessage_text();
        String messageUser = getItem(position).getMessage_user();
        long msgTime = getItem(position).getTimeStamp();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource,parent,false);

        TextView message_text,message_user,timeStamp;
        message_text= (TextView) convertView.findViewById(R.id.chat_text_message);
        message_user= (TextView) convertView.findViewById(R.id.chat_user_message);
        timeStamp= (TextView) convertView.findViewById(R.id.chat_time_message);

        message_text.setText(textMessage);
        message_user.setText(messageUser);
        timeStamp.setText(DateFormat.format("dd-MM--yyyy (HH:mm:ss)",msgTime));
        return convertView;
    }
}