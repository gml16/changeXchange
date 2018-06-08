package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import entity.changexchange.R;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private Context context;
    private List<Offer> offers;

    OfferAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
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
        Offer offer = offers.get(position);

        // Get offer announcement.
        holder.title.setText(
                offer.getPoster_nickname()
                        + " is looking to buy "
                        + offer.getAmount() + " "
                        + offer.getBuying().toString()
//                        + " for " + offer.getSelling().toString()
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
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView exchangeValue;
        TextView note;

        OfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.offer_title);
            exchangeValue = itemView.findViewById(R.id.offer_rate);
            note = itemView.findViewById(R.id.offer_note);
        }
    }
}
