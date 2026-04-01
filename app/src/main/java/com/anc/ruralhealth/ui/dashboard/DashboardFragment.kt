package com.anc.ruralhealth.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.databinding.FragmentDashboardBinding
import com.anc.ruralhealth.utils.AuthManager

/**
 * Dashboard Fragment - Shows metrics and statistics
 */
class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: DashboardViewModel
    private lateinit var authManager: AuthManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val database = AppDatabase.getDatabase(requireContext())
        viewModel = DashboardViewModel(database)
        authManager = AuthManager(requireContext())
        
        observeData()
        loadDashboard()
    }
    
    private fun loadDashboard() {
        // Load dashboard based on user role
        val district = if (authManager.isDistrictAdmin()) {
            authManager.getDistrict()
        } else {
            null
        }
        
        viewModel.loadDashboardMetrics(district)
    }
    
    private fun observeData() {
        // Total pregnancies
        viewModel.totalPregnancies.observe(viewLifecycleOwner) { count ->
            binding.textTotalPregnancies.text = count.toString()
        }
        
        // High risk count
        viewModel.highRiskCount.observe(viewLifecycleOwner) { count ->
            binding.textHighRisk.text = count.toString()
        }
        
        // Defaulters (missed visits)
        viewModel.missedVisitsCount.observe(viewLifecycleOwner) { count ->
            binding.textDefaulters.text = count.toString()
        }
        
        // Compliance rate
        viewModel.complianceRate.observe(viewLifecycleOwner) { rate ->
            binding.textComplianceRate.text = String.format("%.1f%%", rate)
            binding.progressCompliance.progress = rate.toInt()
        }
        
        // Upcoming visits
        viewModel.upcomingVisitsCount.observe(viewLifecycleOwner) { count ->
            binding.textUpcomingVisits.text = count.toString()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


