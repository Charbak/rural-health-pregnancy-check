package com.anc.ruralhealth.ui.register

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.anc.ruralhealth.R
import com.anc.ruralhealth.databinding.FragmentRegisterPregnancyBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Register Pregnancy Fragment - Form to register a new pregnancy
 */
class RegisterPregnancyFragment : Fragment() {
    
    private var _binding: FragmentRegisterPregnancyBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: RegisterPregnancyViewModel
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPregnancyBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[RegisterPregnancyViewModel::class.java]
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // LMP Date Picker
        binding.editLmpDate.setOnClickListener {
            showDatePicker()
        }
        
        // Register button
        binding.buttonRegister.setOnClickListener {
            registerPregnancy()
        }
        
        // Cancel button
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.editLmpDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set max date to today
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        
        // Set min date to 280 days ago (typical pregnancy duration)
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.DAY_OF_YEAR, -280)
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis
        
        datePickerDialog.show()
    }
    
    private fun registerPregnancy() {
        val patientName = binding.editPatientName.text.toString().trim()
        val patientId = binding.editPatientId.text.toString().trim()
        val lmpDate = binding.editLmpDate.text.toString().trim()
        val phoneNumber = binding.editPhoneNumber.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val age = binding.editAge.text.toString().trim()
        val hemoglobin = binding.editHemoglobin.text.toString().trim()
        
        // Validation
        if (patientName.isEmpty()) {
            binding.editPatientName.error = getString(R.string.error_required)
            return
        }
        
        if (patientId.isEmpty()) {
            binding.editPatientId.error = getString(R.string.error_required)
            return
        }
        
        if (lmpDate.isEmpty()) {
            binding.editLmpDate.error = getString(R.string.error_required)
            return
        }
        
        if (phoneNumber.isEmpty()) {
            binding.editPhoneNumber.error = getString(R.string.error_required)
            return
        }
        
        if (age.isEmpty()) {
            binding.editAge.error = getString(R.string.error_required)
            return
        }
        
        // Show loading
        binding.buttonRegister.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        
        // Register pregnancy
        viewModel.registerPregnancy(
            patientName = patientName,
            patientId = patientId,
            lmpDate = calendar.time,
            phoneNumber = phoneNumber,
            address = address,
            age = age.toIntOrNull() ?: 0,
            hemoglobin = hemoglobin.toDoubleOrNull()
        )
    }
    
    private fun observeData() {
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            binding.buttonRegister.isEnabled = true
            binding.progressBar.visibility = View.GONE
            
            if (result.isSuccess) {
                Toast.makeText(
                    context,
                    getString(R.string.pregnancy_registered_success),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.pregnancy_registered_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Made with Bob