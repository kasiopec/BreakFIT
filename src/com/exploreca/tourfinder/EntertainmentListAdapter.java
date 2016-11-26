package com.exploreca.tourfinder;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exploreca.tourfinder.model.Entertainment;

public class EntertainmentListAdapter extends ArrayAdapter<Entertainment> {
	Context context;
	List<Entertainment> entertainments;

	public EntertainmentListAdapter(Context context, List<Entertainment> tours) {
		super(context, android.R.id.content, tours);
		this.context = context;
		this.entertainments = tours;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.tour_detail, null);

		Entertainment tour = entertainments.get(position);

		TextView tv_title = (TextView) view.findViewById(R.id.titleText);
		tv_title.setText(tour.getTitle());

		TextView tv_desc = (TextView) view.findViewById(R.id.descText);
		tv_desc.setText(tour.getDescription());

		TextView tv_main_muscle = (TextView) view
				.findViewById(R.id.textView_main_muscle);
		tv_main_muscle.setText(tour.getPrice());

		ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
		int imageResource = context.getResources().getIdentifier(
				tour.getImage(), "drawable", context.getPackageName());
		if (imageResource != 0) {
			iv.setImageResource(imageResource);
		}

		return view;
	}

}