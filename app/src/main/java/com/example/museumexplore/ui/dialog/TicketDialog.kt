package com.example.museumexplore.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.museumexplore.R
import com.example.museumexplore.databinding.TicketDialogBinding

class TicketDialog : DialogFragment() {
    private var _binding: TicketDialogBinding? = null
    private val binding get() = _binding!!

    private var ticketId: String? = null
    private var museumName: String? = null
    private var visitDate: String? = null

    companion object {
        fun newInstance(bundle: Bundle): TicketDialog {
            val fragment = TicketDialog()
            fragment.arguments = bundle
            return fragment
        }

        fun show(fm: FragmentManager, bundle: Bundle): TicketDialog {
            val dialog = TicketDialog.newInstance(bundle)
            dialog.show(fm, "fragment_ticket_dialog")
            return dialog
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TicketDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.DialogFragment)
        dialog.setCancelable(true)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            ticketId = bundle.getString("ticketId")
            museumName = bundle.getString("museumName")
            visitDate = bundle.getString("visitDate")
        }

        binding.textViewMuseumName.text = museumName
        binding.textViewVisitDate.text = visitDate
    }
}