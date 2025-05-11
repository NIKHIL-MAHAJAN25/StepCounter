package com.nikhil.stepcounter

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.nikhil.stepcounter.databinding.FragmentAccountBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAccountBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db= Firebase.firestore
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
        binding=FragmentAccountBinding.inflate(layoutInflater)
        fetchdata()
        binding.btnLogout.setOnClickListener {
                auth.signOut()
            Toast.makeText(requireContext(), "Successfully logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HostActivity::class.java)

            startActivity(intent)
        }

        return binding.root
    }

    private fun fetchdata() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("Users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        binding.tvname.text = document.getString("name")
                        binding.tvage.text = "Age: ${document.getLong("age")?.toInt()}"
                        binding.tvgender.text = "Gender: ${document.getLong("gender")}"
                        binding.tvweight.text = "Weight: ${document.getDouble("weight")?.toInt() ?: 0} kg"
//                        binding.totalsteps.text = "${document.getLong("totalSteps") ?: 0}"


                        val gender = document.get("gender")
                        if (gender == 1) {
                            binding.genderimage.setImageResource(R.drawable.male)
                        } else {
                            binding.genderimage.setImageResource(R.drawable.female)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Profile data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}