package com.example.playlistmakerfirstproject.audioplayer.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.presentation.library.FavPlaylistFragmentViewModel
import com.example.playlistmakerfirstproject.databinding.FragmentFavPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavPlaylistFragment : Fragment() {

    private val favPlaylistFragmentViewModel: FavPlaylistFragmentViewModel by activityViewModel()


    private var _binding: FragmentFavPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    companion object {
        @JvmStatic
        fun newInstance() = FavPlaylistFragment()
    }
}