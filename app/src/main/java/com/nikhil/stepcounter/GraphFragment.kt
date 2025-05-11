package com.nikhil.stepcounter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.nikhil.stepcounter.databinding.FragmentGraphBinding
import com.nikhil.stepcounter.dataclasses.Steps
import com.nikhil.stepcounter.dataclasses.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GraphFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GraphFragment : Fragment() {
    lateinit var binding: FragmentGraphBinding
    private var barChart: BarChart? = null
    val db = Firebase.firestore
    val collectioname = "Users"
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGraphBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid
        barChart = binding.barChart
        db.collection(collectioname).document(uid!!).addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshots != null && snapshots.exists()) {
                val user = snapshots.toObject(User::class.java)
                val stepsList = user?.data ?: arrayListOf()
                Log.e("Firestore", "Fetched steps list: $stepsList")
                updateChart(stepsList)
            } else {
                Log.e("Firestore", "No user data found.")
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GraphFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraphFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun updateChart(stepsList: List<Steps>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((index, stepData) in stepsList.withIndex()) {
            entries.add(BarEntry(index.toFloat(), stepData.totalsteps?.toFloat() ?: 0f))
            labels.add(stepData.date)
        }

        val dataSet = BarDataSet(entries, "Steps")


        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart?.data = barData
        barChart?.setFitBars(true)
        barChart?.invalidate()
        barChart?.axisLeft?.isEnabled = false
        barChart?.axisRight?.isEnabled = false
        barChart?.xAxis?.setDrawGridLines(false)
        barChart?.xAxis?.setDrawAxisLine(false)
        barChart?.axisLeft?.setDrawGridLines(false)
        barChart?.axisRight?.setDrawGridLines(false)
        barChart?.description?.isEnabled = false
        barChart?.legend?.isEnabled = false

        val xAxis = barChart?.xAxis
        xAxis?.isEnabled = true
        xAxis?.granularity = 1f
        xAxis?.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis?.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawLabels(true)
        xAxis?.textColor = ContextCompat.getColor(requireContext(), R.color.white)
        barChart?.setTouchEnabled(false)
    }

}