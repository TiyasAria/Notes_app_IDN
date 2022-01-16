package com.tiyas.notesapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tiyas.notesapp.R
import com.tiyas.notesapp.data.models.NoteData
import com.tiyas.notesapp.data.viewModelData.NotesViewModel
import com.tiyas.notesapp.databinding.FragmentListBinding
import com.tiyas.notesapp.fragments.SharedViewModels
import com.tiyas.notesapp.fragments.list.adapter.ListAdapter
import com.tiyas.notesapp.utils.hideKeyboard
import jp.wasabeef.recyclerview.animators.LandingAnimator


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val  mNotesViewModel : NotesViewModel by viewModels()
    private val adapter : ListAdapter by lazy { ListAdapter() }
    private val mSharedViewModels : SharedViewModels by viewModels()
    private var _listBinding  : FragmentListBinding? = null
    private  val listBinding get() =  _listBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _listBinding  = FragmentListBinding.inflate(inflater, container, false)
        listBinding.lifecycleOwner = this
        listBinding.mSharedViewModel = mSharedViewModels

//        untuk setUp Recyclerview
        setUpRecyclerview()

//        observe live data
        mNotesViewModel.getAllData.observe(viewLifecycleOwner,  { data ->
//panggil viewModel yang di sharedViewModels , check apakah ada db nya atau gak ?
            mSharedViewModels.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return listBinding.root
    }


    private fun setUpRecyclerview() {
        //        set untuk atur recylerviewnya
        listBinding.rvList.adapter = adapter
        listBinding.rvList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        listBinding.rvList.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }
//        swipe to Delete
        swipeToDelete(listBinding.rvList)
    }

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
//                delete item
                mNotesViewModel.deleteData(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
//                restore deleted item
                restoreDeletedData(viewHolder.itemView, deletedItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem : NoteData){
        val snackbar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Undo"){
            mNotesViewModel.insertData(deletedItem)
        }
        snackbar.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_delete_all -> confirmRemoveAll()
            R.id.menu_priority_high -> mNotesViewModel.sortByHighPriority.observe(this,  {
                adapter.setData(it)
            })
            R.id.menu_priority_low -> mNotesViewModel.sortByLowPriority.observe(this,  {
                adapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All ?")
            .setMessage("Are you sure want to remove All?")
            .setPositiveButton("Yes"){ _, _ ->
                mNotesViewModel.deleteAllData()
                Toast.makeText(requireContext(), "Successfully Removed All ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query : String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"

        mNotesViewModel.searchDatabase(searchQuery).observe(this,  {
                list -> list.let { adapter.setData(it) }
        })
    }

    override fun onDestroy() {
        _listBinding = null
        super.onDestroy()
    }

}