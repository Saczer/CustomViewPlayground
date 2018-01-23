package pl.olszak.michal.customview

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_page.*

/**
 * @author molszak
 *         created on 22.01.2018.
 */
class PagerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if(it.containsKey(KEY)){
                val color : String = arguments.getString(KEY)
                colorTxt.text = color
                container.setBackgroundColor(Color.parseColor(color))
            }
        }
    }

    companion object {
        const val KEY = "color"
    }
}