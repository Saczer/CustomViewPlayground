package pl.olszak.michal.customview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * @author molszak
 *         created on 22.01.2018.
 */
class PagerAdapter constructor(fm: FragmentManager,
                               private val colors: List<String>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment: PagerFragment = PagerFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(PagerFragment.KEY, colors[position])

        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int = colors.size

}