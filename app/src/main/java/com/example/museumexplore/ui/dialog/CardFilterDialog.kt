package com.example.museumexplore.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.CardFilterDialogBinding
import com.example.museumexplore.databinding.CategoryDisplayBinding
import com.example.museumexplore.modules.Category
import com.example.museumexplore.ui.home.ArtWorksFragment
import kotlinx.coroutines.launch

class CardFilterDialog : DialogFragment() {
    private var _binding: CardFilterDialogBinding? = null
    private val binding get() = _binding!!

    private var museumId: String? = null

    private val categoryAdapter = CategoryAdapter()

    private var categoriesList = arrayListOf<Category>()

    var onResultCallback : ((String?)-> Unit)? = null
    companion object {
        fun newInstance(bundle: Bundle): CardFilterDialog {
            val fragment = CardFilterDialog()
            fragment.arguments = bundle
            return fragment
        }

        fun show(fm: FragmentManager, bundle: Bundle, onResultCallback: (String?)-> Unit): CardFilterDialog {
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
        }

        binding.listViewFilter.adapter = categoryAdapter

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                museumId?.let { currentMuseumId ->
                    appDatabase.categoryDao().getAll(currentMuseumId).observe(viewLifecycleOwner) {
                        categoriesList = it as ArrayList<Category>
                        categoryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class CategoryAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return categoriesList.size
        }

        override fun getItem(position: Int): Any {
            return categoriesList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = CategoryDisplayBinding.inflate(layoutInflater)

            rootView.textView2.text = categoriesList[position].descritpion

            rootView.root.setOnClickListener {
                onResultCallback?.invoke(categoriesList[position].id)
                dismiss()
            }

            return rootView.root
        }

    }
}