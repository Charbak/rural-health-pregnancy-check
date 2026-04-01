package com.anc.ruralhealth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anc.ruralhealth.R
import com.anc.ruralhealth.databinding.FragmentHomeBinding

/**
 * Home Fragment - Main screen showing upcoming visits and quick actions
 */
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState: Bundle?)
        
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // Setup RecyclerView for upcoming visits
        binding.recyclerUpcomingVisits.layoutManager = LinearLayoutManager(context)
        
        // Register pregnancy button
        binding.buttonRegisterPregnancy.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_register)
        }
    }
    
    private fun observeData() {
        // Observe upcoming visits
        viewModel.upcomingVisits.observe(viewLifecycleOwner) { visits ->
            // Update RecyclerView adapter
        }
        
        // Observe missed visits count
        viewModel.missedVisitsCount.observe(viewLifecycleOwner) { count ->
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Made with Bob
