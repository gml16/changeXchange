package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import entity.changexchange.MainActivity;
import entity.changexchange.R;
import entity.changexchange.sendText;

import static android.support.v4.content.ContextCompat.startActivity;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);

        holder.title.setText(
                offer.getPoster_nickname()
                        + " is looking to buy "
                        + offer.getAmount() + " "
                        + offer.getBuying().toString()
                        + " for " + offer.getSelling().toString()
                        + " at "
                        + offer.getLocation().toString()
                        + "!"
        );

        new ExchangeRateTracker(holder.exchangeRate).execute(
                offer.getBuying().toString(),
                offer.getSelling().toString()
        );

//        holder.accept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, sendText.class));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView exchangeRate;
        Button accept;

        public OfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.offer_title);
            exchangeRate = itemView.findViewById(R.id.offer_rate);
            accept = itemView.findViewById(R.id.offer_accept);
        }
    }

}
