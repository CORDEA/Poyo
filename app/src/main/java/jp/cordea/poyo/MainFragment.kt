package jp.cordea.poyo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        slimeButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections
                    .actionMainFragmentToSlimeFragment()
            )
        }
        circleButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections
                    .actionMainFragmentToCircleFragment()
            )
        }
    }
}
