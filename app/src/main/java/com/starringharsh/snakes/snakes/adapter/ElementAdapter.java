package com.starringharsh.snakes.snakes.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starringharsh.snakes.snakes.model.Element;
import com.starringharsh.snakes.snakes.R;
import com.starringharsh.snakes.snakes.ui.GameActivity;
import com.starringharsh.snakes.snakes.utils.SquareLinearLayout;

import java.util.List;

/**
 * Created by user2 on 28-08-2017.
 */

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.MyViewHolder> {

    private List<Element> elementList;
    private Activity activity;

    public ElementAdapter(Activity activity, List<Element> elementList) {
        this.activity = activity;
        this.elementList = elementList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Element element = elementList.get(position);
        if (element.property==0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.llElement.setBackgroundColor(activity.getResources().getColor(R.color.elementBackground, null));
            } else {
                holder.llElement.setBackgroundColor(activity.getResources().getColor(R.color.elementBackground));
            }
        }
        else if (element.property==1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.llElement.setBackground(activity.getResources().getDrawable(R.drawable.body, null));
            } else {
                holder.llElement.setBackground(activity.getResources().getDrawable(R.drawable.body));
            }
        }
        else if (element.property==2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.llElement.setBackground(activity.getResources().getDrawable(R.drawable.coin, null));
            } else {
                holder.llElement.setBackground(activity.getResources().getDrawable(R.drawable.coin));
            }

            holder.llElement.animate()
                    .rotationY(0)
                    .rotationYBy(359)
                    .setDuration(400)
                    .start();

        }
    }


    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        SquareLinearLayout llElement;
        MyViewHolder(View view){
            super(view);
            llElement = (SquareLinearLayout) view.findViewById(R.id.llElement);
        }

    }
}
