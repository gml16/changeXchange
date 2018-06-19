package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import entity.changexchange.EditOffer;
import entity.changexchange.MyOffers;
import entity.changexchange.OfferInterests;
import entity.changexchange.OtherProfile;
import entity.changexchange.R;

import static entity.changexchange.utils.Util.RATING;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private User user;
    private Context context;
    private List<Offer> offers;
    private boolean inMyOffers;

    OfferAdapter(Context context, List<Offer> offers, User user) {
        this.context = context;
        this.offers = offers;
        this.user = user;
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

        String nickname = offer.getPoster_nickname();

        // In MainActivity clicking the name shows their profile, otherwise the interests.
        SpannableString click_title = new SpannableString(
                nickname + " is looking to buy " + offer.getAmount() + " "
                        + offer.getBuying().toString() + " at " + offer.getLocation().toString() + "!");
        click_title.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                if (inMyOffers) {
                    ctx.startActivity(new Intent(ctx, OfferInterests.class)
                            .putExtra("user", user)
                            .putExtra("offer", offer)
                    );
                } else {
                    ctx.startActivity(new Intent(ctx, OtherProfile.class)
                            .putExtra("user", user)
                            .putExtra("nickname", offer.getPoster_nickname())
                            .putExtra("hide_contact", true));
                }

            }
        }, 0, nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(click_title);
        holder.title.setMovementMethod(LinkMovementMethod.getInstance());

        // Get exchange rate.
        new ExchangeRateTracker(holder.exchangeValue, offer.getAmount(), offer.getSelling()).execute(
                offer.getBuying().toString(),
                offer.getSelling().toString()
        );

        // Get note.
        holder.note.setText(offer.getNote());

        // Set hidden nickname of poster.
        holder.poster.setText(nickname);

        // Set user rating.
        new RequestDatabase(holder.rating, RATING).execute(
                "SELECT * FROM users WHERE nickname='" + nickname + "'; "
        );

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
            holder.star.setVisibility(View.GONE);
            holder.offer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    ctx.startActivity(new Intent(ctx, EditOffer.class)
                            .putExtra("offer", offer)
                            .putExtra("user", user));
                }
            });
        } else {
            holder.accept.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    private void deleteOffer(Offer offer) {
        new RequestDatabase().execute(
                "DELETE FROM offers WHERE "
                        + "nickname='" + offer.getPoster_nickname() + "' and "
                        + "buying='" + offer.getBuying() + "' and "
                        + "selling='" + offer.getSelling() + "' and "
                        + "amount=" + offer.getAmount() + " and "
                        + "location=" + offer.getLocation() + ";"
        );
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout offer;
        TextView title;
        TextView exchangeValue;
        TextView note;
        TextView poster;
        TextView rating;
        FloatingActionButton accept;
        FloatingActionButton delete;

        ImageView star;

        OfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.offer_title);
            exchangeValue = itemView.findViewById(R.id.offer_amount_recieve);
            note = itemView.findViewById(R.id.offer_note);
            poster = itemView.findViewById(R.id.offer_poster_hidden);
            rating = itemView.findViewById(R.id.offer_poster_rating);
            accept = itemView.findViewById(R.id.offer_accept);
            delete = itemView.findViewById(R.id.offer_delete);
            star = itemView.findViewById(R.id.offer_star);
            offer = itemView.findViewById(R.id.offer);
        }
    }
}
