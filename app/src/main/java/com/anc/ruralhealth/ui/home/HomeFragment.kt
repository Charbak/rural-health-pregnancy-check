package com.anc.ruralhealth.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anc.ruralhealth.R
import com.anc.ruralhealth.databinding.FragmentHomeBinding
import com.anc.ruralhealth.utils.TestDataHelper

/**
 * Home Fragment - Main screen showing upcoming visits and quick actions
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var visitAdapter: VisitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[HomeViewModel::class.java]

        setupUI()
        observeData()
    }

    private fun setupUI() {
        // Setup RecyclerView for upcoming visits
        visitAdapter = VisitAdapter()
        binding.recyclerUpcomingVisits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = visitAdapter
        }

        // Register pregnancy button
        binding.buttonRegisterPregnancy.setOnClickListener {
            findNavController().navigate(R.id.navigation_register)
        }

        // Add test data button
        binding.buttonAddTestData.setOnClickListener {
            addTestData()
        }
    }

    private fun observeData() {
        // Observe upcoming visits
        viewModel.upcomingVisits.observe(viewLifecycleOwner) { visits ->
            Log.d(TAG, "Upcoming visits updated: ${visits.size} visits")
            visitAdapter.submitList(visits)

            // Show empty state if no visits
            if (visits.isEmpty()) {
                Log.d(TAG, "No upcoming visits to display")
            }
        }

        // Observe missed visits count
        viewModel.missedVisitsCount.observe(viewLifecycleOwner) { count ->
            Log.d(TAG, "Missed visits count: $count")
            if (count > 0) {
                binding.cardMissedVisits.visibility = View.VISIBLE
                binding.textMissedCount.text = "$count missed visits"
            } else {
                binding.cardMissedVisits.visibility = View.GONE
            }
        }

        // Observe user name
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.textUserName.text = "Welcome, $name"
        }
    }

    /**
     * Add test pregnancy with near-term ANC visit
     */
    private fun addTestData() {
        Log.d(TAG, "Adding test data...")
        Toast.makeText(requireContext(), "Creating test pregnancy...", Toast.LENGTH_SHORT).show()

        TestDataHelper.createTestPregnancyAsync(
            context = requireContext(),
            scope = lifecycleScope,
            onSuccess = { pregnancyId ->
                Log.d(TAG, "Test pregnancy created: $pregnancyId")
                Toast.makeText(
                    requireContext(),
                    "Test pregnancy created! Check upcoming visits below.",
                    Toast.LENGTH_LONG
                ).show()

                // Refresh data
                viewModel.refreshData()
            },
            onError = { error ->
                Log.e(TAG, "Failed to create test pregnancy", error)
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ANC_HomeFragment"
    }
}


