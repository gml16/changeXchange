package entity.changexchange.utils;

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

    public OfferAdapter(Context context, List<Offer> offers) {
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

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);

        holder.poster.setText(offer.getPoster_nickname());
        holder.buying.setText(offer.getBuying().toString());
        holder.selling.setText(offer.getSelling().toString());
        holder.amount.setText(String.valueOf(offer.getAmount()));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView poster;
        TextView buying;
        TextView selling;
        TextView amount;

        public OfferViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.textViewTitle);
            buying = itemView.findViewById(R.id.textViewShortDesc);
            selling = itemView.findViewById(R.id.textViewRating);
            amount = itemView.findViewById(R.id.textViewPrice);

        }
    }

}
