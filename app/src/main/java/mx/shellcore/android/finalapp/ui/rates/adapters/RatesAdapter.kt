package mx.shellcore.android.finalapp.ui.rates.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_rates_item.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.models.Rate
import mx.shellcore.android.finalapp.utils.CirclerTransform
import mx.shellcore.android.finalapp.utils.inflate
import java.text.SimpleDateFormat
import java.util.*

class RatesAdapter(val items: List<Rate>) : RecyclerView.Adapter<RatesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(parent.inflate(R.layout.fragment_rates_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bind(items[position])

    override fun getItemCount()
            = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(rate: Rate) = with(itemView) {
            txtProfile.text = rate.text
            txtRatePunt.text = "${rate.rate}"
            txtCalendar.text = SimpleDateFormat("yyyy MM dd", Locale.US).format(rate.createdAt)
            Picasso.get()
                    .load(rate.profileImageUrl)
                    .resize(100, 100)
                    .centerCrop()
                    .transform(CirclerTransform())
                    .into(imgProfile)
        }
    }
}