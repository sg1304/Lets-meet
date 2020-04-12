package com.project.letsmeet;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;

    public UserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String firstName = getItem(position).getFirstName();
        String lastName = getItem(position).getLastName();
        String email = getItem(position).getEmail();
        String image = getItem(position).getImage();

        User user = new User(firstName, lastName, email, image);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource,parent,false);

        CircleImageView myImage = (CircleImageView) convertView.findViewById(R.id.all_users_profile_image);
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile_image).into(myImage);
        TextView myName = (TextView) convertView.findViewById(R.id.all_users_name);
        myName.setText(user.getFirstName() + " " + user.getLastName());
        TextView myEmail = (TextView) convertView.findViewById(R.id.all_users_email);
        myEmail.setText(user.getEmail());
        return convertView;
    }
}