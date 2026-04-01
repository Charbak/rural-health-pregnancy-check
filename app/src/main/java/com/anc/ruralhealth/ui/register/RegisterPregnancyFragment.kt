package com.anc.ruralhealth.ui.register

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    
    // Indian States
    private val indianStates = arrayOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal"
    )
    
    // Sample districts - in production, this would be loaded based on selected state
    private val sampleDistricts = arrayOf(
        "District 1", "District 2", "District 3", "District 4", "District 5"
    )
    
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
        // Auto-generate Patient ID
        val patientId = generatePatientId()
        binding.editPatientId.setText(patientId)
        binding.editPatientId.isEnabled = false // Make it read-only
        
        // Setup State dropdown
        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, indianStates)
        binding.spinnerState.setAdapter(stateAdapter)
        binding.spinnerState.setOnItemClickListener { _, _, position, _ ->
            // In production, load districts based on selected state
            val districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sampleDistricts)
            binding.spinnerDistrict.setAdapter(districtAdapter)
        }
        
        // Setup District dropdown
        val districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sampleDistricts)
        binding.spinnerDistrict.setAdapter(districtAdapter)
        
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
    
    private fun generatePatientId(): String {
        // Generate ID in format: PREG-YYYYMMDD-HHMMSS
        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "PREG-$timestamp"
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
        val state = binding.spinnerState.text.toString().trim()
        val district = binding.spinnerDistrict.text.toString().trim()
        val village = binding.editVillage.text.toString().trim()
        val pincode = binding.editPincode.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val age = binding.editAge.text.toString().trim()
        val hemoglobin = binding.editHemoglobin.text.toString().trim()
        
        // Validation
        if (patientName.isEmpty()) {
            binding.editPatientName.error = getString(R.string.error_required)
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
        
        if (state.isEmpty()) {
            Toast.makeText(context, "Please select a state", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (district.isEmpty()) {
            Toast.makeText(context, "Please select a district", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (village.isEmpty()) {
            binding.editVillage.error = getString(R.string.error_required)
            return
        }
        
        if (pincode.isEmpty() || pincode.length != 6) {
            binding.editPincode.error = "Please enter valid 6-digit pincode"
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
            state = state,
            district = district,
            village = village,
            pincode = pincode,
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
                val error = result.exceptionOrNull()
                Toast.makeText(
                    context,
                    "${getString(R.string.pregnancy_registered_error)}\n${error?.message}",
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


