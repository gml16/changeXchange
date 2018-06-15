package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import entity.changexchange.MyOffers;
import entity.changexchange.R;

public class MyOfferAdapter extends RecyclerView.Adapter<MyOfferAdapter.MyOfferViewHolder> {

    private Context context;
    private List<Offer> offers;

    public MyOfferAdapter(Context activity, List<Offer> offers) {
        this.context = activity;
        this.offers = offers;
    }

    @NonNull
    @Override
    public MyOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_offer_items, null);

        return new MyOfferViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyOfferViewHolder holder, int position) {
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

        // Set buttons.
        holder.accepted.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_offer(offer);
                    }
                }
        );
        holder.delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_offer(offer);
                    }
                }
        );
        holder.edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }

    private void delete_offer(Offer offer) {
        new RequestDatabase().execute(
                "DELETE * FROM offers WHERE "
                        + "nickname=" + offer.getPoster_nickname() + " and "
                        + "buying=" + offer.getBuying() + " and "
                        + "selling=" + offer.getSelling() + " and "
                        + "amount=" + offer.getAmount() + " and "
                        + "location=" + offer.getLocation() + ";"
        );
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class MyOfferViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView exchangeValue;
        TextView note;
        FloatingActionButton accepted;
        FloatingActionButton delete;
        FloatingActionButton edit;

        MyOfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.my_offer_title);
            exchangeValue = itemView.findViewById(R.id.my_offer_rate);
            note = itemView.findViewById(R.id.my_offer_note);
            accepted = itemView.findViewById(R.id.my_offer_accept);
            delete = itemView.findViewById(R.id.my_offer_delete);
            edit = itemView.findViewById(R.id.my_offer_edit);
        }
    }
}
