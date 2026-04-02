package com.anc.ruralhealth.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anc.ruralhealth.R
import com.anc.ruralhealth.data.entity.ANCVisitEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying ANC visits in a RecyclerView
 * Shows visit cards with status color coding
 */
class VisitAdapter : RecyclerView.Adapter<VisitAdapter.VisitViewHolder>() {

    private var visits: List<ANCVisitEntity> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    /**
     * ViewHolder for visit items
     */
    class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusIndicator: View = itemView.findViewById(R.id.view_status_indicator)
        val visitType: TextView = itemView.findViewById(R.id.text_visit_type)
        val visitDescription: TextView = itemView.findViewById(R.id.text_visit_description)
        val scheduledDate: TextView = itemView.findViewById(R.id.text_scheduled_date)
        val gestationalRange: TextView = itemView.findViewById(R.id.text_gestational_range)
        val status: TextView = itemView.findViewById(R.id.text_status)
        val statusBadge: View = status.parent as View
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visit, parent, false)
        return VisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val visit = visits[position]
        val context = holder.itemView.context

        // Set visit details
        holder.visitType.text = visit.visitType

        // Set description based on visit type
        val description = when (visit.visitType) {
            "ANC1" -> "Confirmation and baseline screening"
            "ANC2" -> "PIH and anemia screening"
            "ANC3" -> "Multiple pregnancy exclusion"
            "ANC4" -> "Birth preparedness"
            else -> "Antenatal care visit"
        }
        holder.visitDescription.text = description

        // Set scheduled date
        holder.scheduledDate.text = "Scheduled: ${dateFormat.format(visit.scheduledDate)}"

        // Set gestational week range
        holder.gestationalRange.text = "Week ${visit.gestationalWeekMin}-${visit.gestationalWeekMax}"

        // Determine status and color
        val currentDate = Date()
        val (statusText, statusColor) = when {
            visit.isCompleted -> {
                Pair("✓ Completed",
                     ContextCompat.getColor(context, R.color.visit_completed))
            }
            visit.scheduledDate.before(currentDate) -> {
                Pair("⚠ Missed",
                     ContextCompat.getColor(context, R.color.visit_missed))
            }
            else -> {
                val daysUntil = ((visit.scheduledDate.time - currentDate.time) / (1000 * 60 * 60 * 24)).toInt()
                Pair("In $daysUntil days",
                     ContextCompat.getColor(context, R.color.visit_upcoming))
            }
        }

        // Set status text and colors
        holder.status.text = statusText
        holder.statusIndicator.setBackgroundColor(statusColor)
        holder.statusBadge.setBackgroundColor(statusColor)
    }

    override fun getItemCount(): Int = visits.size

    /**
     * Update the list of visits
     */
    fun submitList(newVisits: List<ANCVisitEntity>) {
        visits = newVisits
        notifyDataSetChanged()
    }
}
