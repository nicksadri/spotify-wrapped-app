package com.example.spotifysdkimplementation;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifysdkimplementation.TopArtistPage;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;

    public static ArrayList<WrappedClass> getActualList() {
        return actualList;
    }

    private static ArrayList<WrappedClass> actualList;

    private int count = 0;

    public RecyclerViewAdapter(Context context, ArrayList<WrappedClass> actualList) {
        this.context = context;
        this.actualList = actualList;
        for (int i = 0; i < TopArtistPage.getPreviousTopArtists().size(); i++) {
            WrappedClass wrapped = new WrappedClass(TopArtistPage.getPreviousTopArtists().get(i), TopTrackPage.getPreviousTopTracks().get(i));
            actualList.add(wrapped);
            WrappedClass wrapped2 = new WrappedClass(TopArtistPage.getPreviousTopArtists2().get(i), TopTrackPage.getPreviousTopTracks2().get(i));
            actualList.add(wrapped2);
            WrappedClass wrapped3 = new WrappedClass(TopArtistPage.getPreviousTopArtists3().get(i), TopTrackPage.getPreviousTopTracks3().get(i));
            actualList.add(wrapped3);
            WrappedClass wrapped4 = new WrappedClass(TopArtistPage.getPreviousTopArtists4().get(i), TopTrackPage.getPreviousTopTracks4().get(i));
            actualList.add(wrapped4);
            WrappedClass wrapped5 = new WrappedClass(TopArtistPage.getPreviousTopArtists5().get(i), TopTrackPage.getPreviousTopTracks5().get(i));
            actualList.add(wrapped5);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView topArtist;
        private TextView topTrack;
        private TextView topArtist2;
        private TextView topTrack2;
        private TextView topArtist3;
        private TextView topTrack3;
        private TextView topArtist4;
        private TextView topTrack4;
        private TextView topArtist5;
        private TextView topTrack5;

        public ViewHolder(View view) {
            super(view);
            topArtist = view.findViewById(R.id.topArtist);
            topTrack = view.findViewById(R.id.topTrack);
            topArtist2 = view.findViewById(R.id.topArtist2);
            topTrack2 = view.findViewById(R.id.topTrack2);
            topArtist3 = view.findViewById(R.id.topArtist3);
            topTrack3 = view.findViewById(R.id.topTrack3);
            topArtist4 = view.findViewById(R.id.topArtist4);
            topTrack4 = view.findViewById(R.id.topTrack4);
            topArtist5 = view.findViewById(R.id.topArtist5);
            topTrack5 = view.findViewById(R.id.topTrack5);

        }

        public TextView getTopArtist() {
            return topArtist;
        }

        public TextView getTopTrack() {
            return topTrack;
        }
        public TextView getTopArtist2() {
            return topArtist2;
        }

        public TextView getTopTrack2() {
            return topTrack2;
        }
        public TextView getTopArtist3() {
            return topArtist3;
        }

        public TextView getTopTrack3() {
            return topTrack3;
        }
        public TextView getTopArtist4() {
            return topArtist4;
        }

        public TextView getTopTrack4() {
            return topTrack4;
        }
        public TextView getTopArtist5() {
            return topArtist5;
        }

        public TextView getTopTrack5() {
            return topTrack5;
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_row_layout, viewGroup, false);


        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        String artistText = actualList.get(position - count).getTopArtist();
        String trackText = actualList.get(position - count).getTopTrack();
        String artistText2 = actualList.get(position + 1 - count).getTopArtist();
        String trackText2 = actualList.get(position + 1 - count).getTopTrack();
        String artistText3 = actualList.get(position + 2 - count).getTopArtist();
        String trackText3 = actualList.get(position + 2 - count).getTopTrack();
        String artistText4 = actualList.get(position + 3 - count).getTopArtist();
        String trackText4 = actualList.get(position + 3 - count).getTopTrack();
        String artistText5 = actualList.get(position + 4 - count).getTopArtist();
        String trackText5 = actualList.get(position + 4 - count).getTopTrack();
        viewHolder.getTopArtist().setText(artistText);
        viewHolder.getTopTrack().setText(trackText);
        viewHolder.getTopArtist2().setText(artistText2);
        viewHolder.getTopTrack2().setText(trackText2);
        viewHolder.getTopArtist3().setText(artistText3);
        viewHolder.getTopTrack3().setText(trackText3);
        viewHolder.getTopArtist4().setText(artistText4);
        viewHolder.getTopTrack4().setText(trackText4);
        viewHolder.getTopArtist5().setText(artistText5);
        viewHolder.getTopTrack5().setText(trackText5);

        count++;
        if (count == 5) {
            count = 0;
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return actualList.size() / 5;
    }

}
