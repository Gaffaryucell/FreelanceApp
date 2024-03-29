package com.androiddevelopers.freelanceapp.view.discover

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.adapters.CommentsAdapter
import com.androiddevelopers.freelanceapp.databinding.FragmentCommentsBinding
import com.androiddevelopers.freelanceapp.viewmodel.discover.CommentsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : Fragment() {

    private lateinit var viewModel: CommentsViewModel

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private var adapter = CommentsAdapter()

    var postId : String? = null
    var token : String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        postId = arguments?.getString("postId")
        token = arguments?.getString("token")
        if (postId != null){
            viewModel.getAllComments(postId!!)
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()

        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = adapter
/////////////////////////////////////////////////////////
        //Resim için post verisi çekilmeli
        binding.btnSendComment.setOnClickListener{
            val comment = binding.etCommentInput.text.toString()
            val notification = viewModel.createNotificationData(
                userToken = token ?: "","",postId.toString()
            )
            viewModel.makeComment(postId ?: "",comment,notification)
            binding.etCommentInput.setText("")
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData(){
        viewModel.discoverPostComments.observe(viewLifecycleOwner, Observer {
            if (it != null){
                adapter.commentList = it
                adapter.notifyDataSetChanged()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation()
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}