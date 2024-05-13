package software.galaniberico.aniversaryagenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Entry (val name: String, val date: LocalDate){
    companion object {
        var dateFormat = "YYYY-MM-dd"
    }


    fun getView(li: LayoutInflater): View {
        val v = li.inflate(R.layout.entry, null)
        val card = ((v as ViewGroup).getChildAt(0) as ViewGroup)
        (card.getChildAt(0) as TextView).apply {
            text = name
        }
        (card.getChildAt(1) as TextView).apply {

            text = date.format(DateTimeFormatter.ofPattern(dateFormat))
        }
        return v
    }

    override fun toString(): String {
        return "$name###$date"
    }
}
