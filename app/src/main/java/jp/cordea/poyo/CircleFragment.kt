package jp.cordea.poyo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.circle_fragment.*

class CircleFragment : Fragment(R.layout.circle_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CircleViewAnimator(circleView).animate()
    }
}
