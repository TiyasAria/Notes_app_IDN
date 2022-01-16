package com.tiyas.notesapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tiyas.notesapp.R
import com.tiyas.notesapp.data.models.NoteData
import com.tiyas.notesapp.data.viewModelData.NotesViewModel
import com.tiyas.notesapp.databinding.FragmentUpdateBinding
import com.tiyas.notesapp.fragments.SharedViewModels

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModels : SharedViewModels by viewModels()
    private val mNotesViewModel : NotesViewModel by viewModels()

    private var _binding : FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

//    untuk mengupdate dari warna spinner
        binding.spUpdate.onItemSelectedListener = mSharedViewModels.listener

        //        untuk set Menu
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

//     membuat fun untuk konfirm kalau mau di delete menggunakan alert dialog
    private fun confirmItemRemoval() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete '${args.currentItem.title}' ?")
            .setMessage("Are you sure want to remove '${args.currentItem.title}'?")
            .setPositiveButton("Yes"){ _, _ ->
                mNotesViewModel.deleteData(args.currentItem)
                Toast.makeText(requireContext(), "Successfully Removed : ${args.currentItem.title} ",
                Toast.LENGTH_SHORT
                    ).show()
//                nav back
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No"){_,_ -> }
            .create()
            .show()
    }

    private fun updateItem() {
        val title = binding.etUptitle.text.toString()
        val description = binding.etDescUpdate.text.toString()
        val getPriority = binding.spUpdate.selectedItem.toString()

        val validation = mSharedViewModels.verifyDataFromUser(title, description)
        if (validation) {
            val updateItem = NoteData(
                args.currentItem.id,
                title,
                mSharedViewModels.parsePriority(getPriority),
                description

            )
            mNotesViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Berhasil di Update" , Toast.LENGTH_SHORT).show()
//            navigasi back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        } else{
            Toast.makeText(requireContext(), "Tolong isi semua persyaratan" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}