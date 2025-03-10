    package com.example.playlistmakerfirstproject.audioplayer.presentation.settings


    import android.os.Bundle
    import android.view.LayoutInflater

    import android.view.View
    import android.view.ViewGroup
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.fragment.app.Fragment
    import com.example.playlistmakerfirstproject.R
    import com.example.playlistmakerfirstproject.databinding.FragmentSettingsBinding
    import org.koin.androidx.viewmodel.dsl.viewModel
    import org.koin.androidx.viewmodel.ext.android.viewModel


    class SettingsFragment : Fragment() {

        private lateinit var binding: FragmentSettingsBinding
        private val viewModel: SettingViewModel by viewModel()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentSettingsBinding.inflate(inflater, container, false)
            return binding.root
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


            viewModel.getModeLiveData().observe(viewLifecycleOwner) {isDarkMode ->
                changeMode(isDarkMode)
            }

            binding.theme.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.changeMode(isChecked) }


            // sharing part

            binding.support.setOnClickListener {
                viewModel.openSupport(getString(R.string.subject_message_support),getString(R.string.message_support))
            }

            binding.share.setOnClickListener {
                viewModel.shareApp()
            }

            binding.agreement.setOnClickListener {
                viewModel.legalAgreement()
            }



        }

        private fun changeMode (isDarkMode:Boolean) {
            if(isDarkMode==true) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.theme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.theme.isChecked = false
            }
        }




    }