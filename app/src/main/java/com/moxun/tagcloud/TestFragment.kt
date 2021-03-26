package com.moxun.tagcloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moxun.tagcloudlib.view.TagCloudView

/**
 * A simple [Fragment] subclass.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment() {
    private var rootView: View? = null
    private var fragmentTagcloud: TagCloudView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_test, container, false)
        instantiationViews()
        val adapter = TextTagsAdapter(*arrayOfNulls(20))
        fragmentTagcloud!!.setAdapter(adapter)
        return rootView
    }

    private fun instantiationViews() {
        fragmentTagcloud = rootView!!.findViewById<View>(R.id.fragment_tagcloud) as TagCloudView
    }

    companion object {
        fun newInstance(): TestFragment {
            return TestFragment()
        }
    }
}