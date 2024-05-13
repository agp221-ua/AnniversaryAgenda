package software.galaniberico.aniversaryagenda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar3)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.button).setOnClickListener {
            val text = this@NewEntry.findViewById<EditText>(R.id.newentry_date).text.toString()
            if (!Regex("""^\d{4}/\d{2}/\d{2}$""").matches(text)) {
                Toast.makeText(
                    this@NewEntry,
                    "Please enter a valid date (YYYY/MM/DD)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val values = text.split('/')

            val i = Intent()
            i.putExtra("name", this@NewEntry.findViewById<EditText>(R.id.newentry_name).text.toString())
            i.putExtra("year", values[0])
            i.putExtra("month", values[1])
            i.putExtra("day", values[2])
            setResult(RESULT_OK, i)
            finish()
        }
    }
}