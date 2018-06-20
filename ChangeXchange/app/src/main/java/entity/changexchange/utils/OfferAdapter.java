package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import entity.changexchange.ContactDetails;
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

        final String nickname = offer.getPoster_nickname();

        // In MainActivity clicking the name shows their profile, otherwise the interests.
        String title =
                nickname + " is selling " + offer.getAmount() + " "
                        + offer.getSelling().toString() + " at " + offer.getLocation().toString() + "!";
        if (!inMyOffers) {
            SpannableString click_title = new SpannableString(title);
            click_title.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    ctx.startActivity(new Intent(ctx, OtherProfile.class)
                            .putExtra("user", user)
                            .putExtra("nickname", offer.getPoster_nickname())
                            .putExtra("hide_contact", true));

                }
            }, 0, nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(click_title);
            holder.title.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.title.setText(title);
        }

        // Get exchange rate.
        new ExchangeRateTracker(holder.exchangeValue, offer.getAmount(), offer.getBuying()).execute(
                offer.getSelling().toString(),
                offer.getBuying().toString()
        );

        // Get note.
        holder.note.setText(offer.getNote());

        // Set hidden nickname of poster.
        holder.poster.setText(nickname);

        // Set user rating.
        new RequestDatabase(holder.rating, holder.num_rating, RATING).execute(
                "SELECT * FROM users WHERE nickname='" + nickname + "'; "
        );

        // Set buttons.
        holder.delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOffer(offer);
                        Context ctx = v.getContext();
                        ctx.startActivity(new Intent(ctx, MyOffers.class)
                                .putExtra("user", user));
                    }
                }
        );

        // Set visibility of different elements.
        if (inMyOffers) {
            holder.rating.setVisibility(View.GONE);
            holder.num_rating.setVisibility(View.GONE);
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
            ClickableSpan click = new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    ctx.startActivity(new Intent(ctx, ContactDetails.class)
                            .putExtra("user", user)
                            .putExtra("nickname", nickname)
                            .putExtra("offer", offer)
                            .putExtra("interest", "yes"));

                }
            };
            SpannableStringBuilder interestss = new SpannableStringBuilder("Interests: ");
            if (offer.getInterests().isEmpty()) {
                holder.interests.setText("No interests registered yet!");
            } else {
                for (String s : offer.getInterests()) {
                    int start = interestss.length();
                    interestss.append(s);
                    interestss.setSpan(click, start, start + s.length(), Spanned.SPAN_COMPOSING);
                    interestss.append(" - ");
                }
                holder.interests.setText(interestss);
                // TODO: Make each name clickable
                holder.interests.setMovementMethod(LinkMovementMethod.getInstance());
            }
        } else {
            holder.interests.setVisibility(View.GONE);
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
                        + "location='" + offer.getLocation() + "';"
        );
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout offer;
        TextView title;
        TextView exchangeValue;
        TextView note;
        TextView poster;
        TextView rating;
        TextView num_rating;
        TextView interests;
        FloatingActionButton delete;

        ImageView star;

        OfferViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.offer_title);
            exchangeValue = itemView.findViewById(R.id.offer_amount_recieve);
            note = itemView.findViewById(R.id.offer_note);
            poster = itemView.findViewById(R.id.offer_poster_hidden);
            rating = itemView.findViewById(R.id.offer_poster_rating);
            delete = itemView.findViewById(R.id.offer_delete);
            star = itemView.findViewById(R.id.offer_star);
            offer = itemView.findViewById(R.id.offer);
            num_rating = itemView.findViewById(R.id.offer_num_rat);
            interests = itemView.findViewById(R.id.offer_interests);
        }
    }
}
