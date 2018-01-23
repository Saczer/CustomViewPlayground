package pl.olszak.michal.customview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = PagerAdapter(supportFragmentManager, getColors())
        indicator.attachDynamically(view_pager)
        rectangle.startAnimation()
    }

    private fun getColors(): List<String> {
        return listOf("#910505", "#74f441", "#f4bb41")
    }
}
