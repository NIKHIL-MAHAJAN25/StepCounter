package com.nikhil.stepcounter

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.nikhil.stepcounter.databinding.FragmentHomeBinding
import com.nikhil.stepcounter.dataclasses.Steps
import kotlinx.coroutines.currentCoroutineContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(),SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepsensor:Sensor?=null
    private var totalsteps= 0f
    lateinit var binding: FragmentHomeBinding
    lateinit var stepsdata:Steps
    val db= Firebase.firestore
    var collectioname="Users"
    var subcollection="StepData"
    private var stepsAtStartOfDay = 0f
    private var currentDate = ""
    private var auth:FirebaseAuth=FirebaseAuth.getInstance()
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
        binding=FragmentHomeBinding.inflate(layoutInflater)
        currentDate = getCurrentDate()
        if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACTIVITY_RECOGNITION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),1)
        }
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepsensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        binding.stepProgress.max=100

       return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        stepsensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            totalsteps = event.values[0]
            val progress=((totalsteps/10000f)*100).toInt()
            val distance=totalsteps*0.8f
            val calories=totalsteps*0.05f
            val daily=10000
            val uid=auth.currentUser?.uid
            Log.e("","uid:${uid}")
            val curdate=getCurrentDate()
            if (curdate != currentDate) {
                save(currentDate, (totalsteps - stepsAtStartOfDay).toInt())
                stepsAtStartOfDay = totalsteps
                currentDate = curdate
            }
            val steps=Steps(date = curdate, totalsteps = totalsteps.toInt(), dailygoal = daily,calories=calories.toInt(),distance=distance.toInt())

            binding.stepProgress.progress=progress
            binding.stepCountText.text=totalsteps.toString()
            binding.tvdistance.text=distance.toString()
            binding.tvcalburned.text=calories.toString()
            db.collection(collectioname).document(uid!!).update("data",FieldValue.arrayUnion(steps)).addOnSuccessListener{
                Log.e("Firestore", "Steps data updated successfully")
            }
                .addOnFailureListener {
                    Log.e("Firestore", "Failed to update steps data")
                }
            Log.d("Steps", "Total steps: $totalsteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun save(date: String, steps: Int) {
        val uid = auth.currentUser?.uid ?: return
        val dailyGoal = 10000
        val distance = steps * 0.8f
        val calories = steps * 0.05f

        val stepData = Steps(
            date = date,
            totalsteps = steps,
            dailygoal = dailyGoal,
            calories = calories.toInt(),
            distance = distance.toInt()
        )

        db.collection(collectioname).document(uid).update("data", FieldValue.arrayUnion(stepData))
            .addOnSuccessListener {
                Log.e("Firestore", "Saved $steps steps for $date")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to save steps for $date")
            }
    }
}