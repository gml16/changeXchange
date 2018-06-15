package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import entity.changexchange.MyOffers;
import entity.changexchange.R;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private Context context;
    private List<Offer> offers;
    private boolean inMyOffers;

    OfferAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
        this.inMyOffers = context instanceof MyOffers;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.offer_items, null);

        return new OfferViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final OfferViewHolder holder, int position) {
        final Offer offer = offers.get(position);

        // Get offer announcement.
        holder.title.setText(
                offer.getPoster_nickname()
                        + " is looking to buy "
                        + offer.getAmount() + " "
                        + offer.getBuying().toString()
                        + " at "
                        + offer.getLocation().toString()
                        + "!"
        );

        // Get exchange rate.
        new ExchangeRateTracker(holder.exchangeValue, offer.getAmount(), offer.getSelling()).execute(
                offer.getBuying().toString(),
                offer.getSelling().toString()
        );

        // Get note.
        holder.note.setText(offer.getNote());

        // Set hidden nickname of poster.
        holder.poster.setText(offer.getPoster_nickname());

        // Set user rating.
        holder.rating.setText("4.76/5");
//        new RequestDatabase(holder.rating).execute(
//                "SELECT * FROM users WHERE nickname=" + offer.getPoster_nickname() + "; "
//        );

        // Set buttons.
        holder.accept.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOffer(offer);

                    }
                }
        );
        holder.delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOffer(offer);
                    }
                }
        );

        // Set visibility of different elements.
        if (inMyOffers) {
            holder.rating.setVisibility(View.GONE);

        } else {
            holder.accept.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    private void deleteOffer(Offer offer) {
        new RequestDatabase().execute(
                "DELETE * FROM offers WHERE "
                        + "nickname=" + offer.getPoster_nickname() + " and "
                        + "buying=" + offer.getBuying() + " and "
                        + "selling=" + offer.getSelling() + " and "
                        + "amount=" + offer.getAmount() + " and "
                        + "location=" + offer.getLocation() + ";"
        );
    }

    public void editOffer (View view) {

    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView exchangeValue;
        TextView note;
        TextView poster;
        TextView rating;
        FloatingActionButton accept;
        FloatingActionButton delete;
        FloatingActionButton edit;

        OfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.offer_title);
            exchangeValue = itemView.findViewById(R.id.offer_rate);
            note = itemView.findViewById(R.id.offer_note);
            poster = itemView.findViewById(R.id.offer_poster_hidden);
            rating = itemView.findViewById(R.id.offer_poster_rating);
            accept = itemView.findViewById(R.id.offer_accept);
            delete = itemView.findViewById(R.id.offer_delete);
            edit = itemView.findViewById(R.id.offer_edit);
        }
    }
}
