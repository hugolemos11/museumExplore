package com.example.museumexplore.ui.dialog
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.CardFilterDialogBinding
import com.example.museumexplore.modules.Category
import com.mapbox.android.gestures.Utils.dpToPx
import kotlinx.coroutines.launch

class CardFilterDialog : DialogFragment() {
    private var _binding: CardFilterDialogBinding? = null
    private val binding get() = _binding!!

    private var museumId: String? = null
    private var categoryId: String? = null


    private var categoriesList = arrayListOf<Category>()

    var onResultCallback: ((String?) -> Unit)? = null

    companion object {
        fun newInstance(bundle: Bundle): CardFilterDialog {
            val fragment = CardFilterDialog()
            fragment.arguments = bundle
            return fragment
        }

        fun show(
            fm: FragmentManager,
            bundle: Bundle,
            onResultCallback: (String?) -> Unit
        ): CardFilterDialog {
            val dialog = newInstance(bundle)
            dialog.show(fm, "fragment_card_filter_dialog")
            dialog.onResultCallback = onResultCallback
            return dialog
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CardFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            museumId = bundle.getString("museumId")
            categoryId = bundle.getString("categoryId")
        }

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)

        loadCategoriesAndPopulateRadioGroup(radioGroup)

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                museumId?.let { currentMuseumId ->
                    appDatabase.categoryDao().getAll(currentMuseumId).observe(viewLifecycleOwner) {
                        categoriesList = it as ArrayList<Category>
                        loadCategoriesAndPopulateRadioGroup(radioGroup)
                    }
                }
            }
        }

        binding.buttonFilter.setOnClickListener {
            val checkedRadioButtonId = radioGroup.checkedRadioButtonId
            val checkedRadioButton: RadioButton = binding.root.findViewById(checkedRadioButtonId)

            val checkedData: String = checkedRadioButton.tag.toString()

            onResultCallback?.invoke(checkedData)
            dismiss()
        }
    }

    @SuppressLint("ResourceType")
    private fun loadCategoriesAndPopulateRadioGroup(radioGroup: RadioGroup) {

        val textStyleResId = R.style.Theme_DescriptionTitle

        val textColorList = ContextCompat.getColorStateList(requireContext(), R.drawable.custom_radio_button_text_color)

        // Check if the "All" RadioButton already exists in the RadioGroup
        val allRadioButton = radioGroup.findViewById<RadioButton>(R.id. allRadioButton)
        if (allRadioButton == null) {
            val newAllRadioButton = RadioButton(requireContext())
            newAllRadioButton.text = "All"
            newAllRadioButton.id = R.id.allRadioButton
            newAllRadioButton.tag = "All"
            newAllRadioButton.buttonDrawable = null
            newAllRadioButton.setTextAppearance(textStyleResId)
            newAllRadioButton.setTextColor(textColorList)
            newAllRadioButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.custom_radio_button_background)

            // Set layout parameters for the "All" RadioButton
            val allLayoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dpToPx(10F).toInt()
                topMargin = dpToPx(5F).toInt()
                marginEnd = dpToPx(10F).toInt()
            }

            val paddingStart = dpToPx(10F).toInt()
            newAllRadioButton.setPadding(paddingStart, 0, 0, 0)

            newAllRadioButton.layoutParams = allLayoutParams

            if ("All" == categoryId) {
                newAllRadioButton.isChecked = true
            }

            // Add the "All" RadioButton to the RadioGroup at the beginning
            radioGroup.addView(newAllRadioButton, 0)
        }

        // Populate RadioGroup with categories
        for (category in categoriesList) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = category.descritpion
            radioButton.id = View.generateViewId()
            radioButton.tag = category.id
            radioButton.buttonDrawable = null
            radioButton.setTextAppearance(textStyleResId)
            radioButton.setTextColor(textColorList)
            radioButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.custom_radio_button_background)

            // Set layout parameters to add margin to the start
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dpToPx(10F).toInt()
                topMargin = dpToPx(5F).toInt()
                bottomMargin = dpToPx(5F).toInt()
                marginEnd = dpToPx(10F).toInt()
            }

            val paddingStart = dpToPx(10F).toInt()
            radioButton.setPadding(paddingStart, 0, 0, 0)

            radioButton.layoutParams = layoutParams

            if (category.id == categoryId) {
                radioButton.isChecked = true
            }

            radioGroup.addView(radioButton)
        }
    }
}